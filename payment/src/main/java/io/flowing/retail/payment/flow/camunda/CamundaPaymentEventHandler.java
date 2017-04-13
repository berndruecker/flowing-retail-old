package io.flowing.retail.payment.flow.camunda;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import javax.json.JsonObject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.variable.Variables;
import org.h2.tools.Server;

import com.camunda.consulting.util.LicenseHelper;
import com.camunda.consulting.util.UserGenerator;

import io.flowing.retail.adapter.EventHandler;

public class CamundaPaymentEventHandler extends EventHandler {

  private ProcessEngine engine;
  private Server h2Server;

  public CamundaPaymentEventHandler() {
    startUpEngineAndInit();
    engine.getRepositoryService().createDeployment().addClasspathResource("Payment.bpmn").deploy();
  }

  @Override
  public boolean handleEvent(String type, String name, String transactionId, JsonObject event) {
    if ("Command".equals(type) && "RetrievePayment".equals(name)) {
      String refId = event.getString("refId");
      String reason = event.getString("reason");
      long amount = event.getJsonNumber("amount").longValue();

      Map<String, Object> variables = Variables.createVariables() //
          .putValue("refId", refId) //
          .putValue("transactionId", transactionId) //
          .putValue("reason", reason) //
          .putValue("amount", amount);

      engine.getRuntimeService().startProcessInstanceByKey("Payment", transactionId, variables);
      return true;
    } else {
      return false;
    }
  }

  private void startUpEngineAndInit() {
    
    boolean h2DbAlreadyRunning = false;
    String h2DbJdbcUrl = "jdbc:h2:tcp://localhost:8092/mem:camunda;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    try {
      Connection connection = DriverManager.getConnection(h2DbJdbcUrl,"sa",null);
      connection.close();      
      h2DbAlreadyRunning = true;
    } catch (Exception ex) {
      h2DbAlreadyRunning = false;
    }    
    
    StandaloneProcessEngineConfiguration config = new StandaloneProcessEngineConfiguration();
    config.setHistoryLevel(HistoryLevel.HISTORY_LEVEL_FULL);

    // if the DB was already started (by another engine in another Microservice) 
    // connect to this DB instead of starting an own one
    if (h2DbAlreadyRunning) {
      config.setJdbcUrl(h2DbJdbcUrl);
      config.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
      config.setDatabaseType("h2");
    } else {
      // use in memory DB, but expose as server
      config.setJdbcUrl("jdbc:h2:mem:camunda");
      config.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
      try {
        h2Server = Server.createTcpServer(new String[] { "-tcpPort", "8092", "-tcpAllowOthers" }).start();
        // now you can connect to "jdbc:h2:tcp://localhost:8092/mem:camunda"
      } catch (Exception ex) {
        throw new RuntimeException("Could not start H2 database server: " + ex.getMessage(), ex);
      }
    }

    engine = config.buildProcessEngine();
    
    // create Demo users and add enterprise license (if existent in file ~/.camunda/build.properties)
    LicenseHelper.setLicense(engine);
    UserGenerator.createDefaultUsers(engine);

    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          engine.close();
          h2Server.stop();
        } catch (Exception e) {
          throw new RuntimeException("Could not disconnect: " + e.getMessage(), e);
        }
      }
    });    
  }

}

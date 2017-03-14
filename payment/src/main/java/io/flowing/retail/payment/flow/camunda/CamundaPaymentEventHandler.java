package io.flowing.retail.payment.flow.camunda;

import java.util.Map;

import javax.json.JsonObject;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.variable.Variables;
import org.h2.tools.Server;

import com.camunda.consulting.util.LicenseHelper;
import com.camunda.consulting.util.UserGenerator;

import io.flowing.retail.adapter.EventHandler;

public class CamundaPaymentEventHandler extends EventHandler {

  private ProcessEngine engine;
  private org.h2.tools.Server h2Server;

  public CamundaPaymentEventHandler() {
    startUpEngineAndInit();
  }

  @Override
  public boolean handleEvent(String type, String name, String transactionId, JsonObject event) {
    if ("Command".equals(type) && "DoPayment".equals(name)) {
      String refId = event.getString("refId");
      String reason = event.getString("reason");
      long amount = event.getJsonNumber("amount").longValue();

      Map<String, Object> variables = Variables.createVariables() //
          .putValue("refId", refId) //
          .putValue("transactionId", transactionId) //
          .putValue("reason", reason) //
          .putValue("amount", amount);

      engine.getRuntimeService().startProcessInstanceByKey("Payment", variables);
      return true;
    } else {
      return false;
    }
  }

  private void startUpEngineAndInit() {
    // check if there is maybe already an engine running in the current JVM 
    // which is the case if multiple services are started by the simple starter
    engine = ProcessEngines.getDefaultProcessEngine();
    if (engine==null) { 
      StandaloneInMemProcessEngineConfiguration config = new StandaloneInMemProcessEngineConfiguration();
      config.setHistoryLevel(HistoryLevel.HISTORY_LEVEL_FULL);
      engine = config.buildProcessEngine();
      LicenseHelper.setLicense(engine);
      UserGenerator.createDefaultUsers(engine);
  
      try {
        h2Server = Server.createTcpServer(new String[] { "-tcpPort", "8092", "-tcpAllowOthers" }).start();
        // now you can connect to "jdbc:h2:tcp://localhost:8092/mem:camunda"
      } catch (Exception ex) {
        throw new RuntimeException("Could not start H2 database server: " + ex.getMessage(), ex);
      }

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
    
    engine.getRepositoryService().createDeployment().addClasspathResource("Payment.bpmn").deploy();    
  }

}

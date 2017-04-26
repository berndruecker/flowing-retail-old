package io.flowing.retail.order.flow.camunda;

import java.sql.Connection;
import java.sql.DriverManager;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.h2.tools.Server;

import com.camunda.consulting.util.LicenseHelper;
import com.camunda.consulting.util.UserGenerator;

public class CamundaEngineHelper {
  private static String h2DbJdbcUrl = "jdbc:h2:tcp://localhost:8092/mem:camunda;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";

  public static ProcessEngine startUpEngineAndInit() {
    Server h2Server = null;

    StandaloneInMemProcessEngineConfiguration config = new StandaloneInMemProcessEngineConfiguration();
    config.setHistoryLevel(HistoryLevel.HISTORY_LEVEL_FULL);

    // if the DB was already started (by another engine in another Microservice)
    // connect to this DB instead of starting an own one
    if (isH2DbAlreadyRunning()) {
      config.setJdbcUrl(h2DbJdbcUrl);
      config.setDatabaseSchemaUpdate("false");
    } else {
      // use in memory DB, but expose as server
      config.setJdbcUrl("jdbc:h2:mem:camunda");
      h2Server = startH2Server();
    }
    ProcessEngine engine = config.buildProcessEngine();

    // create Demo users and add enterprise license (if existent in file
    // ~/.camunda/build.properties)
    LicenseHelper.setLicense(engine);
    UserGenerator.createDefaultUsers(engine);
        
    final Server h2 = h2Server;
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          engine.close();
          if (h2!=null) {
            h2.stop();
          }
        } catch (Exception e) {
          throw new RuntimeException("Could not disconnect: " + e.getMessage(), e);
        }
      }
    });
    
    return engine;
  }
  
  private static Server startH2Server() {
    try {
      return Server.createTcpServer(new String[] { "-tcpPort", "8092", "-tcpAllowOthers" }).start();
      // now you can connect to "jdbc:h2:tcp://localhost:8092/mem:camunda"
    } catch (Exception ex) {
      throw new RuntimeException("Could not start H2 database server: " + ex.getMessage(), ex);
    }
  }

  public static boolean isH2DbAlreadyRunning() {
    try {
      Connection connection = DriverManager.getConnection(h2DbJdbcUrl, "sa", null);
      connection.close();
      return true;
    } catch (Exception ex) {
      return false;
    }
  }
}

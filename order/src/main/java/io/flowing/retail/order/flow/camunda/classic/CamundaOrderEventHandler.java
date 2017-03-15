package io.flowing.retail.order.flow.camunda.classic;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.runtime.ExecutionQuery;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.h2.tools.Server;

import com.camunda.consulting.util.LicenseHelper;
import com.camunda.consulting.util.UserGenerator;

import io.flowing.retail.adapter.EventHandler;
import io.flowing.retail.order.domain.Customer;
import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderItem;
import io.flowing.retail.order.domain.OrderRepository;

public class CamundaOrderEventHandler extends EventHandler {

  private ProcessEngine engine;
  private OrderRepository orderRepository = OrderRepository.instance;
  private Server h2Server;

  public CamundaOrderEventHandler() {
    startUpEngineAndInit();
    engine.getRepositoryService().createDeployment().addClasspathResource("order.bpmn").deploy();    
  }

  @Override
  public boolean handleEvent(String type, String eventName, String transactionId, JsonObject event) {
    VariableMap variables = Variables.createVariables();
    if ("Event".equals(type) && "OrderPlaced".equals(eventName)) {
      // Currently special handling to also persist the order

      Order order = parseOrder(event.getJsonObject("order"));
      // "Persist" order
      orderRepository.persistOrder(order);
      variables.put("orderId", order.getId());
      variables.put("transactionId", transactionId);
      
      engine.getRuntimeService().startProcessInstanceByKey("order", transactionId, variables);
      
      return true;
    } else {
  
      // Currently the transaction is NOT used for correlation, as we can assume
      // to hit some legacy system some time which is not able to handle it
      // That's why we only use it for tracking / monitoring purposes
  
      // Correlate by possible ids in this priority
      VariableMap correlationKeys = getCorrelationKeys(event);
  
      // add all possible additional correlation keys as variables to the flow
      if (event.get("pickId") != null) {
        variables.putValue("pickId", event.getString("pickId"));
      }
      if (event.get("shipmentId") != null) {
        variables.putValue("shipmentId", event.getString("shipmentId"));
      }
  
      MessageCorrelationBuilder correlation = engine.getRuntimeService().createMessageCorrelation(eventName);
      ExecutionQuery query = engine.getRuntimeService().createExecutionQuery().messageEventSubscriptionName(eventName);
      
      for (String key : correlationKeys.keySet()) {
        correlation.processInstanceVariableEquals(key, correlationKeys.get(key));
        query.processVariableValueEquals(key, correlationKeys.get(key));
      }
      
      // if nobody waits for this event we consider it not to be for us
      if (query.count()==0) {
        return false;
      }
      
      // otherwise correlate it
      correlation.setVariables(variables);
      correlation.correlateWithResult();
      return true;      
    }
  }

  private VariableMap getCorrelationKeys(JsonObject event) {
    VariableMap correlationKeys = Variables.createVariables();
    if (event.get("orderId") != null) {
      correlationKeys.put("orderId", event.getString("orderId"));
    } else if (event.get("refId") != null) {
      correlationKeys.put("orderId", event.getString("refId"));
    } else if (event.get("pickId") != null) {
      correlationKeys.put("pickId", event.getString("pickId"));
    } else if (event.get("shipmentId") != null) {
      correlationKeys.put("shipmentId", event.getString("shipmentId"));
    }
    return correlationKeys;
  }
  
  private Order parseOrder(JsonObject orderJson) {
    Order order = new Order();

    // Order Service is NOT interested in customer id - ignore:
    JsonObject customerJson = orderJson.getJsonObject("customer");
    orderJson.getString("customerId");

    Customer customer = new Customer() //
        .setName(customerJson.getString("name")) //
        .setAddress(customerJson.getString("address"));
    order.setCustomer(customer);

    JsonArray jsonArray = orderJson.getJsonArray("items");
    for (JsonObject itemJson : jsonArray.getValuesAs(JsonObject.class)) {
      order.addItem( //
          new OrderItem() //
              .setArticleId(itemJson.getString("articleId")) //
              .setAmount(itemJson.getInt("amount")));
    }

    return order;
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
    
    StandaloneInMemProcessEngineConfiguration config = new StandaloneInMemProcessEngineConfiguration();
    config.setHistoryLevel(HistoryLevel.HISTORY_LEVEL_FULL);

    // if the DB was already started (by another engine in another Microservice) 
    // connect to this DB instead of starting an own one
    if (h2DbAlreadyRunning) {
      config.setJdbcUrl(h2DbJdbcUrl);
    } else {
      // use in memory DB, but expose as server
      config.setJdbcUrl("jdbc:h2:mem:camunda");
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

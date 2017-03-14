package io.flowing.retail.order.flow.camunda.dsl.commons.camunda;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ExecutionQuery;
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

/**
 * Application Service or Command Handler assign a unique process identity
 * 
 * @author ruecker
 *
 */
public abstract class DslCamundaEventHandler extends EventHandler {

  private ProcessEngine engine;
  private OrderRepository orderRepository = OrderRepository.instance;
  private org.h2.tools.Server h2Server;

  public DslCamundaEventHandler() {
    init();
  }

  private void init() {
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

    engine.getRepositoryService().createDeployment().addString("order.bpmn", defineFlow()).deploy();

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


  public abstract String defineFlow();
  
  @Override
  public boolean handleEvent(String type, String name, String transactionId, JsonObject event) {
    if ("Event".equals(type) && "OrderPlaced".equals(name)) { // flow builder
                                                              // could remember
                                                              // this somehow
      Order order = parseOrder(event.getJsonObject("order"));

      // TODO: USe Callback from FlowBuilder
      // "Persist" order
      orderRepository.persistOrder(order);

      // Start workflow for order
      engine.getRuntimeService().startProcessInstanceByKey( //
          "Process_OrderPlaced",
          Variables.createVariables() //
              .putValue("correlationId", transactionId) //
              .putValue("orderId", order.getId()) //
              .putValue("incomingEvent", asString(event)) //
      );

      return true;
    } else {
      // Try to correlate by possible ids in this priority | TODO: Define in
      // flow builder
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

      // add all possible additional correlation keys as variables to the flow
      VariableMap variables = Variables.createVariables();
      variables.putValue("incomingEvent", asString(event));
      if (event.get("pickId") != null) {
        variables.putValue("pickId", event.getString("pickId"));
      }
      if (event.get("shipmentId") != null) {
        variables.putValue("shipmentId", event.getString("shipmentId"));
      }

      return correlateResponseEvent(name, correlationKeys, variables);
    }
  }

  private boolean correlateResponseEvent(String eventName, VariableMap correlationKeys, VariableMap variables) {
    ExecutionQuery query = engine.getRuntimeService().createExecutionQuery() //
        .messageEventSubscriptionName(eventName);

    for (String key : correlationKeys.keySet()) {
      query.processVariableValueEquals(key, correlationKeys.get(key));
    }

    if (query.count() == 0) {
      return false;
    }

    Execution execution = query.singleResult();
    engine.getRuntimeService().signal(execution.getId(), variables);
    return true;
  }

  private JsonArrayBuilder createJsonItemArray(Order order) {
    JsonArrayBuilder itemsArrayBuilder = Json.createArrayBuilder();
    for (OrderItem item : order.getItems()) {
      itemsArrayBuilder.add(Json.createObjectBuilder() //
          .add("articleId", item.getArticleId()) //
          .add("amount", item.getAmount()));
    }
    return itemsArrayBuilder;
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
}

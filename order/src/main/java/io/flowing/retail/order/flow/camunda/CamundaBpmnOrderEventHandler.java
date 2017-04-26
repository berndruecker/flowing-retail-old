package io.flowing.retail.order.flow.camunda;

import javax.json.JsonArray;
import javax.json.JsonObject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ExecutionQuery;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

import io.flowing.retail.adapter.EventHandler;
import io.flowing.retail.order.domain.Customer;
import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderItem;
import io.flowing.retail.order.domain.OrderRepository;

public class CamundaBpmnOrderEventHandler extends EventHandler {

  private ProcessEngine engine;
  private OrderRepository orderRepository = OrderRepository.instance;

  public CamundaBpmnOrderEventHandler() {
    engine = CamundaEngineHelper.startUpEngineAndInit();
    engine.getRepositoryService().createDeployment().addClasspathResource("order.bpmn").deploy();    
  }

  @Override
  public boolean handleEvent(String type, String eventName, String transactionId, JsonObject event) {
    if ("Event".equals(type) && "OrderPlaced".equals(eventName)) {
      // Currently special handling to also persist the order

      Order order = parseOrder(event.getJsonObject("order"));
      // "Persist" order
      orderRepository.persistOrder(order);

      VariableMap variables = Variables.createVariables();
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

      // add all possible additional correlation keys as variables to the flow
      VariableMap newVariables = getNewVariables(event);
      correlation.setVariables(newVariables);
      
      correlation.correlateWithResult();
      return true;      
    }
  }

  private VariableMap getNewVariables(JsonObject event) {
    VariableMap variables = Variables.createVariables();
    if (event.get("pickId") != null) {
      variables.putValue("pickId", event.getString("pickId"));
    }
    if (event.get("shipmentId") != null) {
      variables.putValue("shipmentId", event.getString("shipmentId"));
    }
    return variables;
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

}

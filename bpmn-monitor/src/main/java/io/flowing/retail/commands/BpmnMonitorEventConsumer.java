package io.flowing.retail.commands;

import javax.json.JsonObject;
import javax.json.JsonString;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.runtime.ExecutionQuery;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;

import io.flowing.retail.adapter.EventHandler;

public class BpmnMonitorEventConsumer extends EventHandler {

  @Override
  public boolean handleEvent(String type, String name, String transactionId, JsonObject event) {
    
    JsonString orderId = event.getJsonString("orderId");
    JsonString refId = event.getJsonString("refId");
    JsonString pickId = event.getJsonString("pickId");

    // we need a query to safely check if a process instance is waiting for the event
    ExecutionQuery query = BpmPlatform.getDefaultProcessEngine().getRuntimeService() //
      .createExecutionQuery() //
      .messageEventSubscriptionName(name);
    
    // and a correlation builder to correlate the event to it (if existent). We could also catch
    // the "MismatchingMessageCorrelationException: Cannot correlate message" exception
    // but feels better to ask for the count first
    MessageCorrelationBuilder correlation = BpmPlatform.getDefaultProcessEngine().getRuntimeService() //
        .createMessageCorrelation(name) //
        .setVariable("eventPayload", asString(event));

    if (orderId==null && refId==null && transactionId!=null) {
      correlation.setVariable("transactionId", transactionId);
      query = null; // a new instance will be started
    }
    else if (orderId!=null && refId==null && transactionId!=null) {
      correlation.processInstanceVariableEquals("transactionId", transactionId);
      query.processVariableValueEquals("transactionId", transactionId);
      
      correlation.setVariable("orderId", orderId.getString());
    }
    else if (orderId!=null) {
      correlation.processInstanceVariableEquals("orderId", orderId.getString());
      query.processVariableValueEquals("orderId", orderId.getString());
    } else if (refId!=null) {
      correlation.processInstanceVariableEquals("orderId", refId.getString());
      query.processVariableValueEquals("orderId", refId.getString());
      if (pickId!=null) {
        correlation.setVariable("pickId", pickId.getString());
      }
    } else if (pickId!=null) {
      correlation.processInstanceVariableEquals("pickId", pickId.getString());
      query.processVariableValueEquals("pickId", pickId.getString());
    } else {
      return false;
    }
    
    if (query!=null && query.count()==0) {      
      return false;
    }
    
    correlation.correlateWithResult();
    return true;      
  }

}

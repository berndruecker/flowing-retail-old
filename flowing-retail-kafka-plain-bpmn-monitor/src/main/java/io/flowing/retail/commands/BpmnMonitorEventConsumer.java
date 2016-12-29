package io.flowing.retail.commands;

import javax.json.JsonObject;
import javax.json.JsonString;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.runtime.EventSubscriptionQuery;
import org.camunda.bpm.engine.runtime.ExecutionQuery;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;

import io.flowing.retail.commands.channel.EventConsumer;

public class BpmnMonitorEventConsumer extends EventConsumer {

  @Override
  public boolean handleEvent(String type, String name, JsonObject event) {
    
    JsonString orderId = event.getJsonString("orderId");
    JsonString refId = event.getJsonString("refId");
    JsonString correlationId = event.getJsonString("correlationId");

    // we need a query to savely check if a process instance is waiting for the event
    ExecutionQuery query = BpmPlatform.getDefaultProcessEngine().getRuntimeService() //
      .createExecutionQuery() //
      .messageEventSubscriptionName(name);
    
    // and a correlation builder to correlate the event to it (if existant). We could also catch
    // the "MismatchingMessageCorrelationException: Cannot correlate message" exception
    // but feels beeter to ask for the count first
    MessageCorrelationBuilder correlation = BpmPlatform.getDefaultProcessEngine().getRuntimeService() //
        .createMessageCorrelation(name) //
        .setVariable("eventPayload", asString(event));

    if (orderId==null && refId==null && correlationId!=null) {
      correlation.setVariable("correlationId", correlationId.getString());
      query = null; // a new instance will be started
    }
    else if (orderId!=null && refId==null && correlationId!=null) {
      correlation.processInstanceVariableEquals("correlationId", correlationId.getString());
      query.processVariableValueEquals("correlationId", correlationId.getString());
      
      correlation.setVariable("orderId", orderId.getString());
    }
    else if (orderId!=null) {
      correlation.processInstanceVariableEquals("orderId", orderId.getString());
      query.processVariableValueEquals("orderId", orderId.getString());
    }
    else if (refId!=null) {
      correlation.processInstanceVariableEquals("orderId", refId.getString());
      query.processVariableValueEquals("orderId", refId.getString());
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

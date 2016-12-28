package io.flowing.retail.commands;

import javax.json.JsonObject;
import javax.json.JsonString;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.MessageCorrelationResult;

import io.flowing.retail.commands.channel.EventConsumer;

public class BpmnMonitorEventConsumer extends EventConsumer {

  @Override
  public boolean handleEvent(String type, String name, JsonObject event) {
    
    JsonString orderId = event.getJsonString("orderId");
    JsonString refId = event.getJsonString("refId");
    JsonString correlationId = event.getJsonString("correlationId");

    MessageCorrelationBuilder correlation = BpmPlatform.getDefaultProcessEngine().getRuntimeService() //
        .createMessageCorrelation(name) //
        .setVariable("eventPayload", asString(event));

    if (orderId==null && refId==null && correlationId!=null) {
      correlation.setVariable("correlationId", correlationId.getString());
    }
    else if (orderId!=null && refId==null && correlationId!=null) {
      correlation.processInstanceVariableEquals("correlationId", correlationId.getString());
      correlation.setVariable("orderId", correlationId.getString());
    }
    else if (orderId!=null) {
      correlation.processInstanceVariableEquals("orderId", orderId.getString());
    }
    else if (refId!=null) {
      correlation.processInstanceVariableEquals("orderId", refId.getString());
    }
    
    MessageCorrelationResult result = correlation.correlateWithResult();
    if (result.getProcessInstance()!=null) {
      return true;      
    }
    
    return false;
  }

}

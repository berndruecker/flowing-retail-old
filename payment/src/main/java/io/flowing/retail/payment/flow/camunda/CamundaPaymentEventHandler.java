package io.flowing.retail.payment.flow.camunda;

import java.util.Map;

import javax.json.JsonObject;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.variable.Variables;

import io.flowing.retail.adapter.EventHandler;

public class CamundaPaymentEventHandler extends EventHandler {

  private ProcessEngine engine;

  public CamundaPaymentEventHandler() {
    engine = CamundaEngineHelper.startUpEngineAndInit();
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

}

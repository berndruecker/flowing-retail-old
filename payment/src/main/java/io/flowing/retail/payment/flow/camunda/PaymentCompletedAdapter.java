package io.flowing.retail.payment.flow.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import io.flowing.retail.payment.application.PaymentEventProducer;

public class PaymentCompletedAdapter implements JavaDelegate {

  public void execute(DelegateExecution execution) throws Exception {
    PaymentEventProducer eventProducer = new PaymentEventProducer();
    
    String transactionId = (String)execution.getVariable("transactionId");
    String refId = (String)execution.getVariable("refId");
    String reason = (String)execution.getVariable("reason");

    eventProducer.publishEventPaymentReceivedEvent(transactionId, refId, reason);
  }


}

package io.flowing.retail.payment.flow.entity;

import javax.json.JsonObject;

import io.flowing.retail.adapter.EventHandler;
import io.flowing.retail.payment.application.PaymentEventProducer;
import io.flowing.retail.payment.domain.PaymentService;

public class EntityStatePaymentEventHandler extends EventHandler {

  private PaymentEventProducer eventProducer = new PaymentEventProducer();

  @Override
  public boolean handleEvent(String type, String name, String transactionId, JsonObject event) {
    if ("Command".equals(type) && "DoPayment".equals(name)) {
      String refId = event.getString("refId");
      String reason = event.getString("reason");
      long amount = event.getJsonNumber("amount").longValue();

      String customerAccountDetails = "todo"; // TODO

      if (PaymentService.instance.processPayment(customerAccountDetails, refId, amount)) {
        // I skip a separate service doing the event publishing
        eventProducer.publishEventPaymentReceivedEvent(transactionId, refId, reason);
      } else { 
        eventProducer.publishEventPaymentFailedEvent(transactionId, refId, reason);
      }
    } else {
      return false;
    }
    return true;
  }

}

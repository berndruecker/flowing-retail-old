package io.flowing.retail.payment.application;

import javax.json.JsonObject;

import io.flowing.retail.adapter.EventHandler;
import io.flowing.retail.payment.domain.PaymentService;

public class PaymentEventConsumer extends EventHandler {

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
      } else { // no stock
        eventProducer.publishEventPaymentErrorEvent(transactionId, refId, reason);
      }
    } else {
      return false;
    }
    return true;
  }

}

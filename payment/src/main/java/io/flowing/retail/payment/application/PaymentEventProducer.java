package io.flowing.retail.payment.application;

import javax.json.JsonObjectBuilder;

import io.flowing.retail.adapter.EventProducer;

public class PaymentEventProducer extends EventProducer {

  public void publishEventPaymentReceivedEvent(String transactionId, String refId, String reason) {
    JsonObjectBuilder json = createEventPayloadJson("PaymentReceived", transactionId);
    json //
        .add("refId", refId) //
        .add("reason", reason);
    send(json);
  }

  public void publishEventPaymentFailedEvent(String transactionId, String refId, String reason) {
    JsonObjectBuilder json = createEventPayloadJson("PaymentError", transactionId);
    json //
        .add("refId", refId) //
        .add("reason", reason);
    send(json);
  }

}

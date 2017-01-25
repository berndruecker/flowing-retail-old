package io.flowing.retail.commands;

import javax.json.JsonObjectBuilder;

import io.flowing.retail.commands.channel.EventProducer;

public class PaymentEventProducer extends EventProducer {

  public void publishEventPaymentReceivedEvent(String refId, String reason) {
    JsonObjectBuilder json = createPayloadJson("Event", "PaymentReceived");
    json //
        .add("refId", refId) //
        .add("reason", reason);
    send(json);
  }

  public void publishEventPaymentErrorEvent(String refId, String reason) {
    JsonObjectBuilder json = createPayloadJson("Event", "PaymentError");
    json //
        .add("refId", refId) //
        .add("reason", reason);
    send(json);
  }

}

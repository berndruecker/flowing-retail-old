package io.flowing.retail.commands;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class EventConsumer {

  private EventProducer eventProducer = new EventProducer();

  public void handleEvent(String eventAsJson) {
    System.out.println("EVENT HAPPENED: " + eventAsJson);

    JsonReader jsonReader = Json.createReader(new StringReader(eventAsJson));
    JsonObject event = jsonReader.readObject();
    jsonReader.close();

    String type = event.getString("type");
    String name = event.getString("name");

    if ("Command".equals(type) && "DoPayment".equals(name)) {
      String refId = event.getString("refId");
      String reason = event.getString("reason");
      long amount = event.getJsonNumber("amount").longValue();

      String customerAccountDetails = "todo"; // TODO
      
      if (PaymentService.instance.processPayment(customerAccountDetails, refId, amount)) {
        // I skip a separate service doing the event publishing
        eventProducer.publishEventPaymentReceivedEvent(refId, reason);
      } else { // no stock
        eventProducer.publishEventPaymentErrorEvent(refId, reason);
      }
    } else {
      System.out.println("..ignored");
    }
  }

}

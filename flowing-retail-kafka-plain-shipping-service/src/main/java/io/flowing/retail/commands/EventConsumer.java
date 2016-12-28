package io.flowing.retail.commands;

import java.io.StringReader;

import javax.json.Json;
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

    if ("Command".equals(type) && "ShipGoods".equals(name)) {
      String pickId = event.getString("pickId");
      String logisticsProvider = event.getString("logisticsProvider");
      String recipientName = event.getString("recipientName");
      String recipientAddress = event.getString("recipientAddress");

      String shippingId = ShippingService.instance.createShipment(pickId, recipientName, recipientAddress, logisticsProvider);
      if (shippingId!=null) {
        eventProducer.publishEventGoodsShipped(pickId, shippingId);
      } else { 
        eventProducer.publishEventShipmentError(pickId);
      }
    } else {
      System.out.println("..ignored");
    }
  }


}

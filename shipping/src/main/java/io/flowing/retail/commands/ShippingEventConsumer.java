package io.flowing.retail.commands;

import javax.json.JsonObject;

import io.flowing.retail.commands.channel.EventHandler;

public class ShippingEventConsumer extends EventHandler {

  private ShippingEventProducer eventProducer = new ShippingEventProducer();

  @Override
  public boolean handleEvent(String type, String name, JsonObject event) {
    if ("Command".equals(type) && "ShipGoods".equals(name)) {
      String pickId = event.getString("pickId");
      String logisticsProvider = event.getString("logisticsProvider");
      String recipientName = event.getString("recipientName");
      String recipientAddress = event.getString("recipientAddress");

      String shippingId = ShippingService.instance.createShipment(pickId, recipientName, recipientAddress, logisticsProvider);
      if (shippingId != null) {
        eventProducer.publishEventGoodsShipped(pickId, shippingId);
      } else {
        eventProducer.publishEventShipmentError(pickId);
      }
      return true;
    } else {
      return false;
    }
  }

}

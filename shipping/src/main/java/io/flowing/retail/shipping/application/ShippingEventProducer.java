package io.flowing.retail.shipping.application;

import javax.json.JsonObjectBuilder;

import io.flowing.retail.adapter.ChannelSender;
import io.flowing.retail.adapter.EventProducer;

public class ShippingEventProducer extends EventProducer {

  public void publishEventGoodsShipped(String transactionId, String pickId, String shippingId) {
    JsonObjectBuilder json = createEventPayloadJson("GoodsShipped", transactionId);
    json //
        .add("pickId", pickId) //
        .add("shipmentId", shippingId);
    send(json);
  }

  public void publishEventShipmentError(String transactionId, String pickId) {
    JsonObjectBuilder json = createEventPayloadJson("ShipmentError", transactionId);
    json //
        .add("pickId", pickId);
    send(json);
  }

  @Override
  public void send(String event) {
    ChannelSender.instance.send(event);
  }



  


  

}

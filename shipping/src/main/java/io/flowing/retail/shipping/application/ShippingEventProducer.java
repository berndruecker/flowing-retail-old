package io.flowing.retail.shipping.application;

import javax.json.JsonObjectBuilder;

import io.flowing.retail.adapter.ChannelSender;
import io.flowing.retail.adapter.EventProducer;

public class ShippingEventProducer extends EventProducer {

  public void publishEventGoodsShipped(String pickId, String shippingId) {
    JsonObjectBuilder json = createPayloadJson("Event", "GoodsShipped");
    json //
        .add("pickId", pickId) //
        .add("shipmentId", shippingId);
    send(json);
  }

  public void publishEventShipmentError(String pickId) {
    JsonObjectBuilder json = createPayloadJson("Event", "ShipmentError");
    json //
        .add("pickId", pickId);
    send(json);
  }

  @Override
  public void send(String event) {
    ChannelSender.instance.send(event);
  }



  


  

}

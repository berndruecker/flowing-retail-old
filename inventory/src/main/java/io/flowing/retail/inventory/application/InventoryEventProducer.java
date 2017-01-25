package io.flowing.retail.inventory.application;

import javax.json.JsonObjectBuilder;

import io.flowing.retail.adapter.EventProducer;

public class InventoryEventProducer extends EventProducer {

  public void publishEventGoodsReserved(String refId, String reason) {
    JsonObjectBuilder json = createPayloadJson("Event", "GoodsReserved");
    json //
        .add("refId", refId) //
        .add("reason", reason);
    send(json);
  }

  public void publishEventGoodsNotReserved(String refId, String reason) {
    JsonObjectBuilder json = createPayloadJson("Event", "GoodsNotReserved");
    json //
        .add("refId", refId) //
        .add("reason", reason);
    send(json);
  }

  public void publishEventGoodsPicked(String refId, String reason, String pickId) {
    JsonObjectBuilder json = createPayloadJson("Event", "GoodsPicked");
    json //
        .add("refId", refId) //
        .add("reason", reason) //
        .add("pickId", pickId);
    send(json);
  }

  public void publishEventPickError(String refId, String reason) {
    JsonObjectBuilder json = createPayloadJson("Event", "PickError");
    json //
        .add("refId", refId) //
        .add("reason", reason);
    send(json);
  }

}

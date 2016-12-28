package io.flowing.retail.commands;

import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonObject;

import io.flowing.retail.commands.channel.EventConsumer;

public class InventoryEventConsumer extends EventConsumer {

  private InventoryEventProducer eventProducer = new InventoryEventProducer();

  @Override
  public boolean handleEvent(String type, String name, JsonObject event) {
    if ("Command".equals(type) && "ReserveGoods".equals(name)) {
      String refId = event.getString("refId");
      String reason = event.getString("reason");
      LocalDateTime expirationDate = LocalDateTime.parse(event.getString("expirationDate"));
      ArrayList<Item> items = parseItems(event.getJsonArray("items"));

      if (InventoryService.instance.reserveGoods(items, refId, reason, expirationDate)) {
        // I skip a separate service doing the event publishing
        eventProducer.publishEventGoodsReserved(refId, reason);
      } else { // no stock
        eventProducer.publishEventGoodsNotReserved(refId, reason);
      }
    } else if ("Command".equals(type) && "PickGoods".equals(name)) {
      String refId = event.getString("refId");
      String reason = event.getString("reason");
      ArrayList<Item> items = parseItems(event.getJsonArray("items"));

      String pickId = InventoryService.instance.pickItems(items, refId, reason);
      if (pickId != null) {
        // TODO: Maybe move in inventory service?
        eventProducer.publishEventGoodsPicked(refId, reason, pickId);
      } else { // no stock
        eventProducer.publishEventPickError(refId, reason);
      }
    } else {
      return false;
    }
    return true;
  }

  private ArrayList<Item> parseItems(JsonArray jsonArray) {
    ArrayList<Item> items = new ArrayList<Item>();
    for (JsonObject itemJson : jsonArray.getValuesAs(JsonObject.class)) {
      items.add(new Item() //
          .setArticleId(itemJson.getString("articleId")) //
          .setAmount(itemJson.getInt("amount")));
    }
    return items;
  }

}

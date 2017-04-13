package io.flowing.retail.inventory.application;

import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.json.JsonArray;
import javax.json.JsonObject;

import io.flowing.retail.adapter.EventHandler;
import io.flowing.retail.inventory.domain.InventoryService;
import io.flowing.retail.inventory.domain.Item;

public class InventoryEventHandler extends EventHandler {

  private InventoryEventProducer eventProducer = new InventoryEventProducer();

  @Override
  public boolean handleEvent(String type, String name, String transactionId, JsonObject event) {
    if ("Command".equals(type) && "ReserveGoods".equals(name)) {
      String refId = event.getString("refId");
      String reason = event.getString("reason");
      LocalDateTime expirationDate = LocalDateTime.parse(event.getString("expirationDate"));
      ArrayList<Item> items = parseItems(event.getJsonArray("items"));

      if (InventoryService.instance.reserveGoods(items, refId, reason, expirationDate)) {
        // I skip a separate service doing the event publishing
        eventProducer.publishEventGoodsReserved(transactionId, refId, reason);
      } else { // no stock
        eventProducer.publishEventGoodsNotReserved(transactionId, refId, reason);
      }
    } else if ("Command".equals(type) && "FetchGoods".equals(name)) {
      String refId = event.getString("refId");
      String reason = event.getString("reason");
      ArrayList<Item> items = parseItems(event.getJsonArray("items"));

      String pickId = InventoryService.instance.pickItems(items, refId, reason);
      if (pickId != null) {
        // TODO: Maybe move in inventory service?
        eventProducer.publishEventGoodsFetched(transactionId, refId, reason, pickId);
      } else { // no stock
        eventProducer.publishEventPickError(transactionId, refId, reason);
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

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
    }else if ("Command".equals(type) && "PickGoods".equals(name)) {
        String refId = event.getString("refId");
        String reason = event.getString("reason");
        ArrayList<Item> items = parseItems(event.getJsonArray("items"));

        String pickId = InventoryService.instance.pickItems(items, refId, reason);
        if (pickId!=null) {
          // TODO: Maybe move in inventory service?
          eventProducer.publishEventGoodsPicked(refId, reason, pickId);
        } else { // no stock
          eventProducer.publishEventPickError(refId, reason);
        }
    } else {
      System.out.println("..ignored");
    }
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

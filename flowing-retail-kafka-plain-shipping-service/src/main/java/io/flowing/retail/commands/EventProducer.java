package io.flowing.retail.commands;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import io.flowing.retail.commands.channel.ChannelSender;

public class EventProducer {

  public void publishEventGoodsShipped(String pickId, String shippingId) {
    JsonObjectBuilder json = createPayloadJson("Event", "GoodsShipped");
    json //
        .add("pickId", pickId) //
        .add("shipmentId", shippingId);
    ChannelSender.instance.send(asString(json));
  }

  public void publishEventShipmentError(String pickId) {
    JsonObjectBuilder json = createPayloadJson("Event", "ShipmentError");
    json //
        .add("pickId", pickId);
    ChannelSender.instance.send(asString(json));
  }

  private JsonObjectBuilder createPayloadJson(String type, String name) {
    return Json.createObjectBuilder() //
        .add("type", type)//
        .add("name", name);
  }

//  private JsonArrayBuilder createJsonItemArray(List<Item> items) {
//    JsonArrayBuilder itemsArrayBuilder = Json.createArrayBuilder();
//    for (Item item : items) {
//      itemsArrayBuilder.add(Json.createObjectBuilder() //
//          .add("articleId", item.getArticleId()) //
//          .add("amount", item.getAmount()));
//    }
//    return itemsArrayBuilder;
//  }
  
  private String asString(JsonObjectBuilder builder) {
    JsonObject jsonObject = builder.build();

    StringWriter eventStringWriter = new StringWriter();
    JsonWriter writer = Json.createWriter(eventStringWriter);
    writer.writeObject(jsonObject);
    writer.close();

    return eventStringWriter.toString();
  }

  


  

}

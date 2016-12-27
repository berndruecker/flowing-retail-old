package io.flowing.retail.command;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonWriter;

import io.flowing.retail.command.channel.ChannelSender;

public class EventProducer {
  
  public void publishEventOrderPlaced(String correlationId, String customerId, ShoppingCart shoppingCart) {
    String eventString = createEventJson(correlationId, customerId, shoppingCart);
    ChannelSender.instance.send(eventString);
  }
  
  public String createEventJson(String correlationId, String customerId, ShoppingCart shoppingCart) {
    JsonArrayBuilder itemsArrayBuilder = Json.createArrayBuilder();
    for (Item item : shoppingCart.getItems()) {
      itemsArrayBuilder.add(Json.createObjectBuilder() //
          .add("articleId", item.getArticleId()).add("amount", item.getAmount()));
    }

    JsonObject event = Json.createObjectBuilder() //
        .add("type", "Event")//
        .add("name", "OrderPlacedEvent") //
        .add("correlationId", correlationId) //
        .add("order",
            Json.createObjectBuilder() //
                .add("customerId", customerId) //
                .add("items", itemsArrayBuilder))
        .build();

    StringWriter eventStringWriter = new StringWriter();
    JsonWriter writer = Json.createWriter(eventStringWriter);
    writer.writeObject(event);
    writer.close();

    return eventStringWriter.toString();
  }
}

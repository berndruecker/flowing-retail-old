package io.flowing.retail.commands;

import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

import io.flowing.retail.commands.channel.ChannelSender;

public class EventProducer {

  public void publishCommandReserveGoods(Order order) {
    JsonObjectBuilder json = createPayloadJson("Command", "ReserveGoods");
    json //
        .add("refId", order.getId()) //
        .add("reason", "CustomerOrder") //
        .add("expirationDate", LocalDateTime.now().plus(2, ChronoUnit.DAYS).toString()) //
        .add("items", createJsonItemArray(order));
    ChannelSender.instance.send(asString(json));
  }

  public void publishCommandDoPayment(Order order) {
    JsonObjectBuilder json = createPayloadJson("Command", "DoPayment");
    json //
        .add("refId", order.getId()) //
        .add("reason", "CustomerOrder") //
        .add("amount", order.getTotalSum());
    ChannelSender.instance.send(asString(json));
  }

  public void publishCommandPickGoods(Order order) {
    JsonObjectBuilder json = createPayloadJson("Command", "PickGoods");
    json //
        .add("refId", order.getId()) //
        .add("reason", "CustomerOrder") //
        .add("items", createJsonItemArray(order));
    ChannelSender.instance.send(asString(json));
  }

  public void publishCommandShipGoods(Order order, String pickId) {
    JsonObjectBuilder json = createPayloadJson("Command", "ShipGoods");
    json //
        .add("pickId", pickId) //
        .add("logisticsProvider", "DHL") // customer orders are always shipped via DHL
        .add("recipientName", order.getCustomer().getName()) //
        .add("recipientAddress", order.getCustomer().getAddress());
    ChannelSender.instance.send(asString(json));
  }

  public void publishEventOrderCompleted(String orderId) {
    JsonObjectBuilder json = createPayloadJson("Event", "OrderCompleted");
    json //
        .add("orderId", orderId);
    ChannelSender.instance.send(asString(json));
  }


  private JsonObjectBuilder createPayloadJson(String type, String name) {
    return Json.createObjectBuilder() //
        .add("type", type)//
        .add("name", name);
  }

  private JsonArrayBuilder createJsonItemArray(Order order) {
    JsonArrayBuilder itemsArrayBuilder = Json.createArrayBuilder();
    for (OrderItem item : order.getItems()) {
      itemsArrayBuilder.add(Json.createObjectBuilder() //
          .add("articleId", item.getArticleId()) //
          .add("amount", item.getAmount()));
    }
    return itemsArrayBuilder;
  }
  
  private String asString(JsonObjectBuilder builder) {
    JsonObject jsonObject = builder.build();

    StringWriter eventStringWriter = new StringWriter();
    JsonWriter writer = Json.createWriter(eventStringWriter);
    writer.writeObject(jsonObject);
    writer.close();

    return eventStringWriter.toString();
  }

}

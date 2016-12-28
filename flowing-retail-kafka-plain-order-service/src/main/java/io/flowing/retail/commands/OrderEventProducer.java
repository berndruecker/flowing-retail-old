package io.flowing.retail.commands;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import io.flowing.retail.commands.channel.EventProducer;

public class OrderEventProducer extends EventProducer {

  public void publishEventOrderCreated(String correlationId, Order order) {
    JsonObjectBuilder json = createPayloadJson("Command", "ReserveGoods");
    json //
        .add("correlationId", correlationId) //
        .add("orderId", order.getId()) //
        .add("items", createJsonItemArray(order));
    send(json);
  }
  
  public void publishCommandReserveGoods(Order order) {
    JsonObjectBuilder json = createPayloadJson("Command", "ReserveGoods");
    json //
        .add("refId", order.getId()) //
        .add("reason", "CustomerOrder") //
        .add("expirationDate", LocalDateTime.now().plus(2, ChronoUnit.DAYS).toString()) //
        .add("items", createJsonItemArray(order));
    send(json);
  }

  public void publishCommandDoPayment(Order order) {
    JsonObjectBuilder json = createPayloadJson("Command", "DoPayment");
    json //
        .add("refId", order.getId()) //
        .add("reason", "CustomerOrder") //
        .add("amount", order.getTotalSum());
    send(json);
  }

  public void publishCommandPickGoods(Order order) {
    JsonObjectBuilder json = createPayloadJson("Command", "PickGoods");
    json //
        .add("refId", order.getId()) //
        .add("reason", "CustomerOrder") //
        .add("items", createJsonItemArray(order));
    send(json);
  }

  public void publishCommandShipGoods(Order order, String pickId) {
    JsonObjectBuilder json = createPayloadJson("Command", "ShipGoods");
    json //
        .add("pickId", pickId) //
        .add("logisticsProvider", "DHL") // customer orders are always shipped
                                         // via DHL
        .add("recipientName", order.getCustomer().getName()) //
        .add("recipientAddress", order.getCustomer().getAddress());
    send(json);
  }

  public void publishEventOrderCompleted(String orderId) {
    JsonObjectBuilder json = createPayloadJson("Event", "OrderCompleted");
    json //
        .add("orderId", orderId);
    send(json);
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

  

}

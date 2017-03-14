package io.flowing.retail.order.application;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import io.flowing.retail.adapter.EventProducer;
import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderItem;

public class OrderEventProducer extends EventProducer {

  public void publishEventOrderCreated(String transactionId, Order order) {
    JsonObjectBuilder json = createPayloadJson("Event", "OrderCreated", transactionId);
    json //
        .add("orderId", order.getId()) //
        .add("items", createJsonItemArray(order));
    send(json);
  }
  
  public void publishCommandReserveGoods(String transactionId, Order order) {
    JsonObjectBuilder json = createPayloadJson("Command", "ReserveGoods", transactionId);
    json //
        .add("refId", order.getId()) //
        .add("reason", "CustomerOrder") //
        .add("expirationDate", LocalDateTime.now().plus(2, ChronoUnit.DAYS).toString()) //
        .add("items", createJsonItemArray(order));
    send(json);
  }

  public void publishCommandDoPayment(String transactionId, Order order) {
    JsonObjectBuilder json = createCommandPayloadJson("DoPayment", transactionId);
    json //
        .add("refId", order.getId()) //
        .add("reason", "CustomerOrder") //
        .add("amount", order.getTotalSum());
    send(json);
  }

  public void publishCommandPickGoods(String transactionId, Order order) {
    JsonObjectBuilder json = createCommandPayloadJson("PickGoods", transactionId);
    json //
        .add("refId", order.getId()) //
        .add("reason", "CustomerOrder") //
        .add("items", createJsonItemArray(order));
    send(json);
  }

  public void publishCommandShipGoods(String transactionId, Order order, String pickId) {
    JsonObjectBuilder json = createCommandPayloadJson("ShipGoods", transactionId);
    json //
        .add("pickId", pickId) //
        .add("logisticsProvider", "DHL") // customer orders are always shipped
                                         // via DHL
        .add("recipientName", order.getCustomer().getName()) //
        .add("recipientAddress", order.getCustomer().getAddress());
    send(json);
  }

  public void publishEventOrderCompleted(String transactionId, String orderId) {
    JsonObjectBuilder json = createEventPayloadJson("OrderCompleted", transactionId);
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

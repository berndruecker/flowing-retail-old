package io.flowing.retail.commands;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class EventConsumer {

  public void handleEvent(String eventAsJson) {
    System.out.println("EVENT HAPPENED: " + eventAsJson);

    JsonReader jsonReader = Json.createReader(new StringReader(eventAsJson));
    JsonObject event = jsonReader.readObject();
    jsonReader.close();

    String type = event.getString("type");
    String name = event.getString("name");

    if ("Event".equals(type) && "OrderPlacedEvent".equals(name)) {
      String correlationId = event.getString("correlationId");
      Order order = parseOrder(event.getJsonObject("order"));

      OrderService.instance.processOrder(order);
    } else if ("Event".equals(type) && "GoodsReserved".equals(name)) {
      String reason = event.getString("reason");
      if ("CustomerOrder".equals(reason)) {
        String orderId = event.getString("refId");
        OrderService.instance.processGoodsReservation(orderId);
      } else {
        System.out.println("..ignored (reservation not for a customer order).");        
      }
    } else if ("Event".equals(type) && "PaymentReceived".equals(name)) {
      String reason = event.getString("reason");
      if ("CustomerOrder".equals(reason)) {
        String orderId = event.getString("refId");
        OrderService.instance.processPaymentReceived(orderId);
      } else {
        System.out.println("..ignored (reservation not for a customer order).");        
      }
    } else if ("Event".equals(type) && "GoodsPicked".equals(name)) {
      String reason = event.getString("reason");
      if ("CustomerOrder".equals(reason)) {
        String orderId = event.getString("refId");
        String pickId = event.getString("pickId");
        OrderService.instance.processGoodsPicked(orderId, pickId); // TODO: oder direkt?
      } else {
        System.out.println("..ignored (reservation not for a customer order).");        
      }
    } else if ("Event".equals(type) && "GoodsShipped".equals(name)) {
      String pickId = event.getString("pickId");
      String shipmentId = event.getString("shipmentId");
      boolean processed = OrderService.instance.processGoodsShipped(pickId, shipmentId);
      if (!processed) {
        System.out.println("..ignored (shipment seems not to belong to a customer order).");        
      }
    } else {
      System.out.println("..ignored.");
    }
  }

  private Order parseOrder(JsonObject orderJson) {
    Order order = new Order();

    // Order Service is NOT interested in customer id - ignore:
    JsonObject customerJson = orderJson.getJsonObject("customer");
    orderJson.getString("customerId");

    Customer customer = new Customer() //
      .setName(customerJson.getString("name")) //
      .setAddress(customerJson.getString("address"));
    order.setCustomer(customer);

    JsonArray jsonArray = orderJson.getJsonArray("items");
    for (JsonObject itemJson : jsonArray.getValuesAs(JsonObject.class)) {
      order.addItem( //
          new OrderItem() //
              .setArticleId(itemJson.getString("articleId")) //
              .setAmount(itemJson.getInt("amount")));
    }

    return order;
  }

}

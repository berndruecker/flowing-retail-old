package io.flowing.retail.commands;

import javax.json.JsonArray;
import javax.json.JsonObject;

import io.flowing.retail.commands.channel.EventConsumer;

public class OrderEventConsumer extends EventConsumer {

  @Override
  public boolean handleEvent(String type, String name, JsonObject event) {
    if ("Event".equals(type) && "OrderPlacedEvent".equals(name)) {
      String correlationId = event.getString("correlationId");
      Order order = parseOrder(event.getJsonObject("order"));

      OrderService.instance.processOrder(correlationId, order);
    } else if ("Event".equals(type) && "GoodsReserved".equals(name)) {
      String reason = event.getString("reason");
      if ("CustomerOrder".equals(reason)) {
        String orderId = event.getString("refId");
        OrderService.instance.processGoodsReservation(orderId);
      } else {
        System.out.println("..ignored (reservation not for a customer order).");
        return false;
      }
    } else if ("Event".equals(type) && "PaymentReceived".equals(name)) {
      String reason = event.getString("reason");
      if ("CustomerOrder".equals(reason)) {
        String orderId = event.getString("refId");
        OrderService.instance.processPaymentReceived(orderId);
      } else {
        System.out.println("..ignored (reservation not for a customer order).");
        return false;
      }
    } else if ("Event".equals(type) && "GoodsPicked".equals(name)) {
      String reason = event.getString("reason");
      if ("CustomerOrder".equals(reason)) {
        String orderId = event.getString("refId");
        String pickId = event.getString("pickId");
        OrderService.instance.processGoodsPicked(orderId, pickId); // TODO: oder
                                                                   // direkt?
      } else {
        System.out.println("..ignored (reservation not for a customer order).");
        return false;
      }
    } else if ("Event".equals(type) && "GoodsShipped".equals(name)) {
      String pickId = event.getString("pickId");
      String shipmentId = event.getString("shipmentId");
      boolean processed = OrderService.instance.processGoodsShipped(pickId, shipmentId);
      if (!processed) {
        System.out.println("..ignored (shipment seems not to belong to a customer order).");
        return false;
      }
    } else {
      return false;
    }
    return true;
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

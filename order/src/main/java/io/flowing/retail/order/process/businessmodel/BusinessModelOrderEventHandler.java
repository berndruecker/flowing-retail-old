package io.flowing.retail.order.process.businessmodel;

import java.util.Collection;

import javax.json.JsonArray;
import javax.json.JsonObject;

import io.flowing.retail.adapter.EventHandler;
import io.flowing.retail.order.application.OrderEventProducer;
import io.flowing.retail.order.domain.Customer;
import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderItem;
import io.flowing.retail.order.domain.OrderRepository;
import io.flowing.retail.order.process.businessmodel.ExtendedOrder.GoodsDeliveryStatus;

public class BusinessModelOrderEventHandler extends EventHandler {
  
  private OrderEventProducer eventProducer = new OrderEventProducer();
  private OrderRepository orderRepository = OrderRepository.instance;
  
  public void processOrder(String correlationId, Order order) {
    ExtendedOrder extendedOrder = new ExtendedOrder(order);
    System.out.println("order will be processed: " + extendedOrder);
    
    // "Persist" order
    orderRepository.persistOrder(extendedOrder);

    eventProducer.publishEventOrderCreated(correlationId, order);
    
    // issue ReserveGoodsCommand  
    eventProducer.publishCommandReserveGoods(order);
    // issue DoPaymentCommand
    eventProducer.publishCommandDoPayment(order);
  }

  public void processGoodsReservation(String orderId) { 
    ExtendedOrder order = (ExtendedOrder) orderRepository.getOrder(orderId);
    synchronized (order) { // TODO: double check
      order.setDeliveryStatus(GoodsDeliveryStatus.GOODS_RESERVED);
      checkOrderReadyForPicking(order);
    }
  }

  public void processPaymentReceived(String orderId) {    
    ExtendedOrder order = (ExtendedOrder) orderRepository.getOrder(orderId);
    synchronized (order) {
      order.setPaymentReceived(true);
      checkOrderReadyForPicking(order);      
    }
  }

  private void checkOrderReadyForPicking(ExtendedOrder order) {
    if (order.isPaymentReceived() && order.getDeliveryStatus()==GoodsDeliveryStatus.GOODS_RESERVED) {
      eventProducer.publishCommandPickGoods(order);
    }
  }

  public void processGoodsPicked(String orderId, String pickId) {    
    ExtendedOrder order = (ExtendedOrder) orderRepository.getOrder(orderId);
    order.setDeliveryStatus(GoodsDeliveryStatus.GOODS_PICKED);
    order.setPickId(pickId);
    eventProducer.publishCommandShipGoods(order, pickId);    
  }

  public boolean processGoodsShipped(String pickId, String shipmentId) {
    for (ExtendedOrder order : (Collection<? extends ExtendedOrder>)orderRepository.findOrders()) {
      if (pickId.equals(order.getPickId())) {        
        order.setShipped(true);
        // we ignore the shipmentId - as the order service is not interested
        
        eventProducer.publishEventOrderCompleted(order.getId());    
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean handleEvent(String type, String name, JsonObject event) {
    if ("Event".equals(type) && "OrderPlaced".equals(name)) {
      String correlationId = event.getString("correlationId");
      Order order = parseOrder(event.getJsonObject("order"));

      processOrder(correlationId, order);
    } else if ("Event".equals(type) && "GoodsReserved".equals(name)) {
      String reason = event.getString("reason");
      if ("CustomerOrder".equals(reason)) {
        String orderId = event.getString("refId");
        processGoodsReservation(orderId);
      } else {
        System.out.println("..ignored (reservation not for a customer order).");
        return false;
      }
    } else if ("Event".equals(type) && "PaymentReceived".equals(name)) {
      String reason = event.getString("reason");
      if ("CustomerOrder".equals(reason)) {
        String orderId = event.getString("refId");
        processPaymentReceived(orderId);
      } else {
        System.out.println("..ignored (reservation not for a customer order).");
        return false;
      }
    } else if ("Event".equals(type) && "GoodsPicked".equals(name)) {
      String reason = event.getString("reason");
      if ("CustomerOrder".equals(reason)) {
        String orderId = event.getString("refId");
        String pickId = event.getString("pickId");
        processGoodsPicked(orderId, pickId); // TODO: oder
                                                                   // direkt?
      } else {
        System.out.println("..ignored (reservation not for a customer order).");
        return false;
      }
    } else if ("Event".equals(type) && "GoodsShipped".equals(name)) {
      String pickId = event.getString("pickId");
      String shipmentId = event.getString("shipmentId");
      boolean processed = processGoodsShipped(pickId, shipmentId);
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

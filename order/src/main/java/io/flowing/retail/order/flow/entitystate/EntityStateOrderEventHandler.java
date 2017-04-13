package io.flowing.retail.order.flow.entitystate;

import javax.json.JsonArray;
import javax.json.JsonObject;

import io.flowing.retail.adapter.EventHandler;
import io.flowing.retail.order.application.OrderEventProducer;
import io.flowing.retail.order.domain.Customer;
import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderItem;
import io.flowing.retail.order.domain.OrderRepository;
import io.flowing.retail.order.flow.entitystate.OrderWithState.GoodsDeliveryStatus;


public class EntityStateOrderEventHandler extends EventHandler {
  
  private OrderEventProducer eventProducer = new OrderEventProducer();
  private OrderRepository orderRepository = OrderRepository.instance;
  

  @Override
  public boolean handleEvent(String type, String name, String transactionId, JsonObject event) {
    if ("Event".equals(type) && "OrderPlaced".equals(name)) {
      Order order = parseOrder(event.getJsonObject("order"));

      processOrder(transactionId, order);
//    } else if ("Event".equals(type) && "GoodsReserved".equals(name)) {
//      String reason = event.getString("reason");
//      if ("CustomerOrder".equals(reason)) {
//        String orderId = event.getString("refId");
//        processGoodsReservation(orderId);
//      } else {
//        System.out.println("..ignored (reservation not for a customer order).");
//        return false;
//      }
    } else if ("Event".equals(type) && "PaymentReceived".equals(name)) {
      String reason = event.getString("reason");
      if ("CustomerOrder".equals(reason)) {
        String orderId = event.getString("refId");
        processPaymentReceived(orderId);
      } else {
        System.out.println("..ignored (reservation not for a customer order).");
        return false;
      }
    } else if ("Event".equals(type) && "GoodsFetched".equals(name)) {
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
  
  public void processOrder(String transationId, Order order) {
    OrderWithState extendedOrder = new OrderWithState(order);
    extendedOrder.setTransactionId(transationId);
    
    // "Persist" order
    orderRepository.persistOrder(extendedOrder);

    eventProducer.publishEventOrderCreated(transationId, order);
    
//    // issue ReserveGoodsCommand  
//    eventProducer.publishCommandReserveGoods(transationId, order);
    // issue DoPaymentCommand
    eventProducer.publishCommandRetrievePayment(transationId, order);
  }

  public void processGoodsReservation(String orderId) { 
    OrderWithState order = orderRepository.getOrderWithState(orderId);
    synchronized (order) { // TODO: double check
      order.setDeliveryStatus(GoodsDeliveryStatus.GOODS_RESERVED);
      checkOrderReadyForPicking(order);
    }
  }

  public void processPaymentReceived(String orderId) {    
    OrderWithState order = orderRepository.getOrderWithState(orderId);
    synchronized (order) {
      order.setPaymentReceived(true);
      checkOrderReadyForPicking(order);      
    }
  }

  private void checkOrderReadyForPicking(OrderWithState order) {
    if (order.isPaymentReceived()) { 
        //&& order.getDeliveryStatus()==GoodsDeliveryStatus.GOODS_RESERVED) {
      eventProducer.publishCommandFetchGoods(order.getTransactionId(), order.asSimpleOrder());
    }
  }

  public void processGoodsPicked(String orderId, String pickId) {    
    OrderWithState order = orderRepository.getOrderWithState(orderId);
    order.setDeliveryStatus(GoodsDeliveryStatus.GOODS_PICKED);
    order.setPickId(pickId);
    eventProducer.publishCommandShipGoods(order.getTransactionId(), order.asSimpleOrder(), pickId);    
  }

  public boolean processGoodsShipped(String pickId, String shipmentId) {
    for (OrderWithState order : orderRepository.findOrdersWithState()) {
      if (pickId.equals(order.getPickId())) {        
        order.setShipped(true);
        // we ignore the shipmentId - as the order service is not interested
        
        eventProducer.publishEventOrderCompleted(order.getTransactionId(), order.getId());    
        return true;
      }
    }
    return false;
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

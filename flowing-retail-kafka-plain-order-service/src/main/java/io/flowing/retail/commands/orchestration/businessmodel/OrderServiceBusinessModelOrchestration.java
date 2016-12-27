package io.flowing.retail.commands.orchestration.businessmodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import io.flowing.retail.commands.EventProducer;
import io.flowing.retail.commands.Order;
import io.flowing.retail.commands.OrderService;
import io.flowing.retail.commands.orchestration.businessmodel.ExtendedOrder.GoodsDeliveryStatus;

public class OrderServiceBusinessModelOrchestration extends OrderService{

  private EventProducer eventProducer = new EventProducer();
  
  private HashMap<String, ExtendedOrder> orderStorage = new HashMap<String, ExtendedOrder>();
  
  // TODO: This class mixes issues (Persistence and Event Handling)
  public void processOrder(Order order) {
    ExtendedOrder extendedOrder = new ExtendedOrder(order);
    System.out.println("order will be processed: " + extendedOrder);
    
    // "Persist" order
    orderStorage.put(order.getId(), extendedOrder);
    
    // issue ReserveGoodsCommand  
    eventProducer.publishCommandReserveGoods(order);
    // issue DoPaymentCommand
    eventProducer.publishCommandDoPayment(order);
  }

  public Order getOrder(String orderId) {
    // TODO Auto-generated method stub
    return orderStorage.get(orderId);
  }

  public Collection<? extends Order> findOrders() {
    return orderStorage.values();
  }
  
  public void processGoodsReservation(String orderId) { 
    ExtendedOrder order = orderStorage.get(orderId);
    synchronized (order) { // TODO: double check
      order.setDeliveryStatus(GoodsDeliveryStatus.GOODS_RESERVED);
      checkOrderReadyForPicking(order);
    }
  }

  public void processPaymentReceived(String orderId) {    
    ExtendedOrder order = orderStorage.get(orderId);
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
    ExtendedOrder order = orderStorage.get(orderId);
    order.setDeliveryStatus(GoodsDeliveryStatus.GOODS_PICKED);
    eventProducer.publishCommandShipGoods(order, pickId);    
  }

  public void processGoodsShipped(String orderId, String shipmentId) {    
    ExtendedOrder order = orderStorage.get(orderId);
    order.setShipped(true);
    // we ignore the shipmentId - as the order service is not interested

    eventProducer.publishEventOrderCompleted(orderId);    
  }

}

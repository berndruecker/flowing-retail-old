package io.flowing.retail.commands.orchestration.businessmodel;

import java.util.Collection;

import io.flowing.retail.commands.Order;
import io.flowing.retail.commands.OrderEventProducer;
import io.flowing.retail.commands.OrderRepository;
import io.flowing.retail.commands.orchestration.businessmodel.ExtendedOrder.GoodsDeliveryStatus;

public class EventHandler {

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
}

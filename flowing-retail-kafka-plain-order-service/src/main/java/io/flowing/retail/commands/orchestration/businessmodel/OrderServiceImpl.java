package io.flowing.retail.commands.orchestration.businessmodel;

import java.util.List;

import io.flowing.retail.commands.Order;
import io.flowing.retail.commands.OrderService;

public class OrderServiceImpl extends OrderService{

  public void processOrder(Order order) {
    System.out.println("order will be processed: " + order);
    
    // issue ReserveGoodsCommand    
    // issue DoPaymentCommand
    
    // wait for occurrence of the events:
    // - GoodsReservedEvent
    // - PaymentReceivedEvent
    // or some error message, in which case we have to cleanup
    
    // issue PickGoodsCommand
    // wait for occurrence of the events:
    // - GoodsPickedEvent
    
    // issue ShipCommand
    // wait for occurrence of the events:
    // - ShipmentShippedEvent
    
  }

  public Order getOrder(String orderId) {
    // TODO Auto-generated method stub
    return null;
  }

  public List<Order> findOrders() {
    // TODO Auto-generated method stub
    return null;
  }
  
  
}

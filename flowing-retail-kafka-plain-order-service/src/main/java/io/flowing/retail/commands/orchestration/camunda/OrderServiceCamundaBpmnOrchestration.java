package io.flowing.retail.commands.orchestration.camunda;

import java.util.Collection;

import io.flowing.retail.commands.Order;
import io.flowing.retail.commands.OrderService;

public class OrderServiceCamundaBpmnOrchestration extends OrderService{

  @Override
  public void processOrder(String correlationId, Order order) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void processGoodsReservation(String orderId) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void processPaymentReceived(String orderId) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Order getOrder(String orderId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Collection<? extends Order> findOrders() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void processGoodsPicked(String orderId, String pickId) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean processGoodsShipped(String pickId, String shipmentId) {
    // TODO Auto-generated method stub
    return false;
  }

}

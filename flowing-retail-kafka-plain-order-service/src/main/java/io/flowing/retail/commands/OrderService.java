package io.flowing.retail.commands;

import java.util.Collection;

public abstract class OrderService {

  public static OrderService instance;

  public abstract void processOrder(String correlationId, Order order);
  public abstract void processGoodsReservation(String orderId);
  public abstract void processPaymentReceived(String orderId);    
  public abstract void processGoodsPicked(String orderId, String pickId);
  public abstract boolean processGoodsShipped(String pickId, String shipmentId);
  
  /**
   * get the order for the specified orderId
   */
  public abstract Order getOrder(String orderId);
  
  /**
   * Find orders. Currently no filter is required for simple examples
   * 
   * @return all stored orders
   */
  public abstract Collection<? extends Order> findOrders();
  
}

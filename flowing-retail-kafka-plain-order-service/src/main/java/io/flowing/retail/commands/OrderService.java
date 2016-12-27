package io.flowing.retail.commands;

import java.util.List;

public abstract class OrderService {

  public static OrderService instance;

  public abstract void processOrder(Order order);
  
  /**
   * get the order for the specified orderId
   */
  public abstract Order getOrder(String orderId);
  
  /**
   * Find orders. Currently no filter is required for simple examples
   * 
   * @return all stored orders
   */
  public abstract List<Order> findOrders();
  
}

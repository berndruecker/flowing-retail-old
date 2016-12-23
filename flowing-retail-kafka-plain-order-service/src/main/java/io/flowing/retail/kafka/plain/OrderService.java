package io.flowing.retail.kafka.plain;

import java.util.List;

public interface OrderService {
  
  public void placeOrder(Order order);
  
  /**
   * get the order for the specified orderId
   */
  public Order getOrder(String orderId);
  
  /**
   * Find orders. Currently no filter is required for simple examples
   * 
   * @return all stored orders
   */
  public List<Order> findOrders();
  
}

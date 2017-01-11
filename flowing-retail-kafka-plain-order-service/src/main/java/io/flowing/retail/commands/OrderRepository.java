package io.flowing.retail.commands;

import java.util.Collection;
import java.util.HashMap;

public class OrderRepository {

  public static OrderRepository instance = new OrderRepository();

  private HashMap<String, Order> orderStorage = new HashMap<String, Order>();
 
  /**
   * get the order for the specified orderId
   */
  public void persistOrder(Order order) {
    orderStorage.put(order.getId(), order);
  }
  
  /**
   * get the order for the specified orderId
   */
  public Order getOrder(String orderId) {
    return orderStorage.get(orderId);
  }

  /**
   * Find orders. Currently no filter is required for simple examples
   * 
   * @return all stored orders
   */
  public Collection<? extends Order> findOrders() {
    return orderStorage.values();
  }

}

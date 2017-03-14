package io.flowing.retail.order.domain;

import java.util.Collection;
import java.util.HashMap;

import io.flowing.retail.order.flow.entitystate.OrderWithState;

public class OrderRepository {

  public static OrderRepository instance = new OrderRepository();

  private HashMap<String, Order> orderStorage = new HashMap<String, Order>();
  private HashMap<String, OrderWithState> orderWithStateStorage = new HashMap<String, OrderWithState>();

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

  public void persistOrder(OrderWithState order) {
    orderWithStateStorage.put(order.getId(), order);
  }

  public OrderWithState getOrderWithState(String orderId) {
    return (OrderWithState) orderWithStateStorage.get(orderId);
  }

  public Collection<? extends OrderWithState> findOrdersWithState() {
    return orderWithStateStorage.values();
  }

}

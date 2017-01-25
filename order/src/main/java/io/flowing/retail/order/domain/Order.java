package io.flowing.retail.order.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {

  protected String id = UUID.randomUUID().toString();
  protected Customer customer = new Customer(); 
  protected List<OrderItem> items = new ArrayList<OrderItem>();
  private int totalSum = 0;

  public void addItem(OrderItem i) {
    items.add(i);
  }
  
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<OrderItem> getItems() {
    return items;
  }


  public int getTotalSum() {
    return totalSum;
  }

  public void setTotalSum(int totalSum) {
    this.totalSum = totalSum;
  }
 
  @Override
  public String toString() {
    return "Order [id=" + id + ", items=" + items + "]";
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }
  
}

package io.flowing.retail.kafka.plain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Order {

  private String id = UUID.randomUUID().toString();  
  
  private List<OrderItem> items = new ArrayList<OrderItem>();

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

  @Override
  public String toString() {
    return "Order [id=" + id + ", items=" + items + "]";
  }
  
}

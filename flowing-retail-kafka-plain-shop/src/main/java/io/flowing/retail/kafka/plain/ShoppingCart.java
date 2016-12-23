package io.flowing.retail.kafka.plain;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

  private List<Item> items = new ArrayList<Item>();

  public List<Item> getItems() {
    return items;
  }

  public void setItems(List<Item> items) {
    this.items = items;
  }

  public void addItem(String articleId, int amount) {
    // TODO Auto-generated method stub
    
  }

  public void removeItem(String articleId) {
    // TODO Auto-generated method stub
    
  }
  
}

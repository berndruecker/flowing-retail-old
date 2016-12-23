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
    // keep only one item, but increase amount accordingly
    Item existingItem = removeItem(articleId);
    if (existingItem!=null) {
      amount += existingItem.getAmount();
    }
    
    Item item = new Item();
    item.setAmount(amount);
    item.setArticleId(articleId);
    items.add(item);    
  }

  public Item removeItem(String articleId) {
    for (Item item : items) {
      if (articleId.equals(item.getArticleId())) {
        items.remove(item);
        return item;
      }
    }
    return null;
  }
  
}

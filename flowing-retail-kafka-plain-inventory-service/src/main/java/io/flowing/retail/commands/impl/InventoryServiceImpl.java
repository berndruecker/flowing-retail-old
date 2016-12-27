package io.flowing.retail.commands.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import io.flowing.retail.commands.InventoryService;
import io.flowing.retail.commands.Item;

public class InventoryServiceImpl extends InventoryService {

  @Override
  public boolean reserveGoods(List<Item> items, String reason, String refId, LocalDateTime expirationDate) {
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public String pickItems(List<Item> items, String reason, String refId) {
    // TODO Auto-generated method stub
    return UUID.randomUUID().toString();
  }

  @Override
  public void topUpInventory(String articleId, int amount) {
    // TODO Auto-generated method stub
    
  }

}

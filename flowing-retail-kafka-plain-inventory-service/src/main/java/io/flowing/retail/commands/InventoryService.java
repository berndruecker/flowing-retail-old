package io.flowing.retail.commands;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.flowing.retail.commands.impl.InventoryServiceImpl;

public abstract class InventoryService {
  
  public static InventoryService instance = new InventoryServiceImpl();
  
  /**
   * reserve goods on stock for a defined period of time
   * 
   * @param reason A reason why the goods are reserved (e.g. "customer order")
   * @param refId A reference id fitting to the reason of reservation (e.g. the order id), needed to find reservation again later
   * @param expirationDate Date until when the goods are reserved, afterwards the reservation is removed
   * @return if reservation could be done successfully
   */
  public abstract boolean reserveGoods(List<Item> items, String reason, String refId, LocalDateTime expirationDate);

  /**
   * Order to pick the given items in the warehouse. The inventory is decreased. 
   * Reservation fitting the reason/refId might be used to fulfill the order.
   * 
   * If no enough items are on stock - an exception is thrown.
   * Otherwise a unique pick id is returned, which can be used to 
   * reference the bunch of goods in the shipping area.
   * 
   * @param items to be picked
   * @param reason for which items are picked (e.g. "customer order")
   * @param refId Reference id fitting to the reason of the pick (e.g. "order id"). Used to determine which reservations can be used.
   * @return a unique pick ID 
   */
  public abstract String pickItems(List<Item> items, String reason, String refId);

  /**
   * New goods are arrived and inventory is increased
   */
  public abstract void topUpInventory(String articleId, int amount);

}

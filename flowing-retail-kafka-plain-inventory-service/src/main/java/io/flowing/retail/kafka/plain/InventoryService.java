package io.flowing.retail.kafka.plain;

import java.util.Date;
import java.util.List;

public interface InventoryService {
  
  /**
   * reserve goods on stock for a defined period of time
   * 
   * @param articleId The article to be reserved
   * @param amount  
   * @param reason A reason why the goods are reserved (e.g. "customer order")
   * @param refId A reference id fitting to the reason of reservation (e.g. the order id), needed to find reservation again later
   * @param expirationDate Date until when the goods are reserved, afterwards the reservation is removed
   * @return 
   */
  public void reserveGoods(String articleId, int amount, String reason, String refId, Date expirationDate);

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
  public String pickItems(List<ConsignmentItem> items, String reason, String refId);

  /**
   * New goods are arrived and inventory is increased
   */
  public void topUpInventory(String articleId, int amount);
}

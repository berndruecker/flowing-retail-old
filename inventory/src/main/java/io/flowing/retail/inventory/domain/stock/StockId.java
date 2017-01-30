package io.flowing.retail.inventory.domain.stock;

import io.flowing.retail.domain.Identity;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class StockId extends Identity<String> {

  public StockId(String id) {
    super(id);
  }

}

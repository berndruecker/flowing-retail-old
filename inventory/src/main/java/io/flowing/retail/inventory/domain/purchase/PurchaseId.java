package io.flowing.retail.inventory.domain.purchase;

import io.flowing.retail.domain.Identity;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class PurchaseId extends Identity<String> {

  public PurchaseId(String id) {
    super(id);
  }

}

package io.flowing.retail.inventory.domain.order;

import io.flowing.retail.domain.Identity;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class OrderId extends Identity<String> {

  public OrderId(String id) {
    super(id);
  }

}

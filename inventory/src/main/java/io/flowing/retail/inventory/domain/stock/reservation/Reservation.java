package io.flowing.retail.inventory.domain.stock.reservation;

import io.flowing.retail.domain.ValueObject;
import io.flowing.retail.inventory.domain.order.OrderId;

import java.time.LocalDateTime;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Reservation extends ValueObject {

  private int quantity;
  private LocalDateTime expiration;

  public int quantity() { return quantity; }

  public LocalDateTime expiration() { return expiration; }

  public Reservation(int quantity, LocalDateTime expiration) {
    this.quantity = quantity;
    this.expiration = expiration;
  }

  public boolean active() {
    return LocalDateTime.now().isBefore(expiration);
  }

  @Override
  public boolean equals(Object object) {
    boolean equalObjects = false;
    if (object != null && this.getClass() == object.getClass()) {
      Reservation other = (Reservation) object;
      equalObjects = this.quantity() == other.quantity()
       && this.expiration.equals(other.expiration);
    }
    return equalObjects;
  }

}

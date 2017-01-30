package io.flowing.retail.inventory.domain.stock.reservation;

import io.flowing.retail.inventory.domain.order.OrderId;

import java.time.LocalDateTime;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class OrderReservation extends Reservation {

  private OrderId orderId;

  public OrderId orderId() { return orderId; }

  public OrderReservation(OrderId orderId, int quantity, LocalDateTime expiration) {
    super(quantity, expiration);
    this.orderId = orderId;
  }

}

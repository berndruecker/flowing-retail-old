package io.flowing.retail.inventory.domain.stock;

import io.flowing.retail.domain.Entity;
import io.flowing.retail.domain.Event;
import io.flowing.retail.inventory.domain.order.OrderId;
import io.flowing.retail.inventory.domain.purchase.PurchaseId;
import io.flowing.retail.inventory.domain.stock.reservation.OrderQuantityReserved;
import io.flowing.retail.inventory.domain.stock.reservation.OrderReservation;
import io.flowing.retail.inventory.domain.stock.reservation.Reservation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class Stock extends Entity<StockId> {

  private int reserves;
  private List<Reservation> reservations;

  Stock(StockId stockId, int reserves) {
    super(stockId);
    this.reserves = reserves;
    this.reservations = new ArrayList<Reservation>();
    Event.raise(new StockCreated(this, reserves));
  }

  public int reserves() {
    return reserves;
  }

  public int reserved() {
    return reservations.stream().mapToInt(Reservation::quantity).sum();
  }

  public List<Reservation> reservations() {
    return reservations.stream().filter(Reservation::active).collect(Collectors.toList());
  }

  public void topUp(PurchaseId purchaseId, int quantity) {
    reserves += quantity;
    Event.raise(new StockToppedUp(this, quantity, reserves()));
  }

  /**
   * @param orderId
   * @param quantity
   * @param expiration
   * @throws StockInsufficientForOrder
   */
  public void reserve(OrderId orderId, int quantity, LocalDateTime expiration) throws StockInsufficientForOrder {
    if (LocalDateTime.now().isBefore(expiration)) {
      if (reserves() - reserved() >= quantity) {
        OrderReservation orderReservation = new OrderReservation(orderId, quantity, expiration);
        reservations.add(orderReservation);
        Event.raise(new OrderQuantityReserved(this, orderId, quantity));
      } else {
        throw new StockInsufficientForOrder();
      }
    } else {
      throw new IllegalStateException("ReservationExpired");
    }
  }

  public void pick(OrderId orderId, int quantity) throws StockInsufficientForOrder {
    Optional<Reservation> reservation = reservations.stream().filter(res -> {
      if (res instanceof OrderReservation) {
        OrderReservation orderReservation = (OrderReservation) res;
        return orderReservation.orderId().equals(orderId);
      }
      return false;
    }).findFirst();
    if (reservation.isPresent()) {
      reservations.remove(reservation.get());
      reserves -= quantity;
      Event.raise(new StockPickedUp(this, quantity, reserves()));
    } else if (reserves() - reserved() >= quantity) {
      reserves -= quantity;
      Event.raise(new StockPickedUp(this, quantity, reserves()));
    } else {
      throw new StockInsufficientForOrder();
    }
  }

}

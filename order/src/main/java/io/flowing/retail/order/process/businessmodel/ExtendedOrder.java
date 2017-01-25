package io.flowing.retail.order.process.businessmodel;

import io.flowing.retail.order.domain.Order;

public class ExtendedOrder extends Order {
  
  public static enum GoodsDeliveryStatus {

    NOTHING_DONE,
    GOODS_RESERVED,
    GOODS_PICKED
  }
  
  private boolean paymentReceived = false;
  
  private GoodsDeliveryStatus deliveryStatus = GoodsDeliveryStatus.NOTHING_DONE;

  private boolean shipped = false;
  
  /**
   * required to correlate ShipmentCreated event later on - as this don't know about the order id
   * cannot easily store it somewhere else
   */
  private String pickId;
  
  /**
   * Construct an extended order out of a simple order, required
   * as the event consumer only knows about orders to keep it applicable to
   * different orchestration options. 
   */
  public ExtendedOrder(Order o) {
    this.id = o.getId();
    this.items = o.getItems();
    this.customer = o.getCustomer();
  }

  public boolean isPaymentReceived() {
    return paymentReceived;
  }

  public void setPaymentReceived(boolean paymentReceived) {
    this.paymentReceived = paymentReceived;
  }

  public GoodsDeliveryStatus getDeliveryStatus() {
    return deliveryStatus;
  }

  public void setDeliveryStatus(GoodsDeliveryStatus deliveryStatus) {
    this.deliveryStatus = deliveryStatus;
  }

  public boolean isShipped() {
    return shipped;
  }

  public void setShipped(boolean orderShipped) {
    this.shipped = orderShipped;
  }

  @Override
  public String toString() {
    return "ExtendedOrder [id=" + id + ", items=" + items + ", paymentReceived=" + paymentReceived + ", deliveryStatus=" + deliveryStatus + ", shipped="
        + shipped + "]";
  }

  public String getPickId() {
    return pickId;
  }

  public void setPickId(String pickId) {
    this.pickId = pickId;
  }

}

package io.flowing.retail.commands.orchestration.businessmodel;

import io.flowing.retail.commands.Order;

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
   * Construct an extended order out of a simple order, required
   * as the event consumer only knows about orders to keep it applicable to
   * different orchestration options. 
   */
  public ExtendedOrder(Order o) {
    this.id = o.getId();
    this.items = o.getItems();
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

}

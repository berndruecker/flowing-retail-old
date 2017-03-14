package io.flowing.retail.order.flow.entitystate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.flowing.retail.order.domain.Customer;
import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderItem;

public class OrderWithState {

  protected String id = UUID.randomUUID().toString();
  protected Customer customer = new Customer();
  protected List<OrderItem> items = new ArrayList<OrderItem>();

  private boolean paymentReceived = false;

  public static enum GoodsDeliveryStatus {
    NOTHING_DONE, GOODS_RESERVED, GOODS_PICKED
  }

  private GoodsDeliveryStatus deliveryStatus = GoodsDeliveryStatus.NOTHING_DONE;

  private boolean shipped = false;
  
  /**
   * Identifier used to track transactions over various systems - typically just a UUID. We have to store that somewhere for the order
   */
  private String transactionId;

  /**
   * required to correlate ShipmentCreated event later on - as this don't know
   * about the order id cannot easily store it somewhere else
   */
  private String pickId;

  public OrderWithState(Order o) {
    this.id = o.getId();
    this.customer = o.getCustomer();
    this.items = o.getItems();
  }

  public Order asSimpleOrder() {
    Order order = new Order();
    order.setId(this.id);
    order.getItems().addAll(this.items);
    order.setCustomer(this.customer);
    return order;
  }

  public void addItem(OrderItem i) {
    items.add(i);
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

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public String getId() {
    return id;
  }

  public List<OrderItem> getItems() {
    return items;
  }

  public String getTransactionId() {
    return transactionId;
  }

  public void setTransactionId(String transactionId) {
    this.transactionId = transactionId;
  }

}

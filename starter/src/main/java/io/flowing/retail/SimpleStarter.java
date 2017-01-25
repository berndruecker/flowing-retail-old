package io.flowing.retail;

import io.flowing.retail.inventory.InventoryApplication;
import io.flowing.retail.order.OrderApplication;
import io.flowing.retail.payment.PaymentApplication;
import io.flowing.retail.shipping.ShippingApplication;
import io.flowing.retail.shop.ShopApplication;

public class SimpleStarter {

  public static void main(String[] args) throws Exception {
    ShopApplication.main(args);
    OrderApplication.main(args);
    PaymentApplication.main(args);
    InventoryApplication.main(args);
    ShippingApplication.main(args);
  }

}

package io.flowing.retail.command;

import io.flowing.retail.commands.InventoryApplication;
import io.flowing.retail.commands.OrderApplication;
import io.flowing.retail.commands.PaymentApplication;
import io.flowing.retail.commands.ShippingApplication;

public class SimpleStarter {

  public static void main(String[] args) throws Exception {
    ShopApplication.main(args);
    OrderApplication.main(args);
    PaymentApplication.main(args);
    InventoryApplication.main(args);
    ShippingApplication.main(args);
  }

}

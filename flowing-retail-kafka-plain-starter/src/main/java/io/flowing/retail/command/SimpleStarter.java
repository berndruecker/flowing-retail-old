package io.flowing.retail.command;

import io.flowing.retail.commands.ApplicationInventory;
import io.flowing.retail.commands.ApplicationOrder;
import io.flowing.retail.commands.ApplicationPayment;
import io.flowing.retail.commands.ApplicationShipping;

public class SimpleStarter {

  public static void main(String[] args) throws Exception {
    ApplicationShop.main(args);
//    ApplicationOrder.main(args);
    ApplicationPayment.main(args);
    ApplicationInventory.main(args);
    ApplicationShipping.main(args);
  }

}

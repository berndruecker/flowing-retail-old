package io.flowing.retail.shipping;

import io.flowing.retail.adapter.FlowingStartup;
import io.flowing.retail.shipping.application.ShippingEventConsumer;

public class ShippingApplication {

  public static void main(String[] args) throws Exception {
    FlowingStartup.startup("shipping", new ShippingEventConsumer(), args);
  }

}

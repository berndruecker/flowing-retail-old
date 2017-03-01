package io.flowing.retail.inventory;

import io.flowing.retail.adapter.FlowingStartup;
import io.flowing.retail.inventory.application.InventoryEventHandler;

public class InventoryApplication {

  public static void main(String[] args) throws Exception {
    FlowingStartup.startup("inventory", new InventoryEventHandler(), args);
  }

}

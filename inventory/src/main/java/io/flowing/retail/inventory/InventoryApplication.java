package io.flowing.retail.inventory;

import io.flowing.retail.adapter.ChannelStartup;
import io.flowing.retail.adapter.kafka.KafkaChannelConsumer;
import io.flowing.retail.adapter.kafka.KafkaSender;
import io.flowing.retail.inventory.application.InventoryEventHandler;

public class InventoryApplication {

  public static void main(String[] args) throws Exception {
    ChannelStartup.startup( //
        "INVENTORY", //
        new KafkaSender(), //
        new KafkaChannelConsumer("inventory"), //
        new InventoryEventHandler());
  }

}

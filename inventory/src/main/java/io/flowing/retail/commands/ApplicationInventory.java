package io.flowing.retail.commands;

import io.flowing.retail.commands.channel.ChannelStartup;
import io.flowing.retail.commands.channel.kafka.KafkaChannelConsumer;
import io.flowing.retail.commands.channel.kafka.KafkaSender;

public class ApplicationInventory {

  public static void main(String[] args) throws Exception {
    ChannelStartup.startup( //
        "INVENTORY", //
        new KafkaSender(), //
        new KafkaChannelConsumer("inventory"), //
        new InventoryEventConsumer());
  }

}

package io.flowing.retail.commands;

import io.flowing.retail.commands.channel.ChannelStartup;
import io.flowing.retail.commands.channel.kafka.KafkaChannelConsumer;
import io.flowing.retail.commands.channel.kafka.KafkaSender;

public class ApplicationShipping {

  public static void main(String[] args) throws Exception {    
    ChannelStartup.startup( //
        "SHIPPING", //
        new KafkaSender(), //
        new KafkaChannelConsumer("shipping"), //
        new ShippingEventConsumer());
  }

}

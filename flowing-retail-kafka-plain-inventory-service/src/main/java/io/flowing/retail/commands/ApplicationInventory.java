package io.flowing.retail.commands;

import io.flowing.retail.commands.channel.ChannelConsumer;
import io.flowing.retail.commands.channel.ChannelSender;
import io.flowing.retail.commands.channel.kafka.KafkaChannelConsumer;
import io.flowing.retail.commands.channel.kafka.KafkaSender;

public class ApplicationInventory {

  public static void main(String[] args) throws Exception {
    System.out.println("STARTING INVENTORY SERVICE");

    // Select channel
    ChannelConsumer.startup(new KafkaChannelConsumer());
    // ChannelConsumer.startup(new RabbitMqConsumer());
    ChannelSender.startup(new KafkaSender());
    // ChannelSender.startup(new RabbitMqSender());

  }

}

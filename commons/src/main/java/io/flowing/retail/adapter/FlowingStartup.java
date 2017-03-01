package io.flowing.retail.adapter;

import java.util.Arrays;

import io.flowing.retail.adapter.amqp.RabbitMqConsumer;
import io.flowing.retail.adapter.amqp.RabbitMqSender;
import io.flowing.retail.adapter.kafka.KafkaChannelConsumer;
import io.flowing.retail.adapter.kafka.KafkaSender;

public class FlowingStartup {
  
  public static void startup(String name, EventHandler eventHandler, String[] args) {
    System.out.println("START SERVICE: " + name);

    if (Arrays.asList(args).contains("rabbit")) {
      startupChannelRabbitMq();
    } else { // default
      startupChannelKafka(name);      
    }

    EventHandler.instance = eventHandler;    
  }

  public static void startupChannelKafka(String name) {
    ChannelSender.startup(new KafkaSender());
    ChannelConsumer.startup(new KafkaChannelConsumer(name));
  }

  public static void startupChannelRabbitMq() {
    ChannelSender.startup(new RabbitMqSender());
    ChannelConsumer.startup(new RabbitMqConsumer());
  }
}

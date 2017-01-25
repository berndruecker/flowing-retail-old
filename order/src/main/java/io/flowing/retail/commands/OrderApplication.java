package io.flowing.retail.commands;

import io.flowing.retail.commands.channel.ChannelStartup;
import io.flowing.retail.commands.channel.kafka.KafkaChannelConsumer;
import io.flowing.retail.commands.channel.kafka.KafkaSender;
import io.flowing.retail.commands.orchestration.dsl.camunda.OrderDslCamundaEventHandler;

public class OrderApplication {

  public static void main(String[] args) throws Exception {
    // Select type of orchestration
//    OrderService.instance = new OrderServiceBusinessModelOrchestration();

    ChannelStartup.startup( //
        "ORDER", //
        new KafkaSender(), //
        new KafkaChannelConsumer("order"), //
        new OrderDslCamundaEventHandler()); 
  }

}

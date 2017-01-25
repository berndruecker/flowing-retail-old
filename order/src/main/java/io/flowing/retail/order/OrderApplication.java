package io.flowing.retail.order;

import io.flowing.retail.adapter.ChannelStartup;
import io.flowing.retail.adapter.kafka.KafkaChannelConsumer;
import io.flowing.retail.adapter.kafka.KafkaSender;
import io.flowing.retail.order.process.camunda.dsl.CamundaDslOrderEventHandler;

public class OrderApplication {

  public static void main(String[] args) throws Exception {
    // Select type of orchestration
//    OrderService.instance = new OrderServiceBusinessModelOrchestration();

    ChannelStartup.startup( //
        "ORDER", //
        new KafkaSender(), //
        new KafkaChannelConsumer("order"), //
        new CamundaDslOrderEventHandler()); 
  }

}

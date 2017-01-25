package io.flowing.retail.shipping;

import io.flowing.retail.adapter.ChannelStartup;
import io.flowing.retail.adapter.kafka.KafkaChannelConsumer;
import io.flowing.retail.adapter.kafka.KafkaSender;
import io.flowing.retail.shipping.application.ShippingEventConsumer;

public class ShippingApplication {

  public static void main(String[] args) throws Exception {    
    ChannelStartup.startup( //
        "SHIPPING", //
        new KafkaSender(), //
        new KafkaChannelConsumer("shipping"), //
        new ShippingEventConsumer());
  }

}

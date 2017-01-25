package io.flowing.retail.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.flowing.retail.adapter.ChannelStartup;
import io.flowing.retail.adapter.kafka.KafkaChannelConsumer;
import io.flowing.retail.adapter.kafka.KafkaSender;

@SpringBootApplication
public class ShopApplication {

  public static void main(String[] args) {
    ChannelStartup.startup( //
        "SHOP", //
        new KafkaSender(), //
        new KafkaChannelConsumer("shop"), //
        new ShopEventConsumer());
    
    // and startup spring boot for Tomcat + REST
    SpringApplication.run(ShopApplication.class, args);
  }

}

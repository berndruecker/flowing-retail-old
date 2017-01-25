package io.flowing.retail.command;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.flowing.retail.commands.channel.ChannelStartup;
import io.flowing.retail.commands.channel.kafka.KafkaChannelConsumer;
import io.flowing.retail.commands.channel.kafka.KafkaSender;

@SpringBootApplication
public class ApplicationShop {

  public static void main(String[] args) {
    ChannelStartup.startup( //
        "SHOP", //
        new KafkaSender(), //
        new KafkaChannelConsumer("shop"), //
        new ShopEventConsumer());
    
    // and startup spring boot for Tomcat + REST
    SpringApplication.run(ApplicationShop.class, args);
  }

}

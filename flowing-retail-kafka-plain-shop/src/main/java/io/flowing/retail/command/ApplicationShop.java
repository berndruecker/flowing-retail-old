package io.flowing.retail.command;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.flowing.retail.command.channel.ChannelSender;
import io.flowing.retail.command.channel.kafka.KafkaSender;

@SpringBootApplication
public class ApplicationShop {

  public static void main(String[] args) {
    // Set Channel: Kafka
    ChannelSender.startup(new KafkaSender());
    
    // and startup spring boot for Tomcat + REST
    SpringApplication.run(ApplicationShop.class, args);
  }

}

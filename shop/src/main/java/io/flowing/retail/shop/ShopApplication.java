package io.flowing.retail.shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.flowing.retail.adapter.FlowingStartup;

@SpringBootApplication
public class ShopApplication {

  public static void main(String[] args) {
    FlowingStartup.startup("shop", new ShopEventConsumer(), args);

    // and startup spring boot for Tomcat + REST
    SpringApplication.run(ShopApplication.class, args);
  }

}

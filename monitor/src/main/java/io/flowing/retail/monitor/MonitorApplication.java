package io.flowing.retail.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import io.flowing.retail.adapter.FlowingStartup;

@SpringBootApplication
public class MonitorApplication {

  public static void main(String[] args) {
    // and startup spring boot for Tomcat + REST
    ConfigurableApplicationContext context = SpringApplication.run(MonitorApplication.class, args);
    
    // and startupo KAfka Consumer without spring, but tell it where to find the WebSocket Controller from Spring
    SimpMessagingTemplate simpMessageTemplate = context.getBean(SimpMessagingTemplate.class);
    FlowingStartup.startup("monitor", new MonitorEventConsumer(simpMessageTemplate), args);

  } 
  

}

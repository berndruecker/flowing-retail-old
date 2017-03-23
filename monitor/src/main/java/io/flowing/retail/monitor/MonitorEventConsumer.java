package io.flowing.retail.monitor;

import javax.json.JsonObject;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import io.flowing.retail.adapter.EventHandler;

public class MonitorEventConsumer extends EventHandler {

  private SimpMessagingTemplate simpMessageTemplate;

  public MonitorEventConsumer(SimpMessagingTemplate simpMessageTemplate) {
    this.simpMessageTemplate = simpMessageTemplate;
  }

  @Override
  public boolean handleEvent(String type, String name, String transactionId, JsonObject eventContent) {
    String sender = null;
    if (eventContent.containsKey("sender")) {
      sender = eventContent.getString("sender");
    }
    
    PastEvent event = new PastEvent(type, name, transactionId, sender, asString(eventContent));
    LogRepository.instance.addEvent(event);
    simpMessageTemplate.convertAndSend("/topic/events", event);
    return false;
  }
  
  

}

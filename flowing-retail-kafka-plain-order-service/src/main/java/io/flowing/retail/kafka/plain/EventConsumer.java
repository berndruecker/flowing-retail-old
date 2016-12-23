package io.flowing.retail.kafka.plain;

public class EventConsumer {

  public void handleEvent(String eventAsJson) {
    System.out.println("EVENT HAPPENED: " + eventAsJson);
  }
  
  
}

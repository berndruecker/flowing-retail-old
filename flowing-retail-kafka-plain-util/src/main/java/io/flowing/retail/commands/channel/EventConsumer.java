package io.flowing.retail.commands.channel;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public abstract class EventConsumer {

  public static EventConsumer instance = null;

  public void handleEvent(String eventAsJson) {
    System.out.println("-");

    JsonReader jsonReader = Json.createReader(new StringReader(eventAsJson));
    JsonObject event = jsonReader.readObject();
    jsonReader.close();

    String type = event.getString("type");
    String name = event.getString("name");

    boolean handled = handleEvent(type, name, event);
    
    if (handled) {
      System.out.println("Finished handling: " + eventAsJson);
    }else {
      System.out.println("Ignored " + type + " " + name);
    }
  }

  public abstract boolean handleEvent(String type, String name, JsonObject event);

}

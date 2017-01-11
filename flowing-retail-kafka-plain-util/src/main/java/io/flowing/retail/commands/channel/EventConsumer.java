package io.flowing.retail.commands.channel;

import java.io.StringReader;
import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;

public abstract class EventConsumer {

  public static EventConsumer instance = null;

  public void handleEvent(String eventAsJson) {
    System.out.println("-");

    JsonReader jsonReader = Json.createReader(new StringReader(eventAsJson));
    JsonObject event = jsonReader.readObject();
    jsonReader.close();

    String type = event.getString("type");
    String name = event.getString("name");

    try {
      
      boolean handled = handleEvent(type, name, event);
      if (handled) {
        System.out.println("[" + this.getClass().getSimpleName() + "] Handled: " + type + " " + name + " " + eventAsJson);
      }else {
        System.out.println("[" + this.getClass().getSimpleName() + "] Ignored " + type + " " + name + " " + eventAsJson);
      }
      
    } catch (Exception ex) {
      System.out.println(ex.getClass() + " '" + ex.getMessage() + "' while handling: " + eventAsJson);
      ex.printStackTrace();
    }    
  }

  public abstract boolean handleEvent(String type, String name, JsonObject event);

  public String asString(JsonObject jsonObject) {
    StringWriter eventStringWriter = new StringWriter();
    JsonWriter writer = Json.createWriter(eventStringWriter);
    writer.writeObject(jsonObject);
    writer.close();

    return eventStringWriter.toString();
  }

}

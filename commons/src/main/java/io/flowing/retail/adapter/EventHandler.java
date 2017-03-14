package io.flowing.retail.adapter;

import java.io.StringReader;
import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;

public abstract class EventHandler {

  public static EventHandler instance = null;

  public void handleEvent(String eventAsJson) {
    System.out.println("-");

    JsonReader jsonReader = Json.createReader(new StringReader(eventAsJson));
    JsonObject event = jsonReader.readObject();
    jsonReader.close();

    String type = event.getString("type");
    String name = event.getString("name");
    
    String transactionId = null;
    if (event.containsKey("transactionId")) {
      transactionId = event.getString("transactionId");
    }

    try {
      
      boolean handled = handleEvent(type, name, transactionId, event);
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

  public abstract boolean handleEvent(String type, String name, String transactionId, JsonObject event);

  public String asString(JsonObject jsonObject) {
    StringWriter eventStringWriter = new StringWriter();
    JsonWriter writer = Json.createWriter(eventStringWriter);
    writer.writeObject(jsonObject);
    writer.close();

    return eventStringWriter.toString();
  }

}

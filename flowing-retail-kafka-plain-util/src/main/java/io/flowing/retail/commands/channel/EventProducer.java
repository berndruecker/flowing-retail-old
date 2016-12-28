package io.flowing.retail.commands.channel;

import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

public abstract class EventProducer {

  public void send(JsonObjectBuilder json) {
    send(asString(json));
  }

  public void send(String event) {
    ChannelSender.instance.send(event);
  }

  public JsonObjectBuilder createEventPayloadJson(String name) {
    return createPayloadJson("Event", name);
  }

  public JsonObjectBuilder createCommandPayloadJson(String name) {
    return createPayloadJson("Command", name);
  }

  public JsonObjectBuilder createPayloadJson(String type, String name) {
    return Json.createObjectBuilder() //
        .add("type", type)//
        .add("name", name);
  }

  public String asString(JsonObjectBuilder builder) {
    JsonObject jsonObject = builder.build();

    StringWriter eventStringWriter = new StringWriter();
    JsonWriter writer = Json.createWriter(eventStringWriter);
    writer.writeObject(jsonObject);
    writer.close();

    return eventStringWriter.toString();
  }

}

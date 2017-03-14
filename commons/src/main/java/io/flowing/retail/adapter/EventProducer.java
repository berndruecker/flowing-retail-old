package io.flowing.retail.adapter;

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

  public JsonObjectBuilder createEventPayloadJson(String name, String transactionId) {
    return createPayloadJson("Event", name, transactionId);
  }

  public JsonObjectBuilder createCommandPayloadJson(String name, String transactionId) {
    return createPayloadJson("Command", name, transactionId);
  }

  public JsonObjectBuilder createPayloadJson(String type, String name, String transactionId) {
    return Json.createObjectBuilder() //
        .add("type", type)//
        .add("name", name) //
        .add("sender", this.getClass().getSimpleName()) //
        .add("transactionId", transactionId);
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

package io.flowing.retail.order.flow.camunda.dsl.commons;

import java.io.StringReader;
import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;

import io.flowing.retail.order.domain.Order;

public class EventContext {

  private JsonObjectBuilder outgoing;
  
  private Order order;
  private JsonObject incoming;

  public EventContext(Order order) {
    this.order = order;
  }

  public EventContext(String incomingEventString, Order order) {
    this.order = order;
    
    JsonReader jsonReader = Json.createReader(new StringReader(incomingEventString));
    incoming = jsonReader.readObject();
    jsonReader.close();
  }

  public JsonObject incoming() {
    return incoming;
  }

  public JsonObjectBuilder outgoing(String type, String name) {
    outgoing = Json.createObjectBuilder() //
        .add("type", type)//
        .add("name", name);
    return outgoing;
  }
  
  public String outgoingAsString() {
    return asString(outgoing());
  }
  
  private String asString(JsonObjectBuilder builder) {
    JsonObject jsonObject = builder.build();

    StringWriter eventStringWriter = new StringWriter();
    JsonWriter writer = Json.createWriter(eventStringWriter);
    writer.writeObject(jsonObject);
    writer.close();

    return eventStringWriter.toString();
  }
  
  public JsonObjectBuilder outgoing() {
    return outgoing;
  }

  public Order order() {
    return order;
  }

}
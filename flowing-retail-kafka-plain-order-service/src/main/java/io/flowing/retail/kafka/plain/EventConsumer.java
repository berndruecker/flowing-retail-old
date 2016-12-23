package io.flowing.retail.kafka.plain;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class EventConsumer {

  public void handleEvent(String eventAsJson) {
    System.out.println("EVENT HAPPENED: " + eventAsJson);
    
    JsonReader jsonReader = Json.createReader(new StringReader(eventAsJson));
    JsonObject event = jsonReader.readObject();
    jsonReader.close();
    
    String type = event.getString("type");
    String name = event.getString("name");
    
    if ("event".equals(type) && "OrderPlacedEvent".equals(name)) {
      String correlationId = event.getString("correlationId");      
      Order order = parseOrder(event.getJsonObject("order"));
      
      OrderService.instance.processOrder(order);
    }
  }

  private Order parseOrder(JsonObject orderJson) {
    Order order = new Order();      

    // Order Service is NOT interested in customer id - ignore: 
    orderJson.getString("customerId");
    
    JsonArray jsonArray = orderJson.getJsonArray("items");
    for (JsonObject itemJson : jsonArray.getValuesAs(JsonObject.class)) {
      order.addItem( //
          new OrderItem() //
            .setArticleId(itemJson.getString("articleId")) //
            .setAmount(itemJson.getInt("amount")));
    }
    
    return order;
  }
  
  
}

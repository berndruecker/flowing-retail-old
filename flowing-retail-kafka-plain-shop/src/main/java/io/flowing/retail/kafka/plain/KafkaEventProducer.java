package io.flowing.retail.kafka.plain;

import java.io.StringWriter;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonWriter;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
//@Scope("singelton")
public class KafkaEventProducer {

  public static String topicName = "flowing-retail";
  
  private Producer<String, String> producer;

  public void publishOrderPlacedEvent(String correlationId, String customerId, ShoppingCart shoppingCart) {
    String eventString = transformToJson(correlationId, customerId, shoppingCart);
    
    System.out.println("Sending event via Kafka: " + eventString);

    producer.send(
        new ProducerRecord<String, String>(topicName, eventString));    
  }

  private String transformToJson(String correlationId, String customerId, ShoppingCart shoppingCart) {
    JsonArrayBuilder itemsArrayBuilder = Json.createArrayBuilder();
    for (Item item : shoppingCart.getItems()) {
      itemsArrayBuilder.add(Json.createObjectBuilder().add("articleId", item.getArticleId()).add("amount", item.getAmount()));
    }

    JsonObject event = Json.createObjectBuilder().add("type", "event").add("name", "OrderPlacedEvent").add("correlationId", correlationId)
        .add("order", Json.createObjectBuilder().add("customerId", customerId).add("items", itemsArrayBuilder)).build();

    StringWriter eventStringWriter = new StringWriter();
    JsonWriter writer = Json.createWriter(eventStringWriter);
    writer.writeObject(event);
    writer.close();

    return eventStringWriter.toString();
  }

  @PostConstruct
  public void connectKafka() {
    Properties configProperties = new Properties();
    configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
    configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

    producer = new KafkaProducer<String, String>(configProperties);
    System.out.println("Connected to Kafka");
  }

  @PreDestroy
  public void disconnectKafka() {
    producer.close();
  }

  public static void main(String[] args) {
    new KafkaEventProducer().publishOrderPlacedEvent("123", "cust123", new ShoppingCart());
  }
}

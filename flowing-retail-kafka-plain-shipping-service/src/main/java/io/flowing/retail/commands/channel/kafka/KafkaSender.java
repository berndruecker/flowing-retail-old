package io.flowing.retail.commands.channel.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import io.flowing.retail.commands.channel.ChannelSender;

public class KafkaSender extends ChannelSender {

  public static String TOPIC_NAME = "flowing-retail";

  private Producer<String, String> producer;

  public void send(String eventString) {
    System.out.println("Sending event via Kafka: " + eventString);
    producer.send(new ProducerRecord<String, String>(TOPIC_NAME, eventString));
  }

  public void connect() {
    Properties configProperties = new Properties();
    configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
    configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

    producer = new KafkaProducer<String, String>(configProperties);
    System.out.println("Connected to Kafka");
  }

  public void disconnect() {
    producer.close();
  }
}

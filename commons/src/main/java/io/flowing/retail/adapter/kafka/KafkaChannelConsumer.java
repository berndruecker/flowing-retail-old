package io.flowing.retail.adapter.kafka;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;

import io.flowing.retail.adapter.ChannelConsumer;
import io.flowing.retail.adapter.EventHandler;

public class KafkaChannelConsumer extends ChannelConsumer {

  public static String topicName = "flowing-retail";
  // public static String groupId = "inventoryService";
  // public static final Object clientId = "OrderService";
  private KafkaConsumerThread consumerRunnable;
  private String groupId;
  private Object clientId;

  public KafkaChannelConsumer(String serviceName) {
    this.groupId = serviceName;
    this.clientId = serviceName;
  }

  protected void connect() throws Exception {
    consumerRunnable = new KafkaConsumerThread(groupId, clientId);
    consumerRunnable.start();
  }

  protected void disconnect() throws Exception {
    consumerRunnable.getKafkaConsumer().wakeup();
    try {
      consumerRunnable.join();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private static class KafkaConsumerThread extends Thread {

    private KafkaConsumer<String, String> kafkaConsumer;
    private String groupId;
    private Object clientId;

    public KafkaConsumerThread(String groupId, Object clientId) {
      super("kafka-consumer-" + groupId);
      this.groupId = groupId;
      this.clientId = clientId;
    }

    public void run() {
//      ZkUtils.maybeDeletePath("localhost:2181", "/consumers/" + groupId);
      
      Properties configProperties = new Properties();
      configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
      configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
      configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//      configProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
      
      configProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

      EventHandler eventConsumer = EventHandler.instance;

      // Figure out where to start processing messages from
      kafkaConsumer = new KafkaConsumer<String, String>(configProperties);
      kafkaConsumer.subscribe(Arrays.asList(topicName));
      System.out.println("["+groupId+"] Started consumer and subscribed to topic " + topicName);

      // Start processing messages
      try {
        while (true) {
          ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
          for (ConsumerRecord<String, String> record : records) {
            eventConsumer.handleEvent(record.value());
          }
        }
      } catch (WakeupException ex) {
        System.out.println("Exception caught " + ex.getMessage());
      } finally {
        kafkaConsumer.close();
        System.out.println("After closing KafkaConsumer");
      }
    }

    public KafkaConsumer<String, String> getKafkaConsumer() {
      return this.kafkaConsumer;
    }
  }
}

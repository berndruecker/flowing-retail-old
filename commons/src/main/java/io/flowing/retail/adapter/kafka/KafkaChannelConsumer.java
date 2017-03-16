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

  private KafkaConsumerThread consumerRunnable;
  private String groupId;

  private EventHandler eventHandler;

  public KafkaChannelConsumer(String serviceName, EventHandler eventHandler) {
    this.groupId = serviceName;
    this.eventHandler = eventHandler;
  }

  protected void connect() throws Exception {
    consumerRunnable = new KafkaConsumerThread(groupId, eventHandler);
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
    private EventHandler eventHandler;

    public KafkaConsumerThread(String groupId, EventHandler eventHandler) {
      super("kafka-consumer-" + groupId);
      this.groupId = groupId;
      this.eventHandler = eventHandler;
    }

    public void run() {
      Properties configProperties = new Properties();
      configProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
      configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
      configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
      configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
      configProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
//      configProperties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
//      configProperties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "6000");
//      configProperties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "500");
//      
      configProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

      // Figure out where to start processing messages from
      kafkaConsumer = new KafkaConsumer<String, String>(configProperties);
      kafkaConsumer.subscribe(Arrays.asList(topicName));
      System.out.println("["+groupId+"] Started consumer and subscribed to topic " + topicName);

      // Start processing messages
      try {
        while (true) {
          ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
          for (ConsumerRecord<String, String> record : records) {
            eventHandler.handleEvent(record.value());
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

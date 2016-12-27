package io.flowing.retail.commands;
import io.flowing.retail.commands.channel.ChannelConsumer;
import io.flowing.retail.commands.channel.kafka.KafkaChannelConsumer;
import io.flowing.retail.commands.orchestration.businessmodel.OrderServiceImpl;

public class ApplicationOrder {

  public static String topicName = "flowing-retail";
  public static String groupId = "group1";
  public static final Object clientId = "OrderService";

  public static void main(String[] args) throws Exception {

    // Use 
    // - Channel: Kafka
    // - Orchestration: Business Model
    OrderService.instance = new OrderServiceImpl();
    ChannelConsumer.startup(new KafkaChannelConsumer());

  }


}

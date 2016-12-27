package io.flowing.retail.commands;
import io.flowing.retail.commands.channel.ChannelConsumer;
import io.flowing.retail.commands.channel.amqp.RabbitMqConsumer;
import io.flowing.retail.commands.channel.kafka.KafkaChannelConsumer;
import io.flowing.retail.commands.orchestration.businessmodel.OrderServiceImpl;

public class ApplicationOrder {

  public static String topicName = "flowing-retail";
  public static String groupId = "group1";
  public static final Object clientId = "OrderService";

  public static void main(String[] args) throws Exception {

    // Select
    // - Channel:
    // - Orchestration: 
    OrderService.instance = new OrderServiceImpl(); // via business model
//    ChannelConsumer.startup(new KafkaChannelConsumer());
    ChannelConsumer.startup(new RabbitMqConsumer());

  }


}

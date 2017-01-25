package io.flowing.retail.adapter.amqp;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import io.flowing.retail.adapter.ChannelConsumer;
import io.flowing.retail.adapter.EventHandler;

public class RabbitMqConsumer extends ChannelConsumer {

  public static String QUEUE_NAME = "flowing-retail";

  private EventHandler eventConsumer = EventHandler.instance;

  private Channel channel;

  protected void connect() throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    Connection connection = factory.newConnection();
    channel = connection.createChannel();

    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    System.out.println(" [*] Waiting for messages.");

    Consumer consumer = new DefaultConsumer(channel) {
      @Override
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        String message = new String(body, "UTF-8");
        System.out.println(" [x] Received '" + message + "'");
        eventConsumer.handleEvent(message);
      }
    };
    channel.basicConsume(QUEUE_NAME, true, consumer);
  }

  protected void disconnect() throws Exception {
    channel.close();
  }

}

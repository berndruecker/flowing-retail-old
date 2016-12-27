package io.flowing.retail.command.channel.amqp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import io.flowing.retail.command.channel.ChannelSender;

public class RabbitMqSender extends ChannelSender {

  public static String QUEUE_NAME = "flowing-retail";

  private Connection connection;

  private Channel channel;

  public void send(String eventString) throws UnsupportedEncodingException, IOException {
    System.out.println("Sending event via RabbitMQ: " + eventString);
    channel.basicPublish("", QUEUE_NAME, null, eventString.getBytes("UTF-8"));
  }  

  public void connect() throws IOException, TimeoutException {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("localhost");
    connection = factory.newConnection();
    channel = connection.createChannel();

    channel.queueDeclare(QUEUE_NAME, false, false, false, null);

    System.out.println("Connected to RabbitMQ");
  }

  public void disconnect() throws IOException, TimeoutException {
    channel.close();
    connection.close();
  }

//  public static void main(String[] args) throws UnsupportedEncodingException, IOException {
//    new RabbitMqEventSender().publishOrderPlacedEvent("123", "cust123", new ShoppingCart());
//  }
}

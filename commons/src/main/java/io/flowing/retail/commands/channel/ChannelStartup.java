package io.flowing.retail.commands.channel;

public class ChannelStartup {
  
  public static void startup(String name, ChannelSender sender, ChannelConsumer channelConsumer, EventHandler eventConsumer) {
    System.out.println("START SERVICE: " + name);

    ChannelSender.startup(sender);
    ChannelConsumer.startup(channelConsumer);
    EventHandler.instance = eventConsumer;
  }

}

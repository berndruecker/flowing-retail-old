package io.flowing.retail.commands.channel;

public abstract class ChannelConsumer {

  public static ChannelConsumer instance = null;

  public static void startup(final ChannelConsumer consumer) {
    try {
      consumer.connect();
      instance = consumer;
    } catch (Exception ex) {
      throw new RuntimeException("Could not connect consumer: " + ex.getMessage(), ex);
    }
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          consumer.disconnect();
        } catch (Exception ex) {
          throw new RuntimeException("Could not connect consumer: " + ex.getMessage(), ex);
        }
      }
    });

  }

  protected abstract void disconnect() throws Exception;

  protected abstract void connect() throws Exception;
}

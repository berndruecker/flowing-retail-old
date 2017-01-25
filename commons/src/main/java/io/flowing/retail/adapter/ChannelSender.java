package io.flowing.retail.adapter;

public abstract class ChannelSender {

  public static ChannelSender instance = null;

  public abstract void send(String content);

  public static void startup(ChannelSender sender) {
    try {
      sender.connect();
      instance = sender;
    } catch (Exception e) {
      throw new RuntimeException("Could not connect: " + e.getMessage(), e);
    }
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          ChannelSender.instance.disconnect();
        } catch (Exception e) {
          throw new RuntimeException("Could not disconnect: " + e.getMessage(), e);
        }
      }   
    }); 
  }

  protected abstract void connect() throws Exception;
  protected abstract void disconnect() throws Exception;

}

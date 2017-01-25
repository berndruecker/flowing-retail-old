package io.flowing.retail.command;

import javax.json.JsonObject;

import io.flowing.retail.commands.channel.EventHandler;

public class ShopEventConsumer extends EventHandler {

  @Override
  public boolean handleEvent(String type, String name, JsonObject event) {
    // we are not yet interested in any events
    return false;
  }

}

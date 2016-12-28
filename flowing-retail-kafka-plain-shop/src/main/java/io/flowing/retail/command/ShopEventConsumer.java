package io.flowing.retail.command;

import javax.json.JsonObject;

import io.flowing.retail.commands.channel.EventConsumer;

public class ShopEventConsumer extends EventConsumer {

  @Override
  public boolean handleEvent(String type, String name, JsonObject event) {
    // we are not yet interested in any events
    return false;
  }

}

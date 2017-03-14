package io.flowing.retail.shop;

import javax.json.JsonObject;

import io.flowing.retail.adapter.EventHandler;

public class ShopEventConsumer extends EventHandler {

  @Override
  public boolean handleEvent(String type, String name, String transactionId, JsonObject event) {
    // we are not yet interested in any events
    return false;
  }

}

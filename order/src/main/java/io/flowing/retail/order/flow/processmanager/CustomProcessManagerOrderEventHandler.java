package io.flowing.retail.order.flow.processmanager;

import javax.json.JsonObject;

import io.flowing.retail.adapter.EventHandler;

/**
 * @TODO: Implement
 *
 */
public class CustomProcessManagerOrderEventHandler extends EventHandler {

  public boolean handleEvent(String type, String name, String transactionId, JsonObject event) {

    String payload = "";
    switch (type + "_ " + name) {
    case "Event_OrderPlaced":
      event("OrderCreated", payload);
      command("ReserveGoodsCommand", payload);
      command("DoPaymentCommand", payload);
      break;
    case "Event_GoodsReserved":
      event("OrderCreated", payload);
      break;
    // TODO: Two events need to have some internal state to work, based on unique process identifier
    default:
      return false;
    }

    return true;

  }

  private void command(String string, String payload) {
    // TODO Auto-generated method stub

  }

  private void event(String string, String payload) {
    // TODO Auto-generated method stub

  }

}

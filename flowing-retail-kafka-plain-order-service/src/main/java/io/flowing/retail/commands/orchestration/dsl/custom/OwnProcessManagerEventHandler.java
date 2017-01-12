package io.flowing.retail.commands.orchestration.dsl.custom;

import javax.json.JsonObject;

import org.camunda.bpm.engine.impl.event.EventHandler;

import io.flowing.retail.commands.Order;
import io.flowing.retail.commands.channel.EventConsumer;

/**
 * Application Service or Command Handler assign a unique process identity
 * 
 * @author ruecker
 *
 */
public class OwnProcessManagerEventHandler extends EventConsumer {

  public boolean handleEvent(String type, String name, JsonObject event) {

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

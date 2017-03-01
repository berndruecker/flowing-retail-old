package io.flowing.retail.payment;

import io.flowing.retail.adapter.EventHandler;
import io.flowing.retail.adapter.FlowingStartup;
import io.flowing.retail.payment.application.PaymentEventConsumer;

public class PaymentApplication {

  public static void main(String[] args) throws Exception {
    EventHandler eventHandler = null;
//    if (Arrays.asList(args).contains("entity")) {
//      eventHandler = new EntityStateOrderEventHandler();
//    } else if (Arrays.asList(args).contains("camunda-dsl")) {
//      eventHandler = new CamundaDslOrderEventHandler();
//    } else { // Arrays.asList(args).contains("camunda") = default
//      eventHandler = new CamundaClassicOrderEventHandler();
//    }

    FlowingStartup.startup("payment", new PaymentEventConsumer(), args);
  }

}

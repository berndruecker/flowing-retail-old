package io.flowing.retail.payment;

import java.util.Arrays;

import io.flowing.retail.adapter.EventHandler;
import io.flowing.retail.adapter.FlowingStartup;
import io.flowing.retail.payment.flow.camunda.CamundaPaymentEventHandler;
import io.flowing.retail.payment.flow.entity.EntityStatePaymentEventHandler;

public class PaymentApplication {

  public static void main(String[] args) throws Exception {
    EventHandler eventHandler = null;
    if (Arrays.asList(args).contains("entity")) {
      eventHandler = new EntityStatePaymentEventHandler();
//    } else if (Arrays.asList(args).contains("camunda-dsl")) {
//      eventHandler = new CamundaDslOrderEventHandler();
    } else { // Arrays.asList(args).contains("camunda") = default
      eventHandler = new CamundaPaymentEventHandler();
    }

    FlowingStartup.startup("payment", eventHandler, args);
  }

}

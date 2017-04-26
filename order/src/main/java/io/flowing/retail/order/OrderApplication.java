package io.flowing.retail.order;

import java.util.Arrays;

import io.flowing.retail.adapter.EventHandler;
import io.flowing.retail.adapter.FlowingStartup;
import io.flowing.retail.order.flow.camunda.CamundaBpmnOrderEventHandler;
import io.flowing.retail.order.flow.camunda.CamundaModelApiOrderEventHandler;
import io.flowing.retail.order.flow.entitystate.EntityStateOrderEventHandler;

public class OrderApplication {

  public static void main(String[] args) throws Exception {
    EventHandler eventHandler = null;
    if (Arrays.asList(args).contains("entity")) {
      eventHandler = new EntityStateOrderEventHandler();
    } else if (Arrays.asList(args).contains("camunda-bpmn")) {
      eventHandler = new CamundaBpmnOrderEventHandler();
    } else { // Arrays.asList(args).contains("camunda") = default
      eventHandler = new CamundaModelApiOrderEventHandler();
    }

    FlowingStartup.startup("order", eventHandler, args);
  }
}

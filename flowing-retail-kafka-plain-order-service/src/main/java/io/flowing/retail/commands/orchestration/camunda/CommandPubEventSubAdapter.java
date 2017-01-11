package io.flowing.retail.commands.orchestration.camunda;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;

import io.flowing.retail.commands.Order;
import io.flowing.retail.commands.OrderRepository;

public class CommandPubEventSubAdapter extends AbstractBpmnActivityBehavior {

  public static final String EXECUTION_ID = "executionId";
  
  protected OrderRepository orderRepository = OrderRepository.instance;

  public Order getOrder(final ActivityExecution execution) {
    return orderRepository.getOrder((String)execution.getVariable("orderId"));
  }
  
  public void execute(final ActivityExecution execution) throws Exception {

    // Build the payload for the message:
    Map<String, Object> payload = new HashMap<String, Object>(execution.getVariables());
    // Add the execution id to the payload:
    payload.put(EXECUTION_ID, execution.getId());

    // Publish a message to the outbound message queue. This method returns after the message has
    // been put into the queue. The actual service implementation (Business Logic) will not yet
    // be invoked:    

  }

  public void signal(ActivityExecution execution, String signalName, Object signalData) throws Exception {

    // leave the service task activity:
    leave(execution);
  }

}
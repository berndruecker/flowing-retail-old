package io.flowing.retail.order.flow.camunda.classic;

import java.util.HashMap;
import java.util.Map;

import org.camunda.bpm.engine.impl.bpmn.behavior.AbstractBpmnActivityBehavior;
import org.camunda.bpm.engine.impl.event.EventType;
import org.camunda.bpm.engine.impl.persistence.entity.EventSubscriptionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.runtime.LegacyBehavior;

import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderRepository;

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
  
  protected void addMessageSubscription(final ActivityExecution execution, String eventName) {
    ExecutionEntity executionEntity = (ExecutionEntity)execution;
    EventSubscriptionEntity eventSubscriptionEntity = new EventSubscriptionEntity(executionEntity, EventType.MESSAGE);
    eventSubscriptionEntity.setEventName(eventName);
    eventSubscriptionEntity.setActivity(executionEntity.getActivity());
    eventSubscriptionEntity.insert();
  }

}
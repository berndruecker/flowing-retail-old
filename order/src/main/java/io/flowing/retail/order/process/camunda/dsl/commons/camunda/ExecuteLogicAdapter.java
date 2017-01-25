package io.flowing.retail.order.process.camunda.dsl.commons.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderRepository;
import io.flowing.retail.order.process.camunda.dsl.commons.EventContext;
import io.flowing.retail.order.process.camunda.dsl.commons.EventInput;
import io.flowing.retail.order.process.camunda.dsl.commons.Registry;

public class ExecuteLogicAdapter implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    EventInput eventInput = Registry.get(execution.getCurrentActivityId());
    Order order = OrderRepository.instance.getOrder((String) execution.getVariable("orderId"));
    EventContext ctx = new EventContext(order);
    eventInput.execute(ctx);
  }

}

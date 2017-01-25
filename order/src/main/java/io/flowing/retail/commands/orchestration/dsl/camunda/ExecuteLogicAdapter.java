package io.flowing.retail.commands.orchestration.dsl.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import io.flowing.retail.commands.Order;
import io.flowing.retail.commands.OrderRepository;
import io.flowing.retail.commands.orchestration.dsl.EventContext;
import io.flowing.retail.commands.orchestration.dsl.EventInput;
import io.flowing.retail.commands.orchestration.dsl.Registry;

public class ExecuteLogicAdapter implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    EventInput eventInput = Registry.get(execution.getCurrentActivityId());
    Order order = OrderRepository.instance.getOrder((String) execution.getVariable("orderId"));
    EventContext ctx = new EventContext(order);
    eventInput.execute(ctx);
  }

}

package io.flowing.retail.commands.orchestration.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import io.flowing.retail.commands.Order;
import io.flowing.retail.commands.OrderEventProducer;
import io.flowing.retail.commands.OrderRepository;

public class OrderCompletedAdapter implements JavaDelegate {

  public void execute(DelegateExecution execution) throws Exception {
    OrderEventProducer eventProducer = new OrderEventProducer();
    Order order = OrderRepository.instance.getOrder((String)execution.getVariable("orderId")); 

    eventProducer.publishEventOrderCompleted(order.getId());
    // Can response be faster than the current transaction?
    // Kafka Client Commit?
  }


}

package io.flowing.retail.order.process.camunda.classic;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import io.flowing.retail.order.application.OrderEventProducer;
import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderRepository;

public class OrderCompletedAdapter implements JavaDelegate {

  public void execute(DelegateExecution execution) throws Exception {
    OrderEventProducer eventProducer = new OrderEventProducer();
    Order order = OrderRepository.instance.getOrder((String)execution.getVariable("orderId")); 

    eventProducer.publishEventOrderCompleted(order.getId());
    // Can response be faster than the current transaction?
    // Kafka Client Commit?
  }


}

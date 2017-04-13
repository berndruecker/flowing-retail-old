package io.flowing.retail.order.flow.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

import io.flowing.retail.order.application.OrderEventProducer;
import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderRepository;

public class OrderDeliveredAdapter implements JavaDelegate {

  public void execute(DelegateExecution execution) throws Exception {
    OrderEventProducer eventProducer = new OrderEventProducer();
    
    Order order = OrderRepository.instance.getOrder((String)execution.getVariable("orderId")); 
    String transactionId = (String)execution.getVariable("transactionId");

    eventProducer.publishEventOrderCompleted(transactionId, order.getId());
  }


}

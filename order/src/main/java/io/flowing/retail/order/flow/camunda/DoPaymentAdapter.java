package io.flowing.retail.order.flow.camunda;

import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;

import io.flowing.retail.order.application.OrderEventProducer;
import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderRepository;

public class DoPaymentAdapter extends CommandPubEventSubAdapter {

  @Override
  public void execute(ActivityExecution execution) throws Exception {
    OrderEventProducer eventProducer = new OrderEventProducer();

    Order order = OrderRepository.instance.getOrder((String)execution.getVariable("orderId")); 
    String transactionId = (String)execution.getVariable("transactionId");
    
    addMessageSubscription(execution, "PaymentReceived");

    eventProducer.publishCommandDoPayment(transactionId, order);
  }

}

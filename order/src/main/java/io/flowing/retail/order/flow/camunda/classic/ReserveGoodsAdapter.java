package io.flowing.retail.order.flow.camunda.classic;

import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;

import io.flowing.retail.order.application.OrderEventProducer;
import io.flowing.retail.order.domain.Order;

public class ReserveGoodsAdapter extends CommandPubEventSubAdapter {

  @Override
  public void execute(ActivityExecution execution) throws Exception {
    OrderEventProducer eventProducer = new OrderEventProducer();
    Order order = orderRepository.getOrder((String)execution.getVariable("orderId")); 
    execution.setVariableLocal("responseEventName", "GoodsReserved");
    // TODO: Maybe use eventId and responseId?
    // what about transactionId
    eventProducer.publishCommandReserveGoods(order);
    // Can response be faster than the current transaction?
    // Kafka Client Commit?
  }

}

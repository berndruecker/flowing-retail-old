package io.flowing.retail.commands.orchestration.camunda;

import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;

import io.flowing.retail.commands.Order;
import io.flowing.retail.commands.OrderEventProducer;

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

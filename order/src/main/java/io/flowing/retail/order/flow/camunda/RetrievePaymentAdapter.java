package io.flowing.retail.order.flow.camunda;

import javax.json.JsonObjectBuilder;

import org.camunda.bpm.engine.impl.pvm.delegate.ActivityExecution;

import io.flowing.retail.order.application.OrderEventProducer;
import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderRepository;

public class RetrievePaymentAdapter extends CommandPubEventSubAdapter {

  private OrderEventProducer eventProducer = new OrderEventProducer();

  @Override
  public void execute(ActivityExecution execution) throws Exception {
    Order order = OrderRepository.instance.getOrder((String)execution.getVariable("orderId")); 
    String transactionId = (String)execution.getVariable("transactionId");    

    // create event payload and send message
    JsonObjectBuilder payload = eventProducer.createPayloadJson("Command", "RetrievePayment", transactionId)
        .add("refId", order.getId()) //
        .add("reason", "CustomerOrder") //
        .add("amount", order.getTotalSum());
    eventProducer.send(payload);

    addMessageSubscription(execution, "PaymentReceived");
  }

}

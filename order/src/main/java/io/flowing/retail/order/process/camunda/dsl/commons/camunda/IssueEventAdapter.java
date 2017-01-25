package io.flowing.retail.order.process.camunda.dsl.commons.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.el.FixedValue;

import io.flowing.retail.adapter.ChannelSender;
import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderRepository;
import io.flowing.retail.order.process.camunda.dsl.commons.EventContext;
import io.flowing.retail.order.process.camunda.dsl.commons.EventInput;
import io.flowing.retail.order.process.camunda.dsl.commons.Registry;

public class IssueEventAdapter implements JavaDelegate {

  public FixedValue type;
  public FixedValue name;
  
  @Override
  public void execute(DelegateExecution execution) throws Exception {
    EventInput eventInput = Registry.get(execution.getCurrentActivityId());
    
    String incomingEvent = (String) execution.getVariable("incomingEvent");
    Order order = OrderRepository.instance.getOrder((String)execution.getVariable("orderId")); 

    EventContext ctx = new EventContext(incomingEvent, order);
    ctx.outgoing(type.getExpressionText(), name.getExpressionText());
    
    eventInput.execute(ctx);
    
    String outgoingEvent = ctx.outgoingAsString();
    execution.setVariable("outgoingEvent", outgoingEvent);
    
    ChannelSender.instance.send(outgoingEvent);
  }

}

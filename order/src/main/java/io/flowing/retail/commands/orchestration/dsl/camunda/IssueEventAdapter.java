package io.flowing.retail.commands.orchestration.dsl.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.impl.el.FixedValue;

import io.flowing.retail.commands.Order;
import io.flowing.retail.commands.OrderRepository;
import io.flowing.retail.commands.channel.ChannelSender;
import io.flowing.retail.commands.orchestration.dsl.EventContext;
import io.flowing.retail.commands.orchestration.dsl.EventInput;
import io.flowing.retail.commands.orchestration.dsl.Registry;

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

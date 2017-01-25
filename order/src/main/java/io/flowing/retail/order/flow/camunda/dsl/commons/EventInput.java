package io.flowing.retail.order.flow.camunda.dsl.commons;

public interface EventInput {
  public void execute(EventContext ctx);
}
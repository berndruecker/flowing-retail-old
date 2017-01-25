package io.flowing.retail.order.process.camunda.dsl.commons;

public interface EventInput {
  public void execute(EventContext ctx);
}
package io.flowing.retail.commands.orchestration.dsl;

public interface EventInput {
  public void execute(EventContext ctx);
}
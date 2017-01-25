package io.flowing.retail.commands.orchestration.dsl;

import io.flowing.retail.commands.orchestration.dsl.camunda.CamundaFlowBuilder;

public interface FlowBuilder {

  FlowBuilder startWithEvent(String event, EventInput eventInput);

  FlowBuilder end();

  FlowBuilder issueEvent(String string, EventInput eventInput);

  FlowBuilder issueCommand(String string, EventInput eventInput);

  FlowBuilder execute(String name, EventInput eventInput);

  FlowBuilder waitForEvents(String... events);

  FlowBuilder waitForEvent(String event);

  FlowBuilder correlation(String variableName, String... synonymNames);

  FlowBuilder correlationPartner(String... partnerCorrelationVariableName);

}

package io.flowing.retail.order.flow.camunda.dsl.commons.camunda;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaField;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaString;

import io.flowing.retail.order.flow.camunda.dsl.commons.EventInput;
import io.flowing.retail.order.flow.camunda.dsl.commons.FlowBuilder;
import io.flowing.retail.order.flow.camunda.dsl.commons.Registry;

public class CamundaFlowBuilder implements FlowBuilder {

  private ProcessBuilder processBuilder;
  @SuppressWarnings("rawtypes")
  private AbstractFlowNodeBuilder lastElement;
  private BpmnModelInstance modelInstance;

  private int counter = 0;
  private String processId;

  public CamundaFlowBuilder(String flowName) {
    processId = "Process_" + flowName;

    processBuilder = initProcessBuilder();
    processBuilder.id(processId).executable();
  }

  @Override
  public FlowBuilder startWithEvent(String event, EventInput eventInput) {

    lastElement = processBuilder.startEvent();

    Registry.add("Process_" + event, eventInput);
    return this;
  }

  @Override
  public FlowBuilder end() {
    modelInstance = lastElement.endEvent().done();
    return this;
  }

  public BpmnModelInstance getBpmnModelInstance() {
    if (modelInstance == null) {
      throw new RuntimeException("You have to call end() before you can access the model.");
    }
    return modelInstance;
  }

  @Override
  public FlowBuilder waitForEvent(String event) {
    String identifier = "ReceiveTask_" + event + "_" + ++counter;
    lastElement = lastElement.receiveTask().id(identifier).name(event).message(event);
    return this;
  }

  @Override
  public FlowBuilder waitForEvents(String... events) {
    counter++;

    String forkId = "fork-" + counter;
    String joinId = "join-" + counter;

    boolean joinNodeAlreadyCreated = false;

    lastElement = lastElement.parallelGateway(forkId);
    for (String event : events) {
      String identifier = "ReceiveTask_" + event + "_" + ++counter;

      if (joinNodeAlreadyCreated) {
        lastElement = lastElement.moveToNode(forkId);
      }
      lastElement = lastElement.receiveTask().id(identifier).name(event).message(event);
      if (!joinNodeAlreadyCreated) {
        lastElement = lastElement.parallelGateway(joinId);
        joinNodeAlreadyCreated = true;
      } else {
        lastElement = lastElement.connectTo(joinId);
      }
    }
    return this;
  }

  @Override
  public FlowBuilder execute(String name, EventInput eventInput) {
    String identifier = "ServiceTask_" + name + "_" + ++counter;
    lastElement = lastElement.serviceTask().id(identifier) //
        .name(name) //
        .camundaClass(ExecuteLogicAdapter.class.getName());

    Registry.add(identifier, eventInput);
    return this;
  }

  @Override
  public FlowBuilder issueCommand(String name, EventInput eventInput) {
    String identifier = "SendTask_" + name + "_" + ++counter;
    lastElement = lastElement.sendTask().id(identifier) //
        .name(name) //
        .camundaClass(IssueEventAdapter.class.getName()) //
        .addExtensionElement(createFieldInjection("type", "Command")) //
        .addExtensionElement(createFieldInjection("name", name));

    Registry.add(identifier, eventInput);
    return this;
  }

  private CamundaField createFieldInjection(String name, String value) {
    CamundaField fieldInjectionEventType = lastElement.done().newInstance(CamundaField.class);
    fieldInjectionEventType.setCamundaName(name);
    CamundaString camundaString = lastElement.done().newInstance(CamundaString.class);
    camundaString.setTextContent(value);
    fieldInjectionEventType.setCamundaString(camundaString);
    return fieldInjectionEventType;
  }

  @Override
  public FlowBuilder issueEvent(String name, EventInput eventInput) {
    String identifier = "SendTask_" + name + "_" + ++counter;
    lastElement = lastElement.sendTask().id(identifier) //
        .name(name) //
        .camundaClass(IssueEventAdapter.class.getName()) //
        .addExtensionElement(createFieldInjection("type", "Event")) //
        .addExtensionElement(createFieldInjection("name", name));

    Registry.add(identifier, eventInput);
    return this;
  }

  private ProcessBuilder initProcessBuilder() {
    return Bpmn.createProcess(); // .id("Process_" + event).executable();
  }

  public String getFlowBpmnXml() {
    return Bpmn.convertToString(modelInstance);
  }

  @Override
  public FlowBuilder correlation(String variableName, String... synonymNames) {
    return this;
  }

  @Override
  public FlowBuilder correlationPartner(String... partnerCorrelationVariableName) {
    // TODO Auto-generated method stub
    return this;
  }
}

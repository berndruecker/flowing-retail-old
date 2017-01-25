package io.flowing.retail.order.process.camunda.dsl.commons.camunda;

import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.BPMN20_NS;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.BPMNDI_NS;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_NS;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;
import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaField;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaString;
import org.camunda.bpm.model.xml.impl.util.ModelIoException;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import io.flowing.bpmn.autolayout.AutoLayout;
import io.flowing.retail.order.process.camunda.dsl.commons.EventInput;
import io.flowing.retail.order.process.camunda.dsl.commons.FlowBuilder;
import io.flowing.retail.order.process.camunda.dsl.commons.Registry;

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
    try {
      // processBuilder = Bpmn.createProcess().id("Process_" +
      // event).executable();

      // BpmnModelInstance modelInstance = Bpmn.INSTANCE.doCreateEmptyModel();
      // is protected - so do some reflection magic
      Method method = Bpmn.INSTANCE.getClass().getDeclaredMethod("doCreateEmptyModel");
      method.setAccessible(true);
      BpmnModelInstance modelInstance = (BpmnModelInstance) method.invoke(Bpmn.INSTANCE);

      // do it on our own (copied from Bpmn.createProcess()) in order to add bpmndi namespace needed by AutoLayout
      Definitions definitions = modelInstance.newInstance(Definitions.class);
      definitions.setTargetNamespace("http://io.flowing/sample");
      definitions.getDomElement().registerNamespace("bpmn", BPMN20_NS);
      definitions.getDomElement().registerNamespace("camunda", CAMUNDA_NS);
      definitions.getDomElement().registerNamespace("bpmndi", BPMNDI_NS);
      modelInstance.setDefinitions(definitions);
      Process process = modelInstance.newInstance(Process.class);
      definitions.addChildElement(process);

      return process.builder();
    } catch (Exception ex) {
      throw new RuntimeException("Could not create process: " + ex.getMessage(), ex);
    }
  }

  public String getFlowBpmnXml() {
    String xmlWithoutLayout = Bpmn.convertToString(modelInstance);   
    xmlWithoutLayout = addNamespacePrefix(xmlWithoutLayout);
    String xmlWithLayout = new AutoLayout().doAutoLayout(xmlWithoutLayout);    
    xmlWithLayout = xmlWithLayout.replaceAll("<bpmndi:BPMNPlane id=\"BPMNPlane_1\">", "<bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\""+processId+"\">");
    return xmlWithLayout;    
  }

  /**
   * Add the namespace prefix exactly like this to get AutoLayout to work correctly
   * (hack! See https://github.com/bpmn-io/bpmn-moddle-auto-layout/blob/master/lib/AutoLayout.js#L29)
   */
  private String addNamespacePrefix(String xmlWithoutLayout) {
    try {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      // http://stackoverflow.com/questions/13501907/adding-namespace-prefix-xml-string-using-xml-dom
      XMLReader xmlReader = new XMLFilterImpl(XMLReaderFactory.createXMLReader()) {
        String namespace = BpmnModelConstants.BPMN20_NS;
        String pref = "bpmn:";

        @Override
        public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
          if (uri.equals(namespace)) {
            super.startElement(namespace, localName, pref + qName, atts);
          } else {
            super.startElement(uri, localName, qName, atts);            
          }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
          if (uri.equals(namespace)) {
            super.endElement(namespace, localName, pref + qName);
          } else {
            super.endElement(uri, localName, qName);            
          }
        }
    };
    StringWriter s = new StringWriter();
    transformer.transform(new SAXSource(xmlReader, new InputSource(new StringReader(xmlWithoutLayout))), new StreamResult(s));
    xmlWithoutLayout = s.toString();   
     
    } catch (Exception e) {
      throw new ModelIoException("Unable to transform XML: " + e.getMessage(), e);
    }
    return xmlWithoutLayout;
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

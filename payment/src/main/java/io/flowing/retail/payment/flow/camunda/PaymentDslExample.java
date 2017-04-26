package io.flowing.retail.payment.flow.camunda;

import java.io.File;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

public class PaymentDslExample {

  public static void main(String[] args) {
    
    BpmnModelInstance model = Bpmn.createExecutableProcess("payment")
       .startEvent()
       .serviceTask().id("charge").name("Charge credit card").camundaClass(ChargeCreditCardAdapter.class.getName()) //
       .boundaryEvent().error()
         .serviceTask().name("Ask customer to update credit card").camundaExpression("#{true}") // noop
         .receiveTask().id("wait").name("Wait for new credit card data").message("CreditCardUpdated")
         .boundaryEvent().timerWithDuration("PT7D") // 7 days
           .endEvent().camundaExecutionListenerClass("end", PaymentFailedAdapter.class.getName())
         .moveToActivity("wait").connectTo("charge") // loop back with new data
       .moveToActivity("charge")
       .endEvent().camundaExecutionListenerClass("end", PaymentCompletedAdapter.class.getName())
       .done();
    
    File file = new File("result.bpmn");
    Bpmn.writeModelToFile(file, model);
  }
}

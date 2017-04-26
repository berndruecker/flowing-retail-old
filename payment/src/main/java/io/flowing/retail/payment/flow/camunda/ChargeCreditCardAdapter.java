package io.flowing.retail.payment.flow.camunda;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class ChargeCreditCardAdapter implements JavaDelegate {

  public void execute(DelegateExecution execution) throws Exception {
    System.out.println("Charging credit card now...");
  }

}

package io.flowing.retail.payment.domain;

public class PaymentService {
  
  public static PaymentService instance = new PaymentService();
  
  public boolean processPayment(String customerAccountDetails, String refId, long amount) {
    // TODO: implement
    return true;
  }

}

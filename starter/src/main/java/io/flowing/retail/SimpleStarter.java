package io.flowing.retail;

import java.util.Arrays;

import io.flowing.retail.inventory.InventoryApplication;
import io.flowing.retail.monitor.MonitorApplication;
import io.flowing.retail.order.OrderApplication;
import io.flowing.retail.payment.PaymentApplication;
import io.flowing.retail.shipping.ShippingApplication;
import io.flowing.retail.shop.ShopApplication;

public class SimpleStarter {

  public static void main(String[] args) throws Exception {
    // args = new String[] {"rabbit"};
    args = new String[] {"camunda"};
//    args = new String[] {"camunda-dsl"};
//    args = new String[] {"entity"};
    
    if (!Arrays.asList(args).contains("rabbit") && !Arrays.asList(args).contains("no-kafka")) { 
      // if not rabbit lets start up kafka
      KafkaStarter.main(args);
    }
    
    ShopApplication.main(args);
    OrderApplication.main(args);
    PaymentApplication.main(args);
    InventoryApplication.main(args);
    ShippingApplication.main(args);
    
    MonitorApplication.main(args);
  }

}

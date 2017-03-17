package io.flowing.retail;

import java.util.Arrays;

import io.flowing.retail.adapter.ChannelSender;
import io.flowing.retail.inventory.InventoryApplication;
import io.flowing.retail.monitor.MonitorApplication;
import io.flowing.retail.order.OrderApplication;
import io.flowing.retail.payment.PaymentApplication;
import io.flowing.retail.shipping.ShippingApplication;
import io.flowing.retail.shop.ShopApplication;

public class SimpleStarter {

  /**
   * You can use different arguments:
   * 
   * For channel: kafka [default], rabbit
   * no-kafka (means that kafka is not started by the starter but provided yourself)
   * 
   * For flow: camunda [default], camunda-bpmn, camunda-dsl, entity
   */
  public static void main(String[] args) throws Exception {

    ChannelSender.delay = 100; // 100 ms

    
    // if not rabbit lets start up kafka - if not supressed by argument
    if (!Arrays.asList(args).contains("rabbit") && !Arrays.asList(args).contains("no-kafka")) { 
      KafkaStarter.main(args);
    }
    
    // startup all services
    OrderApplication.main(args);
    PaymentApplication.main(args);
    InventoryApplication.main(args);
    ShippingApplication.main(args);

    ShopApplication.main(args);

    MonitorApplication.main(args);
  }

}

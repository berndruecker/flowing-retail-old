package io.flowing.retail.payment;

import io.flowing.retail.adapter.ChannelStartup;
import io.flowing.retail.adapter.kafka.KafkaChannelConsumer;
import io.flowing.retail.adapter.kafka.KafkaSender;
import io.flowing.retail.payment.application.PaymentEventConsumer;

public class PaymentApplication {

  public static void main(String[] args) throws Exception {
    ChannelStartup.startup( //
        "PAYMENT", //
        new KafkaSender(), //
        new KafkaChannelConsumer("payment"), //
        new PaymentEventConsumer());
  }

}

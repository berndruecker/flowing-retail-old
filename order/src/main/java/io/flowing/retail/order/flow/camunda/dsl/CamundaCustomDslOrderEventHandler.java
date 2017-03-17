package io.flowing.retail.order.flow.camunda.dsl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;

import io.flowing.retail.order.domain.Customer;
import io.flowing.retail.order.domain.Order;
import io.flowing.retail.order.domain.OrderItem;
import io.flowing.retail.order.domain.OrderRepository;
import io.flowing.retail.order.flow.camunda.dsl.commons.camunda.CamundaFlowBuilder;
import io.flowing.retail.order.flow.camunda.dsl.commons.camunda.DslCamundaEventHandler;

/**
 * Application Service or Command Handler assign a unique process identity
 * 
 * @author ruecker
 *
 */
public class CamundaCustomDslOrderEventHandler extends DslCamundaEventHandler {

  public String defineFlow() {
    CamundaFlowBuilder flow = new CamundaFlowBuilder("OrderPlaced");
    flow
        .correlation("orderId", "refId")
        .correlationPartner("shipmentId", "pickId")
        /////////////// OrderPlaced
        .startWithEvent("OrderPlaced", (ctx) -> {
          Order order = parseOrder(ctx.incoming().getJsonObject("order"));
          OrderRepository.instance.persistOrder(order);
        })
        .execute("CreateOrder", (ctx) -> {
          System.out.println("CREATE ORDER " + ctx.order());
//          Order order = parseOrder(ctx.incoming().getJsonObject("order"));
//          OrderRepository.instance.persistOrder(order);
        }).issueEvent("OrderCreated", (ctx) -> {
          ctx.outgoing() //
              .add("refId", ctx.order().getId()) //
              .add("transactionId", ctx.incoming().getString("transactionId"));
        }).issueCommand("ReserveGoods", (ctx) -> {
          ctx.outgoing() //
              .add("refId", ctx.order().getId()) //
              .add("reason", "CustomerOrder") //
              .add("expirationDate", LocalDateTime.now().plus(2, ChronoUnit.DAYS).toString()) //
              .add("items", createJsonItemArray(ctx.order()));
        }).issueCommand("DoPayment", (ctx) -> {
          ctx.outgoing() //
              .add("refId", ctx.order().getId()) //
              .add("reason", "CustomerOrder") //
              .add("amount", ctx.order().getTotalSum());
        })

        /////////////// GoodsReservedEvent && PaymentReceivedEvent
        // TODO: Some error message, in which case we have to cleanup
        .waitForEvents("GoodsReserved", "PaymentReceived") //

        .issueCommand("PickGoods", (ctx) -> {
          ctx.outgoing() //
              .add("refId", ctx.order().getId()) //
              .add("reason", "CustomerOrder") //
              .add("items", createJsonItemArray(ctx.order()));

        }) //

        /////////////// GoodsPicked
        .waitForEvent("GoodsPicked")

        // issue ShipCommand
        // wait for occurrence of the events:
        // - ShipmentShippedEvent
        .issueCommand("ShipGoods", (ctx) -> {
          ctx.outgoing() //
              .add("pickId", ctx.incoming().getString("pickId")) //
              // customer orders are always shipped via DHL:
              .add("logisticsProvider", "DHL") //
              .add("recipientName", ctx.order().getCustomer().getName()) //
              .add("recipientAddress", ctx.order().getCustomer().getAddress());
        }) //

        /////////////// ShipmentShipped
        .waitForEvent("GoodsShipped") //
        .issueEvent("OrderCompleted", (ctx) -> {
          ctx.outgoing() //
              .add("orderId", ctx.order().getId());
        }) //

        .end();

    return flow.getFlowBpmnXml();
  }

  private JsonArrayBuilder createJsonItemArray(Order order) {
    JsonArrayBuilder itemsArrayBuilder = Json.createArrayBuilder();
    for (OrderItem item : order.getItems()) {
      itemsArrayBuilder.add(Json.createObjectBuilder() //
          .add("articleId", item.getArticleId()) //
          .add("amount", item.getAmount()));
    }
    return itemsArrayBuilder;
  }

  private Order parseOrder(JsonObject orderJson) {
    Order order = new Order();

    // Order Service is NOT interested in customer id - ignore:
    JsonObject customerJson = orderJson.getJsonObject("customer");
    orderJson.getString("customerId");

    Customer customer = new Customer() //
        .setName(customerJson.getString("name")) //
        .setAddress(customerJson.getString("address"));
    order.setCustomer(customer);

    JsonArray jsonArray = orderJson.getJsonArray("items");
    for (JsonObject itemJson : jsonArray.getValuesAs(JsonObject.class)) {
      order.addItem( //
          new OrderItem() //
              .setArticleId(itemJson.getString("articleId")) //
              .setAmount(itemJson.getInt("amount")));
    }

    return order;
  }
}

package io.flowing.retail.commands.orchestration.dsl.camunda;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ExecutionQuery;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.xml.impl.util.ModelIoException;
import org.camunda.bpm.model.xml.instance.DomDocument;
import org.h2.tools.Server;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import com.camunda.consulting.util.LicenseHelper;
import com.camunda.consulting.util.UserGenerator;

import io.flowing.bpmn.autolayout.AutoLayout;
import io.flowing.retail.commands.Customer;
import io.flowing.retail.commands.Order;
import io.flowing.retail.commands.OrderItem;
import io.flowing.retail.commands.OrderRepository;
import io.flowing.retail.commands.channel.EventHandler;

/**
 * Application Service or Command Handler assign a unique process identity
 * 
 * @author ruecker
 *
 */
public class OrderDslCamundaEventHandler extends DslCamundaEventHandler {

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
              .add("correlationId", ctx.incoming().getString("correlationId"));
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

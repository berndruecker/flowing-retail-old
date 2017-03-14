package io.flowing.retail.shop;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import io.flowing.retail.adapter.EventProducer;

public class ShopEventProducer extends EventProducer {

  public void publishEventOrderPlaced(String transactionId, String customerId, ShoppingCart shoppingCart) {
    JsonObjectBuilder event = createEventPayloadJson("OrderPlaced", transactionId);

    JsonArrayBuilder itemsArrayBuilder = Json.createArrayBuilder();
    for (Item item : shoppingCart.getItems()) {
      itemsArrayBuilder.add(Json.createObjectBuilder() //
          .add("articleId", item.getArticleId()).add("amount", item.getAmount()));
    }

    event //
        .add("order", //
            Json.createObjectBuilder() //
                .add("customerId", customerId) //
                .add("customer",
                    Json.createObjectBuilder() //
                        .add("name", "Camunda") //
                        .add("address", "Zossener Strasse 55\n10961 Berlin\nGermany") //
                ) //
                .add("items", itemsArrayBuilder));

    send(event);
  }

}

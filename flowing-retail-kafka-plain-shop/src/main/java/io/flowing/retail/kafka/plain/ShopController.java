package io.flowing.retail.kafka.plain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.UUID;

import javax.servlet.http.HttpSession;

@RestController
public class ShopController {
  
  @Autowired
  private KafkaEventProducer eventProducer;

  @RequestMapping(path = "/api/cart", method = GET)
  public ShoppingCart getCart(HttpSession httpSession) {
    return getShoppingCart(httpSession);
  }
  
  @RequestMapping(path = "/api/cart/order", method = PUT)
  public String placeOrder(@RequestParam(value = "customerId") String customerId, HttpSession httpSession) {

    String correlationId = UUID.randomUUID().toString();    
    eventProducer.publishOrderPlacedEvent(correlationId, customerId, getShoppingCart(httpSession));    
    httpSession.removeAttribute("cart");
    
    // note that we cannot easily return an order id here - as everything is asynchronous
    // and blocking the client is not what we want.
    // but we return an own correlationId which can be used in the UI to show status maybe later
    return "{correlationId: " + correlationId + "}";
  }

  @RequestMapping(path = "/api/cart/item", method = PUT)
  public ShoppingCart addItemToCart(@RequestParam(value = "articleId") String articleId, @RequestParam(value = "amount") int amount, HttpSession httpSession) {
    ShoppingCart cart = getShoppingCart(httpSession);
    cart.addItem(articleId, amount);
    return cart;
  }

  @RequestMapping(path = "/api/cart/item", method = DELETE)
  public ShoppingCart removeItemFromCart(@RequestParam(value = "articleId") String articleId, HttpSession httpSession) {
    ShoppingCart cart = getShoppingCart(httpSession);
    cart.removeItem(articleId);
    return cart;
  }

  private ShoppingCart getShoppingCart(HttpSession httpSession) {
    ShoppingCart cart = (ShoppingCart) httpSession.getAttribute("cart");
    if (cart == null) {
      cart = new ShoppingCart();
      httpSession.setAttribute("cart", cart);
    }
    return cart;
  }

}
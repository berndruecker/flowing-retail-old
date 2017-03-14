package io.flowing.retail.shop;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestShopController {
  
  private ShopEventProducer eventProducer = new ShopEventProducer();

  @RequestMapping(path = "/api/cart", method = GET)
  public ShoppingCart getCart(HttpSession httpSession) {
    return getShoppingCart(httpSession);
  }
  
  @RequestMapping(path = "/api/cart/order", method = PUT)
  public String placeOrder(@RequestParam(value = "customerId") String customerId, HttpSession httpSession) {

    ShoppingCart cart = getShoppingCart(httpSession);
    
    cart.addItem("article1", 5);
    cart.addItem("article2", 10);
    
    String transactionId = UUID.randomUUID().toString();   
    eventProducer.publishEventOrderPlaced(transactionId, customerId, cart);    
    httpSession.removeAttribute("cart");
    
    // note that we cannot easily return an order id here - as everything is asynchronous
    // and blocking the client is not what we want.
    // but we return an own correlationId which can be used in the UI to show status maybe later
    return "{transactionId: " + transactionId + "}";
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
package io.flowing.retail.inventory.domain.stock.reservation;

import io.flowing.retail.domain.Event;
import io.flowing.retail.inventory.domain.article.ArticleId;
import io.flowing.retail.inventory.domain.order.OrderId;
import io.flowing.retail.inventory.domain.stock.Stock;
import io.flowing.retail.inventory.domain.stock.StockId;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class OrderQuantityReserved extends Event<Stock, StockId> {

  @Override
  protected int release() {
    return 1;
  }

  private OrderId orderId;
  private ArticleId articleId;
  private int orderQuantity;

  public OrderId orderId() { return orderId; }
  public ArticleId articleId() { return articleId; }
  public int orderQuantity() { return orderQuantity; }

  public OrderQuantityReserved(Stock stock, OrderId orderId, int orderQuantity) {
    super(stock);
    this.orderId = orderId;
    this.articleId = new ArticleId(change().id().id());
    this.orderQuantity = orderQuantity;
  }

  private OrderQuantityReserved() {
  }

}

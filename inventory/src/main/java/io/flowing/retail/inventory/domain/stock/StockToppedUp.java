package io.flowing.retail.inventory.domain.stock;

import io.flowing.retail.domain.Event;
import io.flowing.retail.inventory.domain.article.ArticleId;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class StockToppedUp extends Event<Stock, StockId> {

  @Override
  protected int release() {
    return 1;
  }

  private ArticleId articleId;
  private int topUpQuantity;
  private int newReserves;

  public ArticleId articleId() { return articleId; };
  public int topUpQuantity() { return topUpQuantity; };
  public int newReserves() { return newReserves; };

  public StockToppedUp(Stock stock, int topUpQuantity, int newReserves) {
    super(stock);
    this.topUpQuantity = topUpQuantity;
    this.newReserves = newReserves;
    this.articleId = new ArticleId(stock.id().id());
  }

  private StockToppedUp() {
  }

}

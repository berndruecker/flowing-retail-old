package io.flowing.retail.inventory.domain.stock;

import io.flowing.retail.domain.Event;
import io.flowing.retail.inventory.domain.article.ArticleId;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class StockPickedUp extends Event<Stock, StockId> {

  @Override
  protected int release() {
    return 1;
  }

  private ArticleId articleId;
  private int pickUpQuantity;
  private int newReserves;

  public ArticleId articleId() { return articleId; }
  public int pickUpQuantity() { return pickUpQuantity; }
  public int newReserves() { return newReserves; }

  public StockPickedUp(Stock stock, int pickUpQuantity, int newReserves) {
    super(stock);
    this.pickUpQuantity = pickUpQuantity;
    this.newReserves = newReserves;
    this.articleId = new ArticleId(stock.id().id());
  }

  private StockPickedUp() {
  }

}

package io.flowing.retail.inventory.domain.stock;

import io.flowing.retail.domain.Event;
import io.flowing.retail.inventory.domain.article.ArticleId;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class StockCreated extends Event<Stock, StockId> {

  @Override
  protected int release() {
    return 1;
  }

  private ArticleId articleId;
  private int initialReserves;

  public ArticleId articleId() { return articleId; }
  public int initialReserves() { return initialReserves; }

  public StockCreated(Stock stock, int initialReserves) {
    super(stock);
    this.articleId = new ArticleId(stock.id().id());
    this.initialReserves = initialReserves;
  }

  private StockCreated() {
  }

}

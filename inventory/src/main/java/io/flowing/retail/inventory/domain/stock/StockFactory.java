package io.flowing.retail.inventory.domain.stock;

import io.flowing.retail.inventory.domain.article.ArticleId;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface StockFactory {

  default Stock create(ArticleId articleId) {
    return new Stock(new StockId(articleId.id()), 0);
  }

}

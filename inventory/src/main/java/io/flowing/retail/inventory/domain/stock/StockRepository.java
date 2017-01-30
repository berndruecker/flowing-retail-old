package io.flowing.retail.inventory.domain.stock;

import io.flowing.retail.domain.Repository;
import io.flowing.retail.inventory.domain.article.ArticleId;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface StockRepository extends Repository<Stock, StockId> {

  Stock find(ArticleId articleId);

}

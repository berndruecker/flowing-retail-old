package io.flowing.retail.inventory.adapter.persistence;

import io.flowing.retail.adapter.persistence.InMemoryRepository;
import io.flowing.retail.inventory.domain.stock.Stock;
import io.flowing.retail.inventory.domain.stock.StockId;

import java.util.UUID;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class InMemoryStockRepository extends InMemoryRepository<Stock, StockId> {

  @Override
  public StockId nextIdentity() {
    return new StockId(UUID.randomUUID().toString());
  }

}

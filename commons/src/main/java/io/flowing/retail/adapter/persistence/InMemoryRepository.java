package io.flowing.retail.adapter.persistence;

import io.flowing.retail.domain.Entity;
import io.flowing.retail.domain.Identity;
import io.flowing.retail.domain.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class InMemoryRepository<E extends Entity<ID>, ID extends Identity> implements Repository<E, ID> {

  private Map<ID, E> inMemoryStore = new HashMap<>();

  @Override
  public E find(ID id) {
    return inMemoryStore.get(id);
  }

  @Override
  public Set<E> findAll() {
    return inMemoryStore.values().stream().collect(Collectors.toSet());
  }

  @Override
  public void save(E aggregate) {
    inMemoryStore.put(aggregate.id(), aggregate);
  }

  @Override
  public boolean delete(E aggregate) {
    return inMemoryStore.remove(aggregate.id(), aggregate);
  }

}

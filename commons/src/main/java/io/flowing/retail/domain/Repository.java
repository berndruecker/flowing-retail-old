package io.flowing.retail.domain;

import java.util.Set;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface Repository<E extends Entity<ID>, ID extends Identity> {

  E find(ID id);

  Set<E> findAll();

  void save(E aggregate);

  default void saveAll(Set<E> aggregates) {
    for(E aggregate: aggregates) {
      save(aggregate);
    }
  }

  boolean delete(E aggregate);

  default void deleteAll(Set<E> aggregates) {
    for(E aggregate: aggregates) {
      delete(aggregate);
    }
  }

  ID nextIdentity();

}

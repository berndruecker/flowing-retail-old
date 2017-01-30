package io.flowing.retail.domain;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class Change<E extends Entity<ID>, ID extends Identity> {

  private String type;
  private ID id;
  private int version;

  public String type() { return type; };
  public ID id() { return id; };
  public int version() { return version; };

  public Change(E entity) {
    this.type = entity.getClass().getCanonicalName();
    this.id = entity.id();
    this.version = entity.version();
  }

  @Override
  public int hashCode() {
    return 31 * 17 + type().hashCode() + id().hashCode() + version();
  }

  @Override
  public boolean equals(Object anObject) {
    boolean equalObjects = false;
    if (anObject != null && this.getClass() == anObject.getClass()) {
      Change other = (Change) anObject;
      equalObjects = this.type().equals(other.type())
        && this.id().equals(other.id())
        && this.version() == other.version();
    }
    return equalObjects;
  }

}

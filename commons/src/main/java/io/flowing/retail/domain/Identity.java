package io.flowing.retail.domain;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Identity<ID> extends ValueObject {

  private static final long serialVersionUID = 1L;

  private ID id;

  protected Identity(ID id) {
    this.setId(id);
  }

  public ID id() {
    return this.id;
  }

  @Override
  public int hashCode() {
    return 31 * 17 + id.hashCode();
  }

  @Override
  public boolean equals(Object anObject) {
    boolean equalObjects = false;
    if (anObject != null && this.getClass() == anObject.getClass()) {
      Identity other = (Identity) anObject;
      equalObjects = this.id().equals(other.id());
    }
    return equalObjects;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + " [id=" + id + "]";
  }

  private void setId(ID id) {
    this.id = id;
  }

}

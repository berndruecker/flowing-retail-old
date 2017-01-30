package io.flowing.retail.domain;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Entity<ID extends Identity> {

    private ID id;
    private int version;

    protected Entity(ID id) {
        this.id = id;
    }

    public ID id() {
        return this.id;
    }

    public int version() {
        return this.version;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
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

}

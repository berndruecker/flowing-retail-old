package io.flowing.retail.domain;


import java.time.LocalDateTime;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Event<E extends Entity<ID>, ID extends Identity> {

  private int definition;
  private LocalDateTime occuredOn;
  private Change<E, ID> change;

  protected Event() {
    // if needed for reflection based instantiation
    // override with a private default constructor
  }

  protected Event(E entity) {
    definition = release();
    occuredOn = LocalDateTime.now();
    change = new Change<>(entity);
  }

  public int definition() {
    return definition;
  }

  public LocalDateTime occuredOn() {
    return occuredOn;
  }

  public Change<E, ID> change() {
    return change;
  }

  protected abstract int release();

  protected void upgrade(int definition) {
    // Just a quick subclass implementation template
    switch (definition) {
      case 1:
        // Upgrade event from definition 1 to definition 2
        return;
    }
  }

  protected final void upgrade() {
    while (definition() < release())
      upgrade(definition++);
  }

  public static void raise(Event event) {
    Raise.instance().raise(event);
  }

  @Override
  public int hashCode() {
    return change.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return change.equals(object);
  }

  /**
   * @author Martin Schimak <martin.schimak@plexiti.com>
   */
  private static class Raise {

    private static Raise instance = new Raise();

    private static Raise instance() {
      return instance;
    }

    public void raise(Event event) {
      // TODO implement
    }

  }

}

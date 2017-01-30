package io.flowing.retail.application;

import io.flowing.retail.domain.ValueObject;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class AbstractMessage extends ValueObject {

  private String context;
  private String type;
  private int version;
  private LocalDateTime occuredOn;

  protected AbstractMessage() {
    context = context();
    type = getClass().getSimpleName();
    version = release();
    occuredOn = LocalDateTime.now();
  }

  public abstract String context();

  public String type() {
    return type;
  }

  public int version() {
    return version;
  }

  public LocalDateTime occuredOn() {
    return occuredOn;
  }

  protected abstract int release();

  protected void upgrade(int version) {
    // Just a quick subclass implementation template
    switch (version) {
      case 1:
        // Upgrade event from version 1 to version 2
        return;
    }
  }

  protected final void upgrade() {
    while (version() < release())
      upgrade(version++);
  }

  @Override
  public boolean equals(Object other) {
    boolean equalObjects = false;
    if (other != null && this.getClass() == other.getClass()) {
      AbstractMessage notification = (AbstractMessage) other;
      equalObjects = this.context().equals(notification.context())
        && this.type().equals(notification.type())
        && this.occuredOn().equals(notification.occuredOn())
        && this.version() == notification.version();
    }
    return equalObjects;
  }

}

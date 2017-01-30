package io.flowing.retail.application;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Response<C extends Command> extends Notification {

  private C command;

}

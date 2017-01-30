package io.flowing.retail.domain;

import java.io.Serializable;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class ValueObject implements Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public abstract boolean equals(Object anObject);

    protected ValueObject() {
        super();
    }

}

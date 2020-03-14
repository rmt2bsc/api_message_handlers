package org.rmt2.api.handlers.contacts;

import com.RMT2RuntimeException;

/**
 * @author appdev
 *
 */
public class InvalidRequestContactProfileException extends RMT2RuntimeException {

    private static final long serialVersionUID = 5215472098798504260L;

    /**
     * 
     */
    public InvalidRequestContactProfileException() {
        super();
    }

    /**
     * @param msg
     */
    public InvalidRequestContactProfileException(String msg) {
        super(msg);
    }

    /**
     * @param msg
     * @param e
     */
    public InvalidRequestContactProfileException(String msg, Throwable e) {
        super(msg, e);
    }

    /**
     * @param e
     */
    public InvalidRequestContactProfileException(Throwable e) {
        super(e);
    }

}

package org.rmt2.api.handlers.contacts;

import com.RMT2RuntimeException;

/**
 * @author appdev
 *
 */
public class InvalidRequestContactCriteriaException extends RMT2RuntimeException {

    private static final long serialVersionUID = 5215472098798504260L;

    /**
     * 
     */
    public InvalidRequestContactCriteriaException() {
        super();
    }

    /**
     * @param msg
     */
    public InvalidRequestContactCriteriaException(String msg) {
        super(msg);
    }

    /**
     * @param msg
     * @param e
     */
    public InvalidRequestContactCriteriaException(String msg, Throwable e) {
        super(msg, e);
    }

    /**
     * @param e
     */
    public InvalidRequestContactCriteriaException(Throwable e) {
        super(e);
    }

}

package org.rmt2.api.handlers.contacts;

import com.RMT2RuntimeException;

/**
 * @author appdev
 *
 */
public class TooManyContactProfilesException extends RMT2RuntimeException {

    private static final long serialVersionUID = 5215472098798504260L;

    /**
     * 
     */
    public TooManyContactProfilesException() {
        super();
    }

    /**
     * @param msg
     */
    public TooManyContactProfilesException(String msg) {
        super(msg);
    }

    /**
     * @param msg
     * @param e
     */
    public TooManyContactProfilesException(String msg, Throwable e) {
        super(msg, e);
    }

    /**
     * @param e
     */
    public TooManyContactProfilesException(Throwable e) {
        super(e);
    }

}

package org.rmt2.api.handlers.contacts;

import com.RMT2RuntimeException;

/**
 * @author appdev
 *
 */
public class NoContactProfilesAvailableException extends RMT2RuntimeException {

    private static final long serialVersionUID = 5215472098798504260L;

    /**
     * 
     */
    public NoContactProfilesAvailableException() {
        super();
    }

    /**
     * @param msg
     */
    public NoContactProfilesAvailableException(String msg) {
        super(msg);
    }

    /**
     * @param msg
     * @param e
     */
    public NoContactProfilesAvailableException(String msg, Throwable e) {
        super(msg, e);
    }

    /**
     * @param e
     */
    public NoContactProfilesAvailableException(Throwable e) {
        super(e);
    }

}

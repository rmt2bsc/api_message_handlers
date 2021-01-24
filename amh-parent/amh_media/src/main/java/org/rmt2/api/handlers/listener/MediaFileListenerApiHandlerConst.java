package org.rmt2.api.handlers.listener;

import org.rmt2.api.ApiMessageHandlerConst;

/**
 * Media file listener specific constants.
 * 
 * @author appdev
 *
 */
public class MediaFileListenerApiHandlerConst {
    public static final String MESSAGE_STARTED = "Media file listener started";
    public static final String MESSAGE_STOPPED = "Media file listener stopped";
    public static final String MESSAGE_STATUS = "Media file listener status: " + ApiMessageHandlerConst.MSG_PLACEHOLDER;

    public static final String MESSAGE_ERROR = "Media file listener error.  Contact the system adminstrator";

    public MediaFileListenerApiHandlerConst() {

    }

}

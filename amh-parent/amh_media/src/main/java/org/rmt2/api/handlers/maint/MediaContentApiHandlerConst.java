package org.rmt2.api.handlers.maint;

import org.rmt2.api.ApiMessageHandlerConst;

/**
 * Media content manual upload media specific constants.
 * 
 * @author appdev
 *
 */
public class MediaContentApiHandlerConst {
    public static final String MESSAGE_FOUND = "Media content data found!";
    public static final String MESSAGE_NOT_FOUND = "Media content data not found!";
    public static final String MESSAGE_FETCH_ERROR = "Failure to retrieve Media content data";

    public static final String MESSAGE_UPLOAD_SUCCESS = "Media content was uploaded successfully for file: "
            + ApiMessageHandlerConst.MSG_PLACEHOLDER;
    public static final String MESSAGE_UPLOAD_ERROR = "Media content upload failed.  Please consult system administrator";
    public static final String MESSAGE_UPLOAD_MISSING_PROFILE_ERROR = "Media content upload operation requires a profile section";
    public static final String MESSAGE_UPLOAD_MISSING_PROFILE_AUDIOVIDEOCONTENT = "Media content upload operation requires a audio/video content section";

    public MediaContentApiHandlerConst() {

    }

}

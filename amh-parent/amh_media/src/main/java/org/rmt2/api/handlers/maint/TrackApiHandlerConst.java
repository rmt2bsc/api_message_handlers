package org.rmt2.api.handlers.maint;

import org.rmt2.api.ApiMessageHandlerConst;

/**
 * Media track specific constants.
 * 
 * @author appdev
 *
 */
public class TrackApiHandlerConst {
    public static final String MESSAGE_FOUND = "Media track data found!";
    public static final String MESSAGE_NOT_FOUND = "Media track data not found!";
    public static final String MESSAGE_FETCH_ERROR = "Failure to retrieve Media track data";

    public static final String MESSAGE_UPDATE_NEW_SUCCESS = "New media track, " + ApiMessageHandlerConst.MSG_PLACEHOLDER
            + ", was created successfully";
    public static final String MESSAGE_UPDATE_EXISTING_SUCCESS = "Media track, " + ApiMessageHandlerConst.MSG_PLACEHOLDER
            + ", was updated successfully";
    public static final String MESSAGE_UPDATE_NO_CHANGE = "Media track, " + ApiMessageHandlerConst.MSG_PLACEHOLDER
            + ", did not have any updates applied";
    public static final String MESSAGE_UPDATE_ERROR = "Media track update failed";
    public static final String MESSAGE_UPDATE_MISSING_PROFILE_ERROR = "Media track update request requires a profile section";
    public static final String MESSAGE_UPDATE_MISSING_PROFILE_AUDIOVIDEODETAILS = "Media track update request requires a audio/video details section";
    public static final String MESSAGE_UPDATE_MISSING_PROFILE_TRACKS = "Media track update request requires the artist, project, and track sections";
    public static final String MESSAGE_UPDATE_TOO_MANY_TRACKS = "Media track update request can only update one artist, project, and track at a time";

    public static final String MESSAGE_DELETE_SUCCESS = "Delete was successful.  Total number of Media tracks deleted: "
            + ApiMessageHandlerConst.MSG_PLACEHOLDER;
    public static final String MESSAGE_DELETE_ERROR = "An API error occurred for the delete media track operation";

    public TrackApiHandlerConst() {

    }

}

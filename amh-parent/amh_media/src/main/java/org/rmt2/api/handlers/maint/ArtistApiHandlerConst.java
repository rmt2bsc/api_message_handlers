package org.rmt2.api.handlers.maint;

import org.rmt2.api.ApiMessageHandlerConst;

/**
 * Artist specific constants.
 * 
 * @author appdev
 *
 */
public class ArtistApiHandlerConst {
    public static final String MESSAGE_FOUND = "Artist data found!";
    public static final String MESSAGE_NOT_FOUND = "Artist data not found!";
    public static final String MESSAGE_FETCH_ERROR = "Failure to retrieve Artist data";

    public static final String MESSAGE_UPDATE_NEW_SUCCESS = "New artist, " + ApiMessageHandlerConst.MSG_PLACEHOLDER
            + ", was created successfully";
    public static final String MESSAGE_UPDATE_EXISTING_SUCCESS = "Artist, " + ApiMessageHandlerConst.MSG_PLACEHOLDER
            + ", was updated successfully";
    public static final String MESSAGE_UPDATE_NO_CHANGE = "Artist, " + ApiMessageHandlerConst.MSG_PLACEHOLDER
            + ", did not have any updates applied";
    public static final String MESSAGE_UPDATE_ERROR = "Artist update failed";
    public static final String MESSAGE_UPDATE_MISSING_PROFILE_ERROR = "Artist update request requires a profile section";
    public static final String MESSAGE_UPDATE_MISSING_PROFILE_AUDIOVIDEODETAILS = "Artist update request requires a audio/video details section";
    public static final String MESSAGE_UPDATE_MISSING_PROFILE_ARTIST = "Artist update request requires an artist section";
    public static final String MESSAGE_UPDATE_TOO_MANY_ARTIST = "Artist update request can only update one artist at a time";


    public ArtistApiHandlerConst() {

    }

}

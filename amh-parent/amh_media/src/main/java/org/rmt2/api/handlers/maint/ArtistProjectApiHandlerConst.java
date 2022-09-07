package org.rmt2.api.handlers.maint;

import org.rmt2.api.ApiMessageHandlerConst;

/**
 * Artist specific constants.
 * 
 * @author appdev
 *
 */
public class ArtistProjectApiHandlerConst {
    public static final String MESSAGE_FOUND = "Artist project data found!";
    public static final String MESSAGE_NOT_FOUND = "Artist project data not found!";
    public static final String MESSAGE_FETCH_ERROR = "Failure to retrieve artist project data";

    public static final String MESSAGE_UPDATE_NEW_SUCCESS = "New artist project was created successfully";
    public static final String MESSAGE_UPDATE_EXISTING_SUCCESS = "Artist project was updated successfully";
    public static final String MESSAGE_UPDATE_NO_CHANGE = "Artist project, " + ApiMessageHandlerConst.MSG_PLACEHOLDER
            + ", did not have any updates applied";
    public static final String MESSAGE_UPDATE_ERROR = "Artist project update failed";
    public static final String MESSAGE_UPDATE_MISSING_PROFILE_ERROR = "Artist/project update operation requires a profile section";
    public static final String MESSAGE_UPDATE_MISSING_PROFILE_AUDIOVIDEODETAILS = "Artist/project update operation requires a audio/video details section";
    public static final String MESSAGE_UPDATE_MISSING_PROFILE_PROJECT = "Artist/project update operation requires the artist and project sections";
    public static final String MESSAGE_UPDATE_TOO_MANY_ARTIST_PROJECTS = "Artist/project update operation can only manage one artist/project at a time";

    public static final String MESSAGE_DELETE_SUCCESS = "Delete was successful.  Total number of artist projects deleted: "
            + ApiMessageHandlerConst.MSG_PLACEHOLDER;
    public static final String MESSAGE_DELETE_ERROR = "An API error occurred dor the delete artist project operation";

    public ArtistProjectApiHandlerConst() {

    }

}

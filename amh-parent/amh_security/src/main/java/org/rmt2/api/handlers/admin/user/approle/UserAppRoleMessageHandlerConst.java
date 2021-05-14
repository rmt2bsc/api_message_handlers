package org.rmt2.api.handlers.admin.user.approle;

/**
 * User common message handler API constants
 * 
 * @author roy.terrell
 *
 */
public class UserAppRoleMessageHandlerConst {
    public static final String MESSAGE_FOUND = "User Application-Role data found!";
    public static final String MESSAGE_NOT_FOUND = "User Application-Role data not found!";
    public static final String MESSAGE_FETCH_ERROR = "User Application-Role fetch operation failed";

    public static final String MESSAGE_UPDATE_SUCCESS = "User Application-Role were assigned and/or revoked successfully";
    public static final String MESSAGE_ZERO_APPROLES_PROCESSED = "Zero application roles were processed";
    public static final String MESSAGE_UPDATE_ERROR = "User Application-Role update operation failed";

    public static final String MESSAGE_MISSING_USER_SECTION = "User Application-Role section is required for create/update/delete operations";
    public static final String MESSAGE_MISSING_USER_APPROLE_PROFILE_SECTION = "User Application-Role profile section is required for query operations";


    /**
     * 
     */
    public UserAppRoleMessageHandlerConst() {
    }

}

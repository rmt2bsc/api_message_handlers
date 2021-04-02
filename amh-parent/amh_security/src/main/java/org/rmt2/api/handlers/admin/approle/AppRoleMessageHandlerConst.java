package org.rmt2.api.handlers.admin.approle;

/**
 * Application-Role common message handler API constants
 * 
 * @author roy.terrell
 *
 */
public class AppRoleMessageHandlerConst {
    public static final String MESSAGE_FOUND = "Application-Role data found!";
    public static final String MESSAGE_NOT_FOUND = "Application-Role data not found!";
    public static final String MESSAGE_FETCH_ERROR = "Application-Role fetch operation failed";

    public static final String MESSAGE_CREATE_SUCCESS = "Application-Role was created successfully";
    public static final String MESSAGE_UPDATE_SUCCESS = "Application-Role was modified successfully";
    public static final String MESSAGE_UPDATE_ERROR = "Application-Role update operation failed";

    public static final String MESSAGE_DELETE_SUCCESS = "Application-Role was deleted successfully";
    public static final String MESSAGE_DELETE_ERROR = "Application-Role delete operation failed";

    public static final String MESSAGE_MISSING_APPROLE_SECTION = "Application-Role section is required for create/update/delete operations";
    public static final String MESSAGE_MISSING_APPROLE_CRITERIA_SECTION = "Application-Role criteria section is required for query operations";


    /**
     * 
     */
    public AppRoleMessageHandlerConst() {
    }

}

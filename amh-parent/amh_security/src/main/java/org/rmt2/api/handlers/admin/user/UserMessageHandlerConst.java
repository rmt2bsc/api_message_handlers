package org.rmt2.api.handlers.admin.user;

/**
 * User common message handler API constants
 * 
 * @author roy.terrell
 *
 */
public class UserMessageHandlerConst {
    public static final String MESSAGE_FOUND = "User data found!";
    public static final String MESSAGE_NOT_FOUND = "User data not found!";
    public static final String MESSAGE_FETCH_ERROR = "User fetch operation failed";
    public static final String MESSAGE_CREATE_SUCCESS = "User was created successfully";
    public static final String MESSAGE_UPDATE_SUCCESS = "User was modified successfully";
    public static final String MESSAGE_UPDATE_ERROR = "User update operation failed";
    public static final String MESSAGE_CHANGE_PASSWORD_SUCCESS = "Password was changed successfully";
    public static final String MESSAGE_CHANGE_PASSWORD_ERROR = "Password change operation failed";

    public static final String MESSAGE_DELETE_SUCCESS = "User was deleted successfully";
    public static final String MESSAGE_DELETE_ERROR = "User delete operation failed";

    public static final String MESSAGE_MISSING_USER_SECTION = "User section is required for create/update/delete operations";
    public static final String MESSAGE_MISSING_USER_CRITERIA_SECTION = "User criteria section is required for query operations";
    /**
     * 
     */
    public UserMessageHandlerConst() {
    }

}

package org.rmt2.api.handlers.admin.usergroup;

/**
 * User Group common message handler API constants
 * 
 * @author roy.terrell
 *
 */
public class UserGroupMessageHandlerConst {
    public static final String MESSAGE_FOUND = "User Group data found!";
    public static final String MESSAGE_NOT_FOUND = "User Group data not found!";
    public static final String MESSAGE_FETCH_ERROR = "User Group fetch operation failed";

    public static final String MESSAGE_CREATE_SUCCESS = "User Group was created successfully";
    public static final String MESSAGE_UPDATE_SUCCESS = "User Group was modified successfully";
    public static final String MESSAGE_UPDATE_ERROR = "User Group update operation failed";

    public static final String MESSAGE_DELETE_SUCCESS = "User Group was deleted successfully";
    public static final String MESSAGE_DELETE_ERROR = "User Group delete operation failed";

    public static final String MESSAGE_MISSING_USERGROUP_SECTION = "User Group section is required for create/update/delete operations";
    public static final String MESSAGE_MISSING_USERGROUP_CRITERIA_SECTION = "User Group criteria section is required for query operations";


    /**
     * 
     */
    public UserGroupMessageHandlerConst() {
    }

}

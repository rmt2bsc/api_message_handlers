package org.rmt2.api.handlers.admin.application;

/**
 * Application common message handler API constants
 * 
 * @author roy.terrell
 *
 */
public class ApplicationMessageHandlerConst {
    public static final String MESSAGE_FOUND = "Application data found!";
    public static final String MESSAGE_NOT_FOUND = "Application data not found!";
    public static final String MESSAGE_FETCH_ERROR = "Application fetch operation failed";

    public static final String MESSAGE_CREATE_SUCCESS = "Application was created successfully";
    public static final String MESSAGE_UPDATE_SUCCESS = "Application was modified successfully";
    public static final String MESSAGE_UPDATE_ERROR = "Application update operation failed";

    public static final String MESSAGE_DELETE_SUCCESS = "Application was deleted successfully";
    public static final String MESSAGE_DELETE_ERROR = "Application delete operation failed";

    public static final String MESSAGE_MISSING_APPLICATION_SECTION = "Application section is required for create/update operations";


    /**
     * 
     */
    public ApplicationMessageHandlerConst() {
    }

}

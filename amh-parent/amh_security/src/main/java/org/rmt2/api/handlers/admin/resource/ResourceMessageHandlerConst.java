package org.rmt2.api.handlers.admin.resource;

/**
 * Resource common message handler API constants
 * 
 * @author roy.terrell
 *
 */
public class ResourceMessageHandlerConst {
    public static final String MESSAGE_FOUND = "Resource data found!";
    public static final String MESSAGE_NOT_FOUND = "Resource data not found!";
    public static final String MESSAGE_FETCH_ERROR = "Resource fetch operation failed";

    public static final String MESSAGE_CREATE_SUCCESS = "Resource was created successfully";
    public static final String MESSAGE_UPDATE_SUCCESS = "Resource was modified successfully";
    public static final String MESSAGE_UPDATE_ERROR = "Resource update operation failed";

    public static final String MESSAGE_DELETE_SUCCESS = "Resource was deleted successfully";
    public static final String MESSAGE_DELETE_ERROR = "Resource delete operation failed";

    public static final String MESSAGE_MISSING_RESOURCE_SECTION = "Resource section is required for create/update/delete operations";
    public static final String MESSAGE_MISSING_RESOURCE_CRITERIA_SECTION = "Resource criteria section is required for query operations";


    /**
     * 
     */
    public ResourceMessageHandlerConst() {
    }

}

package org.rmt2.api.handlers.admin.resource.type;

/**
 * Resource Type common message handler API constants
 * 
 * @author roy.terrell
 *
 */
public class ResourceTypeMessageHandlerConst {
    public static final String MESSAGE_FOUND = "Resource Type data found!";
    public static final String MESSAGE_NOT_FOUND = "Resource Type data not found!";
    public static final String MESSAGE_FETCH_ERROR = "Resource Type fetch operation failed";

    public static final String MESSAGE_CREATE_SUCCESS = "Resource Type was created successfully";
    public static final String MESSAGE_UPDATE_SUCCESS = "Resource Type was modified successfully";
    public static final String MESSAGE_UPDATE_ERROR = "Resource Type update operation failed";

    public static final String MESSAGE_DELETE_SUCCESS = "Resource Type was deleted successfully";
    public static final String MESSAGE_DELETE_ERROR = "Resource Type delete operation failed";

    public static final String MESSAGE_MISSING_RESOURCE_TYPE_SECTION = "Resource Type section is required for create/update/delete operations";
    public static final String MESSAGE_MISSING_RESOURCE_TYPE_CRITERIA_SECTION = "Resource Type criteria section is required for query operations";


    /**
     * 
     */
    public ResourceTypeMessageHandlerConst() {
    }

}

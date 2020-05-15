package org.rmt2.api.handlers.admin.project;

/**
 * Project message handler API constants
 * 
 * @author appdev
 *
 */
public class ProjectMessageHandlerConst {
    public static final String MESSAGE_FOUND = "Project record(s) found";
    public static final String MESSAGE_NOT_FOUND = "Project data not found!";
    public static final String MESSAGE_FETCH_ERROR = "Failure to retrieve Project(s)";
    public static final String MESSAGE_NEW_PROJECT_UPDATE_SUCCESS = "Project was created successfully";
    public static final String MESSAGE_EXISTING_PROJECT_UPDATE_SUCCESS = "Project was modified successfully";
    public static final String MESSAGE_NEW_PROJECT_UPDATE_FAILED = "Error occurred creating new project";
    public static final String MESSAGE_EXISTING_PROJECT_UPDATE_FAILED = "Error occurred modifying project";
    public static final String MESSAGE_PROJECT_DELETE_SUCCESS = "Project(s) were deleted successfully";
    public static final String MESSAGE_PROJECT_DELETE_FAILED = "Error occurred deleting project(s)";
    public static final String VALIDATION_PROJECT_MISSING = "Update/Delete operations require one or more project(s)";

    /**
     * 
     */
    public ProjectMessageHandlerConst() {
    }

}

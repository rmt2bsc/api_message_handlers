package org.rmt2.api.handlers.admin.task;

/**
 * Task message handler API constants
 * 
 * @author appdev
 *
 */
public class TaskMessageHandlerConst {
    public static final String MESSAGE_FOUND = "Task record(s) found";
    public static final String MESSAGE_NOT_FOUND = "Task data not found!";
    public static final String MESSAGE_FETCH_ERROR = "Failure to retrieve Task(s)";
    public static final String MESSAGE_NEW_TASK_UPDATE_SUCCESS = "Task was created successfully";
    public static final String MESSAGE_EXISTING_TASK_UPDATE_SUCCESS = "Task was modified successfully";
    public static final String MESSAGE_NEW_TASK_UPDATE_FAILED = "Error occurred creating new Task";
    public static final String MESSAGE_EXISTING_TASK_UPDATE_FAILED = "Error occurred modifying Task";

    public static final String VALIDATION_TASK_MISSING = "Update/Delete operations require one and only one task";

    /**
     * 
     */
    public TaskMessageHandlerConst() {
    }

}

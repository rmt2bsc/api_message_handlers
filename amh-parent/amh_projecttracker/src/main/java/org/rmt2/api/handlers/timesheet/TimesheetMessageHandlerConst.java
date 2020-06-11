package org.rmt2.api.handlers.timesheet;

/**
 * Timesheet message handler API constants
 * 
 * @author appdev
 *
 */
public class TimesheetMessageHandlerConst {
    public static final String MESSAGE_FOUND = "Timesheet record(s) found";
    public static final String MESSAGE_NOT_FOUND = "Timesheet data not found!";
    public static final String MESSAGE_FETCH_ERROR = "Failure to retrieve Timesheet(s)";
    public static final String MESSAGE_UPDATE_NEW_SUCCESS = "Timesheet was created successfully";
    public static final String MESSAGE_UPDATE_EXISTING_SUCCESS = "Timesheet was updated successfully";
    public static final String MESSAGE_UPDATE_NEW_ERROR = "Error creating new Timesheet";
    public static final String MESSAGE_UPDATE_EXISTING_ERROR = "Error updating existing Timesheet";
    public static final String MESSAGE_DELETE_SUCCESS = "Timesheet deleted successfully";
    public static final String MESSAGE_DELETE_RECORD_NOT_FOUND = "Delete operation did not find any records for timesheet id, %s";
    public static final String MESSAGE_DELETE_ERROR = "Error deleting Timesheet";
    public static final String MESSAGE_SUBMIT_SUCCESS = "Timesheet submitted successfully";
    public static final String MESSAGE_SUBMIT_ERROR = "Error submitting Timesheet";
    public static final String MESSAGE_SUBMIT_RECORD_NOT_FOUND = "Submit operation did not find any records for timesheet id, %s";

    public static final String MESSAGE_POST_SUBMIT_SUCCESS = "Timesheet was %s";
    public static final String MESSAGE_POST_SUBMIT_ERROR = "Error %s Timesheet";
    public static final String MESSAGE_POST_SUBMIT_RECORD_NOT_FOUND = "Approve/Decline operation did not find any records for timesheet id, %s";

    public static final String VALIDATION_TIMESHEET_MISSING = "Update operation requires the existence of the Timesheet profile";
    public static final String VALIDATION_TIMESHEET_TOO_MANY = "Update operation requires one timesheet record only";

    /**
     * 
     */
    public TimesheetMessageHandlerConst() {
    }

}

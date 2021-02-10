package org.rmt2.api.handlers.employee;

/**
 * Employee/Project message handler API constants
 * 
 * @author appdev
 *
 */
public class EmployeeProjectMessageHandlerConst {
    public static final String MESSAGE_FOUND = "Employee/Project record(s) found";
    public static final String MESSAGE_NOT_FOUND = "Employee/Project data not found!";
    public static final String MESSAGE_FETCH_ERROR = "Failure to retrieve Employee/Project(s)";
    public static final String MESSAGE_UPDATE_NEW_SUCCESS = "Employee/Project was created successfully";
    public static final String MESSAGE_UPDATE_EXISTING_SUCCESS = "Employee/Project was updated successfully";
    public static final String MESSAGE_UPDATE_NEW_ERROR = "Error creating new employee/Project";
    public static final String MESSAGE_UPDATE_EXISTING_ERROR = "Error updating existing employee/Project";
    public static final String VALIDATION_EMPLOYEE_PROJECT_MISSING = "Update operation requires the existence of the Employee Project profile";
    public static final String VALIDATION_EMPLOYEE_PROJECT_TOO_MANY = "Update operation requires one profile record only";

    /**
     * 
     */
    public EmployeeProjectMessageHandlerConst() {
    }

}

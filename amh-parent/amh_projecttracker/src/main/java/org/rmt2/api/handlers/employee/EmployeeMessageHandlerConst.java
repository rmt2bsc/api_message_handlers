package org.rmt2.api.handlers.employee;

/**
 * Employee message handler API constants
 * 
 * @author appdev
 *
 */
public class EmployeeMessageHandlerConst {
    public static final String MESSAGE_FOUND = "Employee record(s) found";
    public static final String MESSAGE_NOT_FOUND = "Employee data not found!";
    public static final String MESSAGE_FETCH_ERROR = "Failure to retrieve Employee(s)";
    public static final String MESSAGE_UPDATE_NEW_SUCCESS = "Employee was created successfully";
    public static final String MESSAGE_UPDATE_EXISTING_SUCCESS = "Employee was updated successfully";
    public static final String MESSAGE_UPDATE_NEW_ERROR = "Error creating new employee";
    public static final String MESSAGE_UPDATE_EXISTING_ERROR = "Error updating existing employee";
    public static final String MESSAGE_CONTACT_UPDATE_ERROR = "A problem occurred applying updates to the employee's contact information in the address book API";

    /**
     * 
     */
    public EmployeeMessageHandlerConst() {
    }

}

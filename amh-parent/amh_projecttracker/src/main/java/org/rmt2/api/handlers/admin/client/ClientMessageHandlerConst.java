package org.rmt2.api.handlers.admin.client;

/**
 * Client message handler API constants
 * 
 * @author appdev
 *
 */
public class ClientMessageHandlerConst {
    public static final String MESSAGE_FOUND = "Client record(s) found";
    public static final String MESSAGE_NOT_FOUND = "Client data not found!";
    public static final String MESSAGE_FETCH_ERROR = "Failure to retrieve Client(s)";
    public static final String MESSAGE_DELETE_SUCCESSFUL = "Client record(s) were deleted successfully";
    public static final String MESSAGE_DELETE_RECORDS_NOT_FOUND = "Client data not found for delete operation";
    public static final String MESSAGE_DELETE_ERROR = "Failure to delete Client(s)";
    public static final String MESSAGE_CUSTOMER_IMPORTED = "The targeted customers were imported successfully into the Project Tracker system";
    public static final String MESSAGE_CUSTOMER_NOT_IMPORTED = "The targeted customers were not imported into the Project Tracker system";
    public static final String MESSAGE_CUSTOMER_IMPORT_ERROR = "An error occurred during the customer import operation for the Project Tracker system";
    public static final String MESSAGE_CUSTOMER_IMPORT_MISSING_CLIENT_STRUCTURE = "The customer import operation requires the client node structure to exists";
    
    
    /**
     * 
     */
    public ClientMessageHandlerConst() {
    }

}

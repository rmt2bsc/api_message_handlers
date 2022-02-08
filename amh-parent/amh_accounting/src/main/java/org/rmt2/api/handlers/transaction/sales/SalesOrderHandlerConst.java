package org.rmt2.api.handlers.transaction.sales;

/**
 * Sales order API message handler constants.
 * 
 * @author roy.terrell
 *
 */
public class SalesOrderHandlerConst {
    public static final String MSG_MISSING_SALESORDER_STRUCTURE = "Sales order structure is required for create sales order operation";
    public static final String MSG_MISSING_CUSTOMER_STRUCTURE = "Customer criteria structure is required";
    public static final String MSG_MISSING_XACT_STRUCTURE = "Transaction criteria structure is required for create sales order operation";
    public static final String MSG_MISSING_PRINT_PARAMETERS = "Sales Order print operation requires sales order id and customer id as selection criteria";
    public static final String MSG_SALESORDER_LIST_EMPTY = "Sales order list cannot be empty for create sales order operation";
    public static final String MSG_SALESORDER_LIST_CONTAINS_TOO_MANY = "Sales order list must contain only 1 entry for create sales order operation";
    public static final String MSG_REVERSE_SUCCESS = "Existing Sales order transaction, %s1, was reversed: %s2";
    public static final String MSG_CREATE_SUCCESS = "New sales order transaction was created: %s";
    public static final String MSG_UPDATE_SUCCESS = "%s sales order(s) were updated successfully";
    public static final String MSG_CREATE_AND_INVOICED_SUCCESS = "Sales order was created and invoiced successfully";
    public static final String MSG_INVOICED_SUCCESS = "Sales order was invoiced successfully";
    public static final String MSG_CREATE_INVOICED_PAYMENT_SUCCESS = "Created, invoiced, and received payment for sales order successfully";
    public static final String MSG_INVOICED_PAYMENT_SUCCESS = "Invoiced and received payment for sales order successfully";
    public static final String MSG_DELETE_SUCCESS = "%s sales order(s) were deleted successfully";
    public static final String MSG_CANCEL_SUCCESS = "%s sales order(s) were cancelled successfully";
    public static final String MSG_REFUND_SUCCESS = "%s sales order(s) were refunded successfully";
    public static final String MSG_GET_SUCCESS = "%s sales order(s) were found";
    public static final String MSG_GET_CUSTOMER_SPECIFIC_SUCCESS = "%s customer sales order(s) were found";
    public static final String MSG_PRINT_SUCCESS = "Sales order(s) printed successfully";
    public static final String MSG_CLOSE_SUCCESS = "%s sales order(s) were closed successfully";
    public static final String MSG_CREATE_FAILURE = "Failure to create sales order";
    public static final String MSG_UPDATE_FAILURE = "Failure to update sales order(s)";
    public static final String MSG_CREATE_AND_INVOICE_FAILURE = "Failure to create and invoice sales order";
    public static final String MSG_INVOICE_FAILURE = "Failure to invoice sales order";
    public static final String MSG_CREATE_INVOICE_PAYMENT_FAILURE = "Failure to create, invoice, receive payment for sales order";
    public static final String MSG_INVOICE_PAYMENT_FAILURE = "Failure to invoice and receive payment for sales order";
    public static final String MSG_DELETE_FAILURE = "Failure to delete sales order(s)";
    public static final String MSG_CANCEL_FAILURE = "Failure to cancel one or more sales orders";
    public static final String MSG_REFUND_FAILURE = "Failure to refund one or more sales orders";
    public static final String MSG_PRINT_FAILURE = "Failure to pring sales order(s)";
    public static final String MSG_GET_CUSTOMER_SPECIFIC_FAILURE = "Failure to query customer sales order(s)";
    public static final String MSG_SALESORDER_CLOSE_TOO_MANY_TRANSACTIONS = "Too many transactions for close sales order operation";
    public static final String MSG_MISSING_GENERAL_CRITERIA = "Sales order query request must contain a valid general criteria object";
    public static final String MSG_MISSING_SUBJECT_CRITERIA = "Selection criteria is required for Accounting Sales Order fetch operation";
    public static final String MSG_MISSING_TARGET_LEVEL = "Sales order fetch request must contain a target level value";
    public static final String MSG_TARGET_LEVEL_DETAILS_NOT_SUPPORTED = "Sales order details only target level is not supported";
    public static final String MSG_MISSING_SALESORDER_CRITERIA_STRUCTURE = "Sales order structure is required for the print sales order operation";
}

package org.rmt2.api.handlers.transaction.sales;

/**
 * Sales order API message handler constants.
 * 
 * @author roy.terrell
 *
 */
public class SalesOrderHandlerConst {
    public static final String MSG_DETAILS_NOT_SUPPORTED = "Transaction level \"DETAILS\" is not supported at this time";
    public static final String MSG_MISSING_TARGET_LEVEL = "Sales order transaction fetch request must contain a target level value";
    public static final String MSG_INCORRECT_TARGET_LEVEL = "Sales order transaction fetch request contains an invalid target level: %s";
    public static final String MSG_MISSING_PROFILE_DATA = "Sales order profile is required for create transaction operation";
    public static final String MSG_MISSING_SALESORDER_STRUCTURE = "Sales order structure is required for create sales order operation";
    public static final String MSG_MISSING_SALESORDER_LIST = "Sales order list is required for create sales order operation";
    public static final String MSG_SALESORDER_LIST_EMPTY = "Sales order list cannot be empty for create sales order operation";
    public static final String MSG_SALESORDER_LIST_CONTAINS_TOO_MANY = "Sales order list must contain only 1 entry for create sales order operation";
    public static final String MSG_MISSING_TRANSACTION_SECTION = "Sales order transaction section is missing from the transaction profile";
    public static final String MSG_REVERSE_SUCCESS = "Existing Sales order transaction, %s1, was reversed: %s2";

}

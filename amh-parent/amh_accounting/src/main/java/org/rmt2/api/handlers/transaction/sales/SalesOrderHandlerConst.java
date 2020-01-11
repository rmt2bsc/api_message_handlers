package org.rmt2.api.handlers.transaction.sales;

/**
 * Sales order API message handler constants.
 * 
 * @author roy.terrell
 *
 */
public class SalesOrderHandlerConst {
    public static final String MSG_MISSING_SALESORDER_STRUCTURE = "Sales order structure is required for create sales order operation";
    public static final String MSG_MISSING_SALESORDER_LIST = "Sales order list is required for create sales order operation";
    public static final String MSG_SALESORDER_LIST_EMPTY = "Sales order list cannot be empty for create sales order operation";
    public static final String MSG_SALESORDER_LIST_CONTAINS_TOO_MANY = "Sales order list must contain only 1 entry for create sales order operation";

}

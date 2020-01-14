package org.rmt2.api.handlers.transaction.sales;

import org.ApiMessageHandlerConst;
import org.rmt2.jaxb.AccountingTransactionRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Utility class for managing sales orders that are targeted for creation.
 * 
 * @author roy.terrell
 *
 */
public class CreateSalesOrderUtil {

    /**
     * Validates the accounting transaction request in regards to createing
     * sales orders.
     * 
     * @param req
     *            instance of {@link AccountingTransactionRequest}
     * @throws InvalidDataException
     *             when profile data is missing, sales order structure is
     *             missing, sales order list is empty, or more that one sale
     *             order element exists in the sales order structure.
     */
    public static final void doBaseValidation(AccountingTransactionRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidRequestException(ApiMessageHandlerConst.MSG_MISSING_PROFILE_DATA);
        }

        try {
            Verifier.verifyNotNull(req.getProfile().getSalesOrders());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_SALESORDER_STRUCTURE);
        }

        try {
            Verifier.verifyNotEmpty(req.getProfile().getSalesOrders().getSalesOrder());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_SALESORDER_LIST_EMPTY);
        }

        try {
            Verifier.verifyTrue(req.getProfile().getSalesOrders().getSalesOrder().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_SALESORDER_LIST_CONTAINS_TOO_MANY);
        }
    }
}

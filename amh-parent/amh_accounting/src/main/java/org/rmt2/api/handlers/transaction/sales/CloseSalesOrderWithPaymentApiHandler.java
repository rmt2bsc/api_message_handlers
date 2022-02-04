package org.rmt2.api.handlers.transaction.sales;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.SalesOrderDto;
import org.dto.XactDto;
import org.modules.transaction.sales.SalesApi;
import org.modules.transaction.sales.SalesApiFactory;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.api.handlers.transaction.XactApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.SalesOrderType;
import org.rmt2.jaxb.XactType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes messages pertaining to the closing a Sales Order with
 * Payment in the Accounting API.
 * 
 * @author rterrell
 *
 */
public class CloseSalesOrderWithPaymentApiHandler extends SalesOrderApiHandler {
    private static final Logger logger = Logger.getLogger(CloseSalesOrderWithPaymentApiHandler.class);

    /**
     * 
     */
    public CloseSalesOrderWithPaymentApiHandler() {
        super();
        logger.info(CloseSalesOrderWithPaymentApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Processes requests pertaining to the closing of a sales order with
     * payment transaction.
     * 
     * @param command
     *            The name of the operation.
     * @param payload
     *            The XML message that is to be processed.
     * @return MessageHandlerResults
     * @throws MessageHandlerCommandException
     *             <i>payload</i> is deemed invalid.
     */
    @Override
    public MessageHandlerResults processMessage(String command, Serializable payload) throws MessageHandlerCommandException {
        MessageHandlerResults r = super.processMessage(command, payload);

        if (r != null) {
            String errMsg = ERROR_MSG_TRANS_NOT_FOUND + command;
            if (r.getErrorMsg() != null && r.getErrorMsg().equalsIgnoreCase(errMsg)) {
                // Ancestor was not able to find command. Continue processing.
            }
            else {
                // This means an error occurred.
                return r;
            }
        }
        switch (command) {
            case ApiTransactionCodes.ACCOUNTING_SALESORDER_CLOSE_WITH_PAYMENT:
                r = this.doOperation(this.requestObj);
                break;

            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE, MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to close a sales order
     * accompanied with a payment.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<SalesOrderType> reqSalesOrders = req.getProfile().getSalesOrders().getSalesOrder();
        XactType reqXact = req.getProfile().getTransactions().getTransaction().get(0);
        List<SalesOrderType> tranRresults = null;

        List<SalesOrderDto> soDtoList = new ArrayList<>();
        
        rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
        rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
        rs.setRecordCount(0);
        
        // IS-71: Changed the scope to local to prevent memory leaks as a result
        // of sharing the API instance that was once contained in ancestor
        // class, SalesORderApiHandler.
        SalesApi api = SalesApiFactory.createApi();
        try {
            for (SalesOrderType item : reqSalesOrders) {
                SalesOrderDto salesOrderDto = SalesOrderJaxbDtoFactory.createSalesOrderHeaderDtoInstance(item);
                soDtoList.add(salesOrderDto);
            }
            XactDto xactDto = TransactionJaxbDtoFactory.createXactDtoInstance(reqXact);

            // Call API method to close sales order
            api.beginTrans();
            int rc = api.closeSalesOrderForPayment(soDtoList, xactDto);

            // Assign messages to the reply status that apply to the outcome of
            // this operation
            String msg = RMT2String.replace(SalesOrderHandlerConst.MSG_CLOSE_SUCCESS, String.valueOf(rc), "%s");
            rs.setMessage(msg);
            rs.setRecordCount(1);
            this.responseObj.setHeader(req.getHeader());
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(SalesOrderHandlerConst.MSG_CREATE_FAILURE);
            rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            tranRresults = reqSalesOrders;
            api.close();
        }

        String xml = this.buildResponse(tranRresults, rs);
        results.setPayload(xml);
        return results;
    }

    /**
     * @see org.rmt2.api.handlers.transaction.XactApiHandler#validateRequest(org.
     *      rmt2.jaxb.AccountingTransactionRequest)
     */
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        super.validateRequest(req);
        SalesOrderRequestUtil.doBaseValidationForUpdates(req);

        // Must include transaction section.
        try {
            Verifier.verifyNotNull(req.getProfile().getTransactions());
        } catch (VerifyException e) {
            throw new InvalidRequestException(XactApiHandler.MSG_MISSING_TRANSACTION_SECTION, e);
        }

        // Transaction profile must contain one and only one transaction
        try {
            Verifier.verifyTrue(req.getProfile().getTransactions().getTransaction().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_SALESORDER_CLOSE_TOO_MANY_TRANSACTIONS, e);
        }
    }
}

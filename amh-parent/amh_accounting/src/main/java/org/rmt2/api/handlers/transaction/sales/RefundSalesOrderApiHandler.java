package org.rmt2.api.handlers.transaction.sales;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.modules.transaction.sales.SalesApi;
import org.modules.transaction.sales.SalesApiFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.SalesOrderType;

import com.InvalidDataException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;

/**
 * Handles and routes messages pertaining to refunding existing Sales Order in
 * the Accounting API.
 * 
 * @author rterrell
 *
 */
public class RefundSalesOrderApiHandler extends SalesOrderApiHandler {
    private static final Logger logger = Logger.getLogger(RefundSalesOrderApiHandler.class);

    /**
     * 
     */
    public RefundSalesOrderApiHandler() {
        super();
        logger.info(RefundSalesOrderApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Processes requests pertaining to the refunding of a sales order
     * transaction.
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
            case ApiTransactionCodes.ACCOUNTING_SALESORDER_REFUND:
                r = this.doOperation(this.requestObj);
                break;

            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE, MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to refund a sales order
     * accounting object.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<SalesOrderType> reqSalesOrder = req.getProfile().getSalesOrders().getSalesOrder();
        List<SalesOrderType> tranRresults = new ArrayList<>();

        // IS-71: Changed the scope to local to prevent memory leaks as a result
        // of sharing the API instance that was once contained in ancestor
        // class, SalesORderApiHandler.
        SalesApi api = SalesApiFactory.createApi();

        // UI-37: Added for capturing the update user id
        api.setApiUser(this.userId);

        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            int refundTotal = 0;
            api.beginTrans();
            for (SalesOrderType so : reqSalesOrder) {
                api.refundSalesOrder(so.getSalesOrderId().intValue());
                refundTotal++;
            }

            // Assign messages to the reply status that apply to the outcome of
            // this operation
            String msg = RMT2String.replace(SalesOrderHandlerConst.MSG_REFUND_SUCCESS, String.valueOf(refundTotal), "%s");
            rs.setMessage(msg);
            rs.setRecordCount(refundTotal);
            this.responseObj.setHeader(req.getHeader());
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(SalesOrderHandlerConst.MSG_REFUND_FAILURE);
            rs.setExtMessage(e.getMessage());
            rs.setRecordCount(0);
            api.rollbackTrans();
        } finally {
            tranRresults.addAll(reqSalesOrder);
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
    }
}

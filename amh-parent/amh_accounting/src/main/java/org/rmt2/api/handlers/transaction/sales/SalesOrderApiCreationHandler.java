package org.rmt2.api.handlers.transaction.sales;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.SalesOrderDto;
import org.dto.SalesOrderItemDto;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.SalesOrderItemType;
import org.rmt2.jaxb.SalesOrderType;

import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;

/**
 * Handles and routes sales order creation messages pertaining to the Sales Order Accounting API.
 * 
 * @author rterrell
 *
 */
public class SalesOrderApiCreationHandler extends SalesOrderApiHandler {
    private static final Logger logger = Logger.getLogger(SalesOrderApiCreationHandler.class);
    public static final String MSG_DATA_FOUND = "Sales order record(s) found";
    public static final String MSG_DATA_NOT_FOUND = "Sales order data not found!";
    public static final String MSG_FAILURE = "Failure to retrieve Sales order transaction(s)";
    public static final String MSG_CREATE_FAILURE = "Failure to create Sales order transaction(s)";
    public static final String MSG_CREATE_SUCCESS = "New Sales order transaction was created: %s";
    public static final String MSG_MISSING_CREDITOR_PROFILE_DATA = "Customer profile is required when creating a Sales order for a customer";

    /**
     * 
     */
    public SalesOrderApiCreationHandler() {
        super();
        logger.info(SalesOrderApiCreationHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Processes requests pertaining to fetching and creating of sales order
     * transactions.
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
            case ApiTransactionCodes.ACCOUNTING_SALESORDER_CREATE:
                r = this.create(this.requestObj);
                break;

            case ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_CREATE:
                r = this.create(this.requestObj);
                break;

            case ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_PAYMENT_CREATE:
                r = this.create(this.requestObj);
                break;
                
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE, MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to create a sales order
     * accounting transaction object.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults create(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        SalesOrderType reqSalesOrder = req.getProfile().getSalesOrders().getSalesOrder().get(0);
        List<SalesOrderType> tranRresults = new ArrayList<>();

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            SalesOrderDto xactDto = SalesOrderJaxbDtoFactory.createSalesOrderHeaderDtoInstance(reqSalesOrder);
            List<SalesOrderItemDto> itemsDtoList = SalesOrderJaxbDtoFactory.createSalesOrderItemsDtoInstance(reqSalesOrder.getSalesOrderItems()
                    .getSalesOrderItem());

            int newXactId = api.updateSalesOrder(xactDto, itemsDtoList);

            // Update XML with new sales order id
            reqSalesOrder.setSalesOrderId(BigInteger.valueOf(newXactId));
            for (SalesOrderItemType item : reqSalesOrder.getSalesOrderItems().getSalesOrderItem()) {
                item.setSalesOrderId(BigInteger.valueOf(newXactId));
            }
            String msg = RMT2String.replace(MSG_CREATE_SUCCESS, String.valueOf(reqSalesOrder.getSalesOrderId()), "%s");
            rs.setMessage(msg);
            rs.setRecordCount(1);

            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(SalesOrderApiCreationHandler.MSG_CREATE_FAILURE);
            rs.setExtMessage(e.getMessage());
        } finally {
            tranRresults.add(reqSalesOrder);
            this.api.close();
        }

        String xml = this.buildResponse(tranRresults, rs);
        results.setPayload(xml);
        return results;
    }
}

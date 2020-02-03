package org.rmt2.api.handlers.transaction.sales;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.SalesOrderDto;
import org.dto.SalesOrderItemDto;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.SalesOrderType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes messages pertaining to creating a new or updating an
 * existing sales order, immediate invoicing, and the immediate receipt of
 * payment for a Sales Order in the Accounting API.
 * 
 * @author rterrell
 *
 */
public class UpdateSalesOrderAutoInvoiceCashReceiptApiHandler extends SalesOrderApiHandler {
    private static final Logger logger = Logger.getLogger(UpdateSalesOrderAutoInvoiceCashReceiptApiHandler.class);

    /**
     * 
     */
    public UpdateSalesOrderAutoInvoiceCashReceiptApiHandler() {
        super();
        logger.info(UpdateSalesOrderAutoInvoiceCashReceiptApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Processes requests pertaining to creation, invoicing, and cash receipt of
     * a sales order transaction.
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
            case ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_PAYMENT_CREATE:
            case ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_PAYMENT_UPDATE:
                r = this.doOperation(this.requestObj);
                break;

            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE, MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to create a sales order
     * and to immediately invoice and apply a cash receipt to the sales order.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        SalesOrderType reqSalesOrder = req.getProfile().getSalesOrders().getSalesOrder().get(0);
        List<SalesOrderType> tranRresults = new ArrayList<>();
        boolean newSalesOrder = false;
        try {
            SalesOrderDto salesOrderDto = SalesOrderJaxbDtoFactory.createSalesOrderHeaderDtoInstance(reqSalesOrder);
            List<SalesOrderItemDto> itemsDtoList = SalesOrderJaxbDtoFactory.createSalesOrderItemsDtoInstance(reqSalesOrder.getSalesOrderItems()
                    .getSalesOrderItem());
            newSalesOrder = (salesOrderDto.getSalesOrderId() == 0);
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);

            // Create sales order
            int updateReturnCode = SalesOrderRequestUtil.updateSalesOrder(this.api, salesOrderDto, itemsDtoList, reqSalesOrder);

            SalesOrderRequestUtil.invoiceSalesOrder(api, salesOrderDto, itemsDtoList, true, reqSalesOrder);

            // Update the request with current sales order status information
            SalesOrderRequestUtil.assignCurrentStatus(this.api, reqSalesOrder);

            // Assign messages to the reply status that apply to the outcome of
            // this operation
            String msg = RMT2String.replace(newSalesOrder ? SalesOrderHandlerConst.MSG_CREATE_SUCCESS
                    : SalesOrderHandlerConst.MSG_UPDATE_SUCCESS, String.valueOf(updateReturnCode), "%s");
            rs.setMessage(msg);
            rs.setRecordCount(1);

            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
            this.api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            if (newSalesOrder) {
                rs.setMessage(SalesOrderHandlerConst.MSG_CREATE_FAILURE);
            }
            else {
                rs.setMessage(SalesOrderHandlerConst.MSG_UPDATE_FAILURE);
            }
            rs.setExtMessage(e.getMessage());
            this.api.rollbackTrans();
        } finally {
            tranRresults.add(reqSalesOrder);
            this.api.close();
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

        try {
            Verifier.verifyTrue(req.getProfile().getSalesOrders().getSalesOrder().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_SALESORDER_LIST_CONTAINS_TOO_MANY);
        }
    }
}

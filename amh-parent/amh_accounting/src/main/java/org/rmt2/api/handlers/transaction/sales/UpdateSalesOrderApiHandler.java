package org.rmt2.api.handlers.transaction.sales;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.SalesOrderDto;
import org.dto.SalesOrderItemDto;
import org.dto.SalesOrderStatusDto;
import org.dto.SalesOrderStatusHistDto;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.SalesOrderStatusType;
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
 * Handles and routes messages pertaining to creating new or updating an
 * existing Sales Order in the Accounting API.
 * 
 * @author rterrell
 *
 */
public class UpdateSalesOrderApiHandler extends SalesOrderApiHandler {
    private static final Logger logger = Logger.getLogger(UpdateSalesOrderApiHandler.class);

    /**
     * 
     */
    public UpdateSalesOrderApiHandler() {
        super();
        logger.info(UpdateSalesOrderApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Processes requests pertaining to the creation of a sales order
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
            case ApiTransactionCodes.ACCOUNTING_SALESORDER_CREATE:
            case ApiTransactionCodes.ACCOUNTING_SALESORDER_UPDATE:
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
     * accounting transaction object.
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
        SalesOrderType respSalesOrder = this.jaxbObjFactory.createSalesOrderType();
        SalesOrderStatusType respSOST = this.jaxbObjFactory.createSalesOrderStatusType();
        boolean newSalesOrder = false;
        try {
            SalesOrderDto salesOrderDto = SalesOrderJaxbDtoFactory.createSalesOrderHeaderDtoInstance(reqSalesOrder);
            List<SalesOrderItemDto> itemsDtoList = SalesOrderJaxbDtoFactory
                    .createSalesOrderItemsDtoInstance(reqSalesOrder.getSalesOrderItems()
                    .getSalesOrderItem());
            newSalesOrder = (salesOrderDto.getSalesOrderId() == 0);
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);

            // Create sales order
            api.beginTrans();
            int updateReturnCode = SalesOrderRequestUtil.updateSalesOrder(this.api, salesOrderDto, itemsDtoList, reqSalesOrder);

            // Update the request with current sales order status information
            SalesOrderRequestUtil.assignCurrentStatus(this.api, reqSalesOrder);

            // Verify transaction
            int verifySalesOrderId = (newSalesOrder ? updateReturnCode : salesOrderDto.getSalesOrderId());
            SalesOrderDto so = this.api.getSalesOrder(verifySalesOrderId);
            SalesOrderStatusHistDto statusHistDto = this.api.getCurrentStatus(verifySalesOrderId);
            SalesOrderStatusDto statusDto = this.api.getStatus(statusHistDto.getSoStatusId());
            if (so != null) {
                respSalesOrder.setSalesOrderId(BigInteger.valueOf(so.getSalesOrderId()));
                respSalesOrder.setOrderTotal(BigDecimal.valueOf(so.getOrderTotal()));
                respSalesOrder.setInvoiced(so.isInvoiced());
                respSOST.setDescription(statusDto.getSoStatusDescription());
                respSOST.setStatusId(BigInteger.valueOf(statusHistDto.getSoStatusId()));
                respSalesOrder.setStatus(respSOST);
            }

            // Assign messages to the reply status that apply to the outcome of
            // this operation
            String msg = RMT2String.replace(newSalesOrder ? SalesOrderHandlerConst.MSG_CREATE_SUCCESS
                    : SalesOrderHandlerConst.MSG_UPDATE_SUCCESS, String.valueOf(updateReturnCode), "%s");
            rs.setMessage(msg);
            rs.setRecordCount(1);

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
            rs.setRecordCount(0);
            rs.setExtMessage(e.getMessage());
            this.api.rollbackTrans();
        } finally {
            tranRresults.add(respSalesOrder);
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

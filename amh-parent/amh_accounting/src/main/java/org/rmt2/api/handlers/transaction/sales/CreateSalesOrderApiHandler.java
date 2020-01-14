package org.rmt2.api.handlers.transaction.sales;

import java.io.Serializable;
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
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.SalesOrderItemType;
import org.rmt2.jaxb.SalesOrderStatusType;
import org.rmt2.jaxb.SalesOrderType;

import com.InvalidDataException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;

/**
 * Handles and routes messages pertaining to the creation of a Sales Order in
 * the Accounting API.
 * 
 * @author rterrell
 *
 */
public class CreateSalesOrderApiHandler extends SalesOrderApiHandler {
    private static final Logger logger = Logger.getLogger(CreateSalesOrderApiHandler.class);

    /**
     * 
     */
    public CreateSalesOrderApiHandler() {
        super();
        logger.info(CreateSalesOrderApiHandler.class.getName() + " was instantiated successfully");
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
            SalesOrderStatusHistDto statusHist = api.getCurrentStatus(newXactId);
            SalesOrderStatusDto status = api.getStatus(statusHist.getSoStatusId());

            // Update XML with new sales order id
            reqSalesOrder.setSalesOrderId(BigInteger.valueOf(newXactId));

            // Update XML with current sales order status
            ObjectFactory fact = new ObjectFactory();
            SalesOrderStatusType salesOrdStatusType = fact.createSalesOrderStatusType();
            salesOrdStatusType.setStatusId(BigInteger.valueOf(status.getSoStatusId()));
            salesOrdStatusType.setDescription(status.getSoStatusDescription());
            reqSalesOrder.setStatus(salesOrdStatusType);

            // Ensure that each sales order item is associated with the sales
            // order.
            for (SalesOrderItemType item : reqSalesOrder.getSalesOrderItems().getSalesOrderItem()) {
                item.setSalesOrderId(BigInteger.valueOf(newXactId));
            }
            String msg = RMT2String.replace(SalesOrderHandlerConst.MSG_CREATE_SUCCESS, String.valueOf(reqSalesOrder.getSalesOrderId()), "%s");
            rs.setMessage(msg);
            rs.setRecordCount(1);

            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(SalesOrderHandlerConst.MSG_CREATE_FAILURE);
            rs.setExtMessage(e.getMessage());
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
        CreateSalesOrderUtil.doBaseValidation(req);
    }
}

package org.rmt2.api.handlers.transaction.sales;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.SalesInvoiceDto;
import org.dto.SalesOrderDto;
import org.dto.SalesOrderItemDto;
import org.dto.SalesOrderStatusDto;
import org.dto.SalesOrderStatusHistDto;
import org.rmt2.api.handlers.transaction.receipts.CashReceiptsRequestUtil;
import org.rmt2.api.handlers.transaction.receipts.PaymentEmailConfirmationException;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.SalesInvoiceType;
import org.rmt2.jaxb.SalesOrderStatusType;
import org.rmt2.jaxb.SalesOrderType;
import org.rmt2.jaxb.XactType;
import org.rmt2.util.accounting.transaction.XactTypeBuilder;
import org.rmt2.util.accounting.transaction.sales.SalesInvoiceTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
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
        SalesOrderType respSalesOrder = this.jaxbObjFactory.createSalesOrderType();
        SalesOrderStatusType respSOST = this.jaxbObjFactory.createSalesOrderStatusType();
        boolean newSalesOrder = false;
        int xactId = 0;
        int salesOrderId = 0;
        int customerId = 0;

        try {
            SalesOrderDto salesOrderDto = SalesOrderJaxbDtoFactory.createSalesOrderHeaderDtoInstance(reqSalesOrder);
            List<SalesOrderItemDto> itemsDtoList = SalesOrderJaxbDtoFactory.createSalesOrderItemsDtoInstance(reqSalesOrder.getSalesOrderItems()
                    .getSalesOrderItem());
            newSalesOrder = (salesOrderDto.getSalesOrderId() == 0);
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);

            // Create sales order
            SalesOrderRequestUtil.updateSalesOrder(this.api, salesOrderDto, itemsDtoList, reqSalesOrder);
            salesOrderId = salesOrderDto.getSalesOrderId();
            customerId = salesOrderDto.getCustomerId();

            // Invoice sales order which should produce a new transaction
            xactId = SalesOrderRequestUtil.invoiceSalesOrder(api, salesOrderDto, itemsDtoList, true, reqSalesOrder);

            // Verify transaction
            SalesInvoiceDto soiDto = this.api.getInvoice(salesOrderDto.getSalesOrderId());
            SalesOrderStatusHistDto statusHistDto = this.api.getCurrentStatus(salesOrderDto.getSalesOrderId());
            SalesOrderStatusDto statusDto = this.api.getStatus(statusHistDto.getSoStatusId());
            if (soiDto != null) {
                respSalesOrder.setSalesOrderId(BigInteger.valueOf(soiDto.getSalesOrderId()));
                respSalesOrder.setOrderTotal(BigDecimal.valueOf(soiDto.getOrderTotal()));
                respSalesOrder.setInvoiced(soiDto.isInvoiced());
                XactType xt = XactTypeBuilder.Builder.create()
                        .withXactId(xactId)
                        .build();
                SalesInvoiceType sit = SalesInvoiceTypeBuilder.Builder.create()
                        .withInvoiceNo(soiDto.getInvoiceNo())
                        .withTransaction(xt)
                        .build();
                respSalesOrder.setInvoiceDetails(sit);
                respSOST.setDescription(statusDto.getSoStatusDescription());
                respSalesOrder.setStatus(respSOST);
            }

            // Assign messages to the reply status that apply to the outcome of
            // this operation
            String msg = (newSalesOrder ? SalesOrderHandlerConst.MSG_CREATE_INVOICED_PAYMENT_SUCCESS
                    : SalesOrderHandlerConst.MSG_INVOICED_PAYMENT_SUCCESS);
            rs.setMessage(msg);
            rs.setRecordCount(1);

            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
            this.api.commitTrans();

            // Send Email Confirmation
            try {
                CashReceiptsRequestUtil util = new CashReceiptsRequestUtil();
                util.emailPaymentConfirmation(customerId, salesOrderId, xactId);
            } catch (PaymentEmailConfirmationException e) {
                logger.error(e);
            }
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            if (newSalesOrder) {
                rs.setMessage(SalesOrderHandlerConst.MSG_CREATE_INVOICE_PAYMENT_FAILURE);
            }
            else {
                rs.setMessage(SalesOrderHandlerConst.MSG_INVOICE_PAYMENT_FAILURE);
            }
            rs.setExtMessage(e.getMessage());
            this.api.rollbackTrans();
            rs.setRecordCount(0);
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

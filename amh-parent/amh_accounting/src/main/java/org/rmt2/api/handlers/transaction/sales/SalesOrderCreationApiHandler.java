package org.rmt2.api.handlers.transaction.sales;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.SalesOrderDto;
import org.dto.SalesOrderItemDto;
import org.dto.XactCreditChargeDto;
import org.dto.XactCustomCriteriaDto;
import org.dto.XactTypeItemActivityDto;
import org.modules.transaction.purchases.creditor.CreditorPurchasesApiException;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.CreditorType;
import org.rmt2.jaxb.SalesOrderItemType;
import org.rmt2.jaxb.SalesOrderType;
import org.rmt2.jaxb.XactType;
import org.rmt2.util.accounting.subsidiary.CreditorTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes sales order creation messages pertaining to the Sales Order Accounting API.
 * 
 * @author rterrell
 *
 */
public class SalesOrderCreationApiHandler extends SalesOrderApiHandler {
    private static final Logger logger = Logger.getLogger(SalesOrderCreationApiHandler.class);
    public static final String MSG_DATA_FOUND = "Sales order record(s) found";
    public static final String MSG_DATA_NOT_FOUND = "Sales order data not found!";
    public static final String MSG_FAILURE = "Failure to retrieve Sales order transaction(s)";
    public static final String MSG_CREATE_FAILURE = "Failure to create Sales order transaction(s)";
    public static final String MSG_CREATE_SUCCESS = "New Sales order transaction was created: %s";
    public static final String MSG_MISSING_CREDITOR_PROFILE_DATA = "Customer profile is required when creating a Sales order for a customer";

   

    /**
     * 
     */
    public SalesOrderCreationApiHandler() {
        super();
        logger.info(SalesOrderCreationApiHandler.class.getName() + " was instantiated successfully");
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

            case ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_CREATE:
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
            rs.setMessage(SalesOrderCreationApiHandler.MSG_CREATE_FAILURE);
            rs.setExtMessage(e.getMessage());
        } finally {
            tranRresults.add(reqSalesOrder);
            this.api.close();
        }

        String xml = this.buildResponse(tranRresults, rs);
        results.setPayload(xml);
        return results;
    }

    // /**
    // * Handler for invoking the appropriate API in order to fetch one or more
    // * creditor purchases transaction objects.
    // * <p>
    // * Currently, the target level, <i>DETAILS</i>, is not supported.
    // *
    // * @param reqS
    // * an instance of {@link AccountingTransactionRequest}
    // * @return an instance of {@link MessageHandlerResults}
    // */
    // @Override
    // protected MessageHandlerResults fetch(AccountingTransactionRequest req) {
    // MessageHandlerResults results = new MessageHandlerResults();
    // MessageHandlerCommonReplyStatus rs = new
    // MessageHandlerCommonReplyStatus();
    // List<XactType> queryDtoResults = null;
    //
    // try {
    // // Set reply status
    // rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
    // XactCreditChargeDto criteriaDto =
    // CreditorPurchasesJaxbDtoFactory.createCreditorPurchasesDtoCriteriaInstance(req.getCriteria()
    // .getXactCriteria().getBasicCriteria());
    // XactCustomCriteriaDto customCriteriaDto =
    // TransactionJaxbDtoFactory.createCustomXactDtoCriteriaInstance(req.getCriteria()
    // .getXactCriteria().getCustomCriteria());
    //
    // this.targetLevel =
    // req.getCriteria().getXactCriteria().getTargetLevel().name().toUpperCase();
    // switch (this.targetLevel) {
    // case TARGET_LEVEL_HEADER:
    // case TARGET_LEVEL_FULL:
    // List<XactCreditChargeDto> dtoList = this.api.get(criteriaDto,
    // customCriteriaDto);
    // if (dtoList == null) {
    // rs.setMessage(SalesOrderCreationApiHandler.MSG_DATA_NOT_FOUND);
    // rs.setRecordCount(0);
    // }
    // else {
    // queryDtoResults = this.buildJaxbTransaction(dtoList, customCriteriaDto);
    // rs.setMessage(SalesOrderCreationApiHandler.MSG_DATA_FOUND);
    // rs.setRecordCount(dtoList.size());
    // }
    // break;
    //
    // default:
    // String msg = RMT2String.replace(MSG_INCORRECT_TARGET_LEVEL, targetLevel,
    // "%s");
    // throw new RMT2Exception(msg);
    // }
    //
    // rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
    // this.responseObj.setHeader(req.getHeader());
    // } catch (Exception e) {
    // logger.error("Error occurred during API Message Handler operation, " +
    // this.command, e);
    // rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
    // rs.setMessage(SalesOrderCreationApiHandler.MSG_FAILURE);
    // rs.setExtMessage(e.getMessage());
    // } finally {
    // this.api.close();
    // }
    //
    // String xml = this.buildResponse(queryDtoResults, rs);
    // results.setPayload(xml);
    // return results;
    // }

    /**
     * @see org.rmt2.api.handlers.transaction.XactApiHandler#validateRequest(org.
     *      rmt2.jaxb.AccountingTransactionRequest)
     */
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        super.validateRequest(req);

        // Perform common validations here...
        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_PROFILE_DATA);
        }

        try {
            Verifier.verifyNotNull(req.getProfile().getSalesOrders());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_SALESORDER_STRUCTURE);
        }

        try {
            Verifier.verifyNotNull(req.getProfile().getSalesOrders().getSalesOrder());
        } catch (VerifyException e) {
            throw new InvalidRequestException(SalesOrderHandlerConst.MSG_MISSING_SALESORDER_LIST);
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

        // Validate request for specfic operations
        switch (this.command) {
            case ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_CREATE:
                // Must contain flag that indicates what level of the
                // transaction object to populate with data
//                this.validateSearchRequest(req);
                break;

            case ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_PAYMENT_CREATE:
                // Transaction profile must exist
//                this.validateUpdateRequest(req);
                break;

            default:
                break;
        }
    }

    // /**
    // * Verifies that the creditor profile exists for the create creditor cash
    // * disbursment transaction.
    // *
    // * @param req
    // * @throws InvalidDataException
    // */
    // @Override
    // protected void validateUpdateRequest(AccountingTransactionRequest req)
    // throws InvalidDataException {
    // super.validateUpdateRequest(req);
    //
    // // Verify that creditor profile exist
    // if
    // (ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_CREATE.equalsIgnoreCase(this.command))
    // {
    // try {
    // Verifier.verifyNotNull(req.getProfile().getTransactions().getTransaction().get(0).getCreditor());
    // } catch (VerifyException e) {
    // throw new
    // InvalidRequestException(SalesOrderCreationApiHandler.MSG_MISSING_CREDITOR_PROFILE_DATA,
    // e);
    // }
    // }
    // }

    /**
     * Builds a List of XactType objects from a List of XactCreditChargeDto
     * objects.
     * 
     * @param results
     *            List<{@link XactCreditChargeDto}>
     * @param customCriteriaDto
     *            custom relational criteria (optional)
     * @return List<{@link XactType}>
     */
    private List<XactType> buildJaxbTransaction(List<XactCreditChargeDto> results, XactCustomCriteriaDto customCriteriaDto) {
        List<XactType> list = new ArrayList<>();

        for (XactCreditChargeDto item : results) {
            List<XactTypeItemActivityDto> xactItems = null;

            // retrieve line items if requested
            if (this.targetLevel.equals(TARGET_LEVEL_FULL)) {
                try {
                    xactItems = this.api.getItems(item.getXactId());
                } catch (CreditorPurchasesApiException e) {
                    logger.error("Unable to fetch line items for transaction id, " + item.getXactId());
                }
            }

            XactType jaxbObj = TransactionJaxbDtoFactory.createXactJaxbInstance(item, 0, xactItems);

            // Build and associate Creditor object
            CreditorType creditor = CreditorTypeBuilder.Builder.create().withCreditorId(item.getCreditorId()).withAccountNo(item.getAccountNumber())
                    .build();
            jaxbObj.setCreditor(creditor);
            list.add(jaxbObj);
        }
        return list;
    }

//    /*
//     * (non-Javadoc)
//     * 
//     * @see
//     * org.rmt2.api.handlers.transaction.XactApiHandler#buildResponse(java.util
//     * .List, com.api.messaging.handler.MessageHandlerCommonReplyStatus)
//     */
//    @Override
//    protected String buildResponse(List<SalesOrderType> payload, MessageHandlerCommonReplyStatus replyStatus) {
//        return super.buildResponse(payload, replyStatus);
//    }

}

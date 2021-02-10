package org.rmt2.api.handlers.transaction.purchases;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.XactCreditChargeDto;
import org.dto.XactCustomCriteriaDto;
import org.dto.XactTypeItemActivityDto;
import org.modules.transaction.purchases.creditor.CreditorPurchasesApi;
import org.modules.transaction.purchases.creditor.CreditorPurchasesApiException;
import org.modules.transaction.purchases.creditor.CreditorPurchasesApiFactory;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.api.handlers.transaction.XactApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.CreditorType;
import org.rmt2.jaxb.XactType;
import org.rmt2.util.accounting.subsidiary.CreditorTypeBuilder;

import com.InvalidDataException;
import com.RMT2Exception;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;

/**
 * Handles and routes Accounting API messages pertaining to the query of
 * Creditor Purchases transactions.
 * 
 * @author rterrell
 *
 */
public class QueryCreditorPurchasesApiHandler extends XactApiHandler {
    private static final Logger logger = Logger.getLogger(QueryCreditorPurchasesApiHandler.class);
    public static final String MSG_DATA_FOUND = "Creditor purchases record(s) found";
    public static final String MSG_DATA_NOT_FOUND = "Creditor purchases data not found!";
    public static final String MSG_FAILURE = "Failure to retrieve Creditor purchases transaction(s)";
    
    private CreditorPurchasesApi api;
    
    /**
     * 
     */
    public QueryCreditorPurchasesApiHandler() {
        super();
        this.api = CreditorPurchasesApiFactory.createApi();
        logger.info(QueryCreditorPurchasesApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Processes requests pertaining to fetching and creating of creditor
     * purchase transactions.
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
                // Ancestor was not able to find command.  Continue processing.
            }
            else {
             // This means an error occurred.
                return r;    
            }
        }
        switch (command) {
            case ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_GET:
                r = this.fetch(this.requestObj);
                break;

            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    
    /**
     * Handler for invoking the appropriate API in order to fetch one or more
     * creditor purchases transaction objects. 
     * <p>
     * Currently, the target level, <i>DETAILS</i>, is not supported.
     * 
     * @param reqS
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected MessageHandlerResults fetch(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<XactType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            XactCreditChargeDto criteriaDto = CreditorPurchasesJaxbDtoFactory
                    .createCreditorPurchasesDtoCriteriaInstance(req.getCriteria().getXactCriteria().getBasicCriteria());
            XactCustomCriteriaDto customCriteriaDto = TransactionJaxbDtoFactory
                    .createCustomXactDtoCriteriaInstance(req.getCriteria().getXactCriteria().getCustomCriteria());
            
            this.targetLevel = req.getCriteria().getXactCriteria().getTargetLevel().name().toUpperCase();
            switch (this.targetLevel) {
                case ApiMessageHandlerConst.TARGET_LEVEL_HEADER:
                case ApiMessageHandlerConst.TARGET_LEVEL_FULL:
                    List<XactCreditChargeDto> dtoList = this.api.get(criteriaDto, customCriteriaDto);
                    if (dtoList == null) {
                        rs.setMessage(QueryCreditorPurchasesApiHandler.MSG_DATA_NOT_FOUND);
                        rs.setRecordCount(0);
                    }
                    else {
                        queryDtoResults = this.buildJaxbTransaction(dtoList, customCriteriaDto);
                        rs.setMessage(QueryCreditorPurchasesApiHandler.MSG_DATA_FOUND);
                        rs.setRecordCount(dtoList.size());
                    }
                    break;
                    
                default:
                    String msg = RMT2String.replace(MSG_INCORRECT_TARGET_LEVEL, targetLevel, "%s");
                    throw new RMT2Exception(msg);
            }
            
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(QueryCreditorPurchasesApiHandler.MSG_FAILURE);
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
 

    /* (non-Javadoc)
     * @see org.rmt2.api.handlers.transaction.XactApiHandler#validateRequest(org.rmt2.jaxb.AccountingTransactionRequest)
     */
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        super.validateRequest(req);
        this.validateSearchRequest(req);
    }

    /**
     * Builds a List of XactType objects from a List of XactCreditChargeDto objects.
     * 
     * @param results List<{@link XactCreditChargeDto}>
     * @param customCriteriaDto custom relational criteria (optional)
     * @return List<{@link XactType}>
     */
    private List<XactType> buildJaxbTransaction(List<XactCreditChargeDto> results, XactCustomCriteriaDto customCriteriaDto) {
        List<XactType> list = new ArrayList<>();
        
        for (XactCreditChargeDto item : results) {
            List<XactTypeItemActivityDto> xactItems = null;
            
            // retrieve line items if requested
            if (this.targetLevel.equals(ApiMessageHandlerConst.TARGET_LEVEL_FULL)) {
                try {
                    xactItems = this.api.getItems(item.getXactId());
                } catch (CreditorPurchasesApiException e) {
                    logger.error("Unable to fetch line items for transaction id, " + item.getXactId());
                }    
            }
            
            XactType jaxbObj = TransactionJaxbDtoFactory.createXactJaxbInstance(item, 0, xactItems);
            
            // Build and associate Creditor object
            CreditorType creditor = CreditorTypeBuilder.Builder.create()
                    .withCreditorId(item.getCreditorId())
                    .withAccountNo(item.getAccountNumber()).build();
            jaxbObj.setCreditor(creditor);
            list.add(jaxbObj);
        }
        return list;
    }
    
    /* (non-Javadoc)
     * @see org.rmt2.api.handlers.transaction.XactApiHandler#buildResponse(java.util.List, com.api.messaging.handler.MessageHandlerCommonReplyStatus)
     */
    @Override
    protected String buildResponse(List<XactType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        return super.buildResponse(payload, replyStatus);
    }

}

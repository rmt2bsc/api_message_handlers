package org.rmt2.api.handlers.transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.CommonXactDto;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.api.handlers.AccountingtMsgHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.jaxb.TransactionDetailGroup;
import org.rmt2.jaxb.XactType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes common attributes of transactions of varying types as
 * messages to the Accounting API. This handler captures only transaction header
 * data, and transation details such as line items is ignored.
 * 
 * @author roy.terrell
 *
 */
public class GenericXactApiHandler extends 
                  AbstractJaxbMessageHandler<AccountingTransactionRequest, AccountingTransactionResponse, List<XactType>> {
    
    private static final Logger logger = Logger.getLogger(GenericXactApiHandler.class);
    public static final String MSG_DATA_FOUND = "Transaction record(s) found";
    public static final String MSG_DATA_NOT_FOUND = "Transaction data not found!";
    public static final String MSG_MISSING_GENERAL_CRITERIA = "Transaction request must contain a valid general criteria object";
    public static final String MSG_MISSING_SUBJECT_CRITERIA = "Selection criteria is required for Accounting Transaction fetch operation";
    public static final String MSG_MISSING_BASIC_CRITERIA = "Transaction fetch request must contain a basic criteria object";

    private XactApi api;
    protected ObjectFactory jaxbObjFactory;
    protected String targetLevel;

    /**
     * Create GenericXactApiHandler object
     * 
     * @param connection
     *            an instance of {@link DaoClient}
     */
    public GenericXactApiHandler() {
        super();
        this.api = XactApiFactory.createDefaultXactApi();

        // UI-37: Added for capturing the update user id
        this.transApi = this.api;
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAccountingTransactionResponse();
        
        // Load cache data
        AccountingtMsgHandlerUtility.loadXactTypeCache(false);
        logger.info(GenericXactApiHandler.class.getName() + " was instantiated successfully");
    }

    
    /**
     * Processes requests pertaining to fetching varying transaction types with
     * common attributes.
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
            // This means an error occurred.
            return r;
        }
        switch (command) {
            case ApiTransactionCodes.ACCOUNTING_GENERIC_TRANSACTION_GET:
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
     * Transaction objects. 
     * <p>
     * Currently, the target level, <i>DETAILS</i>, is not supported.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetch(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<XactType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            CommonXactDto criteriaDto = TransactionJaxbDtoFactory
                    .createGerericXactDtoCriteriaInstance(req.getCriteria().getXactCriteria().getBasicCriteria());
            
            List<CommonXactDto> dtoList = this.api.getXact(criteriaDto);
                    if (dtoList == null) {
                        rs.setMessage(GenericXactApiHandler.MSG_DATA_NOT_FOUND);
                        rs.setRecordCount(0);
                    }
                    else {
                        queryDtoResults = this.buildJaxbTransaction(dtoList);
                        rs.setMessage(GenericXactApiHandler.MSG_DATA_FOUND);
                        rs.setRecordCount(dtoList.size());
                    }
            
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve Transaction(s)");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }

    
    /**
     * Builds a List of XactType objects from a List of XactDto objects.
     * 
     * @param results
     *            List<{@link CommonXactDto}>
     * @return List<{@link XactType}>
     */
    private List<XactType> buildJaxbTransaction(List<CommonXactDto> results) {
        List<XactType> list = new ArrayList<>();
        
        for (CommonXactDto item : results) {
            XactType jaxbObj = AccountingtMsgHandlerUtility.buildGenericTransactionDetails(item);
            list.add(jaxbObj);
        }
        return list;
    }
    
    /**
     * Validates the existence of the request's search criteria or profile data
     * depending on the type of request submitted.
     * 
     * @param req
     *            instance of {@link AccountingTransactionRequest}
     */
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Transaction request element is invalid");
        }
        
        try {
            Verifier.verifyNotNull(req.getCriteria());
            Verifier.verifyNotNull(req.getCriteria().getXactCriteria());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(MSG_MISSING_GENERAL_CRITERIA);
        }
        
        try {
            Verifier.verifyNotNull(req.getCriteria().getXactCriteria().getBasicCriteria());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(MSG_MISSING_BASIC_CRITERIA, e);
        }
    }
    
    

    @Override
    protected String buildResponse(List<XactType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            TransactionDetailGroup profile = this.jaxbObjFactory.createTransactionDetailGroup();
            profile.setTransactions(jaxbObjFactory.createXactListType());
            profile.getTransactions().getTransaction().addAll(payload);
            this.responseObj.setProfile(profile);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

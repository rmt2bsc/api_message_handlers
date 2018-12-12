package org.rmt2.api.handlers.transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.XactCodeDto;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.jaxb.TransactionDetailGroup;
import org.rmt2.jaxb.XactCodeType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Transaction Code related messages to the Accounting API.
 * 
 * @author roy.terrell
 *
 */
public class XactCodeApiHandler extends 
                  AbstractJaxbMessageHandler<AccountingTransactionRequest, AccountingTransactionResponse, List<XactCodeType>> {
    
    private static final Logger logger = Logger.getLogger(XactCodeApiHandler.class);
    public static final String MSG_MISSING_GENERAL_CRITERIA = "Transaction Code request must contain a valid general criteria object";
    public static final String MSG_MISSING_SUBJECT_CRITERIA = "Transaction Code fetch request must contain a valid Transaction Code criteria object";
    
    private ObjectFactory jaxbObjFactory;
    private XactApi api;

    /**
     * Create XactGrouptApiHandler object using an instnace of a DaoClient.
     * <p>
     * Since this class will used by other API's, it will depend on that api's
     * DAO connection for data access.
     * 
     * @param connection
     *            an instance of {@link DaoClient}
     */
    public XactCodeApiHandler() {
        super();
        this.api = XactApiFactory.createDefaultXactApi();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAccountingTransactionResponse();
        logger.info(XactCodeApiHandler.class.getName() + " was instantiated successfully");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.api.messaging.jms.handler.AbstractMessageHandler#processRequest(java
     * .lang.String, java.io.Serializable)
     */
    @Override
    public MessageHandlerResults processMessage(String command, Serializable payload) throws MessageHandlerCommandException {
        MessageHandlerResults r = super.processMessage(command, payload);

        if (r != null) {
            // This means an error occurred.
            return r;
        }
        switch (command) {
            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_CODE_GET:
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
     * Transaction Group objects.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetch(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<XactCodeType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            XactCodeDto criteriaDto = TransactionJaxbDtoFactory
                    .createCodeDtoCriteriaInstance(req.getCriteria().getXactCodeCriteria());
            
            List<XactCodeDto> dtoList = this.api.getCodes(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("Transaction Code data not found!");
                rs.setReturnCode(0);
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Transaction Code record(s) found");
                rs.setReturnCode(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve Transaction Code(s)");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }

    
    private List<XactCodeType> buildJaxbListData(List<XactCodeDto> results) {
        List<XactCodeType> list = new ArrayList<>();
        for (XactCodeDto item : results) {
            XactCodeType jaxbObj = TransactionJaxbDtoFactory.createCodeJaxbInstance(item);
            list.add(jaxbObj);
        }
        return list;
    }
    
    
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Transaction Code request element is invalid");
        }
        try {
            Verifier.verifyNotNull(req.getCriteria());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(MSG_MISSING_GENERAL_CRITERIA);
        }
        
        // Validate request for fetch operations
        switch (this.command) {
            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_CODE_GET:
                try {
                    Verifier.verifyNotNull(req.getCriteria().getXactCodeCriteria());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException(MSG_MISSING_SUBJECT_CRITERIA);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected String buildResponse(List<XactCodeType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            TransactionDetailGroup profile = this.jaxbObjFactory.createTransactionDetailGroup();
            profile.setXactCodes(jaxbObjFactory.createXactCodeListType());
            profile.getXactCodes().getXactCode().addAll(payload);
            this.responseObj.setProfile(profile);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

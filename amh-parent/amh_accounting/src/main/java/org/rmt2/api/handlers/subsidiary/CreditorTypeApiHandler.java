package org.rmt2.api.handlers.subsidiary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.CreditorTypeDto;
import org.modules.CommonAccountingConst;
import org.modules.subsidiary.CreditorApi;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.CreditortypeType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.rmt2.jaxb.TransactionDetailGroup;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Creditor Type related messages to the Accounting API.
 * 
 * @author roy.terrell
 *
 */
public class CreditorTypeApiHandler extends 
                  AbstractJaxbMessageHandler<AccountingTransactionRequest, AccountingTransactionResponse, List<CreditortypeType>> {
    
    private static final Logger logger = Logger.getLogger(CreditorTypeApiHandler.class);
    public static final String MSG_UPDATE_MISSING_CRITERIA = "Creditor Type transaction selection criteria is required for query operation";
    private ObjectFactory jaxbObjFactory;
    private CreditorApi api;

    /**
     * @param payload
     */
    public CreditorTypeApiHandler() {
        super();
        this.api = SubsidiaryApiFactory.createCreditorApi(CommonAccountingConst.APP_NAME);
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAccountingTransactionResponse();
        logger.info(CreditorTypeApiHandler.class.getName() + " was instantiated successfully");
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
            // IS-70: Added logic to close API in cases of an error so to
            // prevent memory leaks.
            if (this.api != null) {
                this.api.close();
                this.api = null;
            }
            return r;
        }
        switch (command) {
            case ApiTransactionCodes.SUBSIDIARY_CREDITOR_TYPE_GET:
                r = this.fetchCreditorType(this.requestObj);
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
     * Creditor Type ojects.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetchCreditorType(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<CreditortypeType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            CreditorTypeDto criteriaDto = SubsidiaryJaxbDtoFactory
                    .createCreditorTypeDtoCriteriaInstance(req.getCriteria().getCreditortypeCriteria());
            
            List<CreditorTypeDto> dtoList = this.api.getCreditorType(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("Creditor Type data not found!");
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Creditor Type record(s) found");
                rs.setRecordCount(dtoList.size());
            }
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve creditor type(s)");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    
    private List<CreditortypeType> buildJaxbListData(List<CreditorTypeDto> results) {
        List<CreditortypeType> list = new ArrayList<>();
        for (CreditorTypeDto item : results) {
            CreditortypeType jaxbObj = SubsidiaryJaxbDtoFactory.createCreditorTypeJaxbInstance(item);
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
            throw new InvalidRequestException("Creditor Type transaction request element is invalid");
        }
        try {
            Verifier.verifyNotNull(req.getCriteria());
            Verifier.verifyNotNull(req.getCriteria().getCreditortypeCriteria());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(CreditorTypeApiHandler.MSG_UPDATE_MISSING_CRITERIA);
        }    
    }

    @Override
    protected String buildResponse(List<CreditortypeType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            TransactionDetailGroup profile = this.jaxbObjFactory.createTransactionDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getCreditorTypes().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

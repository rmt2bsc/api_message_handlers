package org.rmt2.api.handlers.generalledger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.AccountTypeDto;
import org.modules.CommonAccountingConst;
import org.modules.generalledger.GeneralLedgerApiFactory;
import org.modules.generalledger.GlAccountApi;
import org.rmt2.api.adapters.jaxb.AccountingJaxbDtoFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingGeneralLedgerRequest;
import org.rmt2.jaxb.AccountingGeneralLedgerResponse;
import org.rmt2.jaxb.GlAccounttypeType;
import org.rmt2.jaxb.GlDetailGroup;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes General Ledger Account Type related messages to the Accounting
 * API.
 * 
 * @author roy.terrell
 *
 */
public class GlAccountTypeApiHandler extends 
                  AbstractJaxbMessageHandler<AccountingGeneralLedgerRequest, AccountingGeneralLedgerResponse, List<GlAccounttypeType>> {
    
    private static final Logger logger = Logger.getLogger(GlAccountTypeApiHandler.class);
    private ObjectFactory jaxbObjFactory;
    private GlAccountApi api;

    /**
     * @param payload
     */
    public GlAccountTypeApiHandler() {
        super();
        GeneralLedgerApiFactory f = new GeneralLedgerApiFactory();
        this.api = f.createApi(CommonAccountingConst.APP_NAME);
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAccountingGeneralLedgerResponse();
        logger.info(GlAccountTypeApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.GL_ACCOUNT_TYPE_GET:
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
     * Handler for invoking the appropriate API in order to fetch one or more GL Account Type objects.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}           
     */
    protected MessageHandlerResults fetch(AccountingGeneralLedgerRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<GlAccounttypeType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            this.validateRequest(req);
            AccountTypeDto criteriaDto = AccountingJaxbDtoFactory
                    .createGlAccountTypeDtoCriteriaInstance(req.getCriteria().getCriteria());
            
            List<AccountTypeDto> dtoList = this.api.getAccountType(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("GL Account Type data not found!");
                rs.setReturnCode(0);
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("GL Account Type record(s) found");
                rs.setReturnCode(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve GL Account Type(s)");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
  
    private List<GlAccounttypeType> buildJaxbListData(List<AccountTypeDto> results) {
        List<GlAccounttypeType> list = new ArrayList<>();
        for (AccountTypeDto item : results) {
            GlAccounttypeType jaxbObj = AccountingJaxbDtoFactory.createGlAccountTypeJaxbInstance(item);
            list.add(jaxbObj);
        }
        return list;
    }
    
    
    @Override
    protected void validateRequest(AccountingGeneralLedgerRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("GL Account Type message request element is invalid");
        }
        
        // Validate that the request contains an AccountType criteria element
        try {
            Verifier.verifyNotNull(req.getCriteria());
            Verifier.verifyNotNull(req.getCriteria().getCriteria());
            Verifier.verifyNotNull(req.getCriteria().getCriteria().getAcctType());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("GL Account Type criteria is required");
        }
    }

    @Override
    protected String buildResponse(List<GlAccounttypeType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            GlDetailGroup profile = this.jaxbObjFactory.createGlDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getAccountType().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

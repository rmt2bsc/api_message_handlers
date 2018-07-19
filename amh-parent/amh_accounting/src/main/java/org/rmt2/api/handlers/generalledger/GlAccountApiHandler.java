package org.rmt2.api.handlers.generalledger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.AccountDto;
import org.modules.CommonAccountingConst;
import org.modules.generalledger.GeneralLedgerApiException;
import org.modules.generalledger.GeneralLedgerApiFactory;
import org.modules.generalledger.GlAccountApi;
import org.rmt2.api.adapters.jaxb.AccountingJaxbDtoFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingGeneralLedgerRequest;
import org.rmt2.jaxb.AccountingGeneralLedgerResponse;
import org.rmt2.jaxb.GlAccountType;
import org.rmt2.jaxb.GlDetailGroup;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.InvalidDataException;
import com.NotFoundException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes General Ledger Account related messages to the Accounting
 * API.
 * 
 * @author roy.terrell
 *
 */
public class GlAccountApiHandler extends 
                  AbstractJaxbMessageHandler<AccountingGeneralLedgerRequest, AccountingGeneralLedgerResponse, List<GlAccountType>> {
    
    private static final Logger logger = Logger.getLogger(GlAccountApiHandler.class);
    private ObjectFactory jaxbObjFactory;
    private GlAccountApi api;

    /**
     * @param payload
     */
    public GlAccountApiHandler() {
        super();
        GeneralLedgerApiFactory f = new GeneralLedgerApiFactory();
        this.api = f.createApi(CommonAccountingConst.APP_NAME);
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAccountingGeneralLedgerResponse();
        logger.info(GlAccountApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.GL_ACCOUNT_GET:
                r = this.fetch(this.requestObj);
                break;
            case ApiTransactionCodes.GL_ACCOUNT_UPDATE:
                r = this.update(this.requestObj);
                break;
            case ApiTransactionCodes.GL_ACCOUNT_DELETE:
                r = this.delete(this.requestObj);
                
                break;
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to fetch one or more GL Account objects.
     * <p>
     * This method is capable of processing personal, business, or generic
     * contact types.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}           
     */
    protected MessageHandlerResults fetch(AccountingGeneralLedgerRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<GlAccountType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            this.validateRequest(req);
            AccountDto criteriaDto = AccountingJaxbDtoFactory
                    .createGlAccountJaxbCriteriaInstance(req.getCriteria().getCriteria());
            
            List<AccountDto> dtoList = this.api.getAccount(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("GL Account data not found!");
                rs.setReturnCode(0);
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("GL Account record(s) found");
                rs.setReturnCode(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve GL Account(s)");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to update the specified
     * GL Account.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults update(AccountingGeneralLedgerRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<GlAccountType> updateData = null;
        
        boolean newRec = false;
        int rc = 0;
        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            this.validateRequest(req); 
            AccountDto dataObjDto = AccountingJaxbDtoFactory
                    .createGlAccountDtoInstance(req.getProfile().getAccount().get(0));
            newRec = (dataObjDto.getAcctId() == 0);
            
            // call api
            this.api.beginTrans();
            rc = this.api.updateAccount(dataObjDto);
            
            // prepare response with updated contact data
            List<AccountDto> updateList = new ArrayList<>();
            updateList.add(dataObjDto);
            updateData = this.buildJaxbListData(updateList);
            
            // Return code is either the total number of rows updated or the new group id
            rs.setReturnCode(rc);
            if (newRec) {
                rs.setMessage("GL Account was created successfully");
                rs.setExtMessage("The new acct id is " + rc);
            }
            else {
                rs.setMessage("GL Account was modified successfully");
                rs.setExtMessage("Total number of rows modified: " + rc);
            }
            this.api.commitTrans();
            
        } catch (GeneralLedgerApiException | NotFoundException | InvalidDataException e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to update " + (newRec ? "new" : "existing")  + " GL Account");
            rs.setExtMessage(e.getMessage());
            updateData = req.getProfile().getAccount();
            this.api.rollbackTrans();
        } finally {
            this.api.close();
        }
        String xml = this.buildResponse(updateData, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to delete the specified
     * GL Account.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults delete(AccountingGeneralLedgerRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        
        int rc = 0;
        AccountDto criteriaDto = null;
        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            this.validateRequest(req); 
            criteriaDto = AccountingJaxbDtoFactory
                    .createGlAccountDtoInstance(req.getProfile().getAccount().get(0));
            
            // call api
            this.api.beginTrans();
            rc = this.api.deleteAccount(criteriaDto.getAcctId());
            
            // Return code is either the total number of rows deleted
            rs.setReturnCode(rc);
            rs.setMessage("GL Account was deleted successfully");
            rs.setExtMessage("GL Account Id deleted was " + criteriaDto.getAcctId());
            this.api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to delelte GL Account");
            rs.setExtMessage(e.getMessage());
            this.api.rollbackTrans();
        } finally {
            this.api.close();
        }
        
        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<GlAccountType> buildJaxbListData(List<AccountDto> results) {
        List<GlAccountType> list = new ArrayList<>();
        for (AccountDto item : results) {
            GlAccountType jaxbObj = AccountingJaxbDtoFactory.createGlAccountJaxbInstance(item);
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
            throw new InvalidRequestException("LookupCodes message request element is invalid");
        }
        
        // Validate request for update/delete operation
        switch (this.command) {
            case ApiTransactionCodes.GL_ACCOUNT_UPDATE:
            case ApiTransactionCodes.GL_ACCOUNT_DELETE:
                try {
                    Verifier.verifyNotNull(req.getProfile());
                    Verifier.verifyNotEmpty(req.getProfile().getAccount());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException("GL Account data is required for update/delete operation");
                }
                try {
                    Verifier.verifyTrue(req.getProfile().getAccount().size() == 1);
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException("Only one (1) GL Account record is required for update/delete operation");
                }
                
                if (this.command.equals(ApiTransactionCodes.GL_ACCOUNT_DELETE)) {
                    try {
                        Verifier.verifyNotNull(req.getProfile().getAccount().get(0).getAcctId());
                        Verifier.verifyPositive(req.getProfile().getAccount().get(0).getAcctId());
                    }
                    catch (VerifyException e) {
                        throw new InvalidRequestException("A valid account id is required when deleting a GL Account from the database");
                    }   
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected String buildResponse(List<GlAccountType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            GlDetailGroup profile = this.jaxbObjFactory.createGlDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getAccount().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

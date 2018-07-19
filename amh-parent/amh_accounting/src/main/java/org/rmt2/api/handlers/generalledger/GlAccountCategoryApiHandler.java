package org.rmt2.api.handlers.generalledger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.AccountCategoryDto;
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
import org.rmt2.jaxb.GlAccountcatgType;
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
public class GlAccountCategoryApiHandler extends 
                  AbstractJaxbMessageHandler<AccountingGeneralLedgerRequest, AccountingGeneralLedgerResponse, List<GlAccountcatgType>> {
    
    private static final Logger logger = Logger.getLogger(GlAccountCategoryApiHandler.class);
    private ObjectFactory jaxbObjFactory;
    private GlAccountApi api;

    /**
     * @param payload
     */
    public GlAccountCategoryApiHandler() {
        super();
        GeneralLedgerApiFactory f = new GeneralLedgerApiFactory();
        this.api = f.createApi(CommonAccountingConst.APP_NAME);
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAccountingGeneralLedgerResponse();
        logger.info(GlAccountCategoryApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.GL_ACCOUNT_CATG_GET:
                r = this.fetch(this.requestObj);
                break;
            case ApiTransactionCodes.GL_ACCOUNT_CATG_UPDATE:
                r = this.update(this.requestObj);
                break;
            case ApiTransactionCodes.GL_ACCOUNT_CATG_DELETE:
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
     * Handler for invoking the appropriate API in order to fetch one or more GL
     * Account Category objects.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetch(AccountingGeneralLedgerRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<GlAccountcatgType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            AccountCategoryDto criteriaDto = AccountingJaxbDtoFactory
                    .createGlAccountCatgDtoCriteriaInstance(req.getCriteria().getCriteria());
            
            List<AccountCategoryDto> dtoList = this.api.getAccountCategory(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("GL Account Category data not found!");
                rs.setReturnCode(0);
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("GL Account Category record(s) found");
                rs.setReturnCode(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve GL Account Category(s)");
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
     * GL Account Category.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults update(AccountingGeneralLedgerRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<GlAccountcatgType> updateData = null;
        
        boolean newRec = false;
        int rc = 0;
        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            AccountCategoryDto dataObjDto = AccountingJaxbDtoFactory
                    .createGlAccountCatgDtoInstance(req.getProfile().getAccountCategory().get(0));
            newRec = (dataObjDto.getAcctCatgId() == 0);
            
            // call api
            this.api.beginTrans();
            rc = this.api.updateCategory(dataObjDto);
            
            // prepare response with updated contact data
            List<AccountCategoryDto> updateList = new ArrayList<>();
            updateList.add(dataObjDto);
            updateData = this.buildJaxbListData(updateList);
            
            // Return code is either the total number of rows updated or the new group id
            rs.setReturnCode(rc);
            if (newRec) {
                rs.setMessage("GL Account Category was created successfully");
                rs.setExtMessage("The new acct category id is " + rc);
            }
            else {
                rs.setMessage("GL Account Category was modified successfully");
                rs.setExtMessage("Total number of rows modified: " + rc);
            }
            this.api.commitTrans();
            
        } catch (GeneralLedgerApiException | NotFoundException | InvalidDataException e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to update " + (newRec ? "new" : "existing")  + " GL Account Category");
            rs.setExtMessage(e.getMessage());
            updateData = req.getProfile().getAccountCategory();
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
     * GL Account Category.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults delete(AccountingGeneralLedgerRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        
        int rc = 0;
        AccountCategoryDto criteriaDto = null;
        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            criteriaDto = AccountingJaxbDtoFactory
                    .createGlAccountCatgDtoInstance(req.getProfile().getAccountCategory().get(0));
            
            // call api
            this.api.beginTrans();
            rc = this.api.deleteCategory(criteriaDto.getAcctCatgId());
            
            // Return code is either the total number of rows deleted
            rs.setReturnCode(rc);
            rs.setMessage("GL Account Category was deleted successfully");
            rs.setExtMessage("GL Account Category Id deleted was " + criteriaDto.getAcctCatgId());
            this.api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to delelte GL Account Category");
            rs.setExtMessage(e.getMessage());
            this.api.rollbackTrans();
        } finally {
            this.api.close();
        }
        
        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<GlAccountcatgType> buildJaxbListData(List<AccountCategoryDto> results) {
        List<GlAccountcatgType> list = new ArrayList<>();
        for (AccountCategoryDto item : results) {
            GlAccountcatgType jaxbObj = AccountingJaxbDtoFactory.createGlAccountCatgJaxbInstance(item);
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
            throw new InvalidRequestException("GL Account Category message request element is invalid");
        }
        
        // Validate request for update/delete operation
        switch (this.command) {
            case ApiTransactionCodes.GL_ACCOUNT_CATG_UPDATE:
            case ApiTransactionCodes.GL_ACCOUNT_CATG_DELETE:
                try {
                    Verifier.verifyNotNull(req.getProfile());
                    Verifier.verifyNotEmpty(req.getProfile().getAccountCategory());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException("GL Account Category data is required for update/delete operation");
                }
                try {
                    Verifier.verifyTrue(req.getProfile().getAccountCategory().size() == 1);
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException("Only one (1) GL Account record is required for update/delete operation");
                }
                
                if (this.command.equals(ApiTransactionCodes.GL_ACCOUNT_CATG_DELETE)) {
                    try {
                        Verifier.verifyNotNull(req.getProfile().getAccountCategory().get(0).getAcctCatgId());
                        Verifier.verifyPositive(req.getProfile().getAccountCategory().get(0).getAcctCatgId());
                    }
                    catch (VerifyException e) {
                        throw new InvalidRequestException("A valid account category id is required when deleting a GL Account Category from the database");
                    }   
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected String buildResponse(List<GlAccountcatgType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            GlDetailGroup profile = this.jaxbObjFactory.createGlDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getAccountCategory().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

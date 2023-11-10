package org.rmt2.api.handlers.subsidiary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dto.CreditorDto;
import org.dto.CreditorXactHistoryDto;
import org.dto.XactDto;
import org.modules.CommonAccountingConst;
import org.modules.subsidiary.CreditorApi;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiException;
import org.modules.transaction.XactApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.CreditorType;
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
 * Handles and routes Creditor related messages to the Accounting API.
 * 
 * @author roy.terrell
 *
 */
public class CreditorApiHandler extends 
                  AbstractJaxbMessageHandler<AccountingTransactionRequest, AccountingTransactionResponse, List<CreditorType>> {
    
    private static final Logger logger = Logger.getLogger(CreditorApiHandler.class);
    public static final String MSG_UPDATE_MISSING_CRITERIA = "Creditor transaction selection criteria is required for query/delete operation";
    public static final String MSG_UPDATE_MISSING_PROFILE = "Creditor transaction profile data is required for update operation";
    private ObjectFactory jaxbObjFactory;
    private CreditorApi api;

    /**
     * @param payload
     */
    public CreditorApiHandler() {
        super();
        this.api = SubsidiaryApiFactory.createCreditorApi(CommonAccountingConst.APP_NAME);

        // UI-37: Added for capturing the update user id
        this.transApi = this.api;
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAccountingTransactionResponse();
        logger.info(CreditorApiHandler.class.getName() + " was instantiated successfully");
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
            // IS-70: Added logic to close API instance in order to prevent
            // memory leaks from left over open DB connections
            if (this.api != null) {
                this.api.close();
                this.api = null;
            }
            return r;
        }
        switch (command) {
            case ApiTransactionCodes.SUBSIDIARY_CREDITOR_GET:
                r = this.fetchCreditor(this.requestObj);
                break;
            case ApiTransactionCodes.SUBSIDIARY_CREDITOR_TRAN_HIST_GET:
                r = this.fetchTransHistory(this.requestObj);
                break;
            case ApiTransactionCodes.SUBSIDIARY_CREDITOR_UPDATE:
                r = this.updateCreditor(this.requestObj);
                break;
            case ApiTransactionCodes.SUBSIDIARY_CREDITOR_DELETE:
                r = this.deleteCreditor(this.requestObj);
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
     * Creditor ojects.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetchCreditor(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<CreditorType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            CreditorDto criteriaDto = SubsidiaryJaxbDtoFactory
                    .createCreditorDtoCriteriaInstance(req.getCriteria().getCreditorCriteria());
            
            List<CreditorDto> dtoList = this.api.getExt(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("Creditor data not found!");
                rs.setRecordCount(0);
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Creditor record(s) found");
                rs.setRecordCount(dtoList.size());
            }
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve creditor(s)");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
            this.api = null;
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to fetch one or more
     * Creditor ojects.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults updateCreditor(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<CreditorType> queryDtoResults = null;
        int rc = 0;
        boolean newCreditor = false;
        List<CreditorDto> dtoList = new ArrayList<>();
        CreditorDto profileDto = null;
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            profileDto = SubsidiaryJaxbDtoFactory.createCreditorDtoInstance(req.getProfile().getCreditors().getCreditor().get(0));
            newCreditor = profileDto.getCreditorId() == 0;

            api.beginTrans();
            rc = this.api.update(profileDto);
            if (rc > 0) {
                if (newCreditor) {
                    profileDto.setCreditorId(rc);
                    rs.setMessage("Creditor record(s) created successfully");
                    rs.setRecordCount(1);
                }
                else {
                    rs.setMessage("Creditor record(s) modified successfully");
                    rs.setRecordCount(rc);
                }
            }
            else {
                rs.setMessage("Creditor data not found for update operation");
                String extraMsg = "Creditor Id: " + profileDto.getCreditorId() + ", Creditor Name: " + profileDto.getContactName();
                rs.setExtMessage(extraMsg);
                rs.setRecordCount(0);
            }
            this.responseObj.setHeader(req.getHeader());
            this.api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to update creditor(s)");
            rs.setExtMessage(e.getMessage());
            this.api.rollbackTrans();
        } finally {
            this.api.close();
            this.api = null;
        }

        dtoList.add(profileDto);
        queryDtoResults = this.buildJaxbListData(dtoList);
        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to delete Creditor ojects.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults deleteCreditor(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        int rc = 0;
        CreditorDto criteriaDto = null;
        
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            criteriaDto = SubsidiaryJaxbDtoFactory
                    .createCreditorDtoCriteriaInstance(req.getCriteria().getCreditorCriteria());
            api.beginTrans();
            rc = this.api.delete(criteriaDto);
            rs.setMessage("Creditor delete operation completed!");
            rs.setExtMessage("Total records deleted: " + rc);
            rs.setRecordCount(rc);
            this.responseObj.setHeader(req.getHeader());
            this.api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to delete creditor: "  + criteriaDto.getCreditorId());
            rs.setExtMessage(e.getMessage());
            this.api.rollbackTrans();
        } finally {
            this.api.close();
            this.api = null;
        }

        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to fetch one or more
     * creditor transaction history ojects.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetchTransHistory(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<CreditorType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            CreditorDto criteriaDto = SubsidiaryJaxbDtoFactory
                    .createCreditorDtoCriteriaInstance(req.getCriteria().getCreditorCriteria());
            
            List<CreditorDto> dtoCredList = this.api.getExt(criteriaDto);
            if (dtoCredList != null && dtoCredList.size() == 1) {
                List<CreditorXactHistoryDto> dtoList = this.api.getTransactionHistory(criteriaDto.getCreditorId());
                if (dtoList == null) {
                    rs.setMessage("Creditor transaction history data not found!");
                    rs.setRecordCount(1);
                }
                else {
                    rs.setMessage("Creditor transaction history record(s) found");
                    rs.setRecordCount(dtoCredList.size());
                }
                queryDtoResults = this.buildJaxbListData(dtoCredList.get(0), dtoList);
            }
            else {
                rs.setMessage("Either creditor data not found, too many creditors were returned, or error is unknown");
                rs.setRecordCount(0);
            }
            
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve creditor transaction history");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
            this.api = null;
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
   
    private List<CreditorType> buildJaxbListData(List<CreditorDto> results) {
        List<CreditorType> list = new ArrayList<>();
        for (CreditorDto item : results) {
            CreditorType jaxbObj = SubsidiaryJaxbDtoFactory.createCreditorJaxbInstance(item, null, null);
            list.add(jaxbObj);
        }
        return list;
    }
    
    private List<CreditorType> buildJaxbListData(CreditorDto creditor, List<CreditorXactHistoryDto> transHistory) {
        List<CreditorType> list = new ArrayList<>();
        Map<Integer, XactDto> xactDetails = this.buildActivityToXactMap(transHistory);
        CreditorType cust = SubsidiaryJaxbDtoFactory.createCreditorJaxbInstance(creditor, xactDetails, transHistory);
        list.add(cust);
        return list;
    }
    
    private Map<Integer, XactDto> buildActivityToXactMap(List<CreditorXactHistoryDto> transHistory) {
        if (transHistory == null) {
            return null;
        }

        Map<Integer, XactDto> map = new HashMap<>();
        XactApi xactApi = XactApiFactory.createDefaultXactApi();
        for (CreditorXactHistoryDto item : transHistory) {
            try {
                XactDto dto = xactApi.getXactById(item.getXactId());
                if (dto != null) {
                    map.put(item.getActivityId(), dto);
                }
            } catch (XactApiException e) {
                logger.error("Unable to fetch transaction details for creditor transaction history item, " + item.getActivityId(), e);
            }
        }
        xactApi.close();
        return map;
    }
    
    
    @Override
    protected void validateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Creditor transaction request element is invalid");
        }
        
        switch (this.command) {
            case ApiTransactionCodes.SUBSIDIARY_CREDITOR_GET:
            case ApiTransactionCodes.SUBSIDIARY_CREDITOR_TRAN_HIST_GET:
            case ApiTransactionCodes.SUBSIDIARY_CREDITOR_DELETE:
                try {
                    Verifier.verifyNotNull(req.getCriteria());
                    Verifier.verifyNotNull(req.getCriteria().getCreditorCriteria());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException(CreditorApiHandler.MSG_UPDATE_MISSING_CRITERIA);
                }    
                break;
                
            case ApiTransactionCodes.SUBSIDIARY_CREDITOR_UPDATE:
                try {
                    Verifier.verifyNotNull(req.getProfile());
                    Verifier.verifyNotNull(req.getProfile().getCreditors());
                    Verifier.verifyNotEmpty(req.getProfile().getCreditors().getCreditor());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException(CreditorApiHandler.MSG_UPDATE_MISSING_PROFILE);
                }    
                break;
                
             default:
                 logger.warn("Creditor API message handler command key, " + this.command + ", could not be validated");
                 break;
        }
    }

    @Override
    protected String buildResponse(List<CreditorType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            TransactionDetailGroup profile = this.jaxbObjFactory.createTransactionDetailGroup();
            profile.setCreditors(this.jaxbObjFactory.createCreditorListType());
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getCreditors().getCreditor().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

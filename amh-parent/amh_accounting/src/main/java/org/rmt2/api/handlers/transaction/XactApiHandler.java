package org.rmt2.api.handlers.transaction;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.XactDto;
import org.dto.XactTypeItemActivityDto;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiException;
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
import com.RMT2Exception;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2Date;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes General Transaction messages to the Accounting API.
 * 
 * @author roy.terrell
 *
 */
public class XactApiHandler extends 
                  AbstractJaxbMessageHandler<AccountingTransactionRequest, AccountingTransactionResponse, List<XactType>> {
    
    private static final Logger logger = Logger.getLogger(XactApiHandler.class);
    public static final String MSG_MISSING_GENERAL_CRITERIA = "Transaction request must contain a valid general criteria object";
    public static final String MSG_MISSING_SUBJECT_CRITERIA = "Selection criteria is required for Accounting Transaction fetch operation";
    public static final String MSG_MISSING_TARGET_LEVEL = "Transaction fetch request must contain a target level value";
    public static final String MSG_INCORRECT_TARGET_LEVEL = "Transaction fetch request contains an invalid target level: %s";
    public static final String MSG_MISSING_PROFILE_DATA = "Transaction profile is required for create transaction operation";
    public static final String MSG_REQUIRED_NO_TRANSACTIONS_INCORRECT = "Transaction profile is required to contain one and only one transaction for the create transaction operation";
    
    public static final String TARGET_LEVEL_HEADER = "HEADER";
    public static final String TARGET_LEVEL_DETAILS = "DETAILS";
    public static final String TARGET_LEVEL_FULL = "FULL";
    
    private ObjectFactory jaxbObjFactory;
    private XactApi api;
    private String targetLevel;

    /**
     * Create XactApiHandler object
     * 
     * @param connection
     *            an instance of {@link DaoClient}
     */
    public XactApiHandler() {
        super();
        this.api = XactApiFactory.createDefaultXactApi();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAccountingTransactionResponse();
        
        // Load cache data
        AccountingtMsgHandlerUtility.loadXactTypeCache(false);
        logger.info(XactApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_GET:
                r = this.fetch(this.requestObj);
                break;
                
            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_CREATE:
                r = this.create(this.requestObj);
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
            XactDto criteriaDto = TransactionJaxbDtoFactory
                    .createBaseXactDtoCriteriaInstance(req.getCriteria().getXactCriteria().getBasicCriteria());
            
            this.targetLevel = req.getCriteria().getXactCriteria().getTargetLevel().name().toUpperCase();
            switch (this.targetLevel) {
                case TARGET_LEVEL_HEADER:
                case TARGET_LEVEL_FULL:
                    List<XactDto> dtoList = this.api.getXact(criteriaDto);
                    if (dtoList == null) {
                        rs.setMessage("Transaction data not found!");
                        rs.setRecordCount(0);
                    }
                    else {
                        queryDtoResults = this.buildJaxbListData(dtoList);
                        rs.setMessage("Transaction record(s) found");
                        rs.setRecordCount(dtoList.size());
                    }
                    break;
                    
                case TARGET_LEVEL_DETAILS:
                    // TODO: Implement later.
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
     * Handler for invoking the appropriate API in order to create a general accounting 
     * Transaction object.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults create(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        XactType reqXact = req.getProfile().getTransactions().getTransaction().get(0);
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            XactDto xactDto = TransactionJaxbDtoFactory.createXactDtoInstance(reqXact);
            List<XactTypeItemActivityDto> itemsDtoList = TransactionJaxbDtoFactory
                    .createXactItemDtoInstance(reqXact.getLineitems().getLineitem());
            
            int newXactid = this.api.update(xactDto, itemsDtoList);
            reqXact.setXactId(BigInteger.valueOf(newXactid));
            reqXact.setPostedDate(RMT2Date.toXmlDate(xactDto.getXactPostedDate()));
            rs.setMessage("New Accounting Transaction was created: " + newXactid);
            rs.setRecordCount(1);
            
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to create Transaction");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(req.getProfile().getTransactions().getTransaction(), rs);
        results.setPayload(xml);
        return results;
    }
    
    
    
    private List<XactType> buildJaxbListData(List<XactDto> results) {
        List<XactType> list = new ArrayList<>();
        
        for (XactDto item : results) {
            List<XactTypeItemActivityDto> xactItems = null;
            
            // retrieve line items if requested
            if (this.targetLevel.equals(TARGET_LEVEL_FULL)) {
                try {
                    xactItems = this.api.getXactTypeItemActivityExt(item.getXactId());
                } catch (XactApiException e) {
                    logger.error("Unable to fetch line items for transaction id, " + item.getXactId());
                }    
            }
            
            XactType jaxbObj = TransactionJaxbDtoFactory.createXactJaxbInstance(item, 0, xactItems);
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
        
        // Validate request for fetch operations
        switch (this.command) {
            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_GET:
                try {
                    Verifier.verifyNotNull(req.getCriteria());
                    Verifier.verifyNotNull(req.getCriteria().getXactCriteria());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException(MSG_MISSING_GENERAL_CRITERIA);
                }
                
                // Must contain flag that indicates what level of the transaction object to populate with data
                try {
                    Verifier.verifyNotNull(req.getCriteria().getXactCriteria().getTargetLevel());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException(MSG_MISSING_TARGET_LEVEL, e);
                }
                
                // Selection criteria is required
                try {
                    Verifier.verifyNotNull(req.getCriteria().getXactCriteria().getBasicCriteria());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException(MSG_MISSING_SUBJECT_CRITERIA, e);    
                }
                break;
                
            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_CREATE:
                // Transaction profile must exist
                try {
                    Verifier.verifyNotNull(req.getProfile());
                    Verifier.verifyNotNull(req.getProfile().getTransactions());
                    Verifier.verifyNotNull(req.getProfile().getTransactions().getTransaction());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException(MSG_MISSING_PROFILE_DATA, e);    
                }
                // Transaction profile must contain one and only one transaction
                try {
                    Verifier.verifyNotEmpty(req.getProfile().getTransactions().getTransaction());
                    Verifier.verifyTrue(req.getProfile().getTransactions().getTransaction().size() == 1);
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException(MSG_REQUIRED_NO_TRANSACTIONS_INCORRECT, e);    
                }
                break;
            default:
                break;
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

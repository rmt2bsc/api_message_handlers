package org.rmt2.api.handlers.transaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.XactDto;
import org.dto.XactTypeItemActivityDto;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiException;
import org.modules.transaction.XactApiFactory;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.api.handlers.AccountingtMsgHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.jaxb.TransactionDetailGroup;
import org.rmt2.jaxb.XactBasicCriteriaType;
import org.rmt2.jaxb.XactType;

import com.InvalidDataException;
import com.RMT2Exception;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
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
    public static final String MSG_DATA_FOUND = "Transaction record(s) found";
    public static final String MSG_DATA_NOT_FOUND = "Transaction data not found!";
    public static final String MSG_MISSING_GENERAL_CRITERIA = "Transaction request must contain a valid general criteria object";
    public static final String MSG_MISSING_SUBJECT_CRITERIA = "Selection criteria is required for Accounting Transaction fetch operation";
    public static final String MSG_MISSING_TARGET_LEVEL = "Transaction fetch request must contain a target level value";
    public static final String MSG_INCORRECT_TARGET_LEVEL = "Transaction fetch request contains an invalid target level: %s";
    public static final String MSG_MISSING_PROFILE_DATA = "Transaction profile is required for create transaction operation";
    public static final String MSG_MISSING_TRANSACTION_SECTION = "Transaction section is missing from the transaction profile";
    public static final String MSG_REQUIRED_NO_TRANSACTIONS_INCORRECT = "Transaction profile is required to contain one and only one transaction for the create transaction operation";
    public static final String MSG_REVERSE_SUCCESS = "Existing Accounting Transaction, %s1, was reversed: %s2";
    public static final String MSG_DETAILS_NOT_SUPPORTED = "Transaction level \"DETAILS\" is not supported at this time";
    
    public static final String MSG_DELETE_SUCCESS = "Accounting transaction record(s) were deleted successfully";
    public static final String MSG_DELETE_SUCCESS_EXT = "The following transaction(s) were targeted for deletion: " + ApiMessageHandlerConst.MSG_PLACEHOLDER;
    public static final String MSG_DELETE_XACT_NOT_FOUND = "No accounting transaction record(s) were found";
    public static final String MSG_DELETE_XACT_NOT_FOUND_EXT = "The following transaction(s) were targeted for deletion: " + ApiMessageHandlerConst.MSG_PLACEHOLDER;
    public static final String MSG_DELETE_API_ERROR = "An API error prevented the deletion of transaction(s): " + ApiMessageHandlerConst.MSG_PLACEHOLDER;    
    
    protected ObjectFactory jaxbObjFactory;
    protected String targetLevel;

    /**
     * Create XactApiHandler object
     * 
     * @param connection
     *            an instance of {@link DaoClient}
     */
    public XactApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAccountingTransactionResponse();
        
        // Load cache data
        AccountingtMsgHandlerUtility.loadXactTypeCache(false);
        logger.info(XactApiHandler.class.getName() + " was instantiated successfully");
    }

    
    /**
     * Processes requests pertaining to fetching and creating of common
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
            return r;
        }
        switch (command) {
            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_GET:
                r = this.fetch(this.requestObj);
                break;
                
            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_CREATE:
                r = this.update(this.requestObj);
                break;
                
            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_REVERSE:
                r = this.reverse(this.requestObj);
                break;
                
            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_DELETE:
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

        // IS-71:  Changed the scope to local to prevent conflicts class scoped api variable in XactApiHandler
        XactApi api = null;
        try {
        	api = XactApiFactory.createDefaultXactApi();

            // UI-37: Added for capturing the update user id
            this.transApi = api;
        	
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            XactDto criteriaDto = TransactionJaxbDtoFactory
                    .createBaseXactDtoCriteriaInstance(req.getCriteria().getXactCriteria().getBasicCriteria());
            
            this.targetLevel = req.getCriteria().getXactCriteria().getTargetLevel().name().toUpperCase();
            switch (this.targetLevel) {
                case ApiMessageHandlerConst.TARGET_LEVEL_HEADER:
                case ApiMessageHandlerConst.TARGET_LEVEL_FULL:
                    List<XactDto> dtoList = api.getXact(criteriaDto);
                    if (dtoList == null) {
                        rs.setMessage(XactApiHandler.MSG_DATA_NOT_FOUND);
                        rs.setRecordCount(0);
                    }
                    else {
                        queryDtoResults = this.buildJaxbTransaction(dtoList);
                        rs.setMessage(XactApiHandler.MSG_DATA_FOUND);
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
            rs.setMessage("Failure to retrieve Transaction(s)");
            rs.setExtMessage(e.getMessage());
        } finally {
            api.close();
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
    protected MessageHandlerResults update(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        XactType reqXact = req.getProfile().getTransactions().getTransaction().get(0);
        List<XactType> tranRresults = new ArrayList<>();
        
        // IS-71:  Changed the scope to local to prevent conflicts class scoped api variable in XactApiHandler
        XactApi api = null;
        try {
        	api = XactApiFactory.createDefaultXactApi();

            // UI-37: Added for capturing the update user id
            this.transApi = api;

            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            XactDto xactDto = TransactionJaxbDtoFactory.createXactDtoInstance(reqXact);
            List<XactTypeItemActivityDto> itemsDtoList = TransactionJaxbDtoFactory
                    .createXactItemDtoInstance(reqXact.getLineitems().getLineitem());
            
            int newXactId = api.update(xactDto, itemsDtoList);
            xactDto.setXactId(newXactId);
            XactType XactResults = TransactionJaxbDtoFactory.createXactJaxbInstance(xactDto, 0, itemsDtoList);
            tranRresults.add(XactResults);
            rs.setMessage("New Accounting Transaction was created: " + XactResults.getXactId());
            rs.setRecordCount(1);
           
            this.responseObj.setHeader(req.getHeader());
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to create Transaction");
            rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            api.close();
        }
        String xml = this.buildResponse(tranRresults, rs);
        results.setPayload(xml);
        return results;
    }
    
    
    /**
     * Handler for invoking the appropriate API in order to reverse a general accounting 
     * Transaction object.
     * 
     * @param req
     *            an instance of {@link AccountingTransactionRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults reverse(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        XactType reqXact = req.getProfile().getTransactions().getTransaction().get(0);
        List<XactType> tranRresults = new ArrayList<>();
        int newXactId = 0;
        int oldXactId = 0;
        
        // IS-71:  Changed the scope to local to prevent conflicts class scoped api variable in XactApiHandler
        XactApi api = null;
        try {
        	api = XactApiFactory.createDefaultXactApi();

            // UI-37: Added for capturing the update user id
            this.transApi = api;
        	
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            XactDto xactDto = TransactionJaxbDtoFactory.createXactDtoInstance(reqXact);
            List<XactTypeItemActivityDto> itemsDtoList = TransactionJaxbDtoFactory
                    .createXactItemDtoInstance(reqXact.getLineitems().getLineitem());
            
            oldXactId = xactDto.getXactId();
            newXactId = api.reverse(xactDto, itemsDtoList);
            xactDto.setXactId(newXactId);
            XactType XactResults = TransactionJaxbDtoFactory.createXactJaxbInstance(xactDto, 0, itemsDtoList);
            tranRresults.add(XactResults);
            String msg = RMT2String.replace(MSG_REVERSE_SUCCESS, String.valueOf(oldXactId), "%s1");
            msg = RMT2String.replace(msg, String.valueOf(newXactId), "%s2");
            rs.setMessage(msg);
            rs.setRecordCount(1);
            this.responseObj.setHeader(req.getHeader());
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to reverse Transaction: " + oldXactId);
            rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            api.close();
        }

        String xml = this.buildResponse(tranRresults, rs);
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
    protected MessageHandlerResults delete(AccountingTransactionRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        XactBasicCriteriaType reqCriteria = req.getCriteria().getXactCriteria().getBasicCriteria();
        StringBuilder xactIdStringList = null;
        
        // IS-71:  Changed the scope to local to prevent conflicts class scoped api variable in XactApiHandler
        XactApi api = null;
        try {
        	api = XactApiFactory.createDefaultXactApi();

            // UI-37: Added for capturing the update user id
            this.transApi = api;
        	
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            
            // Build list of transaction id's that are to be deleted.  This is typically used to construct error messages.
            List<Integer> xactIdList = TransactionJaxbDtoFactory.createDeleteXactIdList(reqCriteria);
            if (xactIdList != null) {
            	xactIdStringList = new StringBuilder();
        		for (Integer xactId : xactIdList) {
        			if (xactIdStringList.length() > 0) {
        				xactIdStringList.append(", ");
        			}
        			xactIdStringList.append(xactId);
        		}
            }
            
            int rc = api.deleteXact(xactIdList);
            rs.setRecordCount(rc);
            if (rc > 0) {
            	rs.setMessage(XactApiHandler.MSG_DELETE_SUCCESS);
                rs.setExtMessage(RMT2String.replace(XactApiHandler.MSG_DELETE_SUCCESS_EXT, 
                		xactIdStringList.toString(), ApiMessageHandlerConst.MSG_PLACEHOLDER));	
            }
            else {
            	rs.setMessage(XactApiHandler.MSG_DELETE_XACT_NOT_FOUND);
            	rs.setExtMessage(RMT2String.replace(XactApiHandler.MSG_DELETE_XACT_NOT_FOUND_EXT, 
                		xactIdStringList.toString(), ApiMessageHandlerConst.MSG_PLACEHOLDER));	
            }
           
            this.responseObj.setHeader(req.getHeader());
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(RMT2String.replace(XactApiHandler.MSG_DELETE_API_ERROR, 
            		xactIdStringList.toString(), ApiMessageHandlerConst.MSG_PLACEHOLDER));	
            rs.setExtMessage(e.getMessage()); 
            api.rollbackTrans();
        } finally {
            api.close();
        }
        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Builds a List of XactType objects from a List of XactDto objects.
     * 
     * @param results List<{@link XactDto}>
     * @return List<{@link XactType}>
     */
    private List<XactType> buildJaxbTransaction(List<XactDto> results) {
        List<XactType> list = new ArrayList<>();
        
        // IS-71:  Utilize XactApi in local scope
		XactApi api = null;
		try {
			api = XactApiFactory.createDefaultXactApi();
			for (XactDto item : results) {
				List<XactTypeItemActivityDto> xactItems = null;

				// retrieve line items if requested
				if (this.targetLevel.equals(ApiMessageHandlerConst.TARGET_LEVEL_FULL)) {
					try {
						xactItems = api.getXactTypeItemActivityExt(item.getXactId());
					} catch (XactApiException e) {
						logger.error("Unable to fetch line items for transaction id, " + item.getXactId());
					}
				}

				XactType jaxbObj = TransactionJaxbDtoFactory.createXactJaxbInstance(item, 0, xactItems);
				list.add(jaxbObj);
			}
		} finally {
			api.close();
			api = null;
		}
		return list;
    }
    
    /**
     * Validates the existence of a request's search criteria.
     * 
     * @param req
     * @throws InvalidDataException
     */
    protected void validateSearchRequest(AccountingTransactionRequest req) throws InvalidDataException {
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
        
        // Target level "DETAILS" is not supported.
        try {
            Verifier.verifyFalse(req.getCriteria().getXactCriteria()
                    .getTargetLevel().name()
                    .equalsIgnoreCase(ApiMessageHandlerConst.TARGET_LEVEL_DETAILS));
        } catch (VerifyException e) {
            throw new InvalidRequestException(MSG_DETAILS_NOT_SUPPORTED, e);
        }
    }
    
    /**
     * Validates the existence of a request's update data.
     * 
     * @param req
     * @throws InvalidDataException
     */
    protected void validateUpdateRequest(AccountingTransactionRequest req) throws InvalidDataException {
        // Transaction profile must exist
        try {
            Verifier.verifyNotNull(req.getProfile());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(MSG_MISSING_PROFILE_DATA, e);    
        }

        // Must include transaction section.
        try {
            Verifier.verifyNotNull(req.getProfile().getTransactions());
        } catch (VerifyException e) {
            throw new InvalidRequestException(MSG_MISSING_TRANSACTION_SECTION, e);
        }

        // Transaction profile must contain one and only one transaction
        try {
            Verifier.verifyTrue(req.getProfile().getTransactions().getTransaction().size() == 1);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(MSG_REQUIRED_NO_TRANSACTIONS_INCORRECT, e);    
        }
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
        
        // Validate request for fetch operations
        switch (this.command) {
            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_GET:
            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_DELETE:
                this.validateSearchRequest(req);
                break;
                
            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_CREATE:
            case ApiTransactionCodes.ACCOUNTING_TRANSACTION_REVERSE:
                this.validateUpdateRequest(req);
                
                // Transaction profile must exist
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

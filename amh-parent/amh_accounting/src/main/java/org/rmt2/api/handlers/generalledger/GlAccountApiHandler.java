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
 * Handles and routes General Ledger Account group related messages to the Accounting
 * API.
 * 
 * @author roy.terrell
 *
 */
public class GlAccountApiHandler extends 
                  AbstractJaxbMessageHandler<AccountingGeneralLedgerRequest, AccountingGeneralLedgerResponse, List<GlAccountType>> {
    
    private static final Logger logger = Logger.getLogger(GlAccountApiHandler.class);
    private ObjectFactory jaxbObjFactory;

    /**
     * @param payload
     */
    public GlAccountApiHandler() {
        super();
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
                 r = this.update(this.requestObj);
                break;
            case ApiTransactionCodes.GL_ACCOUNT_UPDATE:
                 r = this.delete(this.requestObj);
                break;
            case ApiTransactionCodes.GL_ACCOUNT_DELETE:
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
            
            GeneralLedgerApiFactory f = new GeneralLedgerApiFactory();
            GlAccountApi api = f.createApi(CommonAccountingConst.APP_NAME);
            List<AccountDto> dtoList = api.getAccount(criteriaDto);
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
        GeneralLedgerApiFactory f = new GeneralLedgerApiFactory();
        GlAccountApi api = f.createApi(CommonAccountingConst.APP_NAME);
        int rc = 0;
        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            this.validateRequest(req); 
            AccountDto dataObjDto = AccountingJaxbDtoFactory
                    .createGlAccountJaxbInstance(req.getProfile().getAccounts().get(0));
            newRec = (dataObjDto.getAcctId() == 0);
            
            // call api
            rc = api.updateAccount(dataObjDto);
            
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
        } catch (GeneralLedgerApiException | NotFoundException | InvalidDataException e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to update " + (newRec ? "new" : "existing")  + " GL Account");
            rs.setExtMessage(e.getMessage());
            updateData = req.getProfile().getAccounts();
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
        
        GeneralLedgerApiFactory f = new GeneralLedgerApiFactory();
        GlAccountApi api = f.createApi(CommonAccountingConst.APP_NAME);
        int rc = 0;
        AccountDto criteriaDto = null;
        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            this.validateRequest(req); 
            criteriaDto = AccountingJaxbDtoFactory
                    .createGlAccountJaxbInstance(req.getProfile().getAccounts().get(0));
            
            // call api
            rc = api.deleteAccount(criteriaDto.getAcctId());
            
            // Return code is either the total number of rows deleted
            rs.setReturnCode(rc);
            rs.setMessage("GL Account was deleted successfully");
            rs.setExtMessage("GL Account Id deleted was " + criteriaDto.getAcctId());
        } catch (GeneralLedgerApiException | InvalidDataException e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to delelte GL Account by acct id, " + criteriaDto.getAcctId());
            rs.setExtMessage(e.getMessage());
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
//   /**
//    * 
//    * @param criteria
//    * @return
//    */
//   private AccountDto extractSelectionCriteria(GlCriteriaType criteria) {
//       AccountDto criteriaDto = Rmt2AccountDtoFactory.createAccountInstance(null);
//       if (criteria != null) {
//           if (criteria.getAcctId() != null) {
//               criteriaDto.setAcctId(criteria.getAcctId().intValue());    
//           }
//           if (criteria.getAcctType() != null && criteria.getAcctType().getAcctTypeId() != null) {
//               criteriaDto.setAcctTypeId(criteria.getAcctType().getAcctTypeId().intValue());    
//           }
//           if (criteria.getAcctCatg() != null && criteria.getAcctCatg().getAcctCatgId() != null) {
//               criteriaDto.setAcctCatgId(criteria.getAcctCatg().getAcctCatgId().intValue());    
//           }
//           criteriaDto.setAcctNo(criteria.getAccountNo());
//           criteriaDto.setAcctCode(criteria.getAccountNo());
//           criteriaDto.setAcctName(criteria.getAccountName());
//       }
//       return criteriaDto;
//   }
//   
//   private LookupGroupDto extractJaxbObject(GlCriteriaType cgtList) {
//       AccountDto dto = Rmt2AddressBookDtoFactory.getNewCodeGroupInstance();
//       
//       if (jaxbObj.getGroupId() != null) {
//           dto.setGrpId(jaxbObj.getGroupId().intValue());    
//       }
//       dto.setGrpDescr(jaxbObj.getGroupDesc());
//       return dto;
//   }
   
//    /**
//     * Validates the request's list of Lookup Groups.
//     */
//    private CodeGroupType validateJaxbData(List<CodeGroupType> cgtList) {
//        try {
//            Verifier.verifyNotEmpty(cgtList);
//        }
//        catch (VerifyException e) {
//            throw new InvalidDataException("AddressBook Lookup Group List is required");
//        }
//        
//        try {
//            Verifier.verifyTrue(cgtList.size() == 1);
//        }
//        catch (VerifyException e) {
//            throw new InvalidDataException("Only one Lookup Group object can be updated at a time");
//        }
//        return cgtList.get(0);
//    }
    
    
    @Override
    protected void validateRequest(AccountingGeneralLedgerRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("LookupCodes message request element is invalid");
        }
        
        // Vakudate request for update/delete operation
        switch (this.command) {
            case ApiTransactionCodes.GL_ACCOUNT_UPDATE:
            case ApiTransactionCodes.GL_ACCOUNT_DELETE:
                try {
                    Verifier.verifyNotNull(req.getProfile());
                    Verifier.verifyNotNull(req.getProfile().getAccounts());
                    Verifier.verifyNotEmpty(req.getProfile().getAccounts());
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException("GL Account data is required for update/delete operation");
                }
                try {
                    Verifier.verifyTrue(req.getProfile().getAccounts().size() == 1);
                }
                catch (VerifyException e) {
                    throw new InvalidRequestException("Only one (1) GL Account record is required for update/delete operation");
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
            this.responseObj.getProfile().getAccounts().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

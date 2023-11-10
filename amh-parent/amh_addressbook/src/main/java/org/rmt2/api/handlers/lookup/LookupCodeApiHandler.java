package org.rmt2.api.handlers.lookup;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.LookupCodeDto;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.modules.AddressBookConstants;
import org.modules.lookup.LookupDataApi;
import org.modules.lookup.LookupDataApiException;
import org.modules.lookup.LookupDataApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.CodeDetailType;
import org.rmt2.jaxb.LookupCodeCriteriaType;
import org.rmt2.jaxb.LookupCodesRequest;
import org.rmt2.jaxb.LookupCodesResponse;
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
import com.api.messaging.webservice.WebServiceConstants;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Lookup Code related messages to the AddressBook
 * API.
 * 
 * @author roy.terrell
 *
 */
public class LookupCodeApiHandler extends
        AbstractJaxbMessageHandler<LookupCodesRequest, LookupCodesResponse, List<CodeDetailType>> {
    private static final Logger logger = Logger.getLogger(LookupCodeApiHandler.class);
    private ObjectFactory jaxbObjFactory;

    // IS-70: Added as member variable to help control DB memory leaks
    private LookupDataApi api;

    /**
     * Default constructor.
     * 
     * @param payload
     */
    public LookupCodeApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createLookupCodesResponse();
        // IS-70: added logic to close DB connections to prevent memeoy leaks
        LookupDataApiFactory f = new LookupDataApiFactory();
        this.api = f.createApi(AddressBookConstants.APP_NAME);

        // UI-37: Added for capturing the update user id
        this.transApi = this.api;
        logger.info(LookupCodeApiHandler.class.getName() + " was instantiated successfully");
    }

    // IS-70: Created to close DB connections to prevent memeoy leaks
    private void shutDown() {
        this.api.close();
        this.api = null;
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
            case ApiTransactionCodes.LOOKUP_CODE_UPDATE:
                 r = this.updateCode(this.requestObj);
                break;
            case ApiTransactionCodes.LOOKUP_CODE_DELETE:
                 r = this.deleteCode(this.requestObj);
                break;
            case ApiTransactionCodes.LOOKUP_CODE_GET:
                r = this.fetchCode(this.requestObj);
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
     * Lookup Code objects.
     * 
     * @param req
     *            an instance of {@link LookupCodesRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetchCode(LookupCodesRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<CodeDetailType> cdtList = null;

        try {
            // Set reply status
            rs.setReturnStatus(WebServiceConstants.RETURN_STATUS_SUCCESS);            
            this.validateRequest(req);
            LookupCodeDto criteriaDto = this.extractSelectionCriteria(req.getCriteria());
            List<LookupCodeDto> dtoList = api.getCode(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("Code Detail Lookup data not found!");
                rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
                rs.setRecordCount(0);
            }
            else {
                cdtList = this.buildJaxbListData(dtoList);
                rs.setMessage("Code Detail Lookup record(s) found");
                rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
                rs.setRecordCount(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
            
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve Lookup Code Detail(s)");
            rs.setExtMessage(e.getMessage());
        } finally {
            // IS-70: added logic to close DB connections to prevent memeoy
            // leaks
            this.shutDown();
        }
        String xml = this.buildResponse(cdtList, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to update the specified
     * Lookup Code.
     * 
     * @param req
     *            an instance of {@link LookupCodesRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults updateCode(LookupCodesRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<CodeDetailType> cdtList = null;
        
        boolean newRec = false;
        int rc = 0;
        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.validateRequest(req); 
            LookupCodeDto dataObjDto = this.extractJaxbObject(req.getDetailCodes());
            newRec = (dataObjDto.getCodeId() == 0);
            
            // call api
            api.beginTrans();
            rc = api.updateCode(dataObjDto);
            
            // prepare response with updated contact data
            List<LookupCodeDto> updateList = new ArrayList<>();
            updateList.add(dataObjDto);
            cdtList = this.buildJaxbListData(updateList);
            
            // Return code is either the total number of rows updated or the new code id
            rs.setRecordCount(rc);
            if (newRec) {
                rs.setMessage("Lookup Code was created successfully");
                rs.setExtMessage("The new code id is " + rc);
                rs.setRecordCount(1);
            }
            else {
                rs.setMessage("Lookup Code was modified successfully");
                rs.setExtMessage("Total number of rows modified: " + rc);
            }
            api.commitTrans();
        } catch (LookupDataApiException | NotFoundException | InvalidDataException e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage("Failure to update " + (newRec ? "new" : "existing")  + " Lookup Code");
            rs.setExtMessage(e.getMessage());
            cdtList = req.getDetailCodes();
            api.rollbackTrans();
        } finally {
            // IS-70: added logic to close DB connections to prevent memeoy
            // leaks
            this.shutDown();
        }
        
        String xml = this.buildResponse(cdtList, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to delete the specified
     * Lookup Code.
     * 
     * @param req
     *            an instance of {@link LookupCodesRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults deleteCode(LookupCodesRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        
        int rc = 0;
        LookupCodeDto criteriaDto = null;
        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.validateRequest(req); 
            criteriaDto = this.extractSelectionCriteria(req.getCriteria());
            
            // call api
            api.beginTrans();
            rc = api.deleteCode(criteriaDto.getCodeId());
            
            // Return code is either the total number of rows deleted
            rs.setRecordCount(rc);
            rs.setMessage("Lookup Code was deleted successfully");
            rs.setExtMessage("Lookup Code Id deleted was " + criteriaDto.getCodeId());
            api.commitTrans();
        } catch (LookupDataApiException | InvalidDataException e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to delelte Lookup Code by code id, " + criteriaDto.getCodeId());
            rs.setExtMessage(e.getMessage());
            rs.setRecordCount(0);
            api.rollbackTrans();
        } finally {
            // IS-70: added logic to close DB connections to prevent memeoy
            // leaks
            this.shutDown();
        }
        
        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<CodeDetailType> buildJaxbListData(List<LookupCodeDto> results) {
        List<CodeDetailType> list = new ArrayList<>();
        for (LookupCodeDto item : results) {
            CodeDetailType jaxbObj = jaxbObjFactory.createCodeDetailType();
            jaxbObj.setGroupId(BigInteger.valueOf(item.getGrpId()));
            jaxbObj.setCodeId(BigInteger.valueOf(item.getCodeId()));
            jaxbObj.setLongdesc(item.getCodeLongName());
            jaxbObj.setShortdesc(item.getCodeShortName());
            list.add(jaxbObj);
        }
        return list;
    }
   /**
    * 
    * @param criteria
    * @return
    */
   private LookupCodeDto extractSelectionCriteria(LookupCodeCriteriaType criteria) {
       LookupCodeDto criteriaDto = Rmt2AddressBookDtoFactory.getNewCodeInstance();
       if (criteria != null) {
           if (criteria.getGroup() != null) {
               criteriaDto.setGrpId(criteria.getGroup().intValue());    
           }
           if (criteria.getCode() != null) {
               criteriaDto.setCodeId(criteria.getCode().intValue());    
           }
           criteriaDto.setCodeLongName(criteria.getCodeLongDescription());
           criteriaDto.setCodeShortDesc(criteria.getCodeShortDescription());
       }
       return criteriaDto;
   }
   
   private LookupCodeDto extractJaxbObject(List<CodeDetailType> cdtList) {
       CodeDetailType jaxbObj = this.validateJaxbData(cdtList);
       LookupCodeDto dto = Rmt2AddressBookDtoFactory.getNewCodeInstance();
       
       if (jaxbObj.getGroupId() != null) {
           dto.setGrpId(jaxbObj.getGroupId().intValue());    
       }
       if (jaxbObj.getCodeId() != null) {
           dto.setCodeId(jaxbObj.getCodeId().intValue());    
       }
       dto.setCodeLongName(jaxbObj.getLongdesc());
       dto.setCodeShortDesc(jaxbObj.getShortdesc());
       return dto;
   }
   
    /**
     * Validates the request's list of Lookup Codes.
     */
    private CodeDetailType validateJaxbData(List<CodeDetailType> cdtList) {
        try {
            Verifier.verifyNotEmpty(cdtList);
        }
        catch (VerifyException e) {
            throw new InvalidDataException("AddressBook Lookup Code List is required");
        }
        
        try {
            Verifier.verifyTrue(cdtList.size() == 1);
        }
        catch (VerifyException e) {
            throw new InvalidDataException("Only one Lookup Code object can be updated at a time");
        }
        return cdtList.get(0);
    }
    
    
    @Override
    protected void validateRequest(LookupCodesRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("LookupCodes message request element is invalid");
        }
    }

    @Override
    protected String buildResponse(List<CodeDetailType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            this.responseObj.getDetailCodes().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

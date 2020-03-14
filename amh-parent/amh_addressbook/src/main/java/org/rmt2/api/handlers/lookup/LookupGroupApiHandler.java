package org.rmt2.api.handlers.lookup;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.LookupGroupDto;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.modules.AddressBookConstants;
import org.modules.lookup.LookupDataApi;
import org.modules.lookup.LookupDataApiException;
import org.modules.lookup.LookupDataApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.CodeGroupType;
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
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Lookup group related messages to the AddressBook
 * API.
 * 
 * @author roy.terrell
 *
 */
public class LookupGroupApiHandler extends 
                  AbstractJaxbMessageHandler<LookupCodesRequest, LookupCodesResponse, List<CodeGroupType>> {
    
    private static final Logger logger = Logger.getLogger(LookupGroupApiHandler.class);
    private ObjectFactory jaxbObjFactory;

    /**
     * @param payload
     */
    public LookupGroupApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createLookupCodesResponse();
        logger.info(LookupGroupApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.LOOKUP_GROUP_UPDATE:
                 r = this.updateGroup(this.requestObj);
                break;
            case ApiTransactionCodes.LOOKUP_GROUP_DELETE:
                 r = this.deleteGroup(this.requestObj);
                break;
            case ApiTransactionCodes.LOOKUP_GROUP_GET:
                r = this.fetchGroup(this.requestObj);
                break;
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to fetch one or more Lookup Group objects.
     * <p>
     * This method is capable of processing personal, business, or generic
     * contact types.
     * 
     * @param req
     *            an instance of {@link LookupCodesRequest}
     * @return an instance of {@link MessageHandlerResults}           
     */
    protected MessageHandlerResults fetchGroup(LookupCodesRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<CodeGroupType> cdgList = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            this.validateRequest(req);
            LookupGroupDto criteriaDto = this.extractSelectionCriteria(req.getCriteria());
            
            LookupDataApiFactory f = new LookupDataApiFactory();
            LookupDataApi api = f.createApi(AddressBookConstants.APP_NAME);
            List<LookupGroupDto> dtoList = api.getGroup(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("Group Lookup data not found!");
                rs.setReturnCode(0);
            }
            else {
                cdgList = this.buildJaxbListData(dtoList);
                rs.setMessage("Group Lookup record(s) found");
                rs.setReturnCode(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve Lookup Group(s)");
            rs.setExtMessage(e.getMessage());
        }
        String xml = this.buildResponse(cdgList, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to update the specified
     * Lookup Group.
     * 
     * @param req
     *            an instance of {@link LookupCodesRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults updateGroup(LookupCodesRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<CodeGroupType> cdgList = null;
        
        boolean newRec = false;
        LookupDataApiFactory f = new LookupDataApiFactory();
        LookupDataApi api = f.createApi(AddressBookConstants.APP_NAME);
        int rc = 0;
        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            this.validateRequest(req); 
            LookupGroupDto dataObjDto = this.extractJaxbObject(req.getGroupCodes());
            newRec = (dataObjDto.getGrpId() == 0);
            
            // call api
            api.beginTrans();
            rc = api.updateGroup(dataObjDto);
            
            // prepare response with updated contact data
            List<LookupGroupDto> updateList = new ArrayList<>();
            updateList.add(dataObjDto);
            cdgList = this.buildJaxbListData(updateList);
            
            // Return code is either the total number of rows updated or the new group id
            rs.setReturnCode(rc);
            if (newRec) {
                rs.setMessage("Lookup Group was created successfully");
                rs.setExtMessage("The new group id is " + rc);
            }
            else {
                rs.setMessage("Lookup Group was modified successfully");
                rs.setExtMessage("Total number of rows modified: " + rc);
            }
            api.commitTrans();
        } catch (LookupDataApiException | NotFoundException | InvalidDataException e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to update " + (newRec ? "new" : "existing")  + " Lookup Group");
            rs.setExtMessage(e.getMessage());
            cdgList = req.getGroupCodes();
            api.rollbackTrans();
        } finally {
            api.close();
        }
        
        String xml = this.buildResponse(cdgList, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to delete the specified
     * Lookup Group.
     * 
     * @param req
     *            an instance of {@link LookupCodesRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults deleteGroup(LookupCodesRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        
        LookupDataApiFactory f = new LookupDataApiFactory();
        LookupDataApi api = f.createApi(AddressBookConstants.APP_NAME);
        int rc = 0;
        LookupGroupDto criteriaDto = null;
        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            this.validateRequest(req); 
            criteriaDto = this.extractSelectionCriteria(req.getCriteria());
            
            // call api
            api.beginTrans();
            rc = api.deleteGroup(criteriaDto.getGrpId());
            
            // Return code is either the total number of rows deleted
            rs.setReturnCode(rc);
            rs.setMessage("Lookup Group was deleted successfully");
            rs.setExtMessage("Lookup Group Id deleted was " + criteriaDto.getGrpId());
            api.commitTrans();
        } catch (LookupDataApiException | InvalidDataException e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to delelte Lookup Group by group id, " + criteriaDto.getGrpId());
            rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            api.close();
        }
        
        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<CodeGroupType> buildJaxbListData(List<LookupGroupDto> results) {
        List<CodeGroupType> list = new ArrayList<>();
        for (LookupGroupDto item : results) {
            CodeGroupType jaxbObj = jaxbObjFactory.createCodeGroupType();
            jaxbObj.setGroupId(BigInteger.valueOf(item.getGrpId()));
            jaxbObj.setGroupDesc(item.getGrpDescr());
            list.add(jaxbObj);
        }
        return list;
    }
   /**
    * 
    * @param criteria
    * @return
    */
   private LookupGroupDto extractSelectionCriteria(LookupCodeCriteriaType criteria) {
       LookupGroupDto criteriaDto = Rmt2AddressBookDtoFactory.getNewCodeGroupInstance();
       if (criteria != null) {
           if (criteria.getGroup() != null) {
               criteriaDto.setGrpId(criteria.getGroup().intValue());    
           }
           criteriaDto.setGrpDescr(criteria.getGroupDescription());
       }
       return criteriaDto;
   }
   
   private LookupGroupDto extractJaxbObject(List<CodeGroupType> cgtList) {
       CodeGroupType jaxbObj = this.validateJaxbData(cgtList);
       LookupGroupDto dto = Rmt2AddressBookDtoFactory.getNewCodeGroupInstance();
       
       if (jaxbObj.getGroupId() != null) {
           dto.setGrpId(jaxbObj.getGroupId().intValue());    
       }
       dto.setGrpDescr(jaxbObj.getGroupDesc());
       return dto;
   }
   
    /**
     * Validates the request's list of Lookup Groups.
     */
    private CodeGroupType validateJaxbData(List<CodeGroupType> cgtList) {
        try {
            Verifier.verifyNotEmpty(cgtList);
        }
        catch (VerifyException e) {
            throw new InvalidDataException("AddressBook Lookup Group List is required");
        }
        
        try {
            Verifier.verifyTrue(cgtList.size() == 1);
        }
        catch (VerifyException e) {
            throw new InvalidDataException("Only one Lookup Group object can be updated at a time");
        }
        return cgtList.get(0);
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
    protected String buildResponse(List<CodeGroupType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            this.responseObj.getGroupCodes().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

package org.rmt2.api.handlers.postal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ZipcodeDto;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.dto.converter.jaxb.ContactsJaxbFactory;
import org.modules.AddressBookConstants;
import org.modules.postal.PostalApi;
import org.modules.postal.PostalApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.PostalRequest;
import org.rmt2.jaxb.PostalResponse;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.jaxb.ZipResultFormatType;
import org.rmt2.jaxb.ZipcodeCriteriaType;
import org.rmt2.jaxb.ZipcodeFullType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.rmt2.jaxb.ZipcodeType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.messaging.webservice.WebServiceConstants;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes ZipCode related messages to the AddressBook
 * API.
 * 
 * @author roy.terrell
 *
 */
public class ZipCodeApiHandler extends AbstractJaxbMessageHandler<PostalRequest, PostalResponse, List> {
    
    private static final Logger logger = Logger.getLogger(ZipCodeApiHandler.class);
    private ObjectFactory jaxbObjFactory;
    private ZipResultFormatType queryResultFormat;

    /**
     * @param payload
     */
    public ZipCodeApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createPostalResponse();
        logger.info(ZipCodeApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.ZIPCODE_GET:
                r = this.fetchZipcode(this.requestObj);
                break;
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to fetch one or more Zipcode objects.
     * 
     * @param req
     *            an instance of {@link PostalRequest}
     * @return an instance of {@link MessageHandlerResults}           
     */
    protected MessageHandlerResults fetchZipcode(PostalRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List queryResults = null;
        PostalApi api = null;
        try {
            // Set reply status
            rs.setReturnStatus(WebServiceConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            this.validateCriteria(req);
            this.queryResultFormat = req.getPostalCriteria().getZipcode().getResultFormat();
            ZipcodeDto criteriaDto = this.extractSelectionCriteria(req.getPostalCriteria().getZipcode());
            
            api = PostalApiFactory.createApi(AddressBookConstants.APP_NAME);
            List<ZipcodeDto> dtoList = api.getZipCode(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("No Zipcode data not found!");
                rs.setRecordCount(0);
            }
            else {
                queryResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Zipcode record(s) found");
                rs.setRecordCount(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
            
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve Zipcode data");
            rs.setExtMessage(e.getMessage());
        } finally {
            // IS-70: added logic to close DB connections to prevent memeoy
            // leaks
            if (api != null) {
                api.close();
            }
        }
        results.setReturnCode(rs.getReturnCode());
        String xml = this.buildResponse(queryResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    
    private List buildJaxbListData(List<ZipcodeDto> results) {
        if (this.queryResultFormat == ZipResultFormatType.FULL) {
            return this.buildFullResultTypeList(results);
        }
        else {
            return this.buildShortResultTypeList(results);
        }
    }
    
    private List<ZipcodeFullType> buildFullResultTypeList(List<ZipcodeDto> results) {
        List<ZipcodeFullType> list = new ArrayList<>();
        for (ZipcodeDto item : results) {
            ZipcodeFullType jaxbObj = ContactsJaxbFactory.getZipFullTypeInstance(item);
            list.add(jaxbObj);
        }
        return list;
    }
    
    private List<ZipcodeType> buildShortResultTypeList(List<ZipcodeDto> results) {
        List<ZipcodeType> list = new ArrayList<>();
        for (ZipcodeDto item : results) {
            ZipcodeType jaxbObj = ContactsJaxbFactory.getZipShortInstance(item);
            list.add(jaxbObj);
        }
        return list;
    }
    
   /**
    * 
    * @param criteria
    * @return
    */
   private ZipcodeDto extractSelectionCriteria(ZipcodeCriteriaType criteria) {
       ZipcodeDto criteriaDto = Rmt2AddressBookDtoFactory.getNewZipCodeInstance();
       if (criteria != null) {
           if (criteria.getZipcode() != null) {
               criteriaDto.setZip(criteria.getZipcode().intValue());    
           }
           criteriaDto.setCity(criteria.getCity());
            criteriaDto.setStateCode(criteria.getState());
           criteriaDto.setAreaCode(criteria.getAreaCode());
           criteriaDto.setCountyName(criteria.getCountyName());
       }
       return criteriaDto;
   }
   
    
    @Override
    protected void validateRequest(PostalRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("PostalRequest message request element is invalid", e);
        }
    }
    
    private void validateCriteria(PostalRequest req) throws InvalidRequestException {
        try {
            Verifier.verifyNotNull(req.getPostalCriteria());
            Verifier.verifyNotNull(req.getPostalCriteria().getZipcode());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("PostalRequest zipcode criteria element is invalid", e);
        }
        
        // Verify that the result format type is specified
        try {
            Verifier.verifyNotNull(req.getPostalCriteria().getZipcode().getResultFormat());
            Verifier.verifyNotEmpty(req.getPostalCriteria().getZipcode().getResultFormat().name());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("The Result Format indicator is required and must be valid", e);
        }
    }

    /**
     * Builds the response payload as either a List<ZipcodeFullType> or as a
     * List<ZipcodeType> type, if available.
     * 
     * @param payload
     *            a raw List masked as either List<ZipcodeFullType> or
     *            List<ZipcodeType> type.
     * @param replyStatus
     * @return XML String
     */
    @Override
    protected String buildResponse(List payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);
        }
        
        if (payload != null) {
            if (this.queryResultFormat.equals(ZipResultFormatType.FULL)) {
                List<ZipcodeFullType> fullFormatResultList = (List<ZipcodeFullType>) ((List<?>) payload);
                this.responseObj.getZipFull().addAll(fullFormatResultList);    
            }
            else {
                List<ZipcodeType> shortFormatResultList = (List<ZipcodeType>) ((List<?>) payload);
                this.responseObj.getZipShort().addAll(shortFormatResultList);    
            }
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

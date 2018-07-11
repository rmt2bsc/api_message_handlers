package org.rmt2.api.handlers.postal;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.TimeZoneDto;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
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
import org.rmt2.jaxb.TimezoneCriteriaType;
import org.rmt2.jaxb.TimezoneType;

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
 * Handles and routes TimeZone information related messages to the AddressBook
 * API.
 * 
 * @author roy.terrell
 *
 */
public class TimezoneApiHandler extends AbstractJaxbMessageHandler<PostalRequest, PostalResponse, List<TimezoneType>> {
    
    private static final Logger logger = Logger.getLogger(TimezoneApiHandler.class);
    private ObjectFactory jaxbObjFactory;

    /**
     * @param payload
     */
    public TimezoneApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createPostalResponse();
        logger.info(TimezoneApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.TIMEZONE_GET:
                r = this.fetch(this.requestObj);
                break;
            default:
                r = this.createErrorReply(-1, ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to fetch IP information.
     * 
     * @param req
     *            an instance of {@link PostalRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetch(PostalRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<TimezoneType> queryResults = null;

        try {
            this.validateRequest(req);
            TimeZoneDto criteriaDto = this.extractSelectionCriteria(req.getPostalCriteria().getTimezone());
            
            PostalApi api = PostalApiFactory.createApi(AddressBookConstants.APP_NAME);
            List<TimeZoneDto> dtoList = null;
            dtoList = api.getTimezone(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("No Timezone data found!");
                rs.setReturnCode(0);
            }
            else {
                queryResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Timezone record(s) found");
                rs.setReturnCode(1);
            }
            this.responseObj.setHeader(req.getHeader());
            // Set reply status
            rs.setReturnStatus(WebServiceConstants.RETURN_STATUS_SUCCESS);
        } catch (Exception e) {
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setReturnStatus(WebServiceConstants.RETURN_STATUS_ERROR);
            rs.setMessage("Failure to retrieve Timezone data");
            rs.setExtMessage(e.getMessage());
        }
        results.setReturnCode(rs.getReturnCode());
        String xml = this.buildResponse(queryResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    
    private List<TimezoneType> buildJaxbListData(List<TimeZoneDto> results) {
        List<TimezoneType> list = new ArrayList<>();
        ObjectFactory fact = new ObjectFactory();
        for (TimeZoneDto item : results) {
            TimezoneType jaxbObj = fact.createTimezoneType();
            jaxbObj.setTimeszoneDesc(item.getTimeZoneDescr());
            jaxbObj.setTimezoneId(BigInteger.valueOf(item.getTimeZoneId()));
            list.add(jaxbObj);
        }
        return list;
    }
    
   /**
    * 
    * @param criteria
    * @return
    */
    private TimeZoneDto extractSelectionCriteria(TimezoneCriteriaType criteria) {
        TimeZoneDto criteriaDto = Rmt2AddressBookDtoFactory.getNewTimezoneInstance();
        if (criteria != null) {
            criteriaDto.setTimeZoneId(criteria.getTimezoneId());
            criteriaDto.setTimeZoneDescr(criteria.getTimezoneDesc());
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
        
        try {
            Verifier.verifyNotNull(req.getPostalCriteria());
            Verifier.verifyNotNull(req.getPostalCriteria().getIpAddr());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("PostalRequest IP criteria element is required", e);
        }
    }
    
    /**
     * Builds the response payload as a IpDetails type.
     * 
     * @param payload
     *            a raw List masked as a IpDetails.
     * @param replyStatus
     * @return XML String
     */
    @Override
    protected String buildResponse(List<TimezoneType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);   
        }
        
        if (payload != null) {
            this.responseObj.getTimezones().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

package org.rmt2.api.handlers.postal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.CountryRegionDto;
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
import org.rmt2.jaxb.StateType;
import org.rmt2.jaxb.StatesCriteriaType;

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
 * Handles and routes Region/State/Province related messages to the AddressBook
 * API.
 * 
 * @author roy.terrell
 *
 */
public class RegionApiHandler extends AbstractJaxbMessageHandler<PostalRequest, PostalResponse, List<StateType>> {
    
    private static final Logger logger = Logger.getLogger(RegionApiHandler.class);
    private ObjectFactory jaxbObjFactory;

    /**
     * @param payload
     */
    public RegionApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createPostalResponse();
        logger.info(RegionApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.REGION_GET:
                r = this.fetchStateRegion(this.requestObj);
                break;
            default:
                r = this.createErrorReply(-1, ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to fetch one or more
     * State objects.
     * <p>
     * Targets the API method responsible for returning a result set composed of
     * detailed state/province and country information.
     * 
     * @param req
     *            an instance of {@link PostalRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetchStateRegion(PostalRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<StateType> queryResults = null;

        try {
            this.validateCriteria(req);
            CountryRegionDto criteriaDto = this.extractSelectionCriteria(req.getPostalCriteria().getProvince());
            
            PostalApi api = PostalApiFactory.createApi(AddressBookConstants.APP_NAME);
            List<CountryRegionDto> dtoList = api.getCountryRegion(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("No Region/State/Province data found!");
                rs.setReturnCode(0);
            }
            else {
                queryResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Region/State/Province record(s) found");
                rs.setReturnCode(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
            // Set reply status
            rs.setReturnStatus(WebServiceConstants.RETURN_STATUS_SUCCESS);
        } catch (Exception e) {
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setReturnStatus(WebServiceConstants.RETURN_STATUS_ERROR);
            rs.setMessage("Failure to retrieve Region/State/Province data");
            rs.setExtMessage(e.getMessage());
        }
        results.setReturnCode(rs.getReturnCode());
        String xml = this.buildResponse(queryResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    
    private List<StateType> buildJaxbListData(List<CountryRegionDto> results) {
        List<StateType> list = new ArrayList<>();
        for (CountryRegionDto item : results) {
            StateType jaxbObj = ContactsJaxbFactory.createStateTypeInstance(item.getStateId(), 
                    item.getStateName(), item.getStateCode(), item.getCountryId(), 
                    item.getCountryName());
            list.add(jaxbObj);
        }
        return list;
    }
    
   /**
    * 
    * @param criteria
    * @return
    */
   private CountryRegionDto extractSelectionCriteria(StatesCriteriaType criteria) {
       CountryRegionDto criteriaDto = Rmt2AddressBookDtoFactory.getNewCountryRegionInstance();
       if (criteria != null) {
            if (criteria.getCountryId() != null) {
                criteriaDto.setCountryId(criteria.getCountryId().intValue());
            }
            if (criteria.getStateId() != null) {
                criteriaDto.setStateId(criteria.getStateId().intValue());
            }
            criteriaDto.setStateName(criteria.getStateName());
            criteriaDto.setStateCode(criteria.getStateCode());
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
            Verifier.verifyNotNull(req.getPostalCriteria().getProvince());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("PostalRequest stae/province criteria element is invalid", e);
        }
    }

    /**
     * Builds the response payload as a List<CountryType> type.
     * 
     * @param payload
     *            a raw List masked as a List<CountryType>.
     * @param replyStatus
     * @return XML String
     */
    @Override
    protected String buildResponse(List<StateType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);
        }
        
        if (payload != null) {
            this.responseObj.getStates().addAll(payload);  
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

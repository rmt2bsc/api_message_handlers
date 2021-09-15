package org.rmt2.api.handlers.postal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.CountryDto;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.dto.converter.jaxb.ContactsJaxbFactory;
import org.modules.AddressBookConstants;
import org.modules.postal.PostalApi;
import org.modules.postal.PostalApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.CountryCriteriaType;
import org.rmt2.jaxb.CountryType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.PostalRequest;
import org.rmt2.jaxb.PostalResponse;
import org.rmt2.jaxb.ReplyStatusType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Country related messages to the AddressBook API.
 * 
 * @author roy.terrell
 *
 */
public class CountryApiHandler extends AbstractJaxbMessageHandler<PostalRequest, PostalResponse, List<CountryType>> {
    
    private static final Logger logger = Logger.getLogger(CountryApiHandler.class);
    private ObjectFactory jaxbObjFactory;

    /**
     * @param payload
     */
    public CountryApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createPostalResponse();
        logger.info(CountryApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.COUNTRY_GET:
                r = this.fetchCountry(this.requestObj);
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
     * Country objects.
     * 
     * @param req
     *            an instance of {@link PostalRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetchCountry(PostalRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<CountryType> queryResults = null;
        PostalApi api = null;
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            
            this.validateCriteria(req);
            CountryDto criteriaDto = this.extractSelectionCriteria(req.getPostalCriteria().getCountry());
            
            api = PostalApiFactory.createApi(AddressBookConstants.APP_NAME);
            List<CountryDto> dtoList = api.getCountry(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("No Country data found!");
                rs.setRecordCount(0);
            }
            else {
                queryResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Country record(s) found");
                rs.setRecordCount(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve Country data");
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
    
    
    private List<CountryType> buildJaxbListData(List<CountryDto> results) {
        List<CountryType> list = new ArrayList<>();
        for (CountryDto item : results) {
            CountryType jaxbObj = ContactsJaxbFactory.createCountryTypeInstance(item.getCountryId(), 
                    item.getCountryName(), item.getCountryCode());
            list.add(jaxbObj);
        }
        return list;
    }
    
   /**
    * 
    * @param criteria
    * @return
    */
   private CountryDto extractSelectionCriteria(CountryCriteriaType criteria) {
       CountryDto criteriaDto = Rmt2AddressBookDtoFactory.getNewCountryInstance();
       if (criteria != null) {
            if (criteria.getCountryId() != null) {
                criteriaDto.setCountryId(criteria.getCountryId().intValue());
            }
            if (criteria.getCountyName() != null) {
                criteriaDto.setCountryName(criteria.getCountyName());
            }
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
            Verifier.verifyNotNull(req.getPostalCriteria().getCountry());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("PostalRequest country criteria element is invalid", e);
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
    protected String buildResponse(List<CountryType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);   
        }
        
        if (payload != null) {
            this.responseObj.getCountries().addAll(payload);  
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

package org.rmt2.api.handlers.postal;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.dto.IpLocationDto;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.dto.converter.jaxb.ContactsJaxbFactory;
import org.modules.AddressBookConstants;
import org.modules.postal.PostalApi;
import org.modules.postal.PostalApiFactory;
import org.rmt2.api.handlers.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.IpCriteriaType;
import org.rmt2.jaxb.IpDetails;
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
import com.api.messaging.webservice.WebServiceConstants;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes IP information related messages to the AddressBook
 * API.
 * 
 * @author roy.terrell
 *
 */
public class IpInfoApiHandler extends AbstractJaxbMessageHandler<PostalRequest, PostalResponse, IpDetails> {
    
    private static final Logger logger = Logger.getLogger(IpInfoApiHandler.class);
    private ObjectFactory jaxbObjFactory;

    /**
     * @param payload
     */
    public IpInfoApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createPostalResponse();
        logger.info(IpInfoApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.IP_INFO_GET:
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
        IpDetails queryResults = null;

        try {
            this.validateRequest(req);
            IpLocationDto criteriaDto = this.extractSelectionCriteria(req.getPostalCriteria().getIpAddr());
            
            PostalApi api = PostalApiFactory.createApi(AddressBookConstants.APP_NAME);
            IpLocationDto dtoList = null;
            if (!criteriaDto.getStandardIp().isEmpty()) {
                dtoList = api.getIpInfo(criteriaDto.getStandardIp());    
            } else {
                dtoList = api.getIpInfo(criteriaDto.getIpRangeId());
            }
            if (dtoList == null) {
                rs.setMessage("No IP data found!");
                rs.setReturnCode(0);
            }
            else {
                queryResults = this.buildJaxbListData(dtoList);
                rs.setMessage("IP record(s) found");
                rs.setReturnCode(1);
            }
            this.responseObj.setHeader(req.getHeader());
            // Set reply status
            rs.setReturnStatus(WebServiceConstants.RETURN_STATUS_SUCCESS);
        } catch (Exception e) {
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setReturnStatus(WebServiceConstants.RETURN_STATUS_ERROR);
            rs.setMessage("Failure to retrieve IP data");
            rs.setExtMessage(e.getMessage());
        }
        results.setReturnCode(rs.getReturnCode());
        String xml = this.buildResponse(queryResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    
    private IpDetails buildJaxbListData(IpLocationDto results) {
        IpDetails jaxbObj = ContactsJaxbFactory.getIpDetailsInstance(results);
        return jaxbObj;
    }
    
   /**
    * 
    * @param criteria
    * @return
    */
    private IpLocationDto extractSelectionCriteria(IpCriteriaType criteria) {
        IpLocationDto criteriaDto = Rmt2AddressBookDtoFactory.getNewIpLocationInstance();
        if (criteria != null) {
            criteriaDto.setStandardIp(criteria.getIpStandard());
            criteriaDto.setIpRangeId(criteria.getIpNetwork());
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
            throw new InvalidRequestException("PostalRequest IP criteria element is invalid", e);
        }
        
        try {
            Verifier.verifyFalse(req.getPostalCriteria().getIpAddr().getIpNetwork() == null && req.getPostalCriteria().getIpAddr().getIpStandard() == null);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("The PostalRequest IP criteria element must contain value", e);
        }
        
        try {
            Verifier.verifyFalse(req.getPostalCriteria().getIpAddr().getIpNetwork() != null && req.getPostalCriteria().getIpAddr().getIpStandard() != null);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("The standard IP String value and the numerical representation of the IP address must be mutually exclusive in PostalRequest IP criteria element", e);
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
    protected String buildResponse(IpDetails payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);   
        }
        
        if (payload != null) {
            this.responseObj.setIpData(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

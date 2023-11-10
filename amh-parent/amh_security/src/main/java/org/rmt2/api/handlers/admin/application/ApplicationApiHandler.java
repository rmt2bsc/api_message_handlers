package org.rmt2.api.handlers.admin.application;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.modules.application.AppApi;
import org.modules.application.AppApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ApplicationType;
import org.rmt2.jaxb.AuthProfileGroupType;
import org.rmt2.jaxb.AuthenticationRequest;
import org.rmt2.jaxb.AuthenticationResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Common Application message handler which works with the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public abstract class ApplicationApiHandler extends
        AbstractJaxbMessageHandler<AuthenticationRequest, AuthenticationResponse, List<ApplicationType>> {
    
    private static final Logger logger = Logger.getLogger(ApplicationApiHandler.class);
    protected ObjectFactory jaxbObjFactory;
    protected AppApi api;
    protected List<ApplicationType> jaxbObj;
    protected MessageHandlerCommonReplyStatus rs;

    /**
     * @param payload
     */
    public ApplicationApiHandler() {
        super();
        this.api = AppApiFactory.createApi();

        // UI-37: Added for capturing the update user id
        this.transApi = this.api;
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAuthenticationResponse();
        this.jaxbObj = null;
        logger.info(ApplicationApiHandler.class.getName() + " was instantiated successfully");
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
            // IS-70: Added logic to close api in the event an error occurred
            // which will prevent memory leaks
            if (this.api != null) {
                this.api.close();
            }
            return r;
        }
        return this.doOperation();
    }


    /**
     * Invokes the appropriate API operation to process the Application related
     * request.
     * 
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation() {
        MessageHandlerResults results = new MessageHandlerResults();
        this.rs = new MessageHandlerCommonReplyStatus();
        rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
        rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);

        // Invoke descendent specific implementation to process transaction
        this.processTransactionCode();

        // Process response
        String xml = this.buildResponse(this.jaxbObj, this.rs);
        results.setPayload(xml);
        return results;
    }

    /**
     * Implement this method to perform specific operations based on the
     * transaction code contained in the request
     * 
     */
    protected abstract void processTransactionCode();
    
    @Override
    protected void validateRequest(AuthenticationRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("LookupCodes message request element is invalid");
        }
    }

    @Override
    protected String buildResponse(List<ApplicationType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        this.responseObj.setHeader(this.requestObj.getHeader());
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            AuthProfileGroupType apgt = this.jaxbObjFactory.createAuthProfileGroupType();
            this.responseObj.setProfile(apgt);
            this.responseObj.getProfile().getApplicationInfo().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

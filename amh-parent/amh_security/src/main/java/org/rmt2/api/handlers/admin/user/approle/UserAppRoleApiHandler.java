package org.rmt2.api.handlers.admin.user.approle;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.modules.roles.RoleSecurityApiFactory;
import org.modules.roles.UserAppRoleApi;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AuthProfileGroupType;
import org.rmt2.jaxb.AuthenticationRequest;
import org.rmt2.jaxb.AuthenticationResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.jaxb.UserType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Common message handler which manages user application-roles for
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public abstract class UserAppRoleApiHandler extends
        AbstractJaxbMessageHandler<AuthenticationRequest, AuthenticationResponse, List<UserType>> {
    
    private static final Logger logger = Logger.getLogger(UserAppRoleApiHandler.class);
    protected ObjectFactory jaxbObjFactory;
    protected List<UserType> jaxbObj;
    protected MessageHandlerCommonReplyStatus rs;
    protected UserAppRoleApi api;

    /**
     * @param payload
     */
    public UserAppRoleApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAuthenticationResponse();
        this.jaxbObj = null;
        this.api = RoleSecurityApiFactory.createUserAppRoleApi();
        logger.info(UserAppRoleApiHandler.class.getName() + " was instantiated successfully");
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
        return this.doOperation();
    }


    /**
     * Invokes the appropriate API operation to process the Resources Info
     * related request.
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
            throw new InvalidRequestException("Resource Info message request element is invalid");
        }
    }

    @Override
    protected String buildResponse(List<UserType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        this.responseObj.setHeader(this.requestObj.getHeader());
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            AuthProfileGroupType apgt = this.jaxbObjFactory.createAuthProfileGroupType();
            this.responseObj.setProfile(apgt);
            this.responseObj.getProfile().getUserInfo().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

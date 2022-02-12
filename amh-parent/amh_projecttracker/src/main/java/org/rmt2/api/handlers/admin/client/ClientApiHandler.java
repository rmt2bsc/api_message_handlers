package org.rmt2.api.handlers.admin.client;

import java.util.List;

import org.apache.log4j.Logger;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.jaxb.ClientType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ProjectDetailGroup;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes client related messages to the Project Tracker
 * Administration API.
 * <p>
 * <b><u>Change Log</u></b><br>
 * IS-71: Removed use of <i>api</i> member variable which was shared amongst all
 * descendant classes, in order to prevent memory leaks.
 * 
 * @author roy.terrell
 *
 */
public class ClientApiHandler extends
        AbstractJaxbMessageHandler<ProjectProfileRequest, ProjectProfileResponse, List<ClientType>> {
    
    private static final Logger logger = Logger.getLogger(ClientApiHandler.class);
    protected ObjectFactory jaxbObjFactory;

    /**
     * @param payload
     */
    public ClientApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createProjectProfileResponse();
        logger.info(ClientApiHandler.class.getName() + " was instantiated successfully");
    }

    
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Employee message request element is invalid");
        }
    }

    @Override
    protected String buildResponse(List<ClientType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            ProjectDetailGroup profile = this.jaxbObjFactory.createProjectDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getClient().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

package org.rmt2.api.handlers.employee;

import java.util.List;

import org.apache.log4j.Logger;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.jaxb.EmployeeType;
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
 * Handles and routes Employee related messages to the Project Tracker
 * Administration API.
 * 
 * @author roy.terrell
 *
 */
public class EmployeeApiHandler extends
        AbstractJaxbMessageHandler<ProjectProfileRequest, ProjectProfileResponse, List<EmployeeType>> {
    
    // IS-71: Removed class member variable, api, which used to be shared with
    // descendant classes. This will eliminate the possibility of memory leaks
    // caused by dangling API instances. 
    private static final Logger logger = Logger.getLogger(EmployeeApiHandler.class);
    protected ObjectFactory jaxbObjFactory;

    /**
     * @param payload
     */
    public EmployeeApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createProjectProfileResponse();
        logger.info(EmployeeApiHandler.class.getName() + " was instantiated successfully");
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
    protected String buildResponse(List<EmployeeType> payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            ProjectDetailGroup profile = this.jaxbObjFactory.createProjectDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getEmployee().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

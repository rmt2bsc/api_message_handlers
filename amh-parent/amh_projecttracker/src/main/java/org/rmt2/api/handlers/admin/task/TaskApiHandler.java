package org.rmt2.api.handlers.admin.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.TaskDto;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ProjectDetailGroup;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.rmt2.jaxb.TaskType;
import org.rmt2.util.projecttracker.admin.TaskTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes task related messages to the Project Tracker
 * Administration API.
 * 
 * @author roy.terrell
 *
 */
public class TaskApiHandler extends
        AbstractJaxbMessageHandler<ProjectProfileRequest, ProjectProfileResponse, List<TaskType>> {
    
    private static final Logger logger = Logger.getLogger(TaskApiHandler.class);
    protected ObjectFactory jaxbObjFactory;
    // IS-71: Removed class member variable, api, which used to be shared with
    // descendant classes. This will eliminate the possibility of memory leaks
    // caused by dangling API instances. 

    /**
     * @param payload
     */
    public TaskApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createProjectProfileResponse();
        logger.info(TaskApiHandler.class.getName() + " was instantiated successfully");
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

    /**
     * Verifyes that the payload of an update or delete operation contains a
     * project profile with projects.
     * 
     * @param req
     * @throws InvalidDataException
     */
    protected void validateUpdateRequest(ProjectProfileRequest req) throws InvalidDataException {

        // Project profile is required
        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidDataException(ApiMessageHandlerConst.MSG_MISSING_PROFILE_DATA, e);
        }
    }

    /**
     * Build JAXB project object to be returned as part of the task profile
     * response for update and delete operations.
     * 
     * @param dto
     *            instance of {@link TaskDto}
     * @return List of {@link TaskType} instances
     */
    protected List<TaskType> buildJaxbUpdateResults(TaskDto dto) {
        List<TaskType> results = new ArrayList<>();
        if (dto == null) {
            return results;
        }
        TaskType obj = TaskTypeBuilder.Builder.create()
                .withTaskId(dto.getTaskId())
                .withTaskName(dto.getTaskDescription())
                .withBillableFlag(dto.getTaskBillable())
                .build();

        results.add(obj);
        return results;
    }

    @Override
    protected String buildResponse(List<TaskType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            ProjectDetailGroup profile = this.jaxbObjFactory.createProjectDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getTask().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

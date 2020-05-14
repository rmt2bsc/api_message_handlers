package org.rmt2.api.handlers.admin.project;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ProjectClientDto;
import org.dto.Project2Dto;
import org.modules.ProjectTrackerApiConst;
import org.modules.admin.ProjectAdminApi;
import org.modules.admin.ProjectAdminApiFactory;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ProjectDetailGroup;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.ProjectType;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.rmt2.util.projecttracker.admin.ProjectTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes project related messages to the Project Tracker
 * Administration API.
 * 
 * @author roy.terrell
 *
 */
public class ProjectApiHandler extends
        AbstractJaxbMessageHandler<ProjectProfileRequest, ProjectProfileResponse, List<ProjectType>> {
    
    private static final Logger logger = Logger.getLogger(ProjectApiHandler.class);
    protected ObjectFactory jaxbObjFactory;
    protected ProjectAdminApi api;

    /**
     * @param payload
     */
    public ProjectApiHandler() {
        super();
        this.api = ProjectAdminApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createProjectProfileResponse();
        logger.info(ProjectApiHandler.class.getName() + " was instantiated successfully");
    }

    
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Project Profile Request message request element is invalid");
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

        // One or more projects must exists
        try {
            Verifier.verifyNotNull(req.getProfile().getProject());
            Verifier.verifyNotEmpty(req.getProfile().getProject());
        } catch (VerifyException e) {
            throw new InvalidDataException(ApiMessageHandlerConst.MSG_MISSING_PROFILE_DATA, e);
        }
    }

    @Override
    protected String buildResponse(List<ProjectType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            ProjectDetailGroup profile = this.jaxbObjFactory.createProjectDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getProject().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }

    /**
     * Build JAXB project object to be returned as part of the project profile
     * response for query operations.
     * 
     * @param dto
     *            a List of {@link ProjectClientDto} instances
     * @return List of {@link ProjectType} instances
     */
    protected List<ProjectType> buildJaxbQueryResults(List<ProjectClientDto> dto) {
        List<ProjectType> results = new ArrayList<>();
        if (dto == null) {
            return results;
        }

        for (ProjectClientDto item : dto) {
            ProjectType jaxbObj = ProjectJaxbDtoFactory.createProjectJaxbInstance(item);
            results.add(jaxbObj);
        }
        return results;
    }

    /**
     * Build JAXB project object to be returned as part of the project profile
     * response for update and delete operations.
     * 
     * @param dto
     *            instance of {@link Project2Dto}
     * @return List of {@link ProjectType} instances
     */
    protected List<ProjectType> buildJaxbUpdateResults(Project2Dto dto) {
        List<ProjectType> results = new ArrayList<>();
        if (dto == null) {
            return results;
        }
        ProjectType pt = ProjectTypeBuilder.Builder.create()
                .withProjectId(dto.getProjId())
                .withProjectName(dto.getProjectDescription())
                .build();

        results.add(pt);
        return results;
    }
}

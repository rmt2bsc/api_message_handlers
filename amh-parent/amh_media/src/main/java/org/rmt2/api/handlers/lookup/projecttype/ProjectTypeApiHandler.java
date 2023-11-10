package org.rmt2.api.handlers.lookup.projecttype;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ProjectTypeDto;
import org.modules.audiovideo.AudioVideoApi;
import org.modules.audiovideo.AudioVideoFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.MultimediaRequest;
import org.rmt2.jaxb.MultimediaResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ProjecttypeType;
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
 * Handles and routes Project-Type codes related messages for the Media API.
 * 
 * @author roy.terrell
 *
 */
public class ProjectTypeApiHandler extends 
        AbstractJaxbMessageHandler<MultimediaRequest, MultimediaResponse, List<ProjecttypeType>> {
    
    private static final Logger logger = Logger.getLogger(ProjectTypeApiHandler.class);

    private ObjectFactory jaxbObjFactory;

    /**
     * @param payload
     */
    public ProjectTypeApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createMultimediaResponse();
        logger.info(ProjectTypeApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.MEDIA_PROJECTTYPE_GET:
                r = this.doOperation(this.requestObj);
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
     * Project-Type objects.
     * 
     * @param req
     *            an instance of {@link MultimediaRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(MultimediaRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<ProjecttypeType> cdtList = null;

        try {
            // Set reply status
            rs.setReturnStatus(WebServiceConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            ProjectTypeDto criteriaDto = ProjectTypeJaxbDtoFactory.createProjectTypeDtoInstance(req.getCriteria()
                    .getAudioVideoCriteria());
            
            AudioVideoApi api = AudioVideoFactory.createApi();

            // UI-37: Added for capturing the update user id
            api.setApiUser(this.userId);

            List<ProjectTypeDto> dtoList = api.getProjectType(criteriaDto);
            if (dtoList == null) {
                rs.setMessage(ProjectTypeApiHandlerConst.MESSAGE_NOT_FOUND);
                rs.setRecordCount(0);
            }
            else {
                cdtList = ProjectTypeJaxbDtoFactory.createProjectTypeJaxbInstance(dtoList);
                rs.setMessage(ProjectTypeApiHandlerConst.MESSAGE_FOUND);
                rs.setRecordCount(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
            
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(ProjectTypeApiHandlerConst.MESSAGE_FETCH_ERROR);
            rs.setExtMessage(e.getMessage());
        }
        String xml = this.buildResponse(cdtList, rs);
        results.setPayload(xml);
        return results;
    }
    

    
    @Override
    protected void validateRequest(MultimediaRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Project Type message request element is invalid");
        }
    }

    @Override
    protected String buildResponse(List<ProjecttypeType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            this.responseObj.setProfile(this.jaxbObjFactory.createMimeDetailGroup());
            this.responseObj.getProfile().setProjecttypes(this.jaxbObjFactory.createProjectTypes());
            this.responseObj.getProfile().getProjecttypes().getProjectType().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }


}

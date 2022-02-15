package org.rmt2.api.handlers.admin.project;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.Project2Dto;
import org.modules.ProjectTrackerApiConst;
import org.modules.admin.ProjectAdminApi;
import org.modules.admin.ProjectAdminApiFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ProjectType;

import com.InvalidDataException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;

/**
 * Handles and routes project delete related messages to the ProjectTracker API.
 * 
 * @author roy.terrell
 *
 */
public class ProjectDeleteApiHandler extends ProjectApiHandler {
    
    private static final Logger logger = Logger.getLogger(ProjectDeleteApiHandler.class);
    private ProjectAdminApi api;
    
    /**
     * @param payload
     */
    public ProjectDeleteApiHandler() {
        super();
        logger.info(ProjectDeleteApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_PROJECT_DELETE:
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
     * Handler for invoking the appropriate API in order to delete a project
     * object for the Project Tracker Admin moudule.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        Project2Dto project2Dto = null;

        // IS-71: Changed the scope to local to prevent memory leaks as a result
        // of sharing the API instance that was once contained in ancestor
        // class, ProjectApiHandler.
        api = ProjectAdminApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            project2Dto = ProjectJaxbDtoFactory.createProjetDtoInstance(req.getProfile().getProject().get(0));
            
            this.api.beginTrans();
            int rc = this.api.deleteProject(project2Dto);
            rs.setMessage(ProjectMessageHandlerConst.MESSAGE_PROJECT_DELETE_SUCCESS);
            rs.setRecordCount(rc);

            this.responseObj.setHeader(req.getHeader());
            this.api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage(ProjectMessageHandlerConst.MESSAGE_PROJECT_DELETE_FAILED);
            rs.setExtMessage(e.getMessage());
            this.api.rollbackTrans();
        } finally {
            this.api.close();
        }

        // There is no need to include profile section for deletes
        List<ProjectType> updateDtoResults = null;

        String xml = this.buildResponse(updateDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);
        super.validateUpdateRequest(req);
    }

}

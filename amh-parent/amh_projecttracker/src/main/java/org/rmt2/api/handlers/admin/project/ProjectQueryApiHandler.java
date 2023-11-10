package org.rmt2.api.handlers.admin.project;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ProjectClientDto;
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
 * Handles and routes project query related messages to the ProjectTracker API.
 * 
 * @author roy.terrell
 *
 */
public class ProjectQueryApiHandler extends ProjectApiHandler {
    
    private static final Logger logger = Logger.getLogger(ProjectQueryApiHandler.class);
    /**
     * @param payload
     */
    public ProjectQueryApiHandler() {
        super();
        logger.info(ProjectQueryApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_PROJECT_GET:
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
     * project objects for the Project Tracker Admin moudule.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<ProjectType> queryDtoResults = null;

        // IS-71: Changed the scope to local to prevent memory leaks as a result
        // of sharing the API instance that was once contained in ancestor
        // class, ProjectApiHandler.
        ProjectAdminApi api = ProjectAdminApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);

        // UI-37: Added for capturing the update user id
        api.setApiUser(this.userId);
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            ProjectClientDto criteriaDto = ProjectJaxbDtoFactory.createProjectClientDtoCriteriaInstance(req.getCriteria()
                    .getProjectCriteria());
            
            List<ProjectClientDto> dtoList = api.getProjectExt(criteriaDto);
            if (dtoList == null) {
                rs.setMessage(ProjectMessageHandlerConst.MESSAGE_NOT_FOUND);
            }
            else {
                queryDtoResults = this.buildJaxbQueryResults(dtoList);
                rs.setMessage(ProjectMessageHandlerConst.MESSAGE_FOUND);
                rs.setRecordCount(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage(ProjectMessageHandlerConst.MESSAGE_FETCH_ERROR);
            rs.setExtMessage(e.getMessage());
        } finally {
            api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);
    }

}

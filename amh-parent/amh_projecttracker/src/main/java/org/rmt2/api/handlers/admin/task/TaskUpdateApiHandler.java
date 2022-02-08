package org.rmt2.api.handlers.admin.task;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.TaskDto;
import org.modules.ProjectTrackerApiConst;
import org.modules.admin.ProjectAdminApi;
import org.modules.admin.ProjectAdminApiFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.TaskType;

import com.InvalidDataException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes task create/modify related messages to the ProjectTracker
 * API.
 * 
 * @author roy.terrell
 *
 */
public class TaskUpdateApiHandler extends TaskApiHandler {
    
    private static final Logger logger = Logger.getLogger(TaskUpdateApiHandler.class);
    /**
     * @param payload
     */
    public TaskUpdateApiHandler() {
        super();
        logger.info(TaskUpdateApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_TASK_UPDATE:
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
     * Handler for invoking the appropriate API in order to create/modify task
     * tracker task objects.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        TaskDto task2Dto = null;
        boolean newTask = false;

        // IS-71: Use local scoped API instance for the purpose of preventing memory leaks
        // caused by dangling API instances. 
        ProjectAdminApi api = ProjectAdminApiFactory.createApi(ProjectTrackerApiConst.APP_NAME); 
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            
            task2Dto = TaskJaxbDtoFactory.createTaskDtoInstance(req.getProfile().getTask().get(0));
            newTask = task2Dto.getTaskId() == 0;
            
            api.beginTrans();
            int rc = api.updateTask(task2Dto);
            if (newTask) {
                rs.setMessage(TaskMessageHandlerConst.MESSAGE_NEW_TASK_UPDATE_SUCCESS);
                rs.setRecordCount(1);
                // Make sure project id is populated in the DTO
                task2Dto.setTaskId(rc);
            }
            else {
                rs.setMessage(TaskMessageHandlerConst.MESSAGE_EXISTING_TASK_UPDATE_SUCCESS);
                rs.setRecordCount(rc);
            }
            this.responseObj.setHeader(req.getHeader());
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            if (newTask) {
                rs.setMessage(TaskMessageHandlerConst.MESSAGE_NEW_TASK_UPDATE_FAILED);
            }
            else {
                rs.setMessage(TaskMessageHandlerConst.MESSAGE_EXISTING_TASK_UPDATE_FAILED);
            }
            rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            api.close();
        }

        // Add Project Type type data to the project profile element
        List<TaskType> updateDtoResults = this.buildJaxbUpdateResults(task2Dto);

        String xml = this.buildResponse(updateDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);
        super.validateUpdateRequest(req);

        // Verify that one and only one task is exists.
        try {
            Verifier.verifyNotNull(req.getProfile().getTask());
            Verifier.verifyNotEmpty(req.getProfile().getTask());
            Verifier.verifyTrue(req.getProfile().getTask().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidDataException(TaskMessageHandlerConst.VALIDATION_TASK_MISSING, e);
        }
    }

}

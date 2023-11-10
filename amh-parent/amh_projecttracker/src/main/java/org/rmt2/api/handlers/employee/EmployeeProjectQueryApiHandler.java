package org.rmt2.api.handlers.employee;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ProjectEmployeeDto;
import org.modules.ProjectTrackerApiConst;
import org.modules.employee.EmployeeApi;
import org.modules.employee.EmployeeApiFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.EmployeeProjectType;
import org.rmt2.jaxb.ProjectProfileRequest;

import com.InvalidDataException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;

/**
 * Handles and routes employee project query related messages to the
 * ProjectTracker API.
 * 
 * @author roy.terrell
 *
 */
public class EmployeeProjectQueryApiHandler extends EmployeeProjectApiHandler {
    
    private static final Logger logger = Logger.getLogger(EmployeeProjectQueryApiHandler.class);
    /**
     * @param payload
     */
    public EmployeeProjectQueryApiHandler() {
        super();
        logger.info(EmployeeProjectQueryApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_EMPLOYEE_PROJECT_GET:
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
     * project tracker employee/project objects.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<EmployeeProjectType> queryDtoResults = null;

        // IS-71: Use local scoped API instance for the purpose of preventing memory leaks
        // caused by dangling API instances.
        EmployeeApi api = EmployeeApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);

        // UI-37: Added for capturing the update user id
        api.setApiUser(this.userId);
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            
            ProjectEmployeeDto criteriaDto = EmployeeProjectJaxbDtoFactory
                    .createDtoCriteriaInstance(req.getCriteria().getEmployeeProjectCriteria());
            
            List<ProjectEmployeeDto> dtoList = api.getProjectEmployee(criteriaDto);
            if (dtoList == null) {
                rs.setMessage(EmployeeProjectMessageHandlerConst.MESSAGE_NOT_FOUND);
            }
            else {
                queryDtoResults = this.buildJaxbResults(dtoList);
                rs.setMessage(EmployeeProjectMessageHandlerConst.MESSAGE_FOUND);
                rs.setRecordCount(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(EmployeeProjectMessageHandlerConst.MESSAGE_FETCH_ERROR);
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

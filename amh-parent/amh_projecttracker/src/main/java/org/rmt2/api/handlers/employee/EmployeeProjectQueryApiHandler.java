package org.rmt2.api.handlers.employee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ProjectEmployeeDto;
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

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            
            ProjectEmployeeDto criteriaDto = EmployeeProjectJaxbDtoFactory
                    .createDtoCriteriaInstance(req.getCriteria().getEmployeeProjectCriteria());
            
            List<ProjectEmployeeDto> dtoList = this.api.getProjectEmployee(criteriaDto);
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
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<EmployeeProjectType> buildJaxbResults(List<ProjectEmployeeDto> results) {
        List<EmployeeProjectType> list = new ArrayList<>();
        for (ProjectEmployeeDto item : results) {
            EmployeeProjectType jaxbObj = EmployeeProjectJaxbDtoFactory.createEmployeeJaxbInstance(item);
            list.add(jaxbObj);
        }
        return list;
    }
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);
    }
}

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
import org.rmt2.util.projecttracker.employee.EmployeeProjectTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes employee project update related messages to the
 * ProjectTracker API.
 * 
 * @author roy.terrell
 *
 */
public class EmployeeProjectUpdateApiHandler extends EmployeeProjectApiHandler {
    
    private static final Logger logger = Logger.getLogger(EmployeeProjectUpdateApiHandler.class);
    /**
     * @param payload
     */
    public EmployeeProjectUpdateApiHandler() {
        super();
        logger.info(EmployeeProjectUpdateApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_EMPLOYEE_PROJECT_UPDATE:
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
        ProjectEmployeeDto profileDto = null;
        boolean newRec = false;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            profileDto = EmployeeProjectJaxbDtoFactory
                    .createEmploiyeeDtoInstance(req.getProfile().getEmployeeProject().get(0));
            newRec = profileDto.getEmpProjId() < 1;
            
            int rc = this.api.update(profileDto);
            if (newRec) {
                rs.setMessage(EmployeeProjectMessageHandlerConst.MESSAGE_UPDATE_NEW_SUCCESS);
                rs.setRecordCount(1);
                profileDto.setEmpProjId(rc);
            }
            else {
                rs.setMessage(EmployeeProjectMessageHandlerConst.MESSAGE_UPDATE_EXISTING_SUCCESS);
                rs.setRecordCount(rc);
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage(EmployeeProjectMessageHandlerConst.MESSAGE_FETCH_ERROR);
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        List<EmployeeProjectType> updateDtoResults = this.buildJaxbResults(profileDto);
        String xml = this.buildResponse(updateDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<EmployeeProjectType> buildJaxbResults(ProjectEmployeeDto profileData) {
        List<EmployeeProjectType> list = new ArrayList<>();
        EmployeeProjectType jaxbObj = EmployeeProjectTypeBuilder.Builder.create()
                .withEmpProjId(profileData.getEmpProjId())
                .withEmployeeId(profileData.getEmpId())
                .withProjectId(profileData.getProjId())
                .withClientId(profileData.getClientId())
                .withClientName(profileData.getClientName())
                .withProjectName(profileData.getProjectDescription())
                .build();

        list.add(jaxbObj);
        return list;
    }
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);

        try {
            Verifier.verifyNotNull(req.getProfile());
            Verifier.verifyNotNull(req.getProfile().getEmployeeProject());
            Verifier.verifyNotEmpty(req.getProfile().getEmployeeProject());
        } catch (VerifyException e) {
            throw new InvalidDataException("Update operation requires the existence of the Employee Project profile", e);
        }

        try {
            Verifier.verify(req.getProfile().getEmployeeProject().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidDataException("Update operation is limited to one Employee Project profile", e);
        }
    }
}

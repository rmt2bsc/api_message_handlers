package org.rmt2.api.handlers.employee.title;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.EmployeeTitleDto;
import org.modules.ProjectTrackerApiConst;
import org.modules.employee.EmployeeApi;
import org.modules.employee.EmployeeApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.EmployeeTitleType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ProjectDetailGroup;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.util.projecttracker.employee.EmployeeTitleTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Employee Title related messages to the Project Tracker
 * Administration API.
 * 
 * @author roy.terrell
 *
 */
public class EmployeeTitleApiHandler extends
        AbstractJaxbMessageHandler<ProjectProfileRequest, ProjectProfileResponse, List<EmployeeTitleType>> {
    
    private static final Logger logger = Logger.getLogger(EmployeeTitleApiHandler.class);
    protected ObjectFactory jaxbObjFactory;
    protected EmployeeApi api;

    /**
     * @param payload
     */
    public EmployeeTitleApiHandler() {
        super();
        this.api = EmployeeApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createProjectProfileResponse();
        logger.info(EmployeeTitleApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_EMPLOYEE_TITLE_GET:
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
        List<EmployeeTitleDto> queryDtoResults = null;
        List<EmployeeTitleType> queryResponse = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);

            // TODO: Getting the selection criteria is not needed at this time.
            // Keeping around in the event API requires selection criteria in
            // the future.
            EmployeeTitleDto criteria = EmployeeTitleJaxbDtoFactory
                    .createDtoCriteriaInstance(req.getCriteria().getEmployeeTitleCriteria());

            queryDtoResults = this.api.getEmployeeTitles();
            if (queryDtoResults != null) {
                rs.setMessage(EmployeeTitleMessageHandlerConst.MESSAGE_FOUND);
                rs.setRecordCount(queryDtoResults.size());
                queryResponse = this.buildJaxbResults(queryDtoResults);
            }
            else {
                rs.setMessage(EmployeeTitleMessageHandlerConst.MESSAGE_NOT_FOUND);
                rs.setRecordCount(0);
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage(EmployeeTitleMessageHandlerConst.MESSAGE_FETCH_ERROR);
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryResponse, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<EmployeeTitleType> buildJaxbResults(List<EmployeeTitleDto> data) {
        List<EmployeeTitleType> list = new ArrayList<>();
        for (EmployeeTitleDto item : data) {
            EmployeeTitleType jaxbObj = EmployeeTitleTypeBuilder.Builder.create()
                    .withEmployeeTitleId(item.getEmployeeTitleId())
                    .withDescription(item.getEmployeeTitleDescription())
                    .build();
            list.add(jaxbObj);
        }
        return list;
    }
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Employee Title query message request element is invalid");
        }
    }

    @Override
    protected String buildResponse(List<EmployeeTitleType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            ProjectDetailGroup profile = this.jaxbObjFactory.createProjectDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getEmployeeTitle().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

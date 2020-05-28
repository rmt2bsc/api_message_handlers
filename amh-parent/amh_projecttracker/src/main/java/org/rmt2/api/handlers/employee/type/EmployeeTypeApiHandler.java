package org.rmt2.api.handlers.employee.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.EmployeeTypeDto;
import org.modules.ProjectTrackerApiConst;
import org.modules.employee.EmployeeApi;
import org.modules.employee.EmployeeApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.EmployeetypeType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ProjectDetailGroup;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.util.projecttracker.employee.EmployeetypeTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Employee Type related messages to the Project Tracker
 * Administration API.
 * 
 * @author roy.terrell
 *
 */
public class EmployeeTypeApiHandler extends
        AbstractJaxbMessageHandler<ProjectProfileRequest, ProjectProfileResponse, List<EmployeetypeType>> {
    
    private static final Logger logger = Logger.getLogger(EmployeeTypeApiHandler.class);
    protected ObjectFactory jaxbObjFactory;
    protected EmployeeApi api;

    /**
     * @param payload
     */
    public EmployeeTypeApiHandler() {
        super();
        this.api = EmployeeApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createProjectProfileResponse();
        logger.info(EmployeeTypeApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_EMPLOYEE_TYPE_GET:
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
     * project tracker employee type objects.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<EmployeeTypeDto> queryDtoResults = null;
        List<EmployeetypeType> queryResponse = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);

            // TODO: Getting the selection criteria is not needed at this time.
            // Keeping around in the event API requires selection criteria in
            // the future.
            EmployeeTypeDto criteria = EmployeeTypeJaxbDtoFactory
                    .createDtoCriteriaInstance(req.getCriteria().getEmployeeTypeCriteria());

            queryDtoResults = this.api.getEmployeeTypes();
            if (queryDtoResults != null) {
                rs.setMessage(EmployeeTypeMessageHandlerConst.MESSAGE_FOUND);
                rs.setRecordCount(queryDtoResults.size());
                queryResponse = this.buildJaxbResults(queryDtoResults);
            }
            else {
                rs.setMessage(EmployeeTypeMessageHandlerConst.MESSAGE_NOT_FOUND);
                rs.setRecordCount(0);
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage(EmployeeTypeMessageHandlerConst.MESSAGE_FETCH_ERROR);
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryResponse, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<EmployeetypeType> buildJaxbResults(List<EmployeeTypeDto> data) {
        List<EmployeetypeType> list = new ArrayList<>();
        for (EmployeeTypeDto item : data) {
            EmployeetypeType jaxbObj = EmployeetypeTypeBuilder.Builder.create()
                    .withEmployeeTypeId(item.getEmployeeTypeId())
                    .withDescription(item.getEmployeeTypeDescription())
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
    protected String buildResponse(List<EmployeetypeType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            ProjectDetailGroup profile = this.jaxbObjFactory.createProjectDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getEmployeeType().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

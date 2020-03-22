package org.rmt2.api.handlers.employee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.EmployeeDto;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.EmployeeType;
import org.rmt2.jaxb.ProjectDetailGroup;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.InvalidDataException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;

/**
 * Handles and routes General Ledger Account related messages to the Accounting
 * API.
 * 
 * @author roy.terrell
 *
 */
public class EmployeeQueryApiHandler extends EmployeeApiHandler {
    
    private static final Logger logger = Logger.getLogger(EmployeeQueryApiHandler.class);
    public static final String MESSAGE_FOUND = "Employee record(s) found";
    public static final String MESSAGE_NOT_FOUND = "Employee data not found!";
    public static final String MESSAGE_ERROR = "Failure to retrieve Employee(s)";

    /**
     * @param payload
     */
    public EmployeeQueryApiHandler() {
        super();
        logger.info(EmployeeQueryApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_EMPLOYEE_GET:
                r = this.fetch(this.requestObj);
                break;
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to fetch one or more GL Account objects.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}           
     */
    protected MessageHandlerResults fetch(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<EmployeeType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            EmployeeDto criteriaDto = EmployeeJaxbDtoFactory
                    .createEmployeeDtoCriteriaInstance(req.getCriteria().getEmployeeCriteria());
            
            List<EmployeeDto> dtoList = this.api.getEmployeeExt(criteriaDto);
            if (dtoList == null) {
                rs.setMessage(EmployeeQueryApiHandler.MESSAGE_NOT_FOUND);
                rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            }
            else {
                queryDtoResults = this.buildJaxbResults(dtoList);
                rs.setMessage(EmployeeQueryApiHandler.MESSAGE_FOUND);
                rs.setRecordCount(dtoList.size());
                rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage(EmployeeQueryApiHandler.MESSAGE_ERROR);
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<EmployeeType> buildJaxbResults(List<EmployeeDto> results) {
        List<EmployeeType> list = new ArrayList<>();
        for (EmployeeDto item : results) {
            EmployeeType jaxbObj = EmployeeJaxbDtoFactory.createEmployeeDtoInstance(item);
            list.add(jaxbObj);
        }
        return list;
    }
    
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);
    }

    @Override
    protected String buildResponse(List<EmployeeType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            ProjectDetailGroup profile = this.jaxbObjFactory.createProjectDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getEmployee().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

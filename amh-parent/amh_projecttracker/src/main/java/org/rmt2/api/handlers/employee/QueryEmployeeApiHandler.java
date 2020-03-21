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
public class QueryEmployeeApiHandler extends EmployeeApiHandler {
    
    private static final Logger logger = Logger.getLogger(QueryEmployeeApiHandler.class);

    /**
     * @param payload
     */
    public QueryEmployeeApiHandler() {
        super();
        logger.info(QueryEmployeeApiHandler.class.getName() + " was instantiated successfully");
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
                rs.setMessage("Employee data not found!");
                rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            }
            else {
                queryDtoResults = this.buildJaxbListData(dtoList);
                rs.setMessage("Employee record(s) found");
                rs.setRecordCount(dtoList.size());
                rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage("Failure to retrieve Employee(s)");
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<EmployeeType> buildJaxbListData(List<EmployeeDto> results) {
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
        
        // Validate request for update/delete operation
        // switch (this.command) {
        // case ApiTransactionCodes.GL_ACCOUNT_UPDATE:
        // case ApiTransactionCodes.GL_ACCOUNT_DELETE:
        // try {
        // Verifier.verifyNotNull(req.getProfile());
        // Verifier.verifyNotEmpty(req.getProfile().getAccount());
        // }
        // catch (VerifyException e) {
        // throw new
        // InvalidRequestException("GL Account data is required for update/delete operation");
        // }
        // try {
        // Verifier.verifyTrue(req.getProfile().getAccount().size() == 1);
        // }
        // catch (VerifyException e) {
        // throw new
        // InvalidRequestException("Only one (1) GL Account record is required for update/delete operation");
        // }
        //
        // if (this.command.equals(ApiTransactionCodes.GL_ACCOUNT_DELETE)) {
        // try {
        // Verifier.verifyNotNull(req.getProfile().getAccount().get(0).getAcctId());
        // Verifier.verifyPositive(req.getProfile().getAccount().get(0).getAcctId());
        // }
        // catch (VerifyException e) {
        // throw new
        // InvalidRequestException("A valid account id is required when deleting a GL Account from the database");
        // }
        // }
        // break;
        // default:
        // break;
        // }
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

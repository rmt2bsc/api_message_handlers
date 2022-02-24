package org.rmt2.api.handlers.timesheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.TimesheetDto;
import org.modules.ProjectTrackerApiConst;
import org.modules.timesheet.TimesheetApi;
import org.modules.timesheet.TimesheetApiFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.TimesheetType;
import org.rmt2.util.projecttracker.timesheet.TimesheetTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes timesheet delete related messages to the ProjectTracker
 * API.
 * 
 * @author roy.terrell
 *
 */
public class TimesheetDeleteApiHandler extends TimesheetApiHandler {
    
    private static final Logger logger = Logger.getLogger(TimesheetDeleteApiHandler.class);
    /**
     * @param payload
     */
    public TimesheetDeleteApiHandler() {
        super();
        logger.info(TimesheetDeleteApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_TIMESHEET_DELETE:
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
     * Handler for invoking the appropriate API in order to delete project
     * tracker timesheet objects.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        TimesheetDto timesheetDto = null;
        List<TimesheetType> updateDtoResults = null;

        // IS-71: Use local scoped API instance for the purpose of preventing memory leaks
        // caused by dangling API instances. 
        TimesheetApi api = TimesheetApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            timesheetDto = TimesheetJaxbDtoFactory
                    .createTimesheetDtoInstance(req.getProfile().getTimesheet().get(0));

            api.beginTrans();
            int rc = api.deleteTimesheet(timesheetDto.getTimesheetId());
            if (rc > 0) {
                rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_DELETE_SUCCESS);
            }
            else {
                String errMsg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_DELETE_RECORD_NOT_FOUND,
                        String.valueOf(timesheetDto.getTimesheetId()), "%s");
                rs.setMessage(errMsg);
            }
            rs.setRecordCount(rc);

            updateDtoResults = this.buildJaxbUpdateResults(timesheetDto);
            this.responseObj.setHeader(req.getHeader());
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_DELETE_ERROR);
            rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            api.close();
        }

        String xml = this.buildResponse(updateDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    @Override
    protected List<TimesheetType> buildJaxbUpdateResults(TimesheetDto dto) {
        List<TimesheetType> list = new ArrayList<>();
        TimesheetType jaxbObj = TimesheetTypeBuilder.Builder.create()
                .withTimesheetId(dto.getTimesheetId())
                .build();

        list.add(jaxbObj);
        return list;
    }
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);

        try {
            Verifier.verifyNotNull(req.getProfile());
            Verifier.verifyNotNull(req.getProfile().getTimesheet());
            Verifier.verifyNotEmpty(req.getProfile().getTimesheet());
        } catch (VerifyException e) {
            throw new InvalidDataException(TimesheetMessageHandlerConst.VALIDATION_TIMESHEET_MISSING, e);
        }

        try {
            Verifier.verify(req.getProfile().getTimesheet().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidDataException(TimesheetMessageHandlerConst.VALIDATION_TIMESHEET_TOO_MANY, e);
        }
    }
}

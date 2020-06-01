package org.rmt2.api.handlers.timesheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dto.EventDto;
import org.dto.ProjectTaskDto;
import org.dto.TimesheetDto;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.TimesheetType;

import com.InvalidDataException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes timesheet update related messages to the ProjectTracker
 * API.
 * 
 * @author roy.terrell
 *
 */
public class TimesheetUpdateApiHandler extends TimesheetApiHandler {
    
    private static final Logger logger = Logger.getLogger(TimesheetUpdateApiHandler.class);
    /**
     * @param payload
     */
    public TimesheetUpdateApiHandler() {
        super();
        logger.info(TimesheetUpdateApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE:
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
     * Handler for invoking the appropriate API in order to update project
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
        Map<ProjectTaskDto, List<EventDto>> workLogDto = null;
        List<TimesheetType> updateDtoResults = null;
        boolean newTimesheet = false;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            timesheetDto = TimesheetJaxbDtoFactory
                    .createTimesheetDtoInstance(req.getProfile().getTimesheet().get(0));
            workLogDto = TimesheetJaxbDtoFactory
                    .createTimesheetWorkLogDtoInstance(req.getProfile().getTimesheet().get(0));
            
            newTimesheet = timesheetDto.getTimesheetId() > 0 ? false : true;

            // TODO: put it DB transaction management logic
            int rc = this.api.updateTimesheet(timesheetDto, workLogDto);
            if (newTimesheet) {
                rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_UPDATE_NEW_SUCCESS);
            }
            else {
                rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_UPDATE_EXISTING_SUCCESS);
            }
            rs.setRecordCount(1);

            // Ensure timeshet id has a value in the event the timesheet is new
            timesheetDto.setTimesheetId(rc);

            updateDtoResults = this.buildJaxbResults(timesheetDto);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            if (newTimesheet) {
                rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_UPDATE_NEW_ERROR);
            }
            else {
                rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_UPDATE_EXISTING_ERROR);
            }
            rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_FETCH_ERROR);
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(updateDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<TimesheetType> buildJaxbResults(TimesheetDto dto) {
        List<TimesheetType> list = new ArrayList<>();
        TimesheetType jaxbObj = TimesheetJaxbDtoFactory.createTimesheetJaxbAbbreviatedInstance(dto);
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

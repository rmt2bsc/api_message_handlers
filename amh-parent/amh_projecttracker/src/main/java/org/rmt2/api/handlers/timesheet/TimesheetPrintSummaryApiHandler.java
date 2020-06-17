package org.rmt2.api.handlers.timesheet;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.dto.BusinessContactDto;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ProjectDetailGroup;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.TimesheetType;

import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;

/**
 * Handles and routes timesheet summary printing related messages to the
 * ProjectTracker API.
 * 
 * @author roy.terrell
 *
 */
public class TimesheetPrintSummaryApiHandler extends AbstractTimesheetPrintApiHandler {
    
    private static final Logger logger = Logger.getLogger(TimesheetPrintSummaryApiHandler.class);

    /**
     * @param payload
     */
    public TimesheetPrintSummaryApiHandler() {
        super();
        logger.info(TimesheetPrintSummaryApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_TIMESHEET_PRINT_SUMMARY:
                r = this.doOperation(this.requestObj);
                break;
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }


    @Override
    protected String buildTimesheetData(BusinessContactDto serviceProvider) {
        this.setReportName("TimesheetPrintSummary.xsl");
        TimesheetType timesheet = TimesheetJaxbDtoFactory.createTimesheetJaxbInstance(this.api.getTimesheet(),
                this.api.getTimesheetSummary(), serviceProvider);

        ObjectFactory fact = new ObjectFactory();
        ProjectProfileResponse resp = fact.createProjectProfileResponse();
        ProjectDetailGroup profile = fact.createProjectDetailGroup();
        profile.getTimesheet().add(timesheet);
        resp.setProfile(profile);

        String xml = this.jaxb.marshalMessage(resp);
        return xml;

    }
}

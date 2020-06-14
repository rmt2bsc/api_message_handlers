package org.rmt2.api.handlers.timesheet;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.dto.BusinessContactDto;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.TimesheetType;

import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;

/**
 * Handles and routes timesheet detail printing related messages to the
 * ProjectTracker API.
 * 
 * @author roy.terrell
 *
 */
public class TimesheetPrintDetailApiHandler extends AbstractTimesheetPrintApiHandler {
    
    private static final Logger logger = Logger.getLogger(TimesheetPrintDetailApiHandler.class);

    /**
     * @param payload
     */
    public TimesheetPrintDetailApiHandler() {
        super();
        logger.info(TimesheetPrintDetailApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_TIMESHEET_PRINT_WORKLOG:
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
        this.setReportName("TimesheetPrintWorkLog.xsl");
        TimesheetType timesheet = TimesheetJaxbDtoFactory.createTimesheetJaxbInstance(this.api.getTimesheet(),
                this.api.getTimesheetHours(), serviceProvider);
        String xml = this.jaxb.marshalMessage(timesheet);
        return xml;

    }
}

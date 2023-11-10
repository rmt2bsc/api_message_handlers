package org.rmt2.api.handlers.timesheet;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.TimesheetDto;
import org.modules.ProjectTrackerApiConst;
import org.modules.timesheet.invoice.InvoiceTimesheetApi;
import org.modules.timesheet.invoice.InvoiceTimesheetApiFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.TimesheetType;

import com.InvalidDataException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;

/**
 * Handles and routes messages pertaining the invoicing of a single timesheet
 * for the ProjectTracker API.
 * 
 * @author roy.terrell
 *
 */
public class TimesheetInvoiceSingleApiHandler extends TimesheetInvoiceApiHandler {
    
    private static final Logger logger = Logger.getLogger(TimesheetInvoiceSingleApiHandler.class);

    /**
     * @param payload
     */
    public TimesheetInvoiceSingleApiHandler() {
        super();
        logger.info(TimesheetInvoiceSingleApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_TIMESHEET_INVOICE:
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
     * Handler for invoking the appropriate API in order to invoice a single
     * timesheet.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<TimesheetType> updateDtoResults = null;
        TimesheetDto criteriaDto = null;

        // IS-71: Use local scoped API instance for the purpose of preventing memory leaks
        // caused by dangling API instances. 
        InvoiceTimesheetApi api = InvoiceTimesheetApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);

        // UI-37: Added for capturing the update user id
        api.setApiUser(this.userId);
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            
            criteriaDto = TimesheetJaxbDtoFactory
                    .createTimesheetDtoCriteriaInstance(req.getCriteria().getTimesheetCriteria());

            api.beginTrans();
            int rc = api.invoice(criteriaDto.getTimesheetId());
            if (rc > 0) {
                rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_INVOICE_SUCCESS);
                criteriaDto.setInvoiceRefNo(String.valueOf(rc));
                updateDtoResults = this.buildJaxbInvoiceResults(criteriaDto);
                rs.setRecordCount(1);
            }
            else {
                String errMsg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_INVOICE_RECORD_NOT_FOUND,
                        String.valueOf(criteriaDto.getTimesheetId()), "%s");
                rs.setMessage(errMsg);
            }

            this.responseObj.setHeader(req.getHeader());
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_INVOICE_ERROR,
                    String.valueOf(criteriaDto.getTimesheetId()), "%s"));
            rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            api.close();
        }

        String xml = this.buildResponse(updateDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);
    }
}

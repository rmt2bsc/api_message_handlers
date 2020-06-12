package org.rmt2.api.handlers.timesheet;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.dao.timesheet.TimesheetConst;
import org.dto.EmployeeDto;
import org.dto.TimesheetDto;
import org.dto.adapter.orm.EmployeeObjectFactory;
import org.modules.employee.EmployeeApi;
import org.modules.employee.EmployeeApiException;
import org.modules.employee.EmployeeApiFactory;
import org.modules.timesheet.TimesheetTransmissionException;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.TimesheetType;

import com.InvalidDataException;
import com.SystemException;
import com.api.messaging.email.EmailMessageBean;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2Date;
import com.api.util.RMT2File;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes timesheet approval/decline related messages to the
 * ProjectTracker API.
 * 
 * @author roy.terrell
 *
 */
public class TimesheetPostSubmitApiHandler extends TimesheetApiHandler {
    
    private static final Logger logger = Logger.getLogger(TimesheetPostSubmitApiHandler.class);
    private static final String SUBJECT_TRANSMISSION_PREFIX = "Time Sheet Submission for ";
    private static final String SUBJECT_CONFIRM_PREFIX = "Time Sheet $confirmStatus$ (Period ending $endingPeriod$)";
    private double totalHours;

    /**
     * @param payload
     */
    public TimesheetPostSubmitApiHandler() {
        super();
        logger.info(TimesheetPostSubmitApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_TIMESHEET_APPROVE:
            case ApiTransactionCodes.PROJTRACK_TIMESHEET_DECLINE:
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
     * Handler for invoking the appropriate API in order to approve or decline
     * timesheet submissions for project tracker API.
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
        boolean okToSendEmail = false;
        String transCode = req.getHeader().getTransaction();
        int rc = 0;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            timesheetDto = TimesheetJaxbDtoFactory.createTimesheetDtoInstance(req.getProfile().getTimesheet().get(0));

            this.api.beginTrans();
            // Approve or decline timesheet
            if (transCode.equalsIgnoreCase(ApiTransactionCodes.PROJTRACK_TIMESHEET_APPROVE)) {
                rc = this.api.approve(timesheetDto.getTimesheetId());
            }
            else {
                rc = this.api.decline(timesheetDto.getTimesheetId());
            }

            // Apply to appropriate text message for operation
            if (rc > 0) {
                String msg = null;
                if (transCode.equalsIgnoreCase(ApiTransactionCodes.PROJTRACK_TIMESHEET_APPROVE)) {
                    msg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_POST_SUBMIT_SUCCESS, "approved", "%s");
                }
                else {
                    msg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_POST_SUBMIT_SUCCESS, "declined", "%s");
                }
                rs.setMessage(msg);
                okToSendEmail = true;
            }
            else {
                rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_POST_SUBMIT_RECORD_NOT_FOUND);
            }

            // Always return "1" record count
            rs.setRecordCount(1);

            // Send email confirmation
            try {
                if (okToSendEmail) {
                    this.sendEmailConfirmation(rc);
                    logger.info("Timesheet approval/declination confirmation email was sent to employee for timesheet: "
                            + timesheetDto.getDisplayValue());
                }
            } catch (TimesheetTransmissionException e) {
                logger.error("Error sending timesheet approval/declination confirmation email to employee: ", e);
                rs.setExtMessage(e.getMessage());
            }

            // Build timesheet response data with updated timesheet data from
            // API
            updateDtoResults = this.buildJaxbStatusChangeResults(this.api.getTimesheet());
            this.responseObj.setHeader(req.getHeader());

            this.api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_POST_SUBMIT_ERROR);
            rs.setExtMessage(e.getMessage());

            // Build timesheet response data with requrest data
            updateDtoResults = this.buildJaxbStatusChangeResults(timesheetDto);
            this.api.rollbackTrans();
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(updateDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    

    private Object sendEmailConfirmation(int currentStatus) throws TimesheetTransmissionException {
        TimesheetDto ts = this.api.getTimesheet();
        EmployeeDto employee = null;

        try {
            EmployeeApi empApi = EmployeeApiFactory.createApi(this.api.getSharedDao());
            EmployeeDto empCriteria = EmployeeObjectFactory.createEmployeeExtendedDtoInstance(null);
            empCriteria.setEmployeeId(ts.getEmpId());

            // TODO: Might need to eliminate the assumption that the employee
            // exists and handle for the "Not Found" possibility
            List<EmployeeDto> employees = empApi.getEmployeeExt(empCriteria);
            employee = employees.get(0);
        } catch (EmployeeApiException e) {
            throw new TimesheetTransmissionException("Failed to obtain employee with extended attributes", e);
        }

        try {
            EmailMessageBean msg = this.createConfirmationMessage(ts, employee, currentStatus);
            Integer rc = (Integer) this.send(msg);
            return rc;
        } catch (TimesheetTransmissionException e) {
            throw new TimesheetTransmissionException("Error occurred sending time sheet email confirmation", e);
        }
    }

    private EmailMessageBean createConfirmationMessage(TimesheetDto timesheet, EmployeeDto employee,
            int currentStatus) throws TimesheetTransmissionException {

        // Get configuration properties
        ResourceBundle localConfig = RMT2File.loadAppConfigProperties("config.transactions.ProjectTracker-MessageHanlerConfig");

        // Begin to build email content
        String confirmDate = null;
        String confirmStatus = (currentStatus == TimesheetConst.STATUS_APPROVED ? "Approved" : "Declined");
        String periodEnd = null;
        try {
            periodEnd = RMT2Date.formatDate(timesheet.getEndPeriod(), "MM-dd-yyyy");
        } catch (SystemException e) {
            this.msg = "Unable to convert timesheet end period date to String";
            throw new TimesheetTransmissionException(this.msg, e);
        }
        try {
            // Use the current date instead of the current status' effective
            // date since the effective date does not capture the full timestamp
            // of the date.
            confirmDate = RMT2Date.formatDate(new Date(), "MM-dd-yyyy HH:mm:ss");
        } catch (SystemException e) {
            this.msg = "Unable to convert timesheet current status' effective date date to String";
            throw new TimesheetTransmissionException(this.msg, e);
        }

        EmailMessageBean email = new EmailMessageBean();

        // Setup basic email components
        email.setToAddress(employee.getEmployeeEmail());

        // TODO: get from the system property
        email.setFromAddress("rmt2bsc@gmail.com");

        String subject = RMT2String.replace(SUBJECT_CONFIRM_PREFIX, confirmStatus, "$confirmStatus$");
        subject = RMT2String.replace(subject, periodEnd, "$endingPeriod$");
        email.setSubject(subject);

        // Get HTML Content
        String htmlContent = null;
        String emailTemplateFile = localConfig.getString("email_post_submit_template");
        try {
            InputStream is = RMT2File.getFileInputStream(emailTemplateFile);
            htmlContent = RMT2File.getStreamStringData(is);
        } catch (SystemException e) {
            throw new TimesheetTransmissionException("Unalbe to get Confirmation HTML content pertaining to timesheet: "
                    + timesheet.getDisplayValue(), e);
        }

        String deltaContent = null;
        deltaContent = RMT2String.replace(htmlContent, employee.getEmployeeFirstname(), "$firstName$");
        deltaContent = RMT2String.replace(deltaContent, employee.getEmployeeLastname(), "$lastName$");
        deltaContent = RMT2String.replace(deltaContent, timesheet.getClientName(), "$clientName$");
        deltaContent = RMT2String.replace(deltaContent, periodEnd, "$endingPeriod$");
        deltaContent = RMT2String.replace(deltaContent, confirmStatus, "$confirmStatus$");
        deltaContent = RMT2String.replace(deltaContent, timesheet.getDisplayValue(), "$timesheetId$");
        deltaContent = RMT2String.replace(deltaContent, confirmDate, "$confirmDate$");
        deltaContent = RMT2String.replace(deltaContent, String.valueOf(timesheet.getBillHrs()), "$totalHours$");

        String outpuEmailDir = localConfig.getString("email_output_directory");
        RMT2File.createFile(deltaContent, outpuEmailDir + "/timesheet_" + timesheet.getDisplayValue() + "_" + confirmStatus
                + "_email.html");
        email.setBody(deltaContent, EmailMessageBean.HTML_CONTENT);
        return email;
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

package org.rmt2.api.handlers.timesheet;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.dto.ClientDto;
import org.dto.EmployeeDto;
import org.dto.EventDto;
import org.dto.ProjectTaskDto;
import org.dto.TimesheetDto;
import org.dto.adapter.orm.EmployeeObjectFactory;
import org.dto.adapter.orm.ProjectObjectFactory;
import org.modules.admin.ProjectAdminApi;
import org.modules.admin.ProjectAdminApiException;
import org.modules.admin.ProjectAdminApiFactory;
import org.modules.employee.EmployeeApi;
import org.modules.employee.EmployeeApiException;
import org.modules.employee.EmployeeApiFactory;
import org.modules.timesheet.TimesheetTransmissionException;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.TimesheetType;
import org.rmt2.util.projecttracker.timesheet.TimesheetTypeBuilder;

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
 * Handles and routes timesheet submittal related messages to the ProjectTracker
 * API.
 * 
 * @author roy.terrell
 *
 */
public class TimesheetSubmitApiHandler extends TimesheetApiHandler {
    
    private static final Logger logger = Logger.getLogger(TimesheetSubmitApiHandler.class);
    private static final String SUBJECT_TRANSMISSION_PREFIX = "Time Sheet Submission for ";
    private double totalHours;

    /**
     * @param payload
     */
    public TimesheetSubmitApiHandler() {
        super();
        logger.info(TimesheetSubmitApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_TIMESHEET_SUBMIT:
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
     * Handler for invoking the appropriate API in order to submit timesheets
     * for project tracker API.
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

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            timesheetDto = TimesheetJaxbDtoFactory.createTimesheetDtoInstance(req.getProfile().getTimesheet().get(0));

            this.api.beginTrans();
            int rc = this.api.submit(timesheetDto.getTimesheetId());
            if (rc > 0) {
                rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_SUBMIT_SUCCESS);
                okToSendEmail = true;
            }
            else {
                String errMsg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_SUBMIT_RECORD_NOT_FOUND,
                        String.valueOf(timesheetDto.getTimesheetId()), "%s");
                rs.setMessage(errMsg);
            }
            rs.setRecordCount(rc);

            updateDtoResults = this.buildJaxbUpdateResults(timesheetDto);
            this.responseObj.setHeader(req.getHeader());
            this.api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_SUBMIT_ERROR);
            rs.setExtMessage(e.getMessage());
            this.api.rollbackTrans();
        } finally {
            this.api.close();
        }

        // Send email confirmation
        try {
            if (okToSendEmail) {
                this.sendEmailConfirmation();
                logger.info("Timesheet submittal confirmation email was sent to manager for timesheet: "
                        + timesheetDto.getDisplayValue());
            }
        } catch (TimesheetTransmissionException e) {
            logger.error("Error sending timesheet submittal confirmation email to manager: ", e);
            rs.setExtMessage(e.getMessage());
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
    
    private Object sendEmailConfirmation() throws TimesheetTransmissionException {
        TimesheetDto ts = this.api.getTimesheet();
        Map<ProjectTaskDto, List<EventDto>> hours = this.api.getTimesheetHours();
        EmployeeApi empApi = null;
        ProjectAdminApi projApi = null;
        try {
            // Get employee profile
            empApi = EmployeeApiFactory.createApi(this.api.getSharedDao());

            EmployeeDto empCriteria = EmployeeObjectFactory.createEmployeeExtendedDtoInstance(null);
            empCriteria.setEmployeeId(ts.getEmpId());

            // TODO: Might need to eliminate the assumption that the employee
            // exists and handle for the "Not Found" possibility
            List<EmployeeDto> employees = empApi.getEmployeeExt(empCriteria);
            EmployeeDto employee = employees.get(0);

            EmployeeDto manager = empApi.getEmployee(employee.getManagerId());

            // get client profile
            projApi = ProjectAdminApiFactory.createApi(this.api.getSharedDao());
            ClientDto clientCriteria = ProjectObjectFactory.createClientDtoInstance(null);
            clientCriteria.setClientId(ts.getClientId());
            List<ClientDto> clients = projApi.getClient(clientCriteria);

            // send timesheet via email
            EmailMessageBean msg = this.createSubmitMessage(ts, employee, manager, clients.get(0), hours);
            Integer rc = (Integer) this.send(msg);
            return rc;
        } catch (TimesheetTransmissionException e) {
            this.msg = "SMTP error occurred attempting to send timesheet: " + ts.getDisplayValue();
            logger.error(this.msg);
            throw new TimesheetTransmissionException(this.msg, e);
        } catch (EmployeeApiException e) {
            this.msg = "Data access error fetching timesheet's employee profile: " + ts.getEmpId();
            logger.error(this.msg);
            throw new TimesheetTransmissionException(this.msg, e);
        } catch (ProjectAdminApiException e) {
            this.msg = "Data access error fetching timesheet's client profile: " + ts.getClientId();
            logger.error(this.msg);
            throw new TimesheetTransmissionException(this.msg, e);
        } finally {
            empApi = null;
            projApi = null;
        }
    }

    /**
     * Builds HTML containing content that represents the Timesheet Submital for
     * Manager Approval message.
     * <p>
     * Uses <i>timesheet</i>, <i>employee</i>, <i>client</i>, and <i>hours</i>
     * to devise the timesheet header, weekly hours for each project-task, and
     * the totals. Uses a timesheet header template file to build the HTML.
     * 
     * @param timesheet
     *            An instance of {@link TimesheetDto}
     * @param employee
     *            An instance of {@link EmployeeDto}
     * @param client
     *            An instance of {@link ClientDto}
     * @param hours
     *            A Map containing the hours for each project/task. The key is
     *            represented as {@link ProjectTaskDto} and the values is
     *            represented as a List of {@link EventDto} objects.
     * @return an instance of {@link EmailMessageBean}. Returns null when the
     *         total number billable hours equals zero.
     * @throws TimesheetTransmissionException
     *             User's session bean is unobtainable. Problem obtaining
     *             timesheet's project-task entries.
     * @throws TimesheetTransmissionValidationException
     *             <i>timesheet</i>, <i>employee</i>, <i>manager</i>,
     *             <i>client</i>, <i>hours</i>, or <i>timesheet end period</i>
     *             is null.
     * @throws ZeroTimesheetHoursException
     *             <i>timesheet hours</i> structure is exists but is empty.
     */
    private EmailMessageBean createSubmitMessage(TimesheetDto timesheet, EmployeeDto employee,
            EmployeeDto manager, ClientDto client, Map<ProjectTaskDto, List<EventDto>> hours)
            throws TimesheetTransmissionException {

        // Get configuration properties
        ResourceBundle localConfig = RMT2File.loadAppConfigProperties("config.transactions.ProjectTracker-MessageHanlerConfig");
        String outpuEmailDir = localConfig.getString("email_output_directory");

        // Begin to build email content
        String periodEnd = null;
        try {
            periodEnd = RMT2Date.formatDate(timesheet.getEndPeriod(), "MM-dd-yyyy");
        } catch (SystemException e) {
            this.msg = "Unable to convert timesheet end period date string to Date object";
            throw new TimesheetTransmissionException(this.msg, e);
        }

        EmailMessageBean email = new EmailMessageBean();
        StringBuffer buf = new StringBuffer();
        // Setup basic email components
        email.setToAddress(manager.getEmployeeEmail());
        email.setFromAddress(employee.getEmployeeEmail());
        buf.append(TimesheetSubmitApiHandler.SUBJECT_TRANSMISSION_PREFIX);
        buf.append(" ");
        buf.append(employee.getEmployeeFirstname());
        buf.append(" ");
        buf.append(employee.getEmployeeLastname());
        buf.append(" for period ending  ");
        buf.append(periodEnd);
        email.setSubject(buf.toString());

        // Build email body
        String root = localConfig.getString("webapp_root");
        String uri = localConfig.getString("email_submit_uri");
        String uriParms = localConfig.getString("email_submit_uri_parms");
        String submitUri = root + uri + RMT2String.replace(uriParms, String.valueOf(timesheet.getTimesheetId()),
                "{$timesheet_id02$}");

        // Get HTML Content
        String htmlContent = null;
        String formattedDate = null;
        String emailTemplateFile = localConfig.getString("email_template");
        try {
            InputStream is = RMT2File.getFileInputStream(emailTemplateFile);
            htmlContent = RMT2File.getStreamStringData(is);
            formattedDate = RMT2Date.formatDate(timesheet.getEndPeriod(), "MM-dd-yyyy");
        } catch (SystemException e) {
            throw new TimesheetTransmissionException("Unalbe to obtain submit email template for timesheet: "
                    + timesheet.getDisplayValue(), e);
        }

        String deltaContent = null;
        deltaContent = RMT2String.replaceAll(htmlContent, root, "$root$");
        deltaContent = RMT2String.replace(deltaContent, employee.getEmployeeFirstname() + " "
                + employee.getEmployeeLastname(), "$employeename$");
        deltaContent = RMT2String.replace(deltaContent, employee.getEmployeeTitle(), "$employeetitle$");
        deltaContent = RMT2String.replace(deltaContent, client.getClientName(), "$clientname$");
        deltaContent = RMT2String.replace(deltaContent, timesheet.getDisplayValue(), "$timesheetid$");
        deltaContent = RMT2String.replace(deltaContent, formattedDate, "$periodending$");

        // Get project/task hours
        String details = this.setupTimesheetEmailHours(hours);

        deltaContent = RMT2String.replace(deltaContent, details, "$timesheetdetails$");
        deltaContent = RMT2String.replace(deltaContent, String.valueOf(this.totalHours), "$totalhours$");
        deltaContent = RMT2String.replace(deltaContent, String.valueOf(timesheet.getTimesheetId()), "$timesheetid$");

        // Setup Approve and Decline comand buttons for manager actions
        deltaContent = RMT2String.replace(deltaContent, submitUri + "approve", "$approveURI$");
        deltaContent = RMT2String.replace(deltaContent, submitUri + "decline", "$declineURI$");

        RMT2File.createFile(deltaContent, outpuEmailDir + "/timesheet_submit_email.html");

        email.setBody(deltaContent, EmailMessageBean.HTML_CONTENT);
        return email;
    }

    /**
     * Builds HTML that is to present the hours of one or more project-task
     * instances.
     * <p>
     * Uses a timesheet details template file to build the HTML into rows an
     * columns for each project/task and its hours. Each project/task will
     * report each day's hours for a total of seven days.
     * 
     * @param hours
     *            A Map of project/task hours which the key exist as an instance
     *            of {@link ProjectTaskDto} and the value exist as a List of
     *            {@link EventDto}, respectively.
     * @return HTML table rows and columns hours for each project-task.
     * @throws TimesheetTransmissionException
     *             Unable to get the contents of the HTML template file used to
     *             build project/task details.
     */
    private String setupTimesheetEmailHours(Map<ProjectTaskDto, List<EventDto>> hours) throws TimesheetTransmissionException {
        String origHtmlContents = null;
        String htmlContents = null;
        String deltaContents = "";
        ProjectTaskDto vtpt = null;
        EventDto pe = null;

        // Get configuration properties
        ResourceBundle localConfig = RMT2File.loadAppConfigProperties("config.transactions.ProjectTracker-MessageHanlerConfig");
        String emailTemplateFile = localConfig.getString("email_details_template");

        try {
            InputStream is = RMT2File.getFileInputStream(emailTemplateFile);
            origHtmlContents = RMT2File.getStreamStringData(is);
        } catch (SystemException e) {
            throw new TimesheetTransmissionException(e);
        }

        Iterator<ProjectTaskDto> keys = hours.keySet().iterator();
        while (keys.hasNext()) {
            htmlContents = origHtmlContents;
            vtpt = keys.next();
            htmlContents = RMT2String.replace(htmlContents, vtpt.getProjectDescription(), "$projectname$");
            htmlContents = RMT2String.replace(htmlContents, vtpt.getTaskDescription(), "$taskname$");

            List<EventDto> list = hours.get(vtpt);
            for (int ndx = 0; ndx < list.size(); ndx++) {
                pe = list.get(ndx);
                totalHours += pe.getEventHours();
                htmlContents = RMT2String.replace(htmlContents, String.valueOf(pe.getEventHours()), "$" + ndx + "hrs$");
            }
            deltaContents += htmlContents;
        }
        return deltaContents;

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

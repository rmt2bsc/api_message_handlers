package org.rmt2.api.handlers.timesheet;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.BusinessContactDto;
import org.dto.ContactDto;
import org.dto.TimesheetDto;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.modules.ProjectTrackerApiConst;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiException;
import org.modules.contacts.ContactsApiFactory;
import org.modules.timesheet.TimesheetApi;
import org.modules.timesheet.TimesheetApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.api.handler.util.PdfReportUtility;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ProjectDetailGroup;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.jaxb.ReportAttachmentType;
import org.rmt2.util.ReportAttachmentTypeBuilder;

import com.InvalidDataException;
import com.NotFoundException;
import com.RMT2Exception;
import com.SystemException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2Money;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes timesheet printing related messages to the ProjectTracker
 * API.
 * 
 * @author roy.terrell
 *
 */
public abstract class AbstractTimesheetPrintApiHandler extends
        AbstractJaxbMessageHandler<ProjectProfileRequest, ProjectProfileResponse, ReportAttachmentType> {
    
    private static final Logger logger = Logger.getLogger(AbstractTimesheetPrintApiHandler.class);
    protected ObjectFactory jaxbObjFactory;
    protected TimesheetApi api;
    private String reportName;

    /**
     * @param payload
     */
    public AbstractTimesheetPrintApiHandler() {
        super();
        TimesheetApiFactory f = new TimesheetApiFactory();
        this.api = f.createApi(ProjectTrackerApiConst.APP_NAME);
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createProjectProfileResponse();
        logger.info(TimesheetApiHandler.class.getName() + " was instantiated successfully");
        logger.info(AbstractTimesheetPrintApiHandler.class.getName() + " was instantiated successfully");
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
        ReportAttachmentType pdfReport = null;
        this.sessionId = req.getHeader().getSessionId();

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            timesheetDto = TimesheetJaxbDtoFactory.createTimesheetDtoInstance(req.getProfile().getTimesheet().get(0));

            // Load base timesheet, timesheet summary, and detail hours
            this.api.load(timesheetDto.getTimesheetId());

            if (this.api.getTimesheet() == null) {
                throw new NotFoundException(TimesheetMessageHandlerConst.MESSAGE_PRINT_TIMESHEET_NOTFOUND);
            }

            // Get service provider data
            BusinessContactDto servProvider = null;
            try {
                String temp = System.getProperty("CompContactId");
                if (temp != null && RMT2Money.isNumeric(temp)) {
                    int contactId = Integer.valueOf(temp);
                    ContactsApi contactApi = ContactsApiFactory.createApi();
                    BusinessContactDto criteria = Rmt2AddressBookDtoFactory.getBusinessInstance(null);
                    criteria.setContactId(contactId);
                    List<ContactDto> custContacts = contactApi.getContact(criteria);
                    if (custContacts != null && !custContacts.isEmpty() && custContacts.get(0) instanceof BusinessContactDto) {
                        servProvider = (BusinessContactDto) custContacts.get(0);
                    }
                }
            } catch (ContactsApiException e) {
                logger.warn("Error fetching business contact data for printing timesheet");
            }

            // Build report
            String reportXml = this.buildTimesheetData(servProvider);
            pdfReport = this.generatePdf(reportXml);

            rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_PRINT_SUCCESS);

            // Always return "1" record count
            rs.setRecordCount(1);
            
             this.responseObj.setHeader(req.getHeader());

        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);

            String msg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_PRINT_ERROR,
                    String.valueOf(timesheetDto.getTimesheetId()), "%s");
            rs.setMessage(msg);
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(pdfReport, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Setup data file that is to be used during the XML transformation process.
     * 
     * @param serviceProvider
     *            instance of {@link BusinessContactDto}
     * @return String as the XML data
     */
    abstract protected String buildTimesheetData(BusinessContactDto serviceProvider);

    private ReportAttachmentType generatePdf(String jaxbXml) {
        PdfReportUtility xform = new PdfReportUtility(this.reportName, jaxbXml, true, this.sessionId);
        try {
            ByteArrayOutputStream pdf = null;
            OutputStream output = xform.buildReport();
            if (output instanceof ByteArrayOutputStream) {
                pdf = (ByteArrayOutputStream) output;
            }
            ReportAttachmentType report = ReportAttachmentTypeBuilder.Builder.create()
                    .withFilePath(xform.getPdfFileName())
                    .withFileSize(pdf.toByteArray().length)
                    .withMimeType("pdf")
                    .withReportId(xform.getReportId())
                    .withReportContent(pdf)
                    .build();
            return report;
        } catch (RMT2Exception e) {
            e.printStackTrace();
            throw new SystemException("Failed to generate Timesheet PDF file", e);
        }
    }

    @Override
    protected String buildResponse(ReportAttachmentType payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);
        }

        if (payload != null) {
            ProjectDetailGroup profile = this.jaxbObjFactory.createProjectDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().setAttachment(payload);
        }

        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }

    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        } catch (VerifyException e) {
            throw new InvalidRequestException("Timesheet message request element is invalid");
        }

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

    /**
     * @param reportName
     *            the reportName to set
     */
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
}

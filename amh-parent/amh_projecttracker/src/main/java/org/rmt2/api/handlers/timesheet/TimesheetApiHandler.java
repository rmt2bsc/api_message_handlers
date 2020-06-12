package org.rmt2.api.handlers.timesheet;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.TimesheetDto;
import org.modules.ProjectTrackerApiConst;
import org.modules.timesheet.TimesheetApi;
import org.modules.timesheet.TimesheetApiFactory;
import org.modules.timesheet.TimesheetTransmissionException;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ProjectDetailGroup;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.jaxb.TimesheetStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.rmt2.jaxb.TimesheetType;
import org.rmt2.util.projecttracker.timesheet.TimesheetStatusTypeBuilder;
import org.rmt2.util.projecttracker.timesheet.TimesheetTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.email.EmailMessageBean;
import com.api.messaging.email.smtp.SmtpApi;
import com.api.messaging.email.smtp.SmtpFactory;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Timesheet related messages to the Project Tracker
 * Administration API.
 * 
 * @author roy.terrell
 *
 */
public class TimesheetApiHandler extends
        AbstractJaxbMessageHandler<ProjectProfileRequest, ProjectProfileResponse, List<TimesheetType>> {
    
    private static final Logger logger = Logger.getLogger(TimesheetApiHandler.class);
    protected ObjectFactory jaxbObjFactory;
    protected TimesheetApi api;

    /**
     * @param payload
     */
    public TimesheetApiHandler() {
        super();
        TimesheetApiFactory f = new TimesheetApiFactory();
        this.api = f.createApi(ProjectTrackerApiConst.APP_NAME);
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createProjectProfileResponse();
        logger.info(TimesheetApiHandler.class.getName() + " was instantiated successfully");
    }

    
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Employee message request element is invalid");
        }
    }

    /**
     * 
     * @param dto
     * @return
     */
    protected List<TimesheetType> buildJaxbQueryResults(List<TimesheetDto> dto) {
        List<TimesheetType> list = new ArrayList<>();
        for (TimesheetDto item : dto) {
            TimesheetType jaxbObj = TimesheetJaxbDtoFactory.createTimesheetJaxbInstance(item);
            list.add(jaxbObj);
        }
        return list;
    }

    /**
     * 
     * @param dto
     * @return
     */
    protected List<TimesheetType> buildJaxbUpdateResults(TimesheetDto dto) {
        List<TimesheetType> list = new ArrayList<>();
        TimesheetType jaxbObj = TimesheetJaxbDtoFactory.createTimesheetJaxbAbbreviatedInstance(dto);
        list.add(jaxbObj);
        return list;
    }

    /**
     * 
     * @param dto
     * @return
     */
    protected List<TimesheetType> buildJaxbStatusChangeResults(TimesheetDto dto) {
        List<TimesheetType> list = new ArrayList<>();

        TimesheetStatusType tst = TimesheetStatusTypeBuilder.Builder.create()
                .withName(dto.getStatusName())
                .build();

        TimesheetType jaxbObj = TimesheetTypeBuilder.Builder.create()
                .withTimesheetId(dto.getTimesheetId())
                .withEmployeeBillableHours(dto.getBillHrs())
                .withStatus(tst)
                .build();

        list.add(jaxbObj);
        return list;
    }

    @Override
    protected String buildResponse(List<TimesheetType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            ProjectDetailGroup profile = this.jaxbObjFactory.createProjectDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getTimesheet().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }

    /**
     * Emails a copy of an employee's timesheet to the employee's manager using
     * <i>timesheet</i>, <i>employee</i>, <i>client</i>, and <i>hours</i>.
     * 
     * @param email
     *            An instance of {@link EmailMessageBean}
     * @return The SMTP return code.
     * @throws TimesheetTransmissionException
     *             Error occurs sending timesheet data to its designated
     *             recipient via the SMTP protocol.
     */
    protected Object send(EmailMessageBean email) throws TimesheetTransmissionException {

        if (email == null) {
            return null;
        }
        SmtpApi emailApi = null;
        try {
            // Send the timesheet over the SMTP protocol.
            emailApi = SmtpFactory.getSmtpInstance();
            Object rc = emailApi.sendMessage(email);
            return rc;
        } catch (Exception e) {
            this.msg = "Error occurred sending email message to " + email.getRecipients();
            throw new TimesheetTransmissionException(this.msg, e);
        } finally {
            emailApi.close();
        }
    }
}

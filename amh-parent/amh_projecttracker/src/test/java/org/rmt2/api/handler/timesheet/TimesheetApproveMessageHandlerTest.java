package org.rmt2.api.handler.timesheet;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dao.timesheet.TimesheetConst;
import org.dto.EmployeeDto;
import org.dto.EventDto;
import org.dto.ProjectTaskDto;
import org.dto.TimesheetDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.admin.ProjectAdminApiFactory;
import org.modules.employee.EmployeeApi;
import org.modules.employee.EmployeeApiException;
import org.modules.employee.EmployeeApiFactory;
import org.modules.timesheet.TimesheetApi;
import org.modules.timesheet.TimesheetApiException;
import org.modules.timesheet.TimesheetApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.ProjectTrackerMockData;
import org.rmt2.api.handler.BaseProjectTrackerMessageHandlerTest;
import org.rmt2.api.handlers.timesheet.TimesheetMessageHandlerConst;
import org.rmt2.api.handlers.timesheet.TimesheetPostSubmitApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.TimesheetType;

import com.SystemException;
import com.api.config.SystemConfigurator;
import com.api.messaging.email.EmailMessageBean;
import com.api.messaging.email.smtp.SmtpApi;
import com.api.messaging.email.smtp.SmtpFactory;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.DaoClient;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2Date;
import com.api.util.RMT2File;
import com.api.util.RMT2String;

/**
 * Test the timesheet approve process of the Project Tracker Api.
 * 
 * @author rterrell
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Rmt2OrmClientFactory.class, TimesheetPostSubmitApiHandler.class, TimesheetApiFactory.class,
        EmployeeApiFactory.class, ProjectAdminApiFactory.class, SmtpFactory.class, SystemConfigurator.class, RMT2Date.class })
public class TimesheetApproveMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    private static final int TIMESHEET_ID = 111;
    public static final String API_ERROR = "Test Error: Timesheet API approve error occurred";
    public static final String END_PERIOD_DATE_FORMAT_ERROR = "Error formatting timeshset end period date";

    private TimesheetApi mockApi;
    private TimesheetApiFactory mockApiFactory;
    private EmployeeApi mockEmpApi;

    private  EmployeeDto employee;
    private TimesheetDto timesheetExt;
    private Map<ProjectTaskDto, List<EventDto>> hours;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();

        mockApi = Mockito.mock(TimesheetApi.class);
        PowerMockito.mockStatic(TimesheetApiFactory.class);
        mockApiFactory = Mockito.mock(TimesheetApiFactory.class);
        PowerMockito.whenNew(TimesheetApiFactory.class).withNoArguments().thenReturn(mockApiFactory);
        when(mockApiFactory.createApi(isA(String.class))).thenReturn(mockApi);

        DaoClient mockDaoClient = Mockito.mock(DaoClient.class);
        when(mockApi.getSharedDao()).thenReturn(mockDaoClient);

        mockEmpApi = Mockito.mock(EmployeeApi.class);
        PowerMockito.mockStatic(EmployeeApiFactory.class);
        when(EmployeeApiFactory.createApi(isA(DaoClient.class))).thenReturn(mockEmpApi);

        doNothing().when(this.mockApi).close();

        SmtpApi mockSmtpApi = Mockito.mock(SmtpApi.class);
        PowerMockito.mockStatic(SmtpFactory.class);
        try {
            when(SmtpFactory.getSmtpInstance()).thenReturn(mockSmtpApi);
        } catch (Exception e) {
            Assert.fail("Failed to stub SmtpFactory.getSmtpInstance method");
        }
        try {
            when(mockSmtpApi.sendMessage(isA(EmailMessageBean.class))).thenReturn(1);
            doNothing().when(mockSmtpApi).close();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("The mocking of TimesheetTransmissionApi's send method failed");
        }

        this.createInputData();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        return;
    }

    private void createInputData() {
        this.employee = ProjectTrackerMockData.createMockMultipleExtEmployee().get(0);
        this.employee.setEmployeeFirstname("John");
        this.employee.setEmployeeLastname("Smith");
        this.timesheetExt = TimesheetMockData.createMockExtTimesheetList().get(0);
        this.timesheetExt.setStatusId(TimesheetConst.STATUS_APPROVED);
        this.timesheetExt.setStatusName("Approved");
    }

    @Test
    public void testSuccess_Approve() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetApproveRequest.xml");
        
        try {
            when(this.mockApi.approve(isA(Integer.class))).thenReturn(TimesheetConst.STATUS_APPROVED);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for approve timesheet method");
        }

        try {
            when(this.mockApi.getTimesheet()).thenReturn(this.timesheetExt);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet graph");
        }

        try {
            when(this.mockEmpApi.getEmployeeExt(isA(EmployeeDto.class))).thenReturn(
                    ProjectTrackerMockData.createMockMultipleExtEmployee());
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet employee");
        }

        TimesheetPostSubmitApiHandler handler = new TimesheetPostSubmitApiHandler();
        MessageHandlerResults results = null;
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_APPROVE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTimesheet());
        Assert.assertEquals(1, actualRepsonse.getProfile().getTimesheet().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String msg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_POST_SUBMIT_SUCCESS, "approved", "%s");
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());

        for (TimesheetType ts : actualRepsonse.getProfile().getTimesheet()) {
            Assert.assertNotNull(ts.getTimesheetId());
            Assert.assertEquals(TIMESHEET_ID, ts.getTimesheetId().intValue());
            Assert.assertNotNull(ts.getStatus());
            Assert.assertEquals("Approved", ts.getStatus().getName());
            Assert.assertNotNull(ts.getBillableHours());
            Assert.assertEquals(40, ts.getBillableHours().doubleValue(), 0);
        }
    }

    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetApproveRequest.xml");

        try {
            when(this.mockApi.approve(isA(Integer.class))).thenThrow(new TimesheetApiException(API_ERROR));
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for approving timesheet method");
        }

        TimesheetPostSubmitApiHandler handler = new TimesheetPostSubmitApiHandler();
        MessageHandlerResults results = null;
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_APPROVE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String msg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_POST_SUBMIT_ERROR, "approving", "%s");
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testError_Email_Send_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetApproveRequest.xml");

        try {
            when(this.mockApi.approve(isA(Integer.class))).thenReturn(1);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for approving timesheet method");
        }

        try {
            when(this.mockApi.getTimesheet()).thenReturn(this.timesheetExt);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet graph");
        }
        try {
            when(this.mockApi.getTimesheetHours()).thenReturn(this.hours);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet hours graph");
        }

        try {
            when(this.mockEmpApi.getEmployeeExt(isA(EmployeeDto.class))).thenThrow(new EmployeeApiException(API_ERROR));
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet employee");
        }

        TimesheetPostSubmitApiHandler handler = new TimesheetPostSubmitApiHandler();
        MessageHandlerResults results = null;
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_APPROVE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTimesheet());
        Assert.assertEquals(1, actualRepsonse.getProfile().getTimesheet().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String msg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_POST_SUBMIT_SUCCESS, "approved", "%s");
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertNotNull(actualRepsonse.getReplyStatus().getExtMessage());

        for (TimesheetType ts : actualRepsonse.getProfile().getTimesheet()) {
            Assert.assertNotNull(ts.getTimesheetId());
            Assert.assertEquals(TIMESHEET_ID, ts.getTimesheetId().intValue());
        }
    }

    @Test
    public void testError_Email_Send_End_Period_Formatting_Error() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetApproveRequest.xml");

        try {
            when(this.mockApi.approve(isA(Integer.class))).thenReturn(1);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for approving timesheet method");
        }

        try {
            when(this.mockApi.getTimesheet()).thenReturn(this.timesheetExt);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet graph");
        }

        try {
            when(this.mockEmpApi.getEmployeeExt(isA(EmployeeDto.class))).thenReturn(
                    ProjectTrackerMockData.createMockMultipleExtEmployee());
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet employee");
        }

        PowerMockito.mockStatic(RMT2Date.class);
        try {
            when(RMT2Date.formatDate(isA(Date.class), isA(String.class))).thenThrow(
                    new SystemException(END_PERIOD_DATE_FORMAT_ERROR));
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for RMT2Date.formatDate method");
        }

        TimesheetPostSubmitApiHandler handler = new TimesheetPostSubmitApiHandler();
        MessageHandlerResults results = null;
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_APPROVE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTimesheet());
        Assert.assertEquals(1, actualRepsonse.getProfile().getTimesheet().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String msg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_POST_SUBMIT_SUCCESS, "approved", "%s");
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());

        Assert.assertNotNull(actualRepsonse.getReplyStatus().getExtMessage());

        for (TimesheetType ts : actualRepsonse.getProfile().getTimesheet()) {
            Assert.assertNotNull(ts.getTimesheetId());
            Assert.assertEquals(TIMESHEET_ID, ts.getTimesheetId().intValue());
        }
    }

    @Test
    public void testSuccess_Timesheet_Not_Exists() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetApproveRequest.xml");

        try {
            when(this.mockApi.approve(isA(Integer.class))).thenReturn(0);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for approving timesheet method");
        }
        
        TimesheetPostSubmitApiHandler handler = new TimesheetPostSubmitApiHandler();
        MessageHandlerResults results = null;
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_APPROVE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTimesheet());
        Assert.assertEquals(1, actualRepsonse.getProfile().getTimesheet().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_POST_SUBMIT_RECORD_NOT_FOUND, actualRepsonse.getReplyStatus()
                .getMessage());
    }
    
    @Test
    public void testValidation_Missing_Profile() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetApproveMissingProfileRequest.xml");

        MessageHandlerResults results = null;
        TimesheetPostSubmitApiHandler handler = new TimesheetPostSubmitApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_APPROVE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(TimesheetMessageHandlerConst.VALIDATION_TIMESHEET_MISSING, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Missing_Timesheet() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetApproveMissingTimesheetRequest.xml");

        MessageHandlerResults results = null;
        TimesheetPostSubmitApiHandler handler = new TimesheetPostSubmitApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_APPROVE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(TimesheetMessageHandlerConst.VALIDATION_TIMESHEET_MISSING, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Too_Many_Timesheets() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetApproveTooManyTimesheetsRequest.xml");

        MessageHandlerResults results = null;
        TimesheetPostSubmitApiHandler handler = new TimesheetPostSubmitApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_APPROVE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(TimesheetMessageHandlerConst.VALIDATION_TIMESHEET_TOO_MANY, actualRepsonse.getReplyStatus()
                .getMessage());
    }
    
}
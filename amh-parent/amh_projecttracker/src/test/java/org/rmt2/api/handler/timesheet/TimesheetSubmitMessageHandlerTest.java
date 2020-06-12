package org.rmt2.api.handler.timesheet;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dao.mapping.orm.rmt2.ProjEvent;
import org.dao.mapping.orm.rmt2.VwTimesheetProjectTask;
import org.dao.timesheet.TimesheetConst;
import org.dto.ClientDto;
import org.dto.EmployeeDto;
import org.dto.EventDto;
import org.dto.ProjectTaskDto;
import org.dto.TimesheetDto;
import org.dto.TimesheetHistDto;
import org.dto.adapter.orm.EmployeeObjectFactory;
import org.dto.adapter.orm.ProjectObjectFactory;
import org.dto.adapter.orm.TimesheetObjectFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.admin.ProjectAdminApi;
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
import org.rmt2.api.handlers.timesheet.TimesheetSubmitApiHandler;
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
 * Test the timesheet submittal process of the Project Tracker Api.
 * 
 * @author rterrell
 * 
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ Rmt2OrmClientFactory.class, TimesheetSubmitApiHandler.class, TimesheetApiFactory.class,
        EmployeeApiFactory.class, ProjectAdminApiFactory.class, SmtpFactory.class, SystemConfigurator.class, RMT2Date.class })
public class TimesheetSubmitMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    private static final int TIMESHEET_ID = 111;
    private static final String TIMESHEET_ID_DISPLAY = "0000000111";
    private static final int EMPLOYEE_ID = 2220;
    public static final String API_ERROR = "Test Error: Timesheet API submit error occurred";
    public static final String END_PERIOD_DATE_FORMAT_ERROR = "Error formatting timeshset end period date";

    private TimesheetApi mockApi;
    private TimesheetApiFactory mockApiFactory;
    private EmployeeApi mockEmpApi;
    private ProjectAdminApi mockProjApi;

    private  ClientDto client;
    private  EmployeeDto employee;
    private  EmployeeDto manager;
    private TimesheetDto timesheet;
    private TimesheetDto timesheetExt;
    private TimesheetHistDto currentStatus;
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

        mockProjApi = Mockito.mock(ProjectAdminApi.class);
        PowerMockito.mockStatic(ProjectAdminApiFactory.class);
        when(ProjectAdminApiFactory.createApi(isA(DaoClient.class))).thenReturn(mockProjApi);

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
        this.client = ProjectTrackerMockData.createMockMultipleClient().get(0);
        this.employee = ProjectTrackerMockData.createMockMultipleExtEmployee().get(0);
        this.employee.setEmployeeFirstname("John");
        this.employee.setEmployeeLastname("Smith");
        this.manager = EmployeeObjectFactory.createEmployeeDtoInstance(ProjectTrackerMockData.createMockSingleManager().get(0));
        this.timesheet = TimesheetObjectFactory.createTimesheetDtoInstance(TimesheetMockData
                .createMockMultipleTimesheetSameClientList().get(0));
        this.timesheetExt = TimesheetMockData.createMockExtTimesheetList().get(0);
        this.timesheetExt.setStatusId(TimesheetConst.STATUS_SUBMITTED);
        this.timesheetExt.setStatusName("Submitted");
        this.currentStatus = TimesheetObjectFactory.createTimesheetHistoryDtoInstance(TimesheetMockData
                .createMockTimesheetStatusHistory().get(2));
        this.hours = this.buildTimesheetHoursDtoMap();
    }
    
    private Map<ProjectTaskDto, List<EventDto>> buildTimesheetHoursDtoMap() {
        Map<ProjectTaskDto, List<EventDto>> hours = new HashMap<>(); 
        for (VwTimesheetProjectTask pt : TimesheetMockData.createMockMultipleVwTimesheetProjectTask()) {
            ProjectTaskDto ptDto = ProjectObjectFactory.createProjectTaskExtendedDtoInstance(pt);
            List<EventDto> eventsDto = this.buildTimesheetEventDtoList(pt.getProjectTaskId());
            hours.put(ptDto, eventsDto);
        }
        return hours;
    }
    
    private List<EventDto> buildTimesheetEventDtoList(int projectTaskId) {
        List<ProjEvent> projEvents = TimesheetMockData.createMockMultiple_Day_Task_Events(projectTaskId);
        List<EventDto> eventsDto = new ArrayList<>();
        for (ProjEvent evt : projEvents) {
            EventDto evtDto = ProjectObjectFactory.createEventDtoInstance(evt);
            eventsDto.add(evtDto);
        }
        return eventsDto;
    }
    

    @Test
    public void testSuccess_SubmitTimesheet() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetSubmitRequest.xml");
        
        try {
            when(this.mockApi.submit(isA(Integer.class))).thenReturn(1);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for submitting timesheet method");
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
            when(this.mockEmpApi.getEmployeeExt(isA(EmployeeDto.class))).thenReturn(
                    ProjectTrackerMockData.createMockMultipleExtEmployee());
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet employee");
        }
        try {
            when(this.mockEmpApi.getEmployee(isA(Integer.class))).thenReturn(this.manager);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet employee manager");
        }
        try {
            when(this.mockProjApi.getClient(isA(ClientDto.class))).thenReturn(ProjectTrackerMockData.createMockMultipleClient());
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet client");
        }

        TimesheetSubmitApiHandler handler = new TimesheetSubmitApiHandler();
        MessageHandlerResults results = null;
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_SUBMIT, request);
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
        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_SUBMIT_SUCCESS, actualRepsonse.getReplyStatus().getMessage());

        for (TimesheetType ts : actualRepsonse.getProfile().getTimesheet()) {
            Assert.assertNotNull(ts.getTimesheetId());
            Assert.assertEquals(TIMESHEET_ID, ts.getTimesheetId().intValue());
            Assert.assertNotNull(ts.getStatus());
            Assert.assertEquals("Submitted", ts.getStatus().getName());
            Assert.assertNotNull(ts.getBillableHours());
            Assert.assertEquals(40, ts.getBillableHours().doubleValue(), 0);
        }
    }

    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetSubmitRequest.xml");

        try {
            when(this.mockApi.submit(isA(Integer.class))).thenThrow(new TimesheetApiException(API_ERROR));
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for submitting timesheet method");
        }

        TimesheetSubmitApiHandler handler = new TimesheetSubmitApiHandler();
        MessageHandlerResults results = null;
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_SUBMIT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_SUBMIT_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testError_Email_Send_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetSubmitRequest.xml");

        try {
            when(this.mockApi.submit(isA(Integer.class))).thenReturn(1);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for submitting timesheet method");
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

        TimesheetSubmitApiHandler handler = new TimesheetSubmitApiHandler();
        MessageHandlerResults results = null;
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_SUBMIT, request);
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
        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_SUBMIT_SUCCESS, actualRepsonse.getReplyStatus().getMessage());

        String msg = "Data access error fetching timesheet's employee profile: " + EMPLOYEE_ID;
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getExtMessage());

        for (TimesheetType ts : actualRepsonse.getProfile().getTimesheet()) {
            Assert.assertNotNull(ts.getTimesheetId());
            Assert.assertEquals(TIMESHEET_ID, ts.getTimesheetId().intValue());
        }
    }

    @Test
    public void testError_Email_Send_End_Period_Formatting_Error() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetSubmitRequest.xml");

        try {
            when(this.mockApi.submit(isA(Integer.class))).thenReturn(1);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for submitting timesheet method");
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
            when(this.mockEmpApi.getEmployeeExt(isA(EmployeeDto.class))).thenReturn(
                    ProjectTrackerMockData.createMockMultipleExtEmployee());
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet employee");
        }
        try {
            when(this.mockEmpApi.getEmployee(isA(Integer.class))).thenReturn(this.manager);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet employee manager");
        }
        try {
            when(this.mockProjApi.getClient(isA(ClientDto.class))).thenReturn(ProjectTrackerMockData.createMockMultipleClient());
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet client");
        }

        PowerMockito.mockStatic(RMT2Date.class);
        try {
            when(RMT2Date.formatDate(isA(Date.class), isA(String.class))).thenThrow(
                    new SystemException(END_PERIOD_DATE_FORMAT_ERROR));
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for RMT2Date.formatDate method");
        }

        TimesheetSubmitApiHandler handler = new TimesheetSubmitApiHandler();
        MessageHandlerResults results = null;
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_SUBMIT, request);
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
        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_SUBMIT_SUCCESS, actualRepsonse.getReplyStatus().getMessage());

        String msg = "SMTP error occurred attempting to send timesheet: " + TIMESHEET_ID_DISPLAY;
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getExtMessage());

        for (TimesheetType ts : actualRepsonse.getProfile().getTimesheet()) {
            Assert.assertNotNull(ts.getTimesheetId());
            Assert.assertEquals(TIMESHEET_ID, ts.getTimesheetId().intValue());
        }
    }

    @Test
    public void testSuccess_Timesheet_Not_Exists() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetSubmitRequest.xml");

        try {
            when(this.mockApi.submit(isA(Integer.class))).thenReturn(0);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for submitting timesheet method");
        }
        
        TimesheetSubmitApiHandler handler = new TimesheetSubmitApiHandler();
        MessageHandlerResults results = null;
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_SUBMIT, request);
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
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String msg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_SUBMIT_RECORD_NOT_FOUND,
                String.valueOf(actualRepsonse.getProfile().getTimesheet().get(0).getTimesheetId().intValue()), "%s");

        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_Missing_Profile() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetSubmitMissingProfileRequest.xml");

        MessageHandlerResults results = null;
        TimesheetSubmitApiHandler handler = new TimesheetSubmitApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_SUBMIT, request);
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
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetSubmitMissingTimesheetRequest.xml");

        MessageHandlerResults results = null;
        TimesheetSubmitApiHandler handler = new TimesheetSubmitApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_SUBMIT, request);
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
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetSubmitTooManyTimesheetsRequest.xml");

        MessageHandlerResults results = null;
        TimesheetSubmitApiHandler handler = new TimesheetSubmitApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_SUBMIT, request);
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
package org.rmt2.api.handler.timesheet;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dao.mapping.orm.rmt2.ProjEvent;
import org.dao.mapping.orm.rmt2.VwTimesheetProjectTask;
import org.dto.BusinessContactDto;
import org.dto.ContactDto;
import org.dto.EventDto;
import org.dto.ProjectTaskDto;
import org.dto.TimesheetDto;
import org.dto.adapter.orm.ProjectObjectFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiFactory;
import org.modules.timesheet.TimesheetApi;
import org.modules.timesheet.TimesheetApiException;
import org.modules.timesheet.TimesheetApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseProjectTrackerMessageHandlerTest;
import org.rmt2.api.handlers.timesheet.TimesheetMessageHandlerConst;
import org.rmt2.api.handlers.timesheet.TimesheetPrintWorkLogApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.ReportAttachmentType;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2File;
import com.api.util.RMT2String;

/**
 * Test the idenity and invocation of the timesheet work log print handler for
 * the Project Tracker API Message Handler.
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class,
        TimesheetPrintWorkLogApiHandler.class, TimesheetApiFactory.class, ContactsApiFactory.class, SystemConfigurator.class })
public class TimesheetPrintWorkLogMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    public static final int TIMESHEET_ID = 111;
    public static final int BUSINESS_ID = 1351;
    public static final String PROP_SERIAL_PATH = "\\temp\\";
    public static final String PROP_RPT_XSLT_PATH = "reports";
    public static final String API_ERROR = "Test API Error for print timesheet work load";

    private TimesheetApi mockApi;
    private TimesheetApiFactory mockApiFactory;
    private ContactsApi mockContactApi;
    private TimesheetDto mockTimesheetExt;
    private Map<ProjectTaskDto, List<EventDto>> mockWorkLog;
    private List<ContactDto> mockBusinessContactDtoList;


    /**
     * 
     */
    public TimesheetPrintWorkLogMessageHandlerTest() {
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see testcases.messaging.MessageToListenerToHandlerTest#setUp()
     */
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        mockApi = Mockito.mock(TimesheetApi.class);
        PowerMockito.mockStatic(TimesheetApiFactory.class);
        mockApiFactory = Mockito.mock(TimesheetApiFactory.class);
        PowerMockito.whenNew(TimesheetApiFactory.class).withNoArguments().thenReturn(mockApiFactory);
        when(mockApiFactory.createApi(isA(String.class))).thenReturn(mockApi);

        mockContactApi = Mockito.mock(ContactsApi.class);
        PowerMockito.mockStatic(ContactsApiFactory.class);
        when(ContactsApiFactory.createApi()).thenReturn(mockContactApi);

        doNothing().when(this.mockApi).close();

        System.setProperty("CompContactId", String.valueOf(BUSINESS_ID));
        System.setProperty("SerialPath", PROP_SERIAL_PATH);
        System.setProperty("RptXsltPath", PROP_RPT_XSLT_PATH);

        this.createInputData();
        return;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see testcases.messaging.MessageToListenerToHandlerTest#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        return;
    }

    private void createInputData() {
        this.mockTimesheetExt = TimesheetMockData.createMockExtTimesheetList().get(0);
        this.mockWorkLog = this.buildTimesheetHoursDtoMap();
        this.mockBusinessContactDtoList = TimesheetMockData.createMockSingleBusinessContactDto();
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
    public void testSuccess_Print() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetPrintWorkLogRequest.xml");

        try {
            when(this.mockApi.load(isA(Integer.class))).thenReturn(null);
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for loading timesheet graph");
        }
        
        try {
            when(this.mockApi.getTimesheet()).thenReturn(this.mockTimesheetExt);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet graph");
        }
        try {
            when(this.mockApi.getTimesheetHours()).thenReturn(this.mockWorkLog);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet work log graph");
        }

        try {
            when(this.mockContactApi.getContact(isA(BusinessContactDto.class))).thenReturn(this.mockBusinessContactDtoList);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed to setup stub for obtaining employee contact information graph");
        }

        MessageHandlerResults results = null;
        TimesheetPrintWorkLogApiHandler handler = new TimesheetPrintWorkLogApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_PRINT_WORKLOG, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile().getAttachment());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_PRINT_SUCCESS, actualRepsonse.getReplyStatus().getMessage());

        ReportAttachmentType att = actualRepsonse.getProfile().getAttachment();
        Assert.assertNotNull(att.getFilePath());
        int rc = RMT2File.verifyFile(att.getFilePath());
        Assert.assertEquals(RMT2File.FILE_IO_EXIST, rc);
        
        Assert.assertNotNull(att.getContent());
        Assert.assertTrue(att.getContent().length > 100);
    }
    
    @Test
    public void test_Print_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetPrintWorkLogRequest.xml");

        try {
            when(this.mockApi.load(isA(Integer.class))).thenReturn(null);
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for loading timesheet graph");
        }

        try {
            when(this.mockApi.getTimesheet()).thenReturn(null);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for obtaining timesheet graph");
        }
        
        MessageHandlerResults results = null;
        TimesheetPrintWorkLogApiHandler handler = new TimesheetPrintWorkLogApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_PRINT_WORKLOG, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        
        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String msg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_PRINT_ERROR, String.valueOf(TIMESHEET_ID), "%s");
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_PRINT_TIMESHEET_NOTFOUND, actualRepsonse.getReplyStatus()
                .getExtMessage());
    }
    
    @Test
    public void testError_Print_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetPrintWorkLogRequest.xml");
        try {
            when(this.mockApi.load(isA(Integer.class))).thenThrow(new TimesheetApiException(API_ERROR));
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for loading timesheet graph");
        }
        
        MessageHandlerResults results = null;
        TimesheetPrintWorkLogApiHandler handler = new TimesheetPrintWorkLogApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_PRINT_WORKLOG, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        String msg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_PRINT_ERROR, String.valueOf(TIMESHEET_ID), "%s");
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testValidation_Missing_Profile() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetPrintWorkLogMissingProfileRequest.xml");

        MessageHandlerResults results = null;
        TimesheetPrintWorkLogApiHandler handler = new TimesheetPrintWorkLogApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_PRINT_WORKLOG, request);
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
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetPrintWorkLogMissingTimesheetRequest.xml");

        MessageHandlerResults results = null;
        TimesheetPrintWorkLogApiHandler handler = new TimesheetPrintWorkLogApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_PRINT_WORKLOG, request);
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
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetPrintWorkLogTooManyTimesheetsRequest.xml");

        MessageHandlerResults results = null;
        TimesheetPrintWorkLogApiHandler handler = new TimesheetPrintWorkLogApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_PRINT_WORKLOG, request);
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

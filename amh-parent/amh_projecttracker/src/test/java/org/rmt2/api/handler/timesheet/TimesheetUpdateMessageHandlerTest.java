package org.rmt2.api.handler.timesheet;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.dto.TimesheetDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.timesheet.TimesheetApi;
import org.modules.timesheet.TimesheetApiException;
import org.modules.timesheet.TimesheetApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseProjectTrackerMessageHandlerTest;
import org.rmt2.api.handlers.timesheet.TimesheetMessageHandlerConst;
import org.rmt2.api.handlers.timesheet.TimesheetUpdateApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.TimesheetType;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2File;

/**
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class,
        TimesheetUpdateApiHandler.class, TimesheetApiFactory.class, SystemConfigurator.class })
public class TimesheetUpdateMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    public static final int TIMESHEET_ID = 900;
    public static final int EMPLOYEE_ID = 2000;
    public static final int UPDATE_ROW_COUNT = 1;
    public static final String EMPLOYEE_FIRSTNAME = "roy";
    public static final String EMPLOYEE_LASTNAME = "terrell";
    public static final String API_ERROR = "Test Error: Timesheet API error occurred";
    private TimesheetApi mockApi;
    private TimesheetApiFactory mockApiFactory;

    /**
     * 
     */
    public TimesheetUpdateMessageHandlerTest() {
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
        doNothing().when(this.mockApi).close();
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
    
    @Test
    public void testSuccess_Create() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetInsertRequest.xml");
        try {
            when(this.mockApi.updateTimesheet(isA(TimesheetDto.class), isA(Map.class))).thenReturn(TIMESHEET_ID);
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for creating timesheet record");
        }
        
        MessageHandlerResults results = null;
        TimesheetUpdateApiHandler handler = new TimesheetUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getTimesheet().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_UPDATE_NEW_SUCCESS, actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTimesheet().size(); ndx++) {
            TimesheetType a = actualRepsonse.getProfile().getTimesheet().get(ndx);
            Assert.assertNotNull(a.getTimesheetId());
            Assert.assertEquals(TIMESHEET_ID, a.getTimesheetId().intValue());
            Assert.assertNotNull(a.getEmployee());
            Assert.assertNotNull(a.getEmployee().getContactDetails());
            Assert.assertNotNull(a.getEmployee().getContactDetails().getFirstName());
            Assert.assertNotNull(a.getEmployee().getContactDetails().getLastName());
            Assert.assertEquals(EMPLOYEE_FIRSTNAME, a.getEmployee().getContactDetails().getFirstName());
            Assert.assertEquals(EMPLOYEE_LASTNAME, a.getEmployee().getContactDetails().getLastName());
        }
    }
    
    @Test
    public void testSuccess_Modify() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetUpdateRequest.xml");
        try {
            when(this.mockApi.updateTimesheet(isA(TimesheetDto.class), isA(Map.class))).thenReturn(TIMESHEET_ID);
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for modifying timesheet record");
        }

        MessageHandlerResults results = null;
        TimesheetUpdateApiHandler handler = new TimesheetUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getTimesheet().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_UPDATE_EXISTING_SUCCESS, actualRepsonse.getReplyStatus()
                .getMessage());

        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTimesheet().size(); ndx++) {
            TimesheetType a = actualRepsonse.getProfile().getTimesheet().get(ndx);
            Assert.assertNotNull(a.getTimesheetId());
            Assert.assertEquals(TIMESHEET_ID, a.getTimesheetId().intValue());
            Assert.assertNotNull(a.getEmployee());
            Assert.assertNotNull(a.getEmployee().getContactDetails());
            Assert.assertNotNull(a.getEmployee().getContactDetails().getFirstName());
            Assert.assertNotNull(a.getEmployee().getContactDetails().getLastName());
            Assert.assertEquals(EMPLOYEE_FIRSTNAME, a.getEmployee().getContactDetails().getFirstName());
            Assert.assertEquals(EMPLOYEE_LASTNAME, a.getEmployee().getContactDetails().getLastName());
        }
    }

    @Test
    public void testError_Insert_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetInsertRequest.xml");
        try {
            when(this.mockApi.updateTimesheet(isA(TimesheetDto.class), isA(Map.class)))
                    .thenThrow(new TimesheetApiException(API_ERROR));
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for creating employee contact record");
        }

        MessageHandlerResults results = null;
        TimesheetUpdateApiHandler handler = new TimesheetUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE, request);
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
        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_UPDATE_NEW_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testError_Modify_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetUpdateRequest.xml");
        try {
            when(this.mockApi.updateTimesheet(isA(TimesheetDto.class), isA(Map.class)))
                    .thenThrow(new TimesheetApiException(API_ERROR));
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for creating employee contact record");
        }

        MessageHandlerResults results = null;
        TimesheetUpdateApiHandler handler = new TimesheetUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE, request);
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
        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_UPDATE_EXISTING_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testValidation_Missing_Profile() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetUpdateMissingProfileRequest.xml");
        try {
            when(this.mockApi.updateTimesheet(isA(TimesheetDto.class), isA(Map.class)))
                    .thenThrow(new TimesheetApiException(API_ERROR));
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for creating employee contact record");
        }

        MessageHandlerResults results = null;
        TimesheetUpdateApiHandler handler = new TimesheetUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE, request);
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
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetUpdateMissingTimesheetRequest.xml");
        try {
            when(this.mockApi.updateTimesheet(isA(TimesheetDto.class), isA(Map.class)))
                    .thenThrow(new TimesheetApiException(API_ERROR));
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for creating employee contact record");
        }

        MessageHandlerResults results = null;
        TimesheetUpdateApiHandler handler = new TimesheetUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE, request);
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
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetUpdateTooManyTimesheetsRequest.xml");
        try {
            when(this.mockApi.updateTimesheet(isA(TimesheetDto.class), isA(Map.class)))
                    .thenThrow(new TimesheetApiException(API_ERROR));
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for creating employee contact record");
        }

        MessageHandlerResults results = null;
        TimesheetUpdateApiHandler handler = new TimesheetUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_UPDATE, request);
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

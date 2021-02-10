package org.rmt2.api.handler.timesheet;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dao.timesheet.TimesheetConst;
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
import org.rmt2.api.handlers.timesheet.TimesheetQueryApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.TimesheetType;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2Date;
import com.api.util.RMT2File;
import com.api.util.RMT2String;

/**
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class,
        TimesheetQueryApiHandler.class, TimesheetApiFactory.class, SystemConfigurator.class })
public class TimesheetQueryMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    public static final int TIMESHEET_ID_SEED = 111;
    public static final int CLIENT_ID = 1110;
    public static final int PROJ_ID = 1234;
    public static final int EMP_ID = 2220;
    public static final int MGR_ID = 3330;
    public static final int STATUS_HIST_ID = 5555;

    public static final int BILLABLE_HOURS = 40;
    public static final int NONBILLABLE_HOURS = 0;
    public static final double HOURLY_PAY_RATE = 70;
    public static final double HOURLY_OVERTIME_PAY_RATE = 80;

    public static final String INVOICE_REF_NO_SEED = "INVREF123";
    public static final String PERIOD_SEED = "2018-01-0";
    public static final String EXT_REF_SEED = "ExtReNo100";
    public static final String STATUS_NAME = "DRAFT";
    public static final String ACCT_NO = "ACCT-111";
    public static final String COMMENTS_SEED = "Comments";
    public static final String CLIENT_NAME_SEED = "ClientName";
    public static final String FIRST_NAME_SEED = "FirstName";
    public static final String LAST_NAME_SEED = "LastName";

    public static final String API_ERROR = "Test validation error: selection criteria is required";
    private TimesheetApi mockApi;
    private TimesheetApiFactory mockApiFactory;


    /**
     * 
     */
    public TimesheetQueryMessageHandlerTest() {
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
    public void testSuccess_Fetch() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetQueryRequest.xml");
        List<TimesheetDto> mockListData = TimesheetMockData.createMockExtTimesheetList();

        try {
            when(this.mockApi.getExt(isA(TimesheetDto.class))).thenReturn(mockListData);
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for fetching timesheet records");
        }
        
        MessageHandlerResults results = null;
        TimesheetQueryApiHandler handler = new TimesheetQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getTimesheet().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTimesheet().size(); ndx++) {
            TimesheetType a = actualRepsonse.getProfile().getTimesheet().get(ndx);
            Assert.assertNotNull(a.getTimesheetId());
            Assert.assertEquals(TIMESHEET_ID_SEED + ndx, a.getTimesheetId().intValue());

            Assert.assertNotNull(a.getClient());
            Assert.assertNotNull(a.getClient().getClientId());
            Assert.assertEquals(CLIENT_ID, a.getClient().getClientId().intValue());
            Assert.assertNotNull(a.getClient().getCustomer());
            Assert.assertEquals(ACCT_NO, a.getClient().getCustomer().getAccountNo());
            Assert.assertEquals(CLIENT_NAME_SEED + CLIENT_ID, a.getClient().getName());

            Assert.assertNotNull(a.getProjId());
            Assert.assertEquals(PROJ_ID, a.getProjId().intValue());

            Assert.assertNotNull(a.getEmployee());
            Assert.assertNotNull(a.getEmployee().getEmployeeId());
            Assert.assertEquals(EMP_ID, a.getEmployee().getEmployeeId().intValue());

            Assert.assertNotNull(a.getEmployee().getContactDetails());
            Assert.assertEquals(FIRST_NAME_SEED + (TIMESHEET_ID_SEED + ndx), a.getEmployee().getContactDetails().getFirstName());
            Assert.assertEquals(LAST_NAME_SEED + (TIMESHEET_ID_SEED + ndx), a.getEmployee().getContactDetails().getLastName());

            Assert.assertNotNull(a.getEmployee().getManagerId());
            Assert.assertEquals(MGR_ID, a.getEmployee().getManagerId().intValue());

            Assert.assertEquals(INVOICE_REF_NO_SEED + ndx, a.getInvoiceRefNo());

            Assert.assertNotNull(a.getPeriodBegin());
            Assert.assertEquals(RMT2Date.stringToDate(PERIOD_SEED + (ndx + 1)), a.getPeriodBegin().toGregorianCalendar()
                    .getTime());
            Assert.assertNotNull(a.getPeriodEnd());
            Assert.assertEquals(RMT2Date.stringToDate(PERIOD_SEED + (ndx + 1)), a.getPeriodEnd().toGregorianCalendar()
                    .getTime());

            Assert.assertEquals(EXT_REF_SEED + ndx, a.getExternalRefNo());

            Assert.assertNotNull(a.getStatus());
            Assert.assertEquals(STATUS_NAME, a.getStatus().getName());
            Assert.assertEquals(STATUS_NAME + "Description", a.getStatus().getDescription());
            Assert.assertNotNull(a.getStatusHistoryId());
            Assert.assertEquals(STATUS_HIST_ID, a.getStatusHistoryId().intValue());

            Assert.assertNotNull(a.getStatus().getTimesheetStatusId());
            Assert.assertEquals(TimesheetConst.STATUS_DRAFT, a.getStatus().getTimesheetStatusId().intValue());
            Assert.assertEquals(RMT2Date.stringToDate(PERIOD_SEED + (ndx + 1)), a.getStatusEffectiveDate().toGregorianCalendar()
                    .getTime());
            Assert.assertNotNull(a.getPeriodEnd());
            Assert.assertEquals(RMT2Date.stringToDate(PERIOD_SEED + (ndx + 1)), a.getStatusEndDate().toGregorianCalendar()
                    .getTime());

            Assert.assertEquals(COMMENTS_SEED + (TIMESHEET_ID_SEED + ndx), a.getComments());

            Assert.assertNotNull(a.getDocumentId());
            Assert.assertEquals(TIMESHEET_ID_SEED + ndx, a.getDocumentId().intValue());

            Assert.assertEquals(RMT2String.padInt(TIMESHEET_ID_SEED + ndx, 10, RMT2String.PAD_LEADING), a.getDisplayValue());

            Assert.assertNotNull(a.getBillableHours());
            Assert.assertEquals(BILLABLE_HOURS, a.getBillableHours().doubleValue(), 0);
            Assert.assertNotNull(a.getNonBillableHours());
            Assert.assertEquals(NONBILLABLE_HOURS, a.getNonBillableHours().doubleValue(), 0);

            Assert.assertNotNull(a.getHourlyRate());
            Assert.assertEquals(HOURLY_PAY_RATE, a.getHourlyRate().doubleValue(), 0);
            Assert.assertNotNull(a.getOvertimeHourlyRate());
            Assert.assertEquals(HOURLY_OVERTIME_PAY_RATE, a.getOvertimeHourlyRate().doubleValue(), 0);
        }
    }
    
    @Test
    public void test_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetQueryRequest.xml");

        try {
            when(this.mockApi.getExt(isA(TimesheetDto.class))).thenReturn(null);
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for fetching timesheet records");
        }
        
        MessageHandlerResults results = null;
        TimesheetQueryApiHandler handler = new TimesheetQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        
        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetQueryRequest.xml");
        try {
            when(this.mockApi.getExt(isA(TimesheetDto.class))).thenThrow(new TimesheetApiException(API_ERROR));
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for fetching employees with an API Error");
        }
        
        MessageHandlerResults results = null;
        TimesheetQueryApiHandler handler = new TimesheetQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_GET, request);
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
        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_FETCH_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }

}

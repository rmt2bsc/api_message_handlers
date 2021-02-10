package org.rmt2.api.handler.timesheet;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.timesheet.invoice.InvoiceTimesheetApi;
import org.modules.timesheet.invoice.InvoiceTimesheetApiException;
import org.modules.timesheet.invoice.InvoiceTimesheetApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseProjectTrackerMessageHandlerTest;
import org.rmt2.api.handlers.timesheet.TimesheetInvoiceSingleApiHandler;
import org.rmt2.api.handlers.timesheet.TimesheetMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.TimesheetType;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
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
@PrepareForTest({ Rmt2OrmClientFactory.class, TimesheetInvoiceSingleApiHandler.class, InvoiceTimesheetApiFactory.class,
        SystemConfigurator.class, RMT2Date.class })
public class TimesheetInvoiceMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    private static final int TEST_TIMESHEET_NOT_FOUND_RC = 0;
    private static final int TEST_TIMESHEET_ID = 625;
    private static final int TEST_INVOICE_ID = 1234567890;
    private static final String TEST_INOVICE_REF_NO = "1234567890";
    public static final String API_ERROR = "Test Error: Timesheet Invoice API invoicing error occurred";

    private InvoiceTimesheetApi mockApi;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();

        mockApi = Mockito.mock(InvoiceTimesheetApi.class);
        PowerMockito.mockStatic(InvoiceTimesheetApiFactory.class);
        when(InvoiceTimesheetApiFactory.createApi(isA(String.class))).thenReturn(mockApi);
        doNothing().when(this.mockApi).close();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        return;
    }


    @Test
    public void testSuccess_Invoice() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetInvoiceRequest.xml");
        
        try {
            when(this.mockApi.invoice(isA(Integer.class))).thenReturn(TimesheetInvoiceMessageHandlerTest.TEST_INVOICE_ID);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for invoice timesheet method");
        }

        TimesheetInvoiceSingleApiHandler handler = new TimesheetInvoiceSingleApiHandler();
        MessageHandlerResults results = null;
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_INVOICE, request);
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

        Assert.assertEquals(TimesheetMessageHandlerConst.MESSAGE_INVOICE_SUCCESS, actualRepsonse.getReplyStatus().getMessage());

        for (TimesheetType ts : actualRepsonse.getProfile().getTimesheet()) {
            Assert.assertNotNull(ts.getTimesheetId());
            Assert.assertEquals(TEST_TIMESHEET_ID, ts.getTimesheetId().intValue());
            Assert.assertNotNull(ts.getInvoiceRefNo());
            Assert.assertEquals(TEST_INOVICE_REF_NO, ts.getInvoiceRefNo());
        }
    }

    @Test
    public void testSuccess_Invoice_Record_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetInvoiceRequest.xml");

        try {
            when(this.mockApi.invoice(isA(Integer.class))).thenReturn(TEST_TIMESHEET_NOT_FOUND_RC);
        } catch (Exception e) {
            Assert.fail("Failed to setup stub for invoice timesheet method");
        }

        TimesheetInvoiceSingleApiHandler handler = new TimesheetInvoiceSingleApiHandler();
        MessageHandlerResults results = null;
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_INVOICE, request);
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
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String errMsg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_INVOICE_RECORD_NOT_FOUND,
                String.valueOf(TEST_TIMESHEET_ID), "%s");
        Assert.assertEquals(errMsg, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/TimesheetInvoiceRequest.xml");

        try {
            when(this.mockApi.invoice(isA(Integer.class))).thenThrow(new InvoiceTimesheetApiException(API_ERROR));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Failed to setup stub for invoicing timesheet method");
        }

        TimesheetInvoiceSingleApiHandler handler = new TimesheetInvoiceSingleApiHandler();
        MessageHandlerResults results = null;
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TIMESHEET_INVOICE, request);
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

        String msg = RMT2String.replace(TimesheetMessageHandlerConst.MESSAGE_INVOICE_ERROR,
                String.valueOf(TEST_TIMESHEET_ID), "%s");
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }
    
}
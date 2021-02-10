package org.rmt2.api.handler.timesheet;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handler.BaseProjectTrackerMessageHandlerTest;
import org.rmt2.api.handlers.timesheet.TimesheetAttachDocumentApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.MediaApplicationLinkResponse;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.DatabaseException;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2File;
import com.api.util.RMT2String;

/**
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class,
        TimesheetAttachDocumentApiHandler.class, TimesheetApiFactory.class, SystemConfigurator.class })
public class TimesheetLinkMediaContentMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
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
    public TimesheetLinkMediaContentMessageHandlerTest() {
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
    public void test_Success() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/MediaApplicationLinkRequest.xml");

        try {
            when(this.mockApi.get(isA(Integer.class))).thenReturn(TimesheetMockData.createMockExtTimesheetList().get(0));
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for reversing a transaction");
        }
        try {
            when(this.mockApi.updateTimesheet(isA(TimesheetDto.class))).thenReturn(1);
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for reversing a transaction");
        }
        
        MessageHandlerResults results = null;
        TimesheetAttachDocumentApiHandler handler = new TimesheetAttachDocumentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_APP_LINK, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MediaApplicationLinkResponse actualRepsonse =
                (MediaApplicationLinkResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData().getAttachment());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        String msg = RMT2String.replace(TimesheetAttachDocumentApiHandler.MSG_UPDATE_SUCCESS,
                String.valueOf(actualRepsonse.getProfile().getMediaLinkData().getAttachment().getContentId()),
                ApiMessageHandlerConst.MSG_PLACEHOLDER1);
        msg = RMT2String.replace(msg,
                String.valueOf(actualRepsonse.getProfile().getMediaLinkData().getAttachment().getPropertyId()),
                ApiMessageHandlerConst.MSG_PLACEHOLDER2);
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testSuccess_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/MediaApplicationLinkRequest.xml");

        try {
            when(this.mockApi.get(isA(Integer.class))).thenReturn(null);
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for fetcing a transaction");
        }

        MessageHandlerResults results = null;
        TimesheetAttachDocumentApiHandler handler = new TimesheetAttachDocumentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_APP_LINK, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MediaApplicationLinkResponse actualRepsonse =
                (MediaApplicationLinkResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData().getAttachment());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(TimesheetAttachDocumentApiHandler.MSG_UPDATE_NOTFOUND, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testSuccess_No_Rows_Effected() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/MediaApplicationLinkRequest.xml");

        try {
            when(this.mockApi.get(isA(Integer.class))).thenReturn(TimesheetMockData.createMockExtTimesheetList().get(0));
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for reversing a transaction");
        }
        try {
            when(this.mockApi.updateTimesheet(isA(TimesheetDto.class))).thenReturn(0);
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for reversing a transaction");
        }

        MessageHandlerResults results = null;
        TimesheetAttachDocumentApiHandler handler = new TimesheetAttachDocumentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_APP_LINK, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MediaApplicationLinkResponse actualRepsonse =
                (MediaApplicationLinkResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData().getAttachment());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(TimesheetAttachDocumentApiHandler.MSG_UPDATE_NO_ROWS_EFFECTED, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/timesheet/MediaApplicationLinkRequest.xml");

        try {
            when(this.mockApi.get(isA(Integer.class))).thenThrow(new DatabaseException("Test API Error occurred"));
        } catch (TimesheetApiException e) {
            Assert.fail("Unable to setup mock stub for reversing a transaction");
        }

        MessageHandlerResults results = null;
        TimesheetAttachDocumentApiHandler handler = new TimesheetAttachDocumentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_APP_LINK, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MediaApplicationLinkResponse actualRepsonse =
                (MediaApplicationLinkResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaLinkData().getAttachment());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(TimesheetAttachDocumentApiHandler.MSG_API_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    
    @Test
    public void testValidation_Missing_Profile() {
        String request = RMT2File
                .getFileContentsAsString("xml/timesheet/MediaApplicationLinkRequest_MissingProfile.xml");

        MessageHandlerResults results = null;
        TimesheetAttachDocumentApiHandler handler = new TimesheetAttachDocumentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_APP_LINK, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MediaApplicationLinkResponse actualRepsonse =
                (MediaApplicationLinkResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals(TimesheetAttachDocumentApiHandler.MSG_MISSING_PROFILE_DATA, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Missing_MediaLinkData_Section() {
        String request = RMT2File
                .getFileContentsAsString("xml/timesheet/MediaApplicationLinkRequest_MissingProfileMediaLinkData.xml");

        MessageHandlerResults results = null;
        TimesheetAttachDocumentApiHandler handler = new TimesheetAttachDocumentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_APP_LINK, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MediaApplicationLinkResponse actualRepsonse =
                (MediaApplicationLinkResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals(TimesheetAttachDocumentApiHandler.MSG_MISSING_PROFILE_DATA, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Missing_Attachment_Section() {
        String request = RMT2File
                .getFileContentsAsString("xml/timesheet/MediaApplicationLinkRequest_MissingAttachment.xml");

        MessageHandlerResults results = null;
        TimesheetAttachDocumentApiHandler handler = new TimesheetAttachDocumentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_APP_LINK, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MediaApplicationLinkResponse actualRepsonse =
                (MediaApplicationLinkResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals(TimesheetAttachDocumentApiHandler.MSG_MISSING_ATTACHMENT_SECTION, actualRepsonse.getReplyStatus()
                .getMessage());
    }
}

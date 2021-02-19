package org.rmt2.api.handler.admin;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.dto.ApplicationDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.application.AppApi;
import org.modules.application.AppApiException;
import org.modules.application.AppApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAuthenticationMessageHandlerTest;
import org.rmt2.api.handler.SecurityMockOrmDataFactory;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.application.ApplicationMessageHandlerConst;
import org.rmt2.api.handlers.admin.application.ApplicationUpdateApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ApplicationType;
import org.rmt2.jaxb.AuthenticationResponse;

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
        ApplicationUpdateApiHandler.class, AppApiFactory.class, SystemConfigurator.class })
public class ApplicationUpdateMessageHandlerTest extends BaseAuthenticationMessageHandlerTest {
    public static final String API_ERROR = "API ERROR: Test failed";
    private AppApi mockApi;


    /**
     * 
     */
    public ApplicationUpdateMessageHandlerTest() {
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
        mockApi = Mockito.mock(AppApi.class);
        PowerMockito.mockStatic(AppApiFactory.class);
        when(AppApiFactory.createApi()).thenReturn(mockApi);
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
        String request = RMT2File.getFileContentsAsString("xml/admin/application/ApplicationInsertRequest.xml");
        try {
            when(this.mockApi.update(isA(ApplicationDto.class))).thenReturn(SecurityMockOrmDataFactory.TEST_NEW_APP_ID);
        } catch (AppApiException e) {
            Assert.fail("Unable to setup mock stub for inserting application record");
        }
        
        MessageHandlerResults results = null;
        ApplicationUpdateApiHandler handler = new ApplicationUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_APPLICATION_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse =
                (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getApplicationInfo());
        Assert.assertEquals(1, actualRepsonse.getProfile().getApplicationInfo().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ApplicationMessageHandlerConst.MESSAGE_CREATE_SUCCESS, actualRepsonse.getReplyStatus()
                .getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getApplicationInfo().size(); ndx++) {
            ApplicationType a = actualRepsonse.getProfile().getApplicationInfo().get(ndx);
            Assert.assertEquals(SecurityMockOrmDataFactory.TEST_NEW_APP_ID, a.getAppId());
        }
    }
    
    @Test
    public void testSuccess_Update() {
        String request = RMT2File.getFileContentsAsString("xml/admin/application/ApplicationUpdateRequest.xml");
        try {
            when(this.mockApi.update(isA(ApplicationDto.class))).thenReturn(1);
        } catch (AppApiException e) {
            Assert.fail("Unable to setup mock stub for inserting application record");
        }
        
        MessageHandlerResults results = null;
        ApplicationUpdateApiHandler handler = new ApplicationUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_APPLICATION_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse =
                (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getApplicationInfo());
        Assert.assertEquals(1, actualRepsonse.getProfile().getApplicationInfo().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ApplicationMessageHandlerConst.MESSAGE_UPDATE_SUCCESS, actualRepsonse.getReplyStatus()
                .getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getApplicationInfo().size(); ndx++) {
            ApplicationType a = actualRepsonse.getProfile().getApplicationInfo().get(ndx);
            Assert.assertEquals(SecurityMockOrmDataFactory.TEST_EXISTING_APP_ID, a.getAppId());
        }
    }


    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/admin/application/ApplicationInsertRequest.xml");
        try {
            when(this.mockApi.update(isA(ApplicationDto.class))).thenThrow(new AppApiException(API_ERROR));
        } catch (AppApiException e) {
            Assert.fail("Unable to setup mock stub for inserting application record");
        }
        
        MessageHandlerResults results = null;
        ApplicationUpdateApiHandler handler = new ApplicationUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_APPLICATION_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse =
                (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ApplicationMessageHandlerConst.MESSAGE_UPDATE_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());

    }
    
    @Test
    public void testValidation_Invalid_Transaction_Code() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/application/ApplicationUpdateRequest_InvalidTransactionCode.xml");

        MessageHandlerResults results = null;
        ApplicationUpdateApiHandler handler = new ApplicationUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_APPLICATION_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse =
                (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Missing_Profile() {
        String request = RMT2File.getFileContentsAsString("xml/admin/application/ApplicationUpdateRequest_MissingProfile.xml");

        MessageHandlerResults results = null;
        ApplicationUpdateApiHandler handler = new ApplicationUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_APPLICATION_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse =
                (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(AuthenticationMessageHandlerConst.MSG_MISSING_PROFILE_SECTION, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Too_Many_App_Records() {
        String request = RMT2File.getFileContentsAsString("xml/admin/application/ApplicationUpdateRequest_TooManyRecords.xml");

        MessageHandlerResults results = null;
        ApplicationUpdateApiHandler handler = new ApplicationUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_APPLICATION_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse =
                (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(AuthenticationMessageHandlerConst.MSG_TOO_MANY_RECORDS, actualRepsonse.getReplyStatus()
                .getMessage());
    }

}

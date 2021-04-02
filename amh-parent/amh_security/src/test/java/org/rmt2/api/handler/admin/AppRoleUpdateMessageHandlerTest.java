package org.rmt2.api.handler.admin;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.dto.CategoryDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.SecurityModuleException;
import org.modules.roles.AppRoleApi;
import org.modules.roles.RoleSecurityApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAuthenticationMessageHandlerTest;
import org.rmt2.api.handler.SecurityMockOrmDataFactory;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.approle.AppRoleMessageHandlerConst;
import org.rmt2.api.handlers.admin.approle.AppRoleUpdateApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AppRoleType;
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
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, RoleSecurityApiFactory.class, SystemConfigurator.class })
public class AppRoleUpdateMessageHandlerTest extends BaseAuthenticationMessageHandlerTest {
    public static final String API_ERROR = "API ERROR: Test failed";
    private AppRoleApi mockApi;


    /**
     * 
     */
    public AppRoleUpdateMessageHandlerTest() {
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
        mockApi = Mockito.mock(AppRoleApi.class);
        PowerMockito.mockStatic(RoleSecurityApiFactory.class);
        when(RoleSecurityApiFactory.createAppRoleApi()).thenReturn(mockApi);
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
        String request = RMT2File.getFileContentsAsString("xml/admin/approle/AppRoleInsertRequest.xml");
        try {
            when(this.mockApi.update(isA(CategoryDto.class))).thenReturn(SecurityMockOrmDataFactory.TEST_APP_ROLE_ID);
        } catch (SecurityModuleException e) {
            Assert.fail("Unable to setup mock stub for inserting user group record");
        }
        MessageHandlerResults results = null;
        AppRoleUpdateApiHandler handler = new AppRoleUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_APP_ROLE_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse =
                (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getAppRoleInfo().get(0));
        Assert.assertEquals(1, actualRepsonse.getProfile().getAppRoleInfo().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(AppRoleMessageHandlerConst.MESSAGE_CREATE_SUCCESS, actualRepsonse.getReplyStatus()
                .getMessage());

        for (int ndx = 0; ndx < actualRepsonse.getProfile().getAppRoleInfo().size(); ndx++) {
            AppRoleType a = actualRepsonse.getProfile().getAppRoleInfo().get(ndx);
            Assert.assertEquals(SecurityMockOrmDataFactory.TEST_APP_ROLE_ID, a.getAppRoleId(), 0);
        }
    }
    
    @Test
    public void testSuccess_Update() {
        String request = RMT2File.getFileContentsAsString("xml/admin/approle/AppRoleUpdateRequest.xml");
        try {
            when(this.mockApi.update(isA(CategoryDto.class))).thenReturn(1);
        } catch (SecurityModuleException e) {
            Assert.fail("Unable to setup mock stub for inserting user group record");
        }
        MessageHandlerResults results = null;
        AppRoleUpdateApiHandler handler = new AppRoleUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_APP_ROLE_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse =
                (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(AppRoleMessageHandlerConst.MESSAGE_UPDATE_SUCCESS, actualRepsonse.getReplyStatus()
                .getMessage());
    }


    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/admin/approle/AppRoleUpdateRequest.xml");
        try {
            when(this.mockApi.update(isA(CategoryDto.class))).thenThrow(new SecurityModuleException(API_ERROR));
        } catch (SecurityModuleException e) {
            Assert.fail("Unable to setup mock stub for inserting application record");
        }

        MessageHandlerResults results = null;
        AppRoleUpdateApiHandler handler = new AppRoleUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_APP_ROLE_UPDATE, request);
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
        Assert.assertEquals(AppRoleMessageHandlerConst.MESSAGE_UPDATE_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());

    }

    @Test
    public void testValidation_Invalid_Transaction_Code() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/approle/AppRoleUpdateRequest_InvalidTransactionCode.xml");

        MessageHandlerResults results = null;
        AppRoleUpdateApiHandler handler = new AppRoleUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_APP_ROLE_UPDATE, request);
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
        String request = RMT2File.getFileContentsAsString("xml/admin/approle/ AppRoleUpdateRequest_MissingProfile.xml");

        MessageHandlerResults results = null;
        AppRoleUpdateApiHandler handler = new AppRoleUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_APP_ROLE_UPDATE, request);
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
    public void testValidation_Missing_AppRole_Section() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/approle/AppRoleUpdateRequest_MisingAppRoleSection.xml");

        MessageHandlerResults results = null;
        AppRoleUpdateApiHandler handler = new AppRoleUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_APP_ROLE_UPDATE, request);
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
        Assert.assertEquals(AppRoleMessageHandlerConst.MESSAGE_MISSING_APPROLE_SECTION, actualRepsonse
                .getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Too_Many_Records() {
        String request = RMT2File.getFileContentsAsString("xml/admin/approle/AppRoleUpdateRequest_TooManyRecords.xml");

        MessageHandlerResults results = null;
        AppRoleUpdateApiHandler handler = new AppRoleUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_APP_ROLE_UPDATE, request);
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

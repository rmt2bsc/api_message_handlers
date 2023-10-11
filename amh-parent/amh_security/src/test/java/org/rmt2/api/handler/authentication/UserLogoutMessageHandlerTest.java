package org.rmt2.api.handler.authentication;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.authentication.Authenticator;
import org.modules.authentication.AuthenticatorFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handler.BaseAuthenticationMessageHandlerTest;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.auth.UserAuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.auth.UserLogoutApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AuthenticationResponse;
import org.rmt2.jaxb.UserType;

import com.InvalidDataException;
import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.security.authentication.web.LogoutException;
import com.api.util.RMT2File;
import com.api.util.RMT2String;

/**
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, AuthenticatorFactory.class, 
    SystemConfigurator.class })
public class UserLogoutMessageHandlerTest extends BaseAuthenticationMessageHandlerTest {
    public static final String LOGOUT_ERROR = "Test invalid user credentials";
    public static final String LOGOUT_VALIDATION_ERROR = "Test validation error";
    public static final int TOTAL_LOGONS = 0;
    public static final int APPS_LOGGED_IN_COUNT = 0;
    private Authenticator mockApi;
    

    /**
     * 
     */
    public UserLogoutMessageHandlerTest() {
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
        mockApi = Mockito.mock(Authenticator.class);
        PowerMockito.mockStatic(AuthenticatorFactory.class);
        when(AuthenticatorFactory.createApi()).thenReturn(mockApi);
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
        String request = RMT2File.getFileContentsAsString("xml/authentication/UserLogoutRequest.xml");
        try {
            when(this.mockApi.logout(isA(String.class))).thenReturn(APPS_LOGGED_IN_COUNT);
        } catch (LogoutException e) {
            Assert.fail("Unable to setup mock stub for logging out the user");
        }
        MessageHandlerResults results = null;
        UserLogoutApiHandler handler = new UserLogoutApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_LOGOUT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse = (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(actualRepsonse.getProfile().getUserInfo().size(), 1);
        UserType u = actualRepsonse.getProfile().getUserInfo().get(0);

        // Assert success message
        Assert.assertEquals(actualRepsonse.getReplyStatus().getRecordCount().intValue(), APPS_LOGGED_IN_COUNT, 0);
        String successMsg = RMT2String.replace(UserAuthenticationMessageHandlerConst.MESSAGE_LOGOUT_SUCCESS,
                u.getUserName(), ApiMessageHandlerConst.MSG_PLACEHOLDER1);
        successMsg = RMT2String.replace(successMsg, String.valueOf(actualRepsonse.getReplyStatus().getRecordCount().intValue()),
                ApiMessageHandlerConst.MSG_PLACEHOLDER2);
        Assert.assertEquals(successMsg, actualRepsonse.getReplyStatus().getMessage());

        Assert.assertEquals(0, u.getLoginId());
        Assert.assertEquals(u.getUserName(), "test_username");
        Assert.assertEquals(TOTAL_LOGONS, u.getTotalLogons(), 0);
    }

    @Test
    public void test_Invalid_Username() {
        String request = RMT2File.getFileContentsAsString("xml/authentication/UserLogoutRequest.xml");

        try {
            when(this.mockApi.logout(isA(String.class))).thenThrow(new LogoutException(LOGOUT_ERROR));
        } catch (LogoutException e) {
            Assert.fail("Unable to setup mock stub for logging out user with invalid username");
        }
        MessageHandlerResults results = null;
        UserLogoutApiHandler handler = new UserLogoutApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_LOGOUT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse = (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(UserAuthenticationMessageHandlerConst.MESSAGE_LOGOUT_FAILED, actualRepsonse.getReplyStatus()
                .getMessage());
        Assert.assertEquals(LOGOUT_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
        
        Assert.assertEquals(actualRepsonse.getProfile().getUserInfo().size(), 1);
        Assert.assertEquals(actualRepsonse.getProfile().getUserInfo().get(0).getUserName(), "test_username");
    }

    @Test
    public void testValidation_Invalid_Transaction_Code() {
        String request = RMT2File.getFileContentsAsString("xml/authentication/UserLogoutRequest_InvalidTransactionCode.xml");

        MessageHandlerResults results = null;
        UserLogoutApiHandler handler = new UserLogoutApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_LOGOUT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse = (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Profile() {
        String request = RMT2File.getFileContentsAsString("xml/authentication/UserLogoutRequest_MissingProfileSection.xml");

        MessageHandlerResults results = null;
        UserLogoutApiHandler handler = new UserLogoutApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_LOGIN, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse = (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(AuthenticationMessageHandlerConst.MSG_MISSING_PROFILE_SECTION, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Profile_UserApplicationRole_Section() {
        String request = RMT2File
                .getFileContentsAsString("xml/authentication/UserLogoutRequest_MissingApplicationAccessSection.xml");

        MessageHandlerResults results = null;
        UserLogoutApiHandler handler = new UserLogoutApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_LOGOUT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse = (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(UserAuthenticationMessageHandlerConst.MESSAGE_INVALID_APPLICATION_ACCESS_INFO, actualRepsonse
                .getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Profile_UserInfo_Section() {
        String request = RMT2File
                .getFileContentsAsString("xml/authentication/UserLogoutRequest_MissingUserInfoSection.xml");

        MessageHandlerResults results = null;
        UserLogoutApiHandler handler = new UserLogoutApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_LOGOUT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse = (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(UserAuthenticationMessageHandlerConst.MESSAGE_INVALID_USERINFO, actualRepsonse
                .getReplyStatus().getMessage());
    }


    @Test
    public void testValidation_Missing_Username() {
        String request = RMT2File
                .getFileContentsAsString("xml/authentication/UserLogoutRequest_MissingUserName.xml");

        try {
            when(this.mockApi.logout(isA(String.class))).thenThrow(new InvalidDataException(LOGOUT_VALIDATION_ERROR));
        } catch (LogoutException e) {
            Assert.fail("Unable to setup mock stub for logging out user with missing username");
        }

        MessageHandlerResults results = null;
        UserLogoutApiHandler handler = new UserLogoutApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_LOGOUT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse = (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(UserAuthenticationMessageHandlerConst.MESSAGE_AUTH_API_VALIDATION_ERROR, actualRepsonse
                .getReplyStatus().getMessage());
        Assert.assertEquals(LOGOUT_VALIDATION_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }
}

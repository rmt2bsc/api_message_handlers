package org.rmt2.api.handler.authentication;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.CategoryDto;
import org.dto.UserDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.SecurityModuleException;
import org.modules.authentication.Authenticator;
import org.modules.authentication.AuthenticatorFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAuthenticationMessageHandlerTest;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.user.permissions.UserAppRoleMaintenanceApiHandler;
import org.rmt2.api.handlers.admin.user.permissions.UserAppRoleMessageHandlerConst;
import org.rmt2.api.handlers.auth.UserAuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.auth.UserLoginApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AuthenticationResponse;
import org.rmt2.jaxb.UserAppRoleType;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.security.User;
import com.api.util.RMT2File;
import com.api.web.security.RMT2SecurityToken;

/**
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, AuthenticatorFactory.class, 
    SystemConfigurator.class })
public class UserLoginMessageHandlerTest extends BaseAuthenticationMessageHandlerTest {
    public static final String API_ERROR = "API ERROR: Test failed";
    private Authenticator mockApi;
    private RMT2SecurityToken mockSecurityToken;
    

    /**
     * 
     */
    public UserLoginMessageHandlerTest() {
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
        this.mockSecurityToken = this.setupSecurityToken();
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

    private RMT2SecurityToken setupSecurityToken() {
        UserDto user = Rmt2OrmDtoFactory.getNewUserInstance();
        user.setLoginUid(7777);
        user.setUsername("test_username");
        user.setTotalLogons(5);
        user.setActive(1);
        user.setLoggedIn(1);
        user.setFirstname("roy");
        user.setLastname("terrell");
        RMT2SecurityToken token = new RMT2SecurityToken();
        User tokenUser = Rmt2OrmDtoFactory.getUserInstance(user);
        tokenUser.addRole("Role1");
        tokenUser.addRole("Role2");
        tokenUser.addRole("Role3");
        tokenUser.addRole("Role4");
        token.update(tokenUser);
        return token;
    }
    
    @Test
    public void test_Success() {
        String request = RMT2File.getFileContentsAsString("xml/authentication/UserLoginRequest.xml");
        
        
        try {
            when(this.mockApi.authenticate(isA(String.class), isA(String.class))).thenReturn(this.mockSecurityToken);
        } catch (SecurityModuleException e) {
            Assert.fail("Unable to setup mock stub for authenticating the user");
        }
        MessageHandlerResults results = null;
        UserLoginApiHandler handler = new UserLoginApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_LOGIN, request);
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
        Assert.assertEquals(UserAuthenticationMessageHandlerConst.MESSAGE_AUTH_SUCCESS, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(actualRepsonse.getProfile().getUserInfo().size(), 1);
        Assert.assertEquals(actualRepsonse.getProfile().getUserInfo().get(0).getLoginId(), 7777, 0);
        Assert.assertNotNull(actualRepsonse.getProfile().getUserInfo().get(0).getGrantedAppRoles());
        Assert.assertEquals(actualRepsonse.getProfile().getUserInfo().get(0).getGrantedAppRoles().getUserAppRole().size(), 4, 0);
        int ndx = 1;
        for (UserAppRoleType uart : actualRepsonse.getProfile().getUserInfo().get(0).getGrantedAppRoles().getUserAppRole()) {
            Assert.assertNotNull(uart.getAppRoleInfo());
            Assert.assertNotNull(uart.getAppRoleInfo().getAppRoleCode());
            Assert.assertEquals("Role" + ndx, uart.getAppRoleInfo().getAppRoleCode());
            ndx++;
        }
    }

    @Test
    public void testSuccess_AppRolesMissingFromRequest() {
        String request = RMT2File.getFileContentsAsString("xml/admin/user/approle/UserAppRoleMaintenanceRequest_AppRolesExcluded.xml");
        try {
            when(this.mockApi.update(isA(CategoryDto.class), isA(List.class))).thenReturn(0);
        } catch (SecurityModuleException e) {
            Assert.fail("Unable to setup mock stub for assigning/unassigning user application roles");
        }
        MessageHandlerResults results = null;
        UserAppRoleMaintenanceApiHandler handler = new UserAppRoleMaintenanceApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_APPROLE_MAINT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse = (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(UserAppRoleMessageHandlerConst.MESSAGE_ZERO_APPROLES_PROCESSED, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/admin/user/approle/UserAppRoleMaintenanceRequest.xml");
        try {
            when(this.mockApi.update(isA(CategoryDto.class), isA(List.class))).thenThrow(new SecurityModuleException(API_ERROR));
        } catch (SecurityModuleException e) {
            Assert.fail("Unable to setup mock stub for inserting application record");
        }

        MessageHandlerResults results = null;
        UserAppRoleMaintenanceApiHandler handler = new UserAppRoleMaintenanceApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_APPROLE_MAINT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse = (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(UserAppRoleMessageHandlerConst.MESSAGE_UPDATE_ERROR, actualRepsonse.getReplyStatus().getMessage());

    }

    @Test
    public void testValidation_Invalid_Transaction_Code() {
        String request = RMT2File.getFileContentsAsString("xml/admin/user/approle/UserAppRoleMaintenanceRequest_InvalidTransactionCode.xml");

        MessageHandlerResults results = null;
        UserAppRoleMaintenanceApiHandler handler = new UserAppRoleMaintenanceApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_APPROLE_MAINT, request);
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
        String request = RMT2File.getFileContentsAsString("xml/admin/user/approle/UserAppRoleMaintenanceRequest_ProfileSectionMissing.xml");

        MessageHandlerResults results = null;
        UserAppRoleMaintenanceApiHandler handler = new UserAppRoleMaintenanceApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_APPROLE_MAINT, request);
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
    public void testValidation_Missing_Profile_User_Section() {
        String request = RMT2File.getFileContentsAsString("xml/admin/user/approle/UserAppRoleMaintenanceRequest_ProfileUserSectionMissing.xml");

        MessageHandlerResults results = null;
        UserAppRoleMaintenanceApiHandler handler = new UserAppRoleMaintenanceApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_APPROLE_MAINT, request);
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
        Assert.assertEquals(UserAppRoleMessageHandlerConst.MESSAGE_MISSING_USER_SECTION, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Too_Many_Records() {
        String request = RMT2File.getFileContentsAsString("xml/admin/user/approle/UserAppRoleMaintenanceRequest_TooManyRecords.xml");

        MessageHandlerResults results = null;
        UserAppRoleMaintenanceApiHandler handler = new UserAppRoleMaintenanceApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_APPROLE_MAINT, request);
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
        Assert.assertEquals(AuthenticationMessageHandlerConst.MSG_TOO_MANY_RECORDS, actualRepsonse.getReplyStatus().getMessage());
    }

}

package org.rmt2.api.handler.admin;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
import org.modules.roles.RoleSecurityApiFactory;
import org.modules.roles.UserAppRoleApi;
import org.modules.users.UserApi;
import org.modules.users.UserApiException;
import org.modules.users.UserApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAuthenticationMessageHandlerTest;
import org.rmt2.api.handler.SecurityMockDtoData;
import org.rmt2.api.handler.SecurityMockOrmDataFactory;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.user.UserMessageHandlerConst;
import org.rmt2.api.handlers.admin.user.permissions.UserPermissionsQueryApiHandler;
import org.rmt2.api.handlers.auth.UserAuthenticationMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AuthenticationResponse;
import org.rmt2.jaxb.UserAppRoleType;
import org.rmt2.jaxb.UserType;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2Date;
import com.api.util.RMT2File;

/**
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, RoleSecurityApiFactory.class, UserApiFactory.class,
        Rmt2OrmDtoFactory.class,
        SystemConfigurator.class })
public class UserPermissionsQueryMessageHandlerTest extends BaseAuthenticationMessageHandlerTest {
    public static final String API_ERROR = "API ERROR: Test failed";
    private UserAppRoleApi mockUserAppRoleApi;
    private UserApi mockUserApi;


    /**
     * 
     */
    public UserPermissionsQueryMessageHandlerTest() {
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

        // Fetches user data
        this.mockUserApi = Mockito.mock(UserApi.class);
        PowerMockito.mockStatic(UserApiFactory.class);
        when(UserApiFactory.createApiInstance()).thenReturn(mockUserApi);
        doNothing().when(this.mockUserApi).close();

        // Fetches user app role data
        mockUserAppRoleApi = Mockito.mock(UserAppRoleApi.class);
        PowerMockito.mockStatic(RoleSecurityApiFactory.class);
        when(RoleSecurityApiFactory.createUserAppRoleApi()).thenReturn(mockUserAppRoleApi);
        doNothing().when(this.mockUserAppRoleApi).close();

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
        String request = RMT2File.getFileContentsAsString("xml/admin/user/approle/UserPermissionsQueryRequest.xml");

        try {
            when(this.mockUserApi.getUser(isA(UserDto.class))).thenReturn(SecurityMockDtoData.createVwUserMockData());
        } catch (UserApiException e) {
            Assert.fail("Unable to setup mock stub for fetching user record");
        }

        try {
            when(this.mockUserAppRoleApi.getAssignedRoles(isA(CategoryDto.class)))
                    .thenReturn(SecurityMockDtoData.createVwUserAppRolesMockData());
        } catch (SecurityModuleException e) {
            Assert.fail("Unable to setup mock stub for fetching user granted application role record");
        }

        try {
            when(this.mockUserAppRoleApi.getRevokedRoles(isA(CategoryDto.class)))
                    .thenReturn(SecurityMockDtoData.createVwUserAppRolesMockData());
        } catch (SecurityModuleException e) {
            Assert.fail("Unable to setup mock stub for fetching user revoked application role record");
        }
        
        MessageHandlerResults results = null;
        UserPermissionsQueryApiHandler handler = new UserPermissionsQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse =
                (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertEquals(5, actualRepsonse.getProfile().getUserInfo().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(UserMessageHandlerConst.MESSAGE_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getUserInfo().size(); ndx++) {
            UserType a = actualRepsonse.getProfile().getUserInfo().get(ndx);
            Assert.assertEquals(SecurityMockOrmDataFactory.TEST_USER_ID + ndx, a.getLoginId(), 0);
            Assert.assertEquals("UserName_" + (SecurityMockOrmDataFactory.TEST_USER_ID + ndx), a.getUserName());
            Assert.assertEquals("firstname_" + (SecurityMockOrmDataFactory.TEST_USER_ID + ndx), a.getFirstName());
            Assert.assertEquals("lastname_" + (SecurityMockOrmDataFactory.TEST_USER_ID + ndx), a.getLastName());
            Assert.assertEquals(RMT2Date.toXmlDate("2018-01-01").toGregorianCalendar(), a.getStartDate().toGregorianCalendar());
            Assert.assertEquals("111-11-1111", a.getSsn());
            Assert.assertEquals(SecurityMockOrmDataFactory.TEST_GROUP_ID, a.getGroupInfo().getGrpId(), 0);

            Assert.assertEquals(2, a.getGrantedAppRoles().getUserAppRole().size());
            for (int ndx2 = 0; ndx2 < a.getGrantedAppRoles().getUserAppRole().size(); ndx2++) {
                UserAppRoleType uart = a.getGrantedAppRoles().getUserAppRole().get(ndx2);
                Assert.assertEquals(SecurityMockOrmDataFactory.TEST_USER_ID + uart.getAppRoleInfo().getAppInfo().getAppId()
                        + uart.getAppRoleInfo().getRoleInfo().getRoleId(), uart.getUserAppRoleId(), 0);
            }

            Assert.assertEquals(2, a.getRevokedAppRoles().getUserAppRole().size());
            for (int ndx2 = 0; ndx2 < a.getRevokedAppRoles().getUserAppRole().size(); ndx2++) {
                UserAppRoleType uart = a.getRevokedAppRoles().getUserAppRole().get(ndx2);
                Assert.assertEquals(SecurityMockOrmDataFactory.TEST_USER_ID + uart.getAppRoleInfo().getAppInfo().getAppId()
                        + uart.getAppRoleInfo().getRoleInfo().getRoleId(), uart.getUserAppRoleId(), 0);
            }
        }
    }
    
    @Test
    public void testSuccess_Fetch_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/admin/user/approle/UserPermissionsQueryRequest.xml");

        try {
            when(this.mockUserApi.getUser(isA(UserDto.class))).thenReturn(null);
        } catch (UserApiException e) {
            Assert.fail("Unable to setup mock stub for fetching user record");
        }

        MessageHandlerResults results = null;
        UserPermissionsQueryApiHandler handler = new UserPermissionsQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse =
                (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(UserMessageHandlerConst.MESSAGE_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/admin/user/approle/UserPermissionsQueryRequest.xml");
        try {
            when(this.mockUserApi.getUser(isA(UserDto.class))).thenThrow(new UserApiException(API_ERROR));
        } catch (UserApiException e) {
            Assert.fail("Unable to setup mock stub for fetching user  record");
        }
        
        MessageHandlerResults results = null;
        UserPermissionsQueryApiHandler handler = new UserPermissionsQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET, request);
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
        Assert.assertEquals(UserMessageHandlerConst.MESSAGE_FETCH_ERROR, actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_Invalid_Transaction_Code() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/user/approle/UserPermissionsQueryRequest_InvalidTransactionCode.xml");

        MessageHandlerResults results = null;
        UserPermissionsQueryApiHandler handler = new UserPermissionsQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET, request);
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
    public void testValidation_Missing_Criteria() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/user/approle/UserPermissionsQueryRequest_MissingCriteria.xml");

        MessageHandlerResults results = null;
        UserPermissionsQueryApiHandler handler = new UserPermissionsQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET, request);
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
        Assert.assertEquals(AuthenticationMessageHandlerConst.MSG_MISSING_CRITERIA_SECTION, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Missing_Entity_Criteria() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/user/approle/UserPermissionsQueryRequest_MissingUserAppRoleCriteria.xml");

        MessageHandlerResults results = null;
        UserPermissionsQueryApiHandler handler = new UserPermissionsQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET, request);
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
        Assert.assertEquals(UserAuthenticationMessageHandlerConst.MESSAGE_MISSING_USER_APP_ROLE_SECTION, actualRepsonse
                .getReplyStatus().getMessage());
    }
}

package org.rmt2.api.handler.admin;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.SecurityModuleException;
import org.modules.roles.RoleSecurityApiFactory;
import org.modules.roles.UserAppRoleApi;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAuthenticationMessageHandlerTest;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.user.permissions.UserAppRoleDeleteApiHandler;
import org.rmt2.api.handlers.admin.user.permissions.UserAppRoleMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
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
public class UserAppRoleDeleteMessageHandlerTest extends BaseAuthenticationMessageHandlerTest {
    public static final String API_ERROR = "API ERROR: Test failed";
    private UserAppRoleApi mockApi;
    public static final int TOTAL_APPROLES_ASSIGNED = 1;


    /**
     * 
     */
    public UserAppRoleDeleteMessageHandlerTest() {
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
        mockApi = Mockito.mock(UserAppRoleApi.class);
        PowerMockito.mockStatic(RoleSecurityApiFactory.class);
        when(RoleSecurityApiFactory.createUserAppRoleApi()).thenReturn(mockApi);
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
    public void testSuccess_Delete() {
        String request = RMT2File.getFileContentsAsString("xml/admin/user/approle/UserAppRoleDeleteRequest.xml");
        try {
            when(this.mockApi.delete(isA(Integer.class))).thenReturn(TOTAL_APPROLES_ASSIGNED);
        } catch (SecurityModuleException e) {
            Assert.fail("Unable to setup mock stub for deleting a single user application roles");
        }
        MessageHandlerResults results = null;
        UserAppRoleDeleteApiHandler handler = new UserAppRoleDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_APPROLE_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse =
                (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(TOTAL_APPROLES_ASSIGNED, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(UserAppRoleMessageHandlerConst.MESSAGE_DELTED_SUCCESS, actualRepsonse.getReplyStatus()
                .getMessage());
    }
    
    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/admin/user/approle/UserAppRoleDeleteRequest.xml");
        try {
            when(this.mockApi.delete(isA(Integer.class))).thenThrow(new SecurityModuleException(API_ERROR));
        } catch (SecurityModuleException e) {
            Assert.fail("Unable to setup mock stub for deleting a user application role record");
        }

        MessageHandlerResults results = null;
        UserAppRoleDeleteApiHandler handler = new UserAppRoleDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_APPROLE_DELETE, request);
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
        Assert.assertEquals(UserAppRoleMessageHandlerConst.MESSAGE_DELETE_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Invalid_Transaction_Code() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/user/approle/UserAppRoleDeleteRequest_InvalidTransactionCode.xml");

        MessageHandlerResults results = null;
        UserAppRoleDeleteApiHandler handler = new UserAppRoleDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_APPROLE_DELETE, request);
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
        String request = RMT2File
                .getFileContentsAsString("xml/admin/user/approle/UserAppRoleDeleteRequest_MissingProfileSection.xml");

        MessageHandlerResults results = null;
        UserAppRoleDeleteApiHandler handler = new UserAppRoleDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_APPROLE_DELETE, request);
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
    public void testValidation_Missing_Profile_UserAppRole_Section() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/user/approle/UserAppRoleDeleteRequest_MissingUserAppRoleSection.xml");

        MessageHandlerResults results = null;
        UserAppRoleDeleteApiHandler handler = new UserAppRoleDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_APPROLE_DELETE, request);
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
        Assert.assertEquals(UserAppRoleMessageHandlerConst.MESSAGE_MISSING_USER_SECTION, actualRepsonse
                .getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Too_Many_Records() {
        String request = RMT2File.getFileContentsAsString("xml/admin/user/approle/UserAppRoleDeleteRequest_TooManyRecords.xml");

        MessageHandlerResults results = null;
        UserAppRoleDeleteApiHandler handler = new UserAppRoleDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_USER_APPROLE_DELETE, request);
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

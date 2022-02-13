package org.rmt2.api.handler.client;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.dto.ClientDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.admin.ProjectAdminApi;
import org.modules.admin.ProjectAdminApiException;
import org.modules.admin.ProjectAdminApiFactory;
import org.modules.contacts.ContactsApiFactory;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handler.BaseProjectTrackerMessageHandlerTest;
import org.rmt2.api.handlers.admin.client.ClientDeleteApiHandler;
import org.rmt2.api.handlers.admin.client.ClientMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileResponse;

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
        ClientDeleteApiHandler.class, ProjectAdminApiFactory.class, SubsidiaryApiFactory.class, ContactsApiFactory.class, SystemConfigurator.class })
public class ClientDeleteMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    public static final int RECORD_COUNT = 5;
    public static final int CLINET_ID_START = 1110;
    public static final int BUSINESS_ID_START = 1350;
    public static final String ACCOUNT_NO_SUFFIX = "-111";
    public static final String API_ERROR_MSG = "Test API error occurred";

    private ProjectAdminApi mockApi;

    /**
     * 
     */
    public ClientDeleteMessageHandlerTest() {
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
        PowerMockito.mockStatic(ProjectAdminApiFactory.class);
        mockApi = Mockito.mock(ProjectAdminApi.class);
        
        doNothing().when(this.mockApi).close();
        
        try {
            when(ProjectAdminApiFactory.createApi(isA(String.class))).thenReturn(mockApi);
        }
        catch (Exception e) {
            Assert.fail("Unable to setup mock stub for ProjectAdminApi");
        }
        
        try {
            when(this.mockApi.deleteClient(isA(ClientDto.class))).thenReturn(1);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for deleting project/client records");
        }
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
        String request = RMT2File.getFileContentsAsString("xml/admin/client/ClientDeleteRequest.xml");
        MessageHandlerResults results = null;
        ClientDeleteApiHandler handler = new ClientDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_CLIENT_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ClientMessageHandlerConst.MESSAGE_DELETE_SUCCESSFUL, actualRepsonse.getReplyStatus().getMessage());

    }
    
    @Test
    public void testRecordsNotFound_Delete() {
        String request = RMT2File.getFileContentsAsString("xml/admin/client/ClientDeleteRequest.xml");
        
        try {
            when(this.mockApi.deleteClient(isA(ClientDto.class))).thenReturn(0);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for deleting project/client records");
        }
        
        MessageHandlerResults results = null;
        ClientDeleteApiHandler handler = new ClientDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_CLIENT_DELETE, request);
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
        Assert.assertEquals(ClientMessageHandlerConst.MESSAGE_DELETE_RECORDS_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());

    }
   
    
    @Test
    public void testError_Delete_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/admin/client/ClientDeleteRequest.xml");
        try {
            when(this.mockApi.deleteClient(isA(ClientDto.class)))
                    .thenThrow(new ProjectAdminApiException(API_ERROR_MSG));
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for fetching employees with an API Error");
        }
        
        MessageHandlerResults results = null;
        ClientDeleteApiHandler handler = new ClientDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_CLIENT_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        ProjectProfileResponse actualRepsonse = (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(ClientMessageHandlerConst.MESSAGE_DELETE_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR_MSG, actualRepsonse.getReplyStatus()
                .getExtMessage());
    }
    
    
    @Test
    public void testError_MissingCriteria() {
        String request = RMT2File.getFileContentsAsString("xml/admin/client/ClientDeleteRequest_MissingCriteria.xml");
        MessageHandlerResults results = null;
        ClientDeleteApiHandler handler = new ClientDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_CLIENT_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        ProjectProfileResponse actualRepsonse = (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(ApiMessageHandlerConst.MSG_MISSING_CRITERIA_DATA, actualRepsonse.getReplyStatus().getMessage());
        
    }
    
    @Test
    public void testError_MissingCriteriaElements() {
        String request = RMT2File.getFileContentsAsString("xml/admin/client/ClientDeleteRequest_CriteriaElementsMissing.xml");
        MessageHandlerResults results = null;
        ClientDeleteApiHandler handler = new ClientDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_CLIENT_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        ProjectProfileResponse actualRepsonse = (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(ApiMessageHandlerConst.MSG_MISSING_CRITERIA_DATA, actualRepsonse.getReplyStatus().getMessage());
        
    }
  
}

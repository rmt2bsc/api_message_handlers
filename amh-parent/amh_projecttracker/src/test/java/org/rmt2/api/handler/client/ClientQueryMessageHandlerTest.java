package org.rmt2.api.handler.client;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.ProjectTrackerMockData;
import org.rmt2.api.handler.BaseProjectTrackerMessageHandlerTest;
import org.rmt2.api.handlers.admin.client.ClientMessageHandlerConst;
import org.rmt2.api.handlers.admin.client.ClientQueryApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ClientType;
import org.rmt2.jaxb.ProjectProfileResponse;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.AbstractDaoClientImpl;
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
        ClientQueryApiHandler.class, ProjectAdminApiFactory.class, SystemConfigurator.class })
public class ClientQueryMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    public static final int RECORD_COUNT = 5;
    public static final int CLINET_ID_START = 1110;
    public static final int BUSINESS_ID_START = 1350;
    public static final String ACCOUNT_NO_SUFFIX = "-111";
    public static final String API_ERROR_MSG = "Test API error occurred";

    private ProjectAdminApi mockApi;


    /**
     * 
     */
    public ClientQueryMessageHandlerTest() {
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
        mockApi = Mockito.mock(ProjectAdminApi.class);
        PowerMockito.mockStatic(ProjectAdminApiFactory.class);
        when(ProjectAdminApiFactory.createApi(isA(String.class))).thenReturn(mockApi);
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
        String request = RMT2File.getFileContentsAsString("xml/admin/client/ClientQueryRequest.xml");
        List<ClientDto> mockListData = ProjectTrackerMockData.createMockMultipleClient();

        try {
            when(this.mockApi.getClient(isA(ClientDto.class))).thenReturn(mockListData);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for fetching project/client records");
        }
        
        MessageHandlerResults results = null;
        ClientQueryApiHandler handler = new ClientQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_CLIENT_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getClient().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ClientMessageHandlerConst.MESSAGE_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getClient().size(); ndx++) {
            ClientType a = actualRepsonse.getProfile().getClient().get(ndx);
            Assert.assertNotNull(a.getClientId());
            Assert.assertEquals(CLINET_ID_START + ndx, a.getClientId().intValue());
            Assert.assertNotNull(a.getCustomer());
            Assert.assertNotNull(a.getCustomer().getBusinessContactDetails());
            Assert.assertNotNull(a.getCustomer().getBusinessContactDetails().getBusinessId());
            Assert.assertEquals(BUSINESS_ID_START + ndx, a.getCustomer().getBusinessContactDetails().getBusinessId().intValue());
            Assert.assertNotNull(a.getName());
            Assert.assertEquals((CLINET_ID_START + ndx) + " Company", a.getName());
            Assert.assertEquals(RMT2String.dupString(String.valueOf(ndx), 3) + ACCOUNT_NO_SUFFIX, a.getCustomer().getAccountNo());
            Assert.assertEquals("firstname" + ndx, a.getCustomer().getBusinessContactDetails().getContactFirstname());
            Assert.assertEquals("lastname" + ndx, a.getCustomer().getBusinessContactDetails().getContactLastname());
            Assert.assertEquals("firstname" + ndx + "lastname" + ndx + "@gte.net", a.getCustomer()
                    .getBusinessContactDetails().getContactEmail());
        }
    }
    
    @Test
    public void test_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/admin/client/ClientQueryRequest.xml");
        try {
            when(this.mockApi.getClient(isA(ClientDto.class))).thenReturn(null);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for fetching project/client records");
        }
        
        MessageHandlerResults results = null;
        ClientQueryApiHandler handler = new ClientQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_CLIENT_GET, request);
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
        Assert.assertEquals(ClientMessageHandlerConst.MESSAGE_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/admin/client/ClientQueryRequest.xml");
        try {
            when(this.mockApi.getClient(isA(ClientDto.class)))
                    .thenThrow(new ProjectAdminApiException(API_ERROR_MSG));
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for fetching employees with an API Error");
        }
        
        MessageHandlerResults results = null;
        ClientQueryApiHandler handler = new ClientQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_CLIENT_GET, request);
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
        Assert.assertEquals(ClientMessageHandlerConst.MESSAGE_FETCH_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR_MSG, actualRepsonse.getReplyStatus()
                .getExtMessage());
    }
    
}

package org.rmt2.api.handler.client;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.BusinessContactDto;
import org.dto.ClientDto;
import org.dto.ContactDto;
import org.dto.CustomerDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.admin.ProjectAdminApi;
import org.modules.admin.ProjectAdminApiException;
import org.modules.admin.ProjectAdminApiFactory;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiFactory;
import org.modules.subsidiary.CustomerApi;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.ProjectTrackerMockData;
import org.rmt2.api.handler.BaseProjectTrackerMessageHandlerTest;
import org.rmt2.api.handlers.admin.client.ClientImportApiHandler;
import org.rmt2.api.handlers.admin.client.ClientMessageHandlerConst;
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
        ClientImportApiHandler.class, ProjectAdminApiFactory.class, SubsidiaryApiFactory.class, ContactsApiFactory.class, SystemConfigurator.class })
public class ClientImportMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    public static final int RECORD_COUNT = 5;
    public static final int CLINET_ID_START = 1110;
    public static final int BUSINESS_ID_START = 1350;
    public static final String ACCOUNT_NO_SUFFIX = "-111";
    public static final String API_ERROR_MSG = "Test API error occurred";

    private ProjectAdminApi mockApi;
    private CustomerApi mockCustApi;
    private ContactsApi contactApi;

    /**
     * 
     */
    public ClientImportMessageHandlerTest() {
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
        PowerMockito.mockStatic(SubsidiaryApiFactory.class);
        PowerMockito.mockStatic(ContactsApiFactory.class);
        mockApi = Mockito.mock(ProjectAdminApi.class);
        mockCustApi = Mockito.mock(CustomerApi.class);
        contactApi = Mockito.mock(ContactsApi.class);
        
        doNothing().when(this.mockApi).close();
        
        try {
            when(ProjectAdminApiFactory.createApi(isA(String.class))).thenReturn(mockApi);
        }
        catch (Exception e) {
            Assert.fail("Unable to setup mock stub for ProjectAdminApi");
        }
        try {
            when(SubsidiaryApiFactory.createCustomerApi(isA(String.class))).thenReturn(mockCustApi);
        }
        catch (Exception e) {
            Assert.fail("Unable to setup mock stub for CustomerApi");
        }
        try {
            when(ContactsApiFactory.createApi()).thenReturn(contactApi);
        }
        catch (Exception e) {
            Assert.fail("Unable to setup mock stub for ContactsApi");
        }
        
        List<ClientDto> mockListData = ProjectTrackerMockData.createMockSingleClient();
        try {
            when(this.mockApi.getClient(isA(ClientDto.class))).thenReturn(null, mockListData);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for fetching project/client records");
        }
        
        CustomerDto mockCustomerData = ProjectTrackerMockData.createMockCustomer().get(0);
        try {
            when(this.mockCustApi.get(isA(Integer.class))).thenReturn(mockCustomerData);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for fetching customer records");
        }
        
        List<ContactDto> mockContactData = ProjectTrackerMockData.createMockSingleContact();
        try {
            when(this.contactApi.getContact(isA(BusinessContactDto.class))).thenReturn(mockContactData);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for fetching business contact records");
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
    public void testSuccess_Import() {
        String request = RMT2File.getFileContentsAsString("xml/admin/client/ClientImportRequest.xml");
        MessageHandlerResults results = null;
        ClientImportApiHandler handler = new ClientImportApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_CLIENT_IMPORT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getClient().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ClientMessageHandlerConst.MESSAGE_CUSTOMER_IMPORTED, actualRepsonse.getReplyStatus().getMessage());
        
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
            Assert.assertEquals("steve", a.getCustomer().getBusinessContactDetails().getContactFirstname());
            Assert.assertEquals("gadd", a.getCustomer().getBusinessContactDetails().getContactLastname());
            Assert.assertEquals("stevegadd@gte.net", a.getCustomer()
                    .getBusinessContactDetails().getContactEmail());
        }
    }
    
    @Test
    public void test_Customer_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/admin/client/ClientImportRequest.xml");
       
        try {
            when(this.mockCustApi.get(isA(Integer.class))).thenReturn(null);
        } catch (Exception e) {
            Assert.fail("Unable to setup mock stub for fetching customer records");
        }
        MessageHandlerResults results = null;
        ClientImportApiHandler handler = new ClientImportApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_CLIENT_IMPORT, request);
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
        Assert.assertEquals(ClientMessageHandlerConst.MESSAGE_CUSTOMER_NOT_IMPORTED, actualRepsonse.getReplyStatus().getMessage());
        
        String extMessage = "Unable to import customer profile identified by customer ID, 100.  Record was skipped due to the customer profile could not be found in the Accounting system.";
        Assert.assertEquals(extMessage, actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void test_BusinessContact_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/admin/client/ClientImportRequest.xml");
        
        try {
            when(this.contactApi.getContact(isA(BusinessContactDto.class))).thenReturn(null);
        } catch (Exception e) {
            Assert.fail("Unable to setup mock stub for fetching business contact records");
        }
        
        MessageHandlerResults results = null;
        ClientImportApiHandler handler = new ClientImportApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_CLIENT_IMPORT, request);
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
        Assert.assertEquals(ClientMessageHandlerConst.MESSAGE_CUSTOMER_NOT_IMPORTED, actualRepsonse.getReplyStatus().getMessage());
        
        String extMessage = "Unable to import customer profile identified by customer ID, 100.  Record was skipped due to the associated business contact profile [1456] could not be found in the Address Book system.";
        Assert.assertEquals(extMessage, actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void test_Target_Import_Duplicate() {
        String request = RMT2File.getFileContentsAsString("xml/admin/client/ClientImportRequest.xml");
        List<ClientDto> mockListData = ProjectTrackerMockData.createMockSingleClient();
        try {
            when(this.mockApi.getClient(isA(ClientDto.class))).thenReturn(mockListData);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for fetching project/client records");
        }
        
        MessageHandlerResults results = null;
        ClientImportApiHandler handler = new ClientImportApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_CLIENT_IMPORT, request);
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
        Assert.assertEquals(ClientMessageHandlerConst.MESSAGE_CUSTOMER_NOT_IMPORTED, actualRepsonse.getReplyStatus().getMessage());
        
        String extMessage = "Unable to import customer profile identified by customer ID, 100.  Record was skipped due to the client profile already exists";
        Assert.assertEquals(extMessage, actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/admin/client/ClientImportRequest.xml");
        try {
            when(this.mockApi.getClient(isA(ClientDto.class)))
                    .thenThrow(new ProjectAdminApiException(API_ERROR_MSG));
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for fetching employees with an API Error");
        }
        
        MessageHandlerResults results = null;
        ClientImportApiHandler handler = new ClientImportApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_CLIENT_IMPORT, request);
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
        Assert.assertEquals(ClientMessageHandlerConst.MESSAGE_CUSTOMER_IMPORT_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR_MSG, actualRepsonse.getReplyStatus()
                .getExtMessage());
    }
    
    
    @Test
    public void testError_MissingProfile() {
        String request = RMT2File.getFileContentsAsString("xml/admin/client/ClientImportRequest_MissingProfile.xml");
        MessageHandlerResults results = null;
        ClientImportApiHandler handler = new ClientImportApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_CLIENT_IMPORT, request);
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
        Assert.assertEquals(ApiMessageHandlerConst.MSG_MISSING_PROFILE_DATA, actualRepsonse.getReplyStatus().getMessage());
        
    }
  
}

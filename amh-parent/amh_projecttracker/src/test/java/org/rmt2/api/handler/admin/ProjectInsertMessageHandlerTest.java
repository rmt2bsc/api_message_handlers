package org.rmt2.api.handler.admin;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.dto.ClientDto;
import org.dto.ContactDto;
import org.dto.CustomerDto;
import org.dto.Project2Dto;
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
import org.modules.contacts.ContactsApiException;
import org.modules.contacts.ContactsApiFactory;
import org.modules.subsidiary.CustomerApi;
import org.modules.subsidiary.CustomerApiException;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.ProjectTrackerMockData;
import org.rmt2.api.handler.BaseProjectTrackerMessageHandlerTest;
import org.rmt2.api.handlers.admin.project.ProjectMessageHandlerConst;
import org.rmt2.api.handlers.admin.project.ProjectUpdateApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.ProjectType;

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
        ProjectUpdateApiHandler.class, ProjectAdminApiFactory.class, ContactsApiFactory.class, SubsidiaryApiFactory.class,
        SystemConfigurator.class })
public class ProjectInsertMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    public static final int PROJECT_COUNT = 28;
    public static final int PROJECT_ID_NEW = 4444;
    public static final String PROJECT_NAME = "Test Project for Business Server";
    public static final String PROJECT_INSERT_API_ERROR = "API ERROR: Project creataion failed";
    private ProjectAdminApi mockApi;
    private ContactsApi mockContactApi;
    private CustomerApi mockCustomerApi;


    /**
     * 
     */
    public ProjectInsertMessageHandlerTest() {
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

        mockContactApi = Mockito.mock(ContactsApi.class);
        PowerMockito.mockStatic(ContactsApiFactory.class);
        when(ContactsApiFactory.createApi()).thenReturn(this.mockContactApi);
        doNothing().when(this.mockApi).close();

        mockCustomerApi = Mockito.mock(CustomerApi.class);
        PowerMockito.mockStatic(SubsidiaryApiFactory.class);
        PowerMockito.when(SubsidiaryApiFactory.createCustomerApi(isA(String.class))).thenReturn(this.mockCustomerApi);
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
    public void testSuccess_Insert_With_ClientId() {
        String request = RMT2File.getFileContentsAsString("xml/admin/project/ProjectInsertRequest.xml");
        try {
            when(this.mockApi.updateProject(isA(Project2Dto.class))).thenReturn(PROJECT_ID_NEW);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for inserting project record");
        }
        
        MessageHandlerResults results = null;
        ProjectUpdateApiHandler handler = new ProjectUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getProject().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ProjectMessageHandlerConst.MESSAGE_NEW_PROJECT_UPDATE_SUCCESS, actualRepsonse.getReplyStatus()
                .getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getProject().size(); ndx++) {
            ProjectType a = actualRepsonse.getProfile().getProject().get(ndx);
            Assert.assertNotNull(a.getProjectId());
            Assert.assertEquals(PROJECT_ID_NEW, a.getProjectId().intValue());
            Assert.assertEquals(PROJECT_NAME, a.getDescription());
        }
    }
    
    @Test
    public void testSuccess_Insert_With_BusinessId() {
        String request = RMT2File.getFileContentsAsString("xml/admin/project/ProjectInsertWithBusinessIdRequest.xml");
        
        try {
            when(this.mockContactApi.getContact(isA(ContactDto.class)))
                     .thenReturn(ProjectTrackerMockData.createMockSingleContact());
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for fetching business contact record");
        }

        try {
            when(this.mockCustomerApi.get(isA(CustomerDto.class)))
                    .thenReturn(ProjectTrackerMockData.createMockCustomer());
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a customer");
        }

        try {
            when(this.mockApi.updateClientWithoutNotification(isA(ClientDto.class))).thenReturn(1);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for updating client");
        }

        try {
            when(this.mockApi.updateProject(isA(Project2Dto.class))).thenReturn(PROJECT_ID_NEW);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for inserting project record");
        }
        
        MessageHandlerResults results = null;
        ProjectUpdateApiHandler handler = new ProjectUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getProject().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ProjectMessageHandlerConst.MESSAGE_NEW_PROJECT_UPDATE_SUCCESS, actualRepsonse.getReplyStatus()
                .getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getProject().size(); ndx++) {
            ProjectType a = actualRepsonse.getProfile().getProject().get(ndx);
            Assert.assertNotNull(a.getProjectId());
            Assert.assertEquals(PROJECT_ID_NEW, a.getProjectId().intValue());
            Assert.assertEquals(PROJECT_NAME, a.getDescription());
        }
    }

    @Test
    public void testError_Insert_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/admin/project/ProjectInsertRequest.xml");
        try {
            when(this.mockApi.updateProject(isA(Project2Dto.class))).thenThrow(
                    new ProjectAdminApiException(PROJECT_INSERT_API_ERROR));
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for inserting project record");
        }
        
        MessageHandlerResults results = null;
        ProjectUpdateApiHandler handler = new ProjectUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        ProjectProfileResponse actualRepsonse = (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(ProjectMessageHandlerConst.MESSAGE_NEW_PROJECT_UPDATE_FAILED, actualRepsonse.getReplyStatus()
                .getMessage());
        Assert.assertEquals(PROJECT_INSERT_API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }
    

    @Test
    /**
     * To date, there are no validations to test.  Leaving stub in case this changes.
     */
    public void testValidation_Profile_Not_Exists() {
        String request = RMT2File.getFileContentsAsString("xml/admin/project/ProjectUpdateMissingProfileRequest.xml");

        MessageHandlerResults results = null;
        ProjectUpdateApiHandler handler = new ProjectUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
    }

    @Test
    public void testValidation_CreateProject_Missing_ClientSection() {
        String request = RMT2File.getFileContentsAsString("xml/admin/project/ProjectInsertWithMissingClientSectionRequest.xml");

        MessageHandlerResults results = null;
        ProjectUpdateApiHandler handler = new ProjectUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_UPDATE, request);
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
        Assert.assertEquals(ProjectMessageHandlerConst.VALIDATION_PROJECT_CLIENT_MISSING, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_CreateProject_With_BusinessId_Missing_CustomerSection() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/project/ProjectInsertWithBusinessIdMissingCustomerSectionRequest.xml");

        MessageHandlerResults results = null;
        ProjectUpdateApiHandler handler = new ProjectUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_UPDATE, request);
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
        Assert.assertEquals(ProjectMessageHandlerConst.VALIDATION_BUSID_MISSING_FOR_NEW_PROJECT, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_CreateProject_With_BusinessId_Missing_CustomerBusinessTypeSection() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/project/ProjectInsertWithBusinessIdMissingCustomerBusinessTypeSectionRequest.xml");

        MessageHandlerResults results = null;
        ProjectUpdateApiHandler handler = new ProjectUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_UPDATE, request);
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
        Assert.assertEquals(ProjectMessageHandlerConst.VALIDATION_BUSID_MISSING_FOR_NEW_PROJECT, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_CreateProject_With_BusinessId_Missing_BusinessId() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/project/ProjectInsertWithBusinessIdMissingCustomerBusinessTypeSectionRequest.xml");

        MessageHandlerResults results = null;
        ProjectUpdateApiHandler handler = new ProjectUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_UPDATE, request);
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
        Assert.assertEquals(ProjectMessageHandlerConst.VALIDATION_BUSID_MISSING_FOR_NEW_PROJECT, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_CreateProject_With_BusinessId_Less_Than_1() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/project/ProjectInsertWithInvalidBusinessIdRequest.xml");

        MessageHandlerResults results = null;
        ProjectUpdateApiHandler handler = new ProjectUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_UPDATE, request);
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
        Assert.assertEquals(ProjectMessageHandlerConst.VALIDATION_BUSID_MISSING_FOR_NEW_PROJECT, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testSuccess_Insert_With_BusinessId_Contact_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/admin/project/ProjectInsertWithBusinessIdRequest.xml");

        try {
            when(this.mockContactApi.getContact(isA(ContactDto.class))).thenReturn(null);
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for fetching business contact record");
        }

        MessageHandlerResults results = null;
        ProjectUpdateApiHandler handler = new ProjectUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_UPDATE, request);
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
        Assert.assertEquals(ProjectMessageHandlerConst.VALIDATION_BUSID_NOT_BUSINESS_CONTACT, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testSuccess_Insert_With_BusinessId_Customer_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/admin/project/ProjectInsertWithBusinessIdRequest.xml");

        try {
            when(this.mockContactApi.getContact(isA(ContactDto.class)))
                    .thenReturn(ProjectTrackerMockData.createMockSingleContact());
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for fetching business contact record");
        }

        try {
            when(this.mockCustomerApi.get(isA(CustomerDto.class))).thenReturn(null);
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a customer");
        }

        MessageHandlerResults results = null;
        ProjectUpdateApiHandler handler = new ProjectUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_UPDATE, request);
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
        Assert.assertEquals(ProjectMessageHandlerConst.VALIDATION_BUSID_NOT_CUSTOMER, actualRepsonse.getReplyStatus()
                .getMessage());
    }
}

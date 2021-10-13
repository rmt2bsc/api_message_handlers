package org.rmt2.api.handler.admin;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.dto.ResourceDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.resource.ResourceRegistryApi;
import org.modules.resource.ResourceRegistryApiException;
import org.modules.resource.ResourceRegistryApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAuthenticationMessageHandlerTest;
import org.rmt2.api.handler.SecurityMockDtoData;
import org.rmt2.api.handler.SecurityMockOrmDataFactory;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.resource.ResourceMessageHandlerConst;
import org.rmt2.api.handlers.admin.resource.ResourceUpdateApiHandler;
import org.rmt2.api.handlers.admin.resource.type.ResourceTypeUpdateApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AuthenticationResponse;
import org.rmt2.jaxb.ResourceType;

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
        ResourceTypeUpdateApiHandler.class, ResourceRegistryApiFactory.class, SystemConfigurator.class })
public class ResourceUpdateMessageHandlerTest extends BaseAuthenticationMessageHandlerTest {
    public static final String API_ERROR = "API ERROR: Test failed";
    private ResourceRegistryApi mockApi;


    /**
     * 
     */
    public ResourceUpdateMessageHandlerTest() {
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
        mockApi = Mockito.mock(ResourceRegistryApi.class);
        PowerMockito.mockStatic(ResourceRegistryApiFactory.class);
        when(ResourceRegistryApiFactory.createWebServiceRegistryApiInstance()).thenReturn(mockApi);
        doNothing().when(this.mockApi).close();

        try {
            when(this.mockApi.getResource(isA(ResourceDto.class))).thenReturn(
                    SecurityMockDtoData.createSingleUserResourceMockData());
        } catch (ResourceRegistryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching resource type record");
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
    public void testSuccess_Create() {
        String request = RMT2File.getFileContentsAsString("xml/admin/resource/ResourceInsertRequest.xml");
        try {
            when(this.mockApi.updateResource(isA(ResourceDto.class))).thenReturn(SecurityMockOrmDataFactory.TEST_RESOURCE_ID);
        } catch (ResourceRegistryApiException e) {
            Assert.fail("Unable to setup mock stub for inserting resource record");
        }
        MessageHandlerResults results = null;
        ResourceUpdateApiHandler handler = new ResourceUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_RESOURCE_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse =
                (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getResourcesInfo());
        Assert.assertNotNull(actualRepsonse.getProfile().getResourcesInfo().getResource());
        Assert.assertEquals(1, actualRepsonse.getProfile().getResourcesInfo().getResource().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ResourceMessageHandlerConst.MESSAGE_CREATE_SUCCESS, actualRepsonse.getReplyStatus()
                .getMessage());

        for (int ndx = 0; ndx < actualRepsonse.getProfile().getResourcesInfo().getResource().size(); ndx++) {
            ResourceType a = actualRepsonse.getProfile().getResourcesInfo().getResource().get(ndx);
            Assert.assertEquals(SecurityMockOrmDataFactory.TEST_RESOURCE_ID, a.getUid(), 0);
        }
    }
    
    @Test
    public void testSuccess_Update() {
        String request = RMT2File.getFileContentsAsString("xml/admin/resource/ResourceUpdateRequest.xml");
        try {
            when(this.mockApi.updateResource(isA(ResourceDto.class))).thenReturn(1);
        } catch (ResourceRegistryApiException e) {
            Assert.fail("Unable to setup mock stub for inserting resource type record");
        }
        MessageHandlerResults results = null;
        ResourceUpdateApiHandler handler = new ResourceUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_RESOURCE_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AuthenticationResponse actualRepsonse =
                (AuthenticationResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getResourcesInfo());
        Assert.assertNotNull(actualRepsonse.getProfile().getResourcesInfo().getResource());
        Assert.assertEquals(1, actualRepsonse.getProfile().getResourcesInfo().getResource().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ResourceMessageHandlerConst.MESSAGE_UPDATE_SUCCESS, actualRepsonse.getReplyStatus()
                .getMessage());
    }


    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/admin/resource/ResourceUpdateRequest.xml");
        try {
            when(this.mockApi.updateResource(isA(ResourceDto.class))).thenThrow(new ResourceRegistryApiException(API_ERROR));
        } catch (ResourceRegistryApiException e) {
            Assert.fail("Unable to setup mock stub for inserting application record");
        }

        MessageHandlerResults results = null;
        ResourceUpdateApiHandler handler = new ResourceUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_RESOURCE_UPDATE, request);
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
        Assert.assertEquals(ResourceMessageHandlerConst.MESSAGE_UPDATE_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());

    }

    @Test
    public void testValidation_Invalid_Transaction_Code() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/resource/ResourceUpdateRequest_InvalidTransactionCode.xml");

        MessageHandlerResults results = null;
        ResourceUpdateApiHandler handler = new ResourceUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_RESOURCE_UPDATE, request);
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
        String request = RMT2File.getFileContentsAsString("xml/admin/resource/ResourceUpdateRequest_MissingProfile.xml");

        MessageHandlerResults results = null;
        ResourceUpdateApiHandler handler = new ResourceUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_RESOURCE_UPDATE, request);
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
    public void testValidation_Missing_Profile_ResourceInfo_Node() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/resource/ResourceUpdateRequest_MissingResourceSection.xml");

        MessageHandlerResults results = null;
        ResourceUpdateApiHandler handler = new ResourceUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_RESOURCE_UPDATE, request);
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
        Assert.assertEquals(ResourceMessageHandlerConst.MESSAGE_MISSING_RESOURCE_SECTION, actualRepsonse
                .getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Too_Many_Records() {
        String request = RMT2File.getFileContentsAsString("xml/admin/resource/ResourceUpdateRequest_TooManyRecords.xml");

        MessageHandlerResults results = null;
        ResourceUpdateApiHandler handler = new ResourceUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_RESOURCE_UPDATE, request);
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

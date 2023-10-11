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
import org.rmt2.api.handlers.admin.application.ApplicationQueryApiHandler;
import org.rmt2.api.handlers.admin.resource.subtype.ResourceSubTypeMessageHandlerConst;
import org.rmt2.api.handlers.admin.resource.subtype.ResourceSubTypeQueryApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AuthenticationResponse;
import org.rmt2.jaxb.ResourcesubtypeType;

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
        ApplicationQueryApiHandler.class, ResourceRegistryApiFactory.class, SystemConfigurator.class })
public class ResourceSubTypeQueryMessageHandlerTest extends BaseAuthenticationMessageHandlerTest {
    public static final String API_ERROR = "API ERROR: Test failed";
    private ResourceRegistryApi mockApi;


    /**
     * 
     */
    public ResourceSubTypeQueryMessageHandlerTest() {
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
        String request = RMT2File.getFileContentsAsString("xml/admin/resource/subtype/ResourceSubTypeQueryRequest.xml");

        try {
            when(this.mockApi.getResourceSubTypeExt(isA(ResourceDto.class)))
                    .thenReturn(SecurityMockDtoData.createVwResourceTypeMockData());
        } catch (ResourceRegistryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching resource sub type record");
        }
        
        MessageHandlerResults results = null;
        ResourceSubTypeQueryApiHandler handler = new ResourceSubTypeQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET, request);
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
        Assert.assertNotNull(actualRepsonse.getProfile().getResourcesInfo().getResourcesubtype());
        Assert.assertEquals(5, actualRepsonse.getProfile().getResourcesInfo().getResourcesubtype().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ResourceSubTypeMessageHandlerConst.MESSAGE_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getResourcesInfo().getResourcesubtype().size(); ndx++) {
            ResourcesubtypeType a = actualRepsonse.getProfile().getResourcesInfo().getResourcesubtype().get(ndx);
            Assert.assertEquals(SecurityMockOrmDataFactory.TEST_RESOURCE_TYPE_ID + ndx, a.getTypeInfo().getUid(), 0);
        }
    }
    
    @Test
    public void testSuccess_Fetch_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/admin/resource/subtype/ResourceSubTypeQueryRequest.xml");

        try {
            when(this.mockApi.getResourceSubTypeExt(isA(ResourceDto.class))).thenReturn(null);
        } catch (ResourceRegistryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching resource type record");
        }

        MessageHandlerResults results = null;
        ResourceSubTypeQueryApiHandler handler = new ResourceSubTypeQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET, request);
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
        Assert.assertEquals(ResourceSubTypeMessageHandlerConst.MESSAGE_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/admin/resource/subtype/ResourceSubTypeQueryRequest.xml");
        try {
            when(this.mockApi.getResourceSubTypeExt(isA(ResourceDto.class))).thenThrow(
                    new ResourceRegistryApiException(API_ERROR));
        } catch (ResourceRegistryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching resource sub type record");
        }
        
        MessageHandlerResults results = null;
        ResourceSubTypeQueryApiHandler handler = new ResourceSubTypeQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET, request);
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
        Assert.assertEquals(ResourceSubTypeMessageHandlerConst.MESSAGE_FETCH_ERROR, actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_Invalid_Transaction_Code() {
        String request = RMT2File
                .getFileContentsAsString("xml/admin/resource/subtype/ResourceSubTypeQueryRequest_InvalidTransactionCode.xml");

        MessageHandlerResults results = null;
        ResourceSubTypeQueryApiHandler handler = new ResourceSubTypeQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET, request);
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
                .getFileContentsAsString("xml/admin/resource/subtype/ResourceSubTypeQueryRequest_MissingCriteria.xml");

        MessageHandlerResults results = null;
        ResourceSubTypeQueryApiHandler handler = new ResourceSubTypeQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET, request);
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
                .getFileContentsAsString("xml/admin/resource/subtype/ResourceSubTypeQueryRequest_MissingResourceSubTypeCriteria.xml");

        MessageHandlerResults results = null;
        ResourceSubTypeQueryApiHandler handler = new ResourceSubTypeQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET, request);
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
        Assert.assertEquals(ResourceSubTypeMessageHandlerConst.MESSAGE_MISSING_RESOURCE_SUBTYPE_CRITERIA_SECTION, actualRepsonse
                .getReplyStatus().getMessage());
    }
}

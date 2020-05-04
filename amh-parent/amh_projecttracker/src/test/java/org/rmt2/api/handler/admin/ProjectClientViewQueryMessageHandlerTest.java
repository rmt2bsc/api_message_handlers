package org.rmt2.api.handler.admin;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.ProjectClientDto;
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
import org.rmt2.api.ProjectTrackerOrmDataFactory;
import org.rmt2.api.handler.BaseProjectTrackerMessageHandlerTest;
import org.rmt2.api.handlers.admin.project.ProjectMessageHandlerConst;
import org.rmt2.api.handlers.admin.project.ProjectQueryApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.ProjectType;

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
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class,
        ProjectQueryApiHandler.class, ProjectAdminApiFactory.class, SystemConfigurator.class })
public class ProjectClientViewQueryMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    public static final int PROJECT_COUNT = 28;
    private ProjectAdminApi mockApi;


    /**
     * 
     */
    public ProjectClientViewQueryMessageHandlerTest() {
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
        String request = RMT2File.getFileContentsAsString("xml/admin/ProjectQueryRequest.xml");
        List<ProjectClientDto> mockListData = ProjectTrackerMockData.createMockProjectClientDto();

        try {
            when(this.mockApi.getProjectExt(isA(ProjectClientDto.class))).thenReturn(mockListData);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for fetching project/client records");
        }
        
        MessageHandlerResults results = null;
        ProjectQueryApiHandler handler = new ProjectQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getProject().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ProjectMessageHandlerConst.MESSAGE_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getEmployee().size(); ndx++) {
            ProjectType a = actualRepsonse.getProfile().getProject().get(ndx);
            Assert.assertNotNull(a.getProjectId());
            Assert.assertEquals(2220 + ndx, a.getProjectId().intValue());
            Assert.assertNotNull(a.getClient());
            Assert.assertNotNull(a.getClient().getClientId());
            Assert.assertEquals(a.getClient().getClientId().intValue(), ProjectTrackerOrmDataFactory.TEST_CLIENT_ID);
            Assert.assertEquals(a.getDescription(), ("Project 222" + ndx));
            Assert.assertEquals(a.getEffectiveDate(), RMT2Date.stringToDate("2018-0" + (ndx + 1) + "-01"));
            Assert.assertEquals(a.getEndDate(), RMT2Date.stringToDate("2018-0" + (ndx + 2) + "-01"));
            Assert.assertEquals(a.getClient().getName(), "Client 1110");
            Assert.assertNotNull(a.getClient().getCustomer());
            Assert.assertNotNull(a.getClient().getCustomer().getBusinessContactDetails());
            Assert.assertNotNull(a.getClient().getCustomer().getBusinessContactDetails().getBusinessId());
            Assert.assertEquals(a.getClient().getCustomer().getBusinessContactDetails().getBusinessId().intValue(), 1440);
        }
    }
    
    @Test
    public void test_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/admin/ProjectQueryRequest.xml");
        try {
            when(this.mockApi.getProjectExt(isA(ProjectClientDto.class))).thenReturn(null);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for fetching project/client records");
        }
        
        MessageHandlerResults results = null;
        ProjectQueryApiHandler handler = new ProjectQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_GET, request);
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
        Assert.assertEquals(ProjectMessageHandlerConst.MESSAGE_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/admin/ProjectQueryRequest.xml");
        try {
            when(this.mockApi.getProjectExt(isA(ProjectClientDto.class)))
                    .thenThrow(new ProjectAdminApiException("Test validation error: selection criteria is required"));
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for fetching employees with an API Error");
        }
        
        MessageHandlerResults results = null;
        ProjectQueryApiHandler handler = new ProjectQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_GET, request);
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
        Assert.assertEquals(ProjectMessageHandlerConst.MESSAGE_FETCH_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test validation error: selection criteria is required", actualRepsonse.getReplyStatus()
                .getExtMessage());
    }
    

    @Test
    /**
     * To date, there are no validations to test.  Leaving stub in case this changes.
     */
    public void testValidation_() {
        String request = RMT2File.getFileContentsAsString("xml/admin/ProjectQueryRequest.xml");

        MessageHandlerResults results = null;
        ProjectQueryApiHandler handler = new ProjectQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_PROJECT_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
    }
}

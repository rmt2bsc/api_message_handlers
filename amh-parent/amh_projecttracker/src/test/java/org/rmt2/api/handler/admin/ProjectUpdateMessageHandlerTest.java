package org.rmt2.api.handler.admin;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
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
        ProjectUpdateApiHandler.class, ProjectAdminApiFactory.class, SystemConfigurator.class })
public class ProjectUpdateMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    public static final int PROJECT_COUNT = 28;
    public static final int PROJECT_ID = 39;
    public static final String PROJECT_NAME = "Test Project for Business Server";
    public static final String PROJECT_API_ERROR = "API ERROR: Project creataion failed";
    private ProjectAdminApi mockApi;


    /**
     * 
     */
    public ProjectUpdateMessageHandlerTest() {
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
    public void testSuccess_Update() {
        String request = RMT2File.getFileContentsAsString("xml/admin/project/ProjectUpdateRequest.xml");
        try {
            when(this.mockApi.updateProject(isA(Project2Dto.class))).thenReturn(1);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for updating project record");
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
        Assert.assertEquals(ProjectMessageHandlerConst.MESSAGE_EXISTING_PROJECT_UPDATE_SUCCESS, actualRepsonse.getReplyStatus()
                .getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getProject().size(); ndx++) {
            ProjectType a = actualRepsonse.getProfile().getProject().get(ndx);
            Assert.assertNotNull(a.getProjectId());
            Assert.assertEquals(PROJECT_ID, a.getProjectId().intValue());
            Assert.assertEquals(PROJECT_NAME, a.getDescription());
        }
    }
    
    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/admin/project/ProjectUpdateRequest.xml");
        try {
            when(this.mockApi.updateProject(isA(Project2Dto.class))).thenThrow(
                    new ProjectAdminApiException(PROJECT_API_ERROR));
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
        Assert.assertEquals(ProjectMessageHandlerConst.MESSAGE_EXISTING_PROJECT_UPDATE_FAILED, actualRepsonse.getReplyStatus()
                .getMessage());
        Assert.assertEquals(PROJECT_API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
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
}

package org.rmt2.api.handler.admin;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.dto.TaskDto;
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
import org.rmt2.api.handlers.admin.task.TaskMessageHandlerConst;
import org.rmt2.api.handlers.admin.task.TaskUpdateApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileResponse;
import org.rmt2.jaxb.TaskType;

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
        TaskUpdateApiHandler.class, ProjectAdminApiFactory.class, SystemConfigurator.class })
public class TaskUpdateMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    public static final int TASK_COUNT = 28;
    public static final int TASK_ID = 39;
    public static final String TASK_NAME = "Test Task Name";
    public static final String TASK_NAME_UPDATED = "Test Task Name Updated";
    public static final String TASK_API_ERROR = "API ERROR: Task update failed";
    private ProjectAdminApi mockApi;


    /**
     * 
     */
    public TaskUpdateMessageHandlerTest() {
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
    public void testSuccess_Insert() {
        String request = RMT2File.getFileContentsAsString("xml/admin/task/TaskInsertRequest.xml");
        try {
            when(this.mockApi.updateTask(isA(TaskDto.class))).thenReturn(TASK_ID);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for creating a task record");
        }

        MessageHandlerResults results = null;
        TaskUpdateApiHandler handler = new TaskUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TASK_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getTask().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(TaskMessageHandlerConst.MESSAGE_NEW_TASK_UPDATE_SUCCESS, actualRepsonse.getReplyStatus()
                .getMessage());

        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTask().size(); ndx++) {
            TaskType a = actualRepsonse.getProfile().getTask().get(ndx);
            Assert.assertNotNull(a.getTaskId());
            Assert.assertEquals(TASK_ID, a.getTaskId().intValue());
            Assert.assertEquals(TASK_NAME, a.getDescription());
            Assert.assertEquals(1, a.getBillable().intValue());
        }
    }
    
    @Test
    public void testSuccess_Update() {
        String request = RMT2File.getFileContentsAsString("xml/admin/task/TaskUpdateRequest.xml");
        try {
            when(this.mockApi.updateTask(isA(TaskDto.class))).thenReturn(1);
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for updating a task record");
        }

        MessageHandlerResults results = null;
        TaskUpdateApiHandler handler = new TaskUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TASK_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getTask().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(TaskMessageHandlerConst.MESSAGE_EXISTING_TASK_UPDATE_SUCCESS, actualRepsonse.getReplyStatus()
                .getMessage());

        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTask().size(); ndx++) {
            TaskType a = actualRepsonse.getProfile().getTask().get(ndx);
            Assert.assertNotNull(a.getTaskId());
            Assert.assertEquals(TASK_ID, a.getTaskId().intValue());
            Assert.assertEquals(TASK_NAME_UPDATED, a.getDescription());
            Assert.assertEquals(1, a.getBillable().intValue());
        }
    }
    
    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/admin/task/TaskUpdateRequest.xml");
        try {
            when(this.mockApi.updateTask(isA(TaskDto.class)))
                    .thenThrow(new ProjectAdminApiException(TASK_API_ERROR));
        } catch (ProjectAdminApiException e) {
            Assert.fail("Unable to setup mock stub for updating task with API ERROR record");
        }
        
        MessageHandlerResults results = null;
        TaskUpdateApiHandler handler = new TaskUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TASK_UPDATE, request);
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
        Assert.assertEquals(TaskMessageHandlerConst.MESSAGE_EXISTING_TASK_UPDATE_FAILED, actualRepsonse.getReplyStatus()
                .getMessage());
        Assert.assertEquals(TASK_API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }
    

    @Test
    public void testValidation_Task_Data_Not_Exists() {
        String request = RMT2File.getFileContentsAsString("xml/admin/task/TaskUpdateMissingTaskDataRequest.xml");

        MessageHandlerResults results = null;
        TaskUpdateApiHandler handler = new TaskUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_TASK_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        ProjectProfileResponse actualRepsonse = (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(BaseProjectTrackerMessageHandlerTest.BAD_REQUEST_STATUS, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(TaskMessageHandlerConst.VALIDATION_TASK_MISSING, actualRepsonse.getReplyStatus()
                .getMessage());
    }
}

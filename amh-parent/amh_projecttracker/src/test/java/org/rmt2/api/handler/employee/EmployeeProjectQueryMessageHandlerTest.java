package org.rmt2.api.handler.employee;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.ProjectEmployeeDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.employee.EmployeeApi;
import org.modules.employee.EmployeeApiException;
import org.modules.employee.EmployeeApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.ProjectTrackerMockData;
import org.rmt2.api.handler.BaseProjectTrackerMessageHandlerTest;
import org.rmt2.api.handlers.employee.EmployeeProjectMessageHandlerConst;
import org.rmt2.api.handlers.employee.EmployeeProjectQueryApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.EmployeeProjectType;
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
        EmployeeProjectQueryApiHandler.class, EmployeeApiFactory.class, SystemConfigurator.class })
public class EmployeeProjectQueryMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    public static final int PROJECT_COUNT = 28;
    public static final int EMPLOYEE_ID = 2220;
    public static final int EMPLOYEE_PROJECT_ID_SEED = 55551;
    public static final String API_ERROR = "Test validation error: selection criteria is required";

    private EmployeeApi mockApi;


    /**
     * 
     */
    public EmployeeProjectQueryMessageHandlerTest() {
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
        mockApi = Mockito.mock(EmployeeApi.class);
        PowerMockito.mockStatic(EmployeeApiFactory.class);
        when(EmployeeApiFactory.createApi(isA(String.class))).thenReturn(mockApi);
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
        String request = RMT2File.getFileContentsAsString("xml/employee/EmployeeProjectQueryRequest.xml");
        List<ProjectEmployeeDto> mockListData = ProjectTrackerMockData.createMockVwEmployeeProjects();

        try {
            when(this.mockApi.getProjectEmployee(isA(ProjectEmployeeDto.class))).thenReturn(mockListData);
        } catch (EmployeeApiException e) {
            Assert.fail("Unable to setup mock stub for fetching employee/project records");
        }
        
        MessageHandlerResults results = null;
        EmployeeProjectQueryApiHandler handler = new EmployeeProjectQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_EMPLOYEE_PROJECT_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getEmployeeProject().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(EmployeeProjectMessageHandlerConst.MESSAGE_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getEmployeeProject().size(); ndx++) {
            EmployeeProjectType a = actualRepsonse.getProfile().getEmployeeProject().get(ndx);
            
            Assert.assertNotNull(a.getEmployeeProjectId());
            Assert.assertNotNull(a.getEmployeeProjectId().intValue());
            Assert.assertEquals(EMPLOYEE_PROJECT_ID_SEED + ndx, a.getEmployeeProjectId().intValue());
            
            Assert.assertNotNull(a.getEmployee());
            Assert.assertNotNull(a.getEmployee().getEmployeeId());
            Assert.assertEquals(EMPLOYEE_ID, a.getEmployee().getEmployeeId().intValue());
            
        }
    }
    
    @Test
    public void test_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/employee/EmployeeProjectQueryRequest.xml");

        try {
            when(this.mockApi.getProjectEmployee(isA(ProjectEmployeeDto.class))).thenReturn(null);
        } catch (EmployeeApiException e) {
            Assert.fail("Unable to setup mock stub for fetching employee/project records");
        }
        
        MessageHandlerResults results = null;
        EmployeeProjectQueryApiHandler handler = new EmployeeProjectQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_EMPLOYEE_PROJECT_GET, request);
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
        Assert.assertEquals(EmployeeProjectMessageHandlerConst.MESSAGE_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/employee/EmployeeProjectQueryRequest.xml");
        try {
            when(this.mockApi.getProjectEmployee(isA(ProjectEmployeeDto.class)))
                    .thenThrow(new EmployeeApiException(API_ERROR));
        } catch (EmployeeApiException e) {
            Assert.fail("Unable to setup mock stub for fetching employees with an API Error");
        }
        
        MessageHandlerResults results = null;
        EmployeeProjectQueryApiHandler handler = new EmployeeProjectQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_EMPLOYEE_PROJECT_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(EmployeeProjectMessageHandlerConst.MESSAGE_FETCH_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }
    
}

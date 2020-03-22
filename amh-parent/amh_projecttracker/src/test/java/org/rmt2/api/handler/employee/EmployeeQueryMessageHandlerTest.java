package org.rmt2.api.handler.employee;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.EmployeeDto;
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
import org.rmt2.api.handlers.employee.EmployeeQueryApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.EmployeeType;
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
        EmployeeQueryApiHandler.class, EmployeeApiFactory.class, SystemConfigurator.class })
public class EmployeeQueryMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {

    private EmployeeApi mockApi;


    /**
     * 
     */
    public EmployeeQueryMessageHandlerTest() {
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

        // This code fragment is only needed when the factory is not using
        // static method calls and need to instantiated.
        // mockApiFactory = Mockito.mock(EmployeeApiFactory.class);
        // try {
        // PowerMockito.whenNew(GeneralLedgerApiFactory.class)
        // .withNoArguments().thenReturn(this.mockApiFactory);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

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
        String request = RMT2File.getFileContentsAsString("xml/employee/EmployeeQueryRequest.xml");
        List<EmployeeDto> mockListData = ProjectTrackerMockData.createMockMultipleExtEmployee();

        try {
            when(this.mockApi.getEmployeeExt(isA(EmployeeDto.class))).thenReturn(mockListData);
        } catch (EmployeeApiException e) {
            Assert.fail("Unable to setup mock stub for fetching employee records");
        }
        
        MessageHandlerResults results = null;
        EmployeeQueryApiHandler handler = new EmployeeQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_EMPLOYEE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getEmployee().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(EmployeeQueryApiHandler.MESSAGE_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getEmployee().size(); ndx++) {
            EmployeeType a = actualRepsonse.getProfile().getEmployee().get(ndx);
            Assert.assertNotNull(a.getEmployeeId());
            Assert.assertEquals(2220 + ndx, a.getEmployeeId().intValue());
        }
    }
    
    @Test
    public void test_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/employee/EmployeeQueryRequest.xml");
        try {
            when(this.mockApi.getEmployeeExt(isA(EmployeeDto.class))).thenReturn(null);
        } catch (EmployeeApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a empty set of employees");
        }
        
        MessageHandlerResults results = null;
        EmployeeQueryApiHandler handler = new EmployeeQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_EMPLOYEE_GET, request);
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
        Assert.assertEquals(EmployeeQueryApiHandler.MESSAGE_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/employee/EmployeeQueryRequest.xml");
        try {
            when(this.mockApi.getEmployeeExt(isA(EmployeeDto.class)))
                    .thenThrow(new EmployeeApiException("Test validation error: selection criteria is required"));
        } catch (EmployeeApiException e) {
            Assert.fail("Unable to setup mock stub for fetching employees with an API Error");
        }
        
        MessageHandlerResults results = null;
        EmployeeQueryApiHandler handler = new EmployeeQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_EMPLOYEE_GET, request);
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
        Assert.assertEquals(EmployeeQueryApiHandler.MESSAGE_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test validation error: selection criteria is required",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    

    @Test
    /**
     * Moving forward, it does not make sense to unit test
     * "invalid transaction code" within this project. It is more applicable to
     * test in the internal business server project where the client sends a
     * message and the framework tests the transaction action code that is
     * gathered from the message against the list of valid transaction codes. 
     */
    public void testError_Incorrect_Trans_Code() {

    }
    

    @Test
    /**
     * To date, there are no validations to test.  Leaving stub in case this changes.
     */
    public void testValidation_() {
        String request = RMT2File
                .getFileContentsAsString("xml/employee/EmployeeQueryRequest.xml");

        MessageHandlerResults results = null;
        EmployeeQueryApiHandler handler = new EmployeeQueryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_EMPLOYEE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        // ProjectProfileResponse actualRepsonse = (ProjectProfileResponse) jaxb
        // .unMarshalMessage(results.getPayload().toString());
        // Assert.assertNull(actualRepsonse.getProfile());
        // Assert.assertEquals(-1,
        // actualRepsonse.getReplyStatus().getReturnCode().intValue());
        // Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST,
        // actualRepsonse.getReplyStatus()
        // .getReturnStatus());
        // Assert.assertEquals("A valid account id is required when deleting a GL Account from the database",
        // actualRepsonse.getReplyStatus().getMessage());
    }
}

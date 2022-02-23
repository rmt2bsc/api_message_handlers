package org.rmt2.api.handler.employee;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.ContactDto;
import org.dto.EmployeeDto;
import org.dto.PersonalContactDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiException;
import org.modules.contacts.ContactsApiFactory;
import org.modules.employee.EmployeeApi;
import org.modules.employee.EmployeeApiException;
import org.modules.employee.EmployeeApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.ProjectTrackerMockData;
import org.rmt2.api.handler.BaseProjectTrackerMessageHandlerTest;
import org.rmt2.api.handlers.employee.EmployeeMessageHandlerConst;
import org.rmt2.api.handlers.employee.EmployeeUpdateApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.EmployeeType;
import org.rmt2.jaxb.ProjectProfileResponse;

import com.NotFoundException;
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
        EmployeeUpdateApiHandler.class, EmployeeApiFactory.class, ContactsApiFactory.class, SystemConfigurator.class })
public class EmployeeUpdateMessageHandlerTest extends BaseProjectTrackerMessageHandlerTest {
    public static final int PROJECT_COUNT = 28;
    public static final int EMPLOYEE_ID = 2000;
    public static final int CONTACT_ID = 900;
    public static final int UPDATE_ROW_COUNT = 1;
    public static final String EMPLOYEE_FIRSTNAME = "John";
    public static final String EMPLOYEE_LASTNAME = "Doe";
    public static final String API_ERROR_CONTACTS = "Test Error: Contacts API error occurred";
    public static final String API_ERROR_EMPLOYEE = "Test Error: Employee API error occurred";
    private EmployeeApi mockApi;
    private ContactsApi mockContactApi;


    /**
     * 
     */
    public EmployeeUpdateMessageHandlerTest() {
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

        mockContactApi = Mockito.mock(ContactsApi.class);
        PowerMockito.mockStatic(ContactsApiFactory.class);
        when(ContactsApiFactory.createApi()).thenReturn(mockContactApi);
        doNothing().when(this.mockContactApi).close();

        mockApi = Mockito.mock(EmployeeApi.class);
        PowerMockito.mockStatic(EmployeeApiFactory.class);
        when(EmployeeApiFactory.createApi(isA(String.class))).thenReturn(mockApi);
        doNothing().when(this.mockApi).close();
        
        List<EmployeeDto> mockEmployeeListData = ProjectTrackerMockData.createMockSingleExtEmployee();
        mockEmployeeListData.get(0).setEmployeeId(2000);
        mockEmployeeListData.get(0).setEmployeeFirstname("John");
        mockEmployeeListData.get(0).setEmployeeLastname("Doe");
        try {
            when(mockApi.getEmployeeExt(isA(EmployeeDto.class))).thenReturn(mockEmployeeListData);
        }
        catch (Exception e) {
            Assert.fail("Unable to setup mock stub for fetching employee records");
        }
        List<ContactDto> mockPersonalContactDto = ProjectTrackerMockData.createMockSinglePersonalContactDto();
        try {
            when(mockContactApi.getContact(isA(PersonalContactDto.class))).thenReturn(mockPersonalContactDto);
        }
        catch (Exception e) {
            Assert.fail("Unable to setup mock stub for fetching personal contact data records");
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
    public void testSuccess_UpdateNew() {
        String request = RMT2File.getFileContentsAsString("xml/employee/EmployeeInsertRequest.xml");

        try {
            when(this.mockContactApi.updateContact(isA(PersonalContactDto.class))).thenReturn(CONTACT_ID);
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for creating employee contact record");
        }
        try {
            when(this.mockApi.update(isA(EmployeeDto.class))).thenReturn(EMPLOYEE_ID);
        } catch (EmployeeApiException e) {
            Assert.fail("Unable to setup mock stub for creating employee record");
        }
        
        MessageHandlerResults results = null;
        EmployeeUpdateApiHandler handler = new EmployeeUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_EMPLOYEE_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getEmployee().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(EmployeeMessageHandlerConst.MESSAGE_UPDATE_NEW_SUCCESS, actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getEmployee().size(); ndx++) {
            EmployeeType a = actualRepsonse.getProfile().getEmployee().get(ndx);
            Assert.assertNotNull(a.getEmployeeId());
            Assert.assertEquals(EMPLOYEE_ID, a.getEmployeeId().intValue());
            Assert.assertNotNull(a.getContactDetails());
            Assert.assertEquals(EMPLOYEE_FIRSTNAME, a.getContactDetails().getFirstName());
            Assert.assertEquals(EMPLOYEE_LASTNAME, a.getContactDetails().getLastName());
        }
    }
    
    @Test
    public void testSuccess_UpdateExisting() {
        String request = RMT2File.getFileContentsAsString("xml/employee/EmployeeUpdateRequest.xml");

        try {
            when(this.mockContactApi.updateContact(isA(PersonalContactDto.class))).thenReturn(UPDATE_ROW_COUNT);
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for modifying employee contact record");
        }
        try {
            when(this.mockApi.update(isA(EmployeeDto.class))).thenReturn(UPDATE_ROW_COUNT);
        } catch (EmployeeApiException e) {
            Assert.fail("Unable to setup mock stub for modifying employee record");
        }

        MessageHandlerResults results = null;
        EmployeeUpdateApiHandler handler = new EmployeeUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_EMPLOYEE_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        ProjectProfileResponse actualRepsonse =
                (ProjectProfileResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getEmployee().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(EmployeeMessageHandlerConst.MESSAGE_UPDATE_EXISTING_SUCCESS, actualRepsonse.getReplyStatus()
                .getMessage());

        for (int ndx = 0; ndx < actualRepsonse.getProfile().getEmployee().size(); ndx++) {
            EmployeeType a = actualRepsonse.getProfile().getEmployee().get(ndx);
            Assert.assertNotNull(a.getEmployeeId());
            Assert.assertEquals(EMPLOYEE_ID, a.getEmployeeId().intValue());
            Assert.assertNotNull(a.getContactDetails());
            Assert.assertEquals(EMPLOYEE_FIRSTNAME, a.getContactDetails().getFirstName());
            Assert.assertEquals(EMPLOYEE_LASTNAME, a.getContactDetails().getLastName());
        }
    }

    @Test
    public void testError_UpdateNew_Contact_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/employee/EmployeeInsertRequest.xml");
        try {
            when(this.mockContactApi.updateContact(isA(PersonalContactDto.class)))
                    .thenThrow(new ContactsApiException(API_ERROR_CONTACTS));
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for creating employee contact record");
        }

        MessageHandlerResults results = null;
        EmployeeUpdateApiHandler handler = new EmployeeUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_EMPLOYEE_UPDATE, request);
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
        Assert.assertEquals(EmployeeMessageHandlerConst.MESSAGE_UPDATE_NEW_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());
        Assert.assertEquals(API_ERROR_CONTACTS, actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testError_UpdateNew_Employee_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/employee/EmployeeInsertRequest.xml");
        try {
            when(this.mockContactApi.updateContact(isA(PersonalContactDto.class))).thenReturn(UPDATE_ROW_COUNT);
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for creating employee contact record");
        }
        try {
            when(this.mockApi.update(isA(EmployeeDto.class)))
                    .thenThrow(new EmployeeApiException(API_ERROR_EMPLOYEE));
        } catch (EmployeeApiException e) {
            Assert.fail("Unable to setup mock stub for creating employee record");
        }

        MessageHandlerResults results = null;
        EmployeeUpdateApiHandler handler = new EmployeeUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_EMPLOYEE_UPDATE, request);
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
        Assert.assertEquals(EmployeeMessageHandlerConst.MESSAGE_UPDATE_NEW_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());
        Assert.assertEquals(API_ERROR_EMPLOYEE, actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testError_UpdateExisting_Contact_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/employee/EmployeeUpdateRequest.xml");
        try {
            when(this.mockContactApi.updateContact(isA(PersonalContactDto.class)))
                    .thenThrow(new ContactsApiException(API_ERROR_CONTACTS));
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for modifying employee contact record");
        }
        
        MessageHandlerResults results = null;
        EmployeeUpdateApiHandler handler = new EmployeeUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_EMPLOYEE_UPDATE, request);
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
        Assert.assertEquals(EmployeeMessageHandlerConst.MESSAGE_UPDATE_EXISTING_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());
        Assert.assertEquals(API_ERROR_CONTACTS, actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testError_UpdateExisting_Employee_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/employee/EmployeeUpdateRequest.xml");
        try {
            when(this.mockContactApi.updateContact(isA(PersonalContactDto.class))).thenReturn(UPDATE_ROW_COUNT);
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for modifying employee contact record");
        }
        try {
            when(this.mockApi.update(isA(EmployeeDto.class)))
                    .thenThrow(new EmployeeApiException(API_ERROR_EMPLOYEE));
        } catch (EmployeeApiException e) {
            Assert.fail("Unable to setup mock stub for modifying employee record");
        }

        MessageHandlerResults results = null;
        EmployeeUpdateApiHandler handler = new EmployeeUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_EMPLOYEE_UPDATE, request);
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
        Assert.assertEquals(EmployeeMessageHandlerConst.MESSAGE_UPDATE_EXISTING_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());
        Assert.assertEquals(API_ERROR_EMPLOYEE, actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testError_UpdateNew_Contact_Update_Failure() {
        String request = RMT2File.getFileContentsAsString("xml/employee/EmployeeInsertRequest.xml");

        try {
            when(this.mockContactApi.updateContact(isA(PersonalContactDto.class))).thenThrow(new NotFoundException());
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for creating employee contact record");
        }
        try {
            when(this.mockApi.update(isA(EmployeeDto.class))).thenReturn(EMPLOYEE_ID);
        } catch (EmployeeApiException e) {
            Assert.fail("Unable to setup mock stub for creating employee record");
        }

        MessageHandlerResults results = null;
        EmployeeUpdateApiHandler handler = new EmployeeUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.PROJTRACK_EMPLOYEE_UPDATE, request);
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
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(EmployeeMessageHandlerConst.MESSAGE_UPDATE_NEW_ERROR, actualRepsonse.getReplyStatus().getMessage());
        String errMsg = RMT2String.replace(EmployeeMessageHandlerConst.MESSAGE_UPDATE_INVALID_CONTACT_PROFILE, 
                        "0", ApiMessageHandlerConst.MSG_PLACEHOLDER1);
        errMsg = RMT2String.replace(errMsg, "John Doe", ApiMessageHandlerConst.MSG_PLACEHOLDER2);
        Assert.assertEquals(errMsg, actualRepsonse.getReplyStatus().getExtMessage());

    }

}

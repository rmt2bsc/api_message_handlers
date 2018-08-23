package org.rmt2.api.handler.subsidiary.customer;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.dto.CustomerDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.subsidiary.CustomerApi;
import org.modules.subsidiary.CustomerApiException;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handlers.subsidiary.CustomerApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionResponse;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.messaging.webservice.WebServiceConstants;
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
    CustomerApiHandler.class, SubsidiaryApiFactory.class, SystemConfigurator.class })
public class CustomerUpdateMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private SubsidiaryApiFactory mockApiFactory;
    private CustomerApi mockApi;


    /**
     * 
     */
    public CustomerUpdateMessageHandlerTest() {
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
        mockApiFactory = Mockito.mock(SubsidiaryApiFactory.class);        
        try {
            PowerMockito.whenNew(SubsidiaryApiFactory.class)
                    .withNoArguments().thenReturn(this.mockApiFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mockApi = Mockito.mock(CustomerApi.class);
        when(mockApiFactory.createCustomerApi(isA(String.class))).thenReturn(mockApi);
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
    public void testSuccess_UpdateCustomer() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerUpdateRequest.xml");
        try {
            when(this.mockApi.update(isA(CustomerDto.class)))
                    .thenReturn(WebServiceConstants.RETURN_CODE_SUCCESS);
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for updating a customer");
        }
        
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getCustomers().getCustomer().size());
        Assert.assertEquals(WebServiceConstants.RETURN_CODE_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Customer profile was updated successfully",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
 
    @Test
    public void testSuccess_UpdateCustomer_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerUpdateRequest.xml");
        try {
            when(this.mockApi.update(isA(CustomerDto.class))).thenReturn(0);
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for updating a customer");
        }
        
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Customer profile was not found - No updates performed",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_UpdateCustomer_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerUpdateRequest.xml");
        try {
            when(this.mockApi.update(isA(CustomerDto.class)))
               .thenThrow(new CustomerApiException("API error occurred"));
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for updating a Customer");
        }
        
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals("Failure to update customer", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("API error occurred", actualRepsonse.getReplyStatus().getExtMessage());
    }
  

    @Test
    public void testValidation_Update_Profile_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerUpdateMissingProfileRequest.xml");
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals(CustomerApiHandler.MSG_UPDATE_MISSING_PROFILE,
                actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Update_CustomerList_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerUpdateMissingCustomerListRequest.xml");
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals(CustomerApiHandler.MSG_UPDATE_MISSING_PROFILE,
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_Update_CustomerList_Empty() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerUpdateEmptyCustomerListRequest.xml");
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals(CustomerApiHandler.MSG_UPDATE_MISSING_PROFILE,
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testSuccess_DeleteCustomer() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerDeleteRequest.xml");
        try {
            when(this.mockApi.delete(isA(CustomerDto.class)))
                    .thenReturn(WebServiceConstants.RETURN_CODE_SUCCESS);
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for delete a customer");
        }
        
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(WebServiceConstants.RETURN_CODE_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Customer delete operation completed!",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Total records deleted: " + WebServiceConstants.RETURN_CODE_SUCCESS,
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testError_DeleteCustomer_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerDeleteRequest.xml");
        try {
            when(this.mockApi.delete(isA(CustomerDto.class)))
               .thenThrow(new CustomerApiException("API error occurred"));
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for updating a Customer");
        }
        
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals("Failure to delete customer: " + 3333, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("API error occurred", actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testValidation_Delete_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerDeleteCriteriaMissingRequest.xml");
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals(CustomerApiHandler.MSG_UPDATE_MISSING_CRITERIA,
                actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Delete_CustomerCriteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerDeleteCustomerCriteriaMissingRequest.xml");
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals(CustomerApiHandler.MSG_UPDATE_MISSING_CRITERIA,
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_InvalidRequest() {
        String request = RMT2File.getFileContentsAsString("xml/InvalidRequest.xml");
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals("An invalid request message was encountered.  Please payload.", actualRepsonse
                .getReplyStatus().getMessage());
    }
}

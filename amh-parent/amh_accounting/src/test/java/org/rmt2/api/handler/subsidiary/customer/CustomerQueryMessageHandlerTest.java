package org.rmt2.api.handler.subsidiary.customer;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dao.transaction.XactDao;
import org.dao.transaction.XactDaoFactory;
import org.dto.CustomerDto;
import org.dto.CustomerXactHistoryDto;
import org.dto.XactDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.subsidiary.CustomerApi;
import org.modules.subsidiary.CustomerApiException;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiException;
import org.modules.transaction.XactApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handler.subsidiary.SubsidiaryMockData;
import org.rmt2.api.handlers.subsidiary.CustomerApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.CustomerActivityType;
import org.rmt2.jaxb.CustomerType;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
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
    CustomerApiHandler.class, SubsidiaryApiFactory.class, XactApiFactory.class, SystemConfigurator.class })
public class CustomerQueryMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    // private SubsidiaryApiFactory mockApiFactory;
    private CustomerApi mockApi;
    private XactApi mockXactApi;


    /**
     * 
     */
    public CustomerQueryMessageHandlerTest() {
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
        mockApi = Mockito.mock(CustomerApi.class);
        PowerMockito.mockStatic(SubsidiaryApiFactory.class);
        PowerMockito.when(SubsidiaryApiFactory.createCustomerApi(isA(String.class))).thenReturn(this.mockApi);
        doNothing().when(this.mockApi).close();
        
        
        XactDaoFactory mockXactDaoFactory = Mockito.mock(XactDaoFactory.class);
        XactDao mockDao = Mockito.mock(XactDao.class);
        mockXactApi = Mockito.mock(XactApi.class);
        PowerMockito.mockStatic(XactApiFactory.class);
        when(mockXactDaoFactory.createRmt2OrmXactDao(isA(String.class))).thenReturn(mockDao);
        PowerMockito.when(XactApiFactory.createDefaultXactApi()).thenReturn(this.mockXactApi);
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
    public void testSuccess_FetchCustomer() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerQueryRequest.xml");
        List<CustomerDto> mockListData = SubsidiaryMockData.createMockCustomers();

        try {
            when(this.mockApi.getExt(isA(CustomerDto.class))).thenReturn(mockListData);
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a customer");
        }
        
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getCustomers().getCustomer().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Customer record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getCustomers().getCustomer().size(); ndx++) {
            CustomerType a = actualRepsonse.getProfile().getCustomers().getCustomer().get(ndx);
            Assert.assertNotNull(a.getCustomerId());
            Assert.assertEquals(200 + ndx, a.getCustomerId().intValue());
            Assert.assertNotNull(a.getBusinessContactDetails());
            Assert.assertNotNull(a.getBusinessContactDetails().getBusinessId());
            Assert.assertEquals(1351 + ndx, a.getBusinessContactDetails().getBusinessId().intValue());
            Assert.assertEquals(333, a.getAcctId().intValue());
            Assert.assertNotNull(a.getAccountNo());
            Assert.assertEquals("C123458" + ndx, a.getAccountNo());
            Assert.assertNotNull(a.getAcctDescription());
            Assert.assertEquals("Customer " + (ndx + 1), a.getAcctDescription());
            
        }
    }
    
 
    @Test
    public void testSuccess_FetchCustomer_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerQueryRequest.xml");
        try {
            when(this.mockApi.getExt(isA(CustomerDto.class))).thenReturn(null);
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a customer");
        }
        
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Customer data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_FetchCustomer_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerQueryRequest.xml");
        try {
            when(this.mockApi.getExt(isA(CustomerDto.class)))
               .thenThrow(new CustomerApiException("Test validation error: selection criteria is required"));
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a Customre");
        }
        
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_GET, request);
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
        Assert.assertEquals("Failure to retrieve customer(s)", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test validation error: selection criteria is required",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testSuccess_FetchTransactionHistory() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerTranHistQueryRequest.xml");
        List<CustomerDto> mockCustData = SubsidiaryMockData.createMockCustomer();
        List<CustomerXactHistoryDto> mockListData = SubsidiaryMockData.createMockCustomerXactHistory();
        List<XactDto> mockXactDetailsData = SubsidiaryMockData.createMockCustomerXactHistoryDetails();

        try {
            when(this.mockApi.getExt(isA(CustomerDto.class))).thenReturn(mockCustData);
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a customer");
        }
        
        try {
            when(this.mockApi.getTransactionHistory(isA(Integer.class))).thenReturn(mockListData);
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a customer transaction history");
        }
        
        try {
            when(this.mockXactApi.getXactById(isA(Integer.class))).thenReturn(
                    mockXactDetailsData.get(0), mockXactDetailsData.get(1),
                    mockXactDetailsData.get(2), mockXactDetailsData.get(3),
                    mockXactDetailsData.get(4));
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a customer transaction history details");
        }
        
        
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_TRAN_HIST_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getCustomers().getCustomer().size());
        Assert.assertEquals(5, actualRepsonse.getProfile().getCustomers().getCustomer().get(0)
                .getTransactions().getTransaction().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Customer transaction history record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getCustomers().getCustomer().size(); ndx++) {
            CustomerType a = actualRepsonse.getProfile().getCustomers().getCustomer().get(ndx);
            Assert.assertNotNull(a.getCustomerId());
            Assert.assertEquals(100, a.getCustomerId().intValue());
            int ndx2 = 0;
            for (CustomerActivityType tran : a.getTransactions().getTransaction()) {
                Assert.assertEquals(1200 + ndx2++, tran.getXactId().intValue());
            }
            Assert.assertEquals(3000.00, a.getBalance().doubleValue(), 0);
        }
    }
    
 
    @Test
    public void testSuccess_FetchTransactionHistory_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerTranHistQueryRequest.xml");
        List<CustomerDto> mockCustData = SubsidiaryMockData.createMockCustomer();
        List<CustomerXactHistoryDto> mockListData = SubsidiaryMockData.createMockCustomerXactHistory();

        try {
            when(this.mockApi.getExt(isA(CustomerDto.class))).thenReturn(mockCustData);
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a customer");
        }
        
        try {
            when(this.mockApi.getTransactionHistory(isA(Integer.class))).thenReturn(null);
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a customer transaction history");
        }
        
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_TRAN_HIST_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Customer transaction history data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testSuccess_FetchTransactionHistory_TooManyCustomersFetched() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerTranHistQueryRequest.xml");
        List<CustomerDto> mockCustData = SubsidiaryMockData.createMockCustomers();

        try {
            when(this.mockApi.getExt(isA(CustomerDto.class))).thenReturn(mockCustData);
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a customer");
        }
        
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_TRAN_HIST_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Customer data not found or too many customers were fetched", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_FetchTransactionHistory_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerTranHistQueryRequest.xml");
        List<CustomerDto> mockCustData = SubsidiaryMockData.createMockCustomer();

        try {
            when(this.mockApi.getExt(isA(CustomerDto.class))).thenReturn(mockCustData);
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a customer");
        }
        try {
            when(this.mockApi.getTransactionHistory(isA(Integer.class)))
               .thenThrow(new CustomerApiException("Test validation error: selection criteria is required"));
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a customer transaction history");
        }
        
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_TRAN_HIST_GET, request);
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
        Assert.assertEquals("Failure to retrieve customer transaction history", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test validation error: selection criteria is required",
                actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerHandlerInvalidTransCodeRequest.xml");
        try {
            when(this.mockApi.getExt(isA(CustomerDto.class)))
               .thenThrow(new CustomerApiException("Test validation error: selection criteria is required"));
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a Customre");
        }
        
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage("INCORRECT_TRAN_CODE", request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(CustomerApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "INCORRECT_TRAN_CODE", actualRepsonse
                .getReplyStatus().getMessage());
    }
    

    @Test
    public void testValidation_Fetch_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerQueryCriteriaMissingRequest.xml");
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_TRAN_HIST_GET, request);
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
    public void testValidation_Fetch_CustomerCriteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/customer/CustomerQueryCustomerCriteriaMissingRequest.xml");
        MessageHandlerResults results = null;
        CustomerApiHandler handler = new CustomerApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_TRAN_HIST_GET, request);
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
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CUSTOMER_TRAN_HIST_GET, request);
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
        Assert.assertEquals(AbstractJaxbMessageHandler.ERROR_MSG_INVALID_TRANS, actualRepsonse
                .getReplyStatus().getMessage());
    }
}

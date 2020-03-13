package org.rmt2.api.handler.transaction.sales;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.BusinessContactDto;
import org.dto.ContactDto;
import org.dto.CustomerDto;
import org.dto.SalesInvoiceDto;
import org.dto.SalesOrderItemDto;
import org.dto.XactDto;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiException;
import org.modules.contacts.ContactsApiFactory;
import org.modules.subsidiary.CustomerApi;
import org.modules.subsidiary.CustomerApiException;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiException;
import org.modules.transaction.XactApiFactory;
import org.modules.transaction.sales.SalesApi;
import org.modules.transaction.sales.SalesApiException;
import org.modules.transaction.sales.SalesApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handler.transaction.common.CommonXactMockData;
import org.rmt2.api.handlers.transaction.receipts.CashReceiptsApiHandler;
import org.rmt2.api.handlers.transaction.sales.PrintCustomerSalesOrderApiHandler;
import org.rmt2.api.handlers.transaction.sales.SalesOrderHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.SalesOrderType;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2File;

/**
 * Tests the sales order print API message handler
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, PrintCustomerSalesOrderApiHandler.class,
        SalesApiFactory.class, SubsidiaryApiFactory.class, ContactsApiFactory.class, XactApiFactory.class,
        SystemConfigurator.class })
public class SalesOrderPrintMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    protected static final double TEST_ORDER_TOTAL = 300;
    protected static final int EXPECTED_REC_TOTAL = 1;
    protected static final int SALES_ORDER_ID = 1000;
    protected static final int CUSTOMER_CONTACT_ID = 1351;
    protected static final int MAIN_COMPANY_CONTACT_ID = 1343;

    private SalesApi mockSalesApi;
    private CustomerApi mockCustApi;
    private ContactsApi mockContactApi;
    private XactApi mockXactApi;

    /**
     * 
     */
    public SalesOrderPrintMessageHandlerTest() {
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
        mockSalesApi = Mockito.mock(SalesApi.class);
        mockCustApi = Mockito.mock(CustomerApi.class);
        mockContactApi = Mockito.mock(ContactsApi.class);
        mockXactApi = Mockito.mock(XactApi.class);
        PowerMockito.mockStatic(SalesApiFactory.class);
        PowerMockito.mockStatic(SubsidiaryApiFactory.class);
        PowerMockito.mockStatic(ContactsApiFactory.class);
        PowerMockito.mockStatic(XactApiFactory.class);
        PowerMockito.when(SalesApiFactory.createApi()).thenReturn(this.mockSalesApi);
        PowerMockito.when(SubsidiaryApiFactory.createCustomerApi()).thenReturn(this.mockCustApi);
        PowerMockito.when(ContactsApiFactory.createApi()).thenReturn(this.mockContactApi);
        PowerMockito.when(XactApiFactory.createDefaultXactApi()).thenReturn(this.mockXactApi);
        doNothing().when(this.mockSalesApi).close();

        // Setup System Properteis
        System.setProperty("SerialPath", "/temp/");
        System.setProperty("SerialDrive", "c:");
        System.setProperty("RptXsltPath", "reports");
        System.setProperty("CompContactId", "1343");
        System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
        String xsltTransformer = System.getProperty("javax.xml.transform.TransformerFactory");

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

    /**
     * Test the print method successfully
     */
    @Test
    public void testSuccess_Print() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderPrintRequest.xml");

        List<SalesInvoiceDto> mockSalesOrderDtoList = SalesOrderMockData.createMockSalesInvoice();
        List<SalesOrderItemDto> mockSalesOrderItems = SalesOrderMockData.createMockSalesOrderItems(SALES_ORDER_ID);
        List<CustomerDto> mockCustomerListData = SalesOrderMockData.createMockCustomer();
        mockCustomerListData.get(0).setCustomerId(SalesOrderMockData.CUSTOMER_ID);
        List<ContactDto> mockCustomerContactDtoList = SalesOrderMockData.createMockSingleBusinessContactDto();
        List<ContactDto> mockMainCompanyContactDtoList = SalesOrderMockData.createMockMainCompanyContactDto();
        List<XactDto> mockXactListData = CommonXactMockData.createMockSingleXact();
        mockXactListData.get(0).setXactAmount(TEST_ORDER_TOTAL);

        try {
            when(this.mockSalesApi.getInvoice(isA(SalesInvoiceDto.class))).thenReturn(mockSalesOrderDtoList);
        } catch (Exception e) {
            Assert.fail("Unable to setup mock stub for fetching sales order DTO list");
        }

        try {
            when(this.mockSalesApi.getLineItems(eq(SALES_ORDER_ID))).thenReturn(mockSalesOrderItems);
        } catch (Exception e) {
            Assert.fail("Unable to setup mock stub for fetching sales order item DTO list for Sales Order Id 1000");
        }

        try {
            when(this.mockCustApi.getExt(isA(CustomerDto.class))).thenReturn(mockCustomerListData);
        } catch (CustomerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a customer");
        }

        try {
            BusinessContactDto criteria = Rmt2AddressBookDtoFactory.getBusinessInstance(null);
            criteria.setContactId(CUSTOMER_CONTACT_ID);
            when(this.mockContactApi.getContact(eq(criteria))).thenReturn(mockCustomerContactDtoList);
            // when(this.mockContactApi.getContact(isA(BusinessContactDto.class))).thenReturn(mockCustomerContactDtoList);
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for fetching customer contact info");
        }

        try {
            BusinessContactDto criteria = Rmt2AddressBookDtoFactory.getBusinessInstance(null);
            criteria.setContactId(MAIN_COMPANY_CONTACT_ID);
            when(this.mockContactApi.getContact(eq(criteria))).thenReturn(mockMainCompanyContactDtoList);
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for fetching main company contact info");
        }

        try {
            when(this.mockXactApi.getXact(isA(XactDto.class))).thenReturn(mockXactListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a BASE transaction");
        }

        MessageHandlerResults results = null;
        PrintCustomerSalesOrderApiHandler handler = new PrintCustomerSalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(EXPECTED_REC_TOTAL, actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size(), 0);
        Assert.assertEquals(EXPECTED_REC_TOTAL, actualRepsonse.getReplyStatus().getRecordCount().intValue(), 0);
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String expectedMsg = "Sales order(s) printed successfully";
        Assert.assertEquals(expectedMsg, actualRepsonse.getReplyStatus().getMessage());

        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getSalesOrders());
        Assert.assertEquals(EXPECTED_REC_TOTAL, actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size(), 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size(); ndx++) {
            SalesOrderType a = actualRepsonse.getProfile().getSalesOrders().getSalesOrder().get(ndx);
            Assert.assertNotNull(a.getSalesOrderId());
            Assert.assertEquals(SALES_ORDER_ID, a.getSalesOrderId().intValue());
            Assert.assertNotNull(a.getCustomerId());
            Assert.assertEquals(SalesOrderMockData.CUSTOMER_ID, a.getCustomerId().intValue());
            Assert.assertEquals(TEST_ORDER_TOTAL, a.getOrderTotal().doubleValue(), 0);
            Assert.assertNull(a.getSalesOrderItems());
        }
    }

    @Test
    public void test_API_Error() {
        String request =
                RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderPrintRequest.xml");

        try {
            when(this.mockSalesApi.getInvoice(isA(SalesInvoiceDto.class)))
                    .thenThrow(new SalesApiException("A Sales order API test error occurred"));
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for sales order transaction");
        }

        MessageHandlerResults results = null;
        PrintCustomerSalesOrderApiHandler handler = new PrintCustomerSalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT, request);
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
        Assert.assertEquals(SalesOrderHandlerConst.MSG_PRINT_FAILURE, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("A Sales order API test error occurred", actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request =
                RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderPrintRequest.xml");

        MessageHandlerResults results = null;
        PrintCustomerSalesOrderApiHandler handler = new PrintCustomerSalesOrderApiHandler();
        try {
            results = handler.processMessage("INCORRECT_TRAN_CODE", request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload()
                .toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(CashReceiptsApiHandler.ERROR_MSG_TRANS_NOT_FOUND +
                "INCORRECT_TRAN_CODE", actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Criteria() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderPrintMissingGeneralCriteriaRequest.xml");

        MessageHandlerResults results = null;
        PrintCustomerSalesOrderApiHandler handler = new PrintCustomerSalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload()
                .toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_MISSING_GENERAL_CRITERIA, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Sales_Order_Criteria() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderPrintRequestMissingSalesCriteria.xml");

        MessageHandlerResults results = null;
        PrintCustomerSalesOrderApiHandler handler = new PrintCustomerSalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload()
                .toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_MISSING_SALESORDER_STRUCTURE, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Customer_Criteria1() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderPrintRequestMissingCustomerCriteria.xml");

        MessageHandlerResults results = null;
        PrintCustomerSalesOrderApiHandler handler = new PrintCustomerSalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload()
                .toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_MISSING_CUSTOMER_STRUCTURE, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Customer_Criteria2() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderPrintRequestMissingCustomerCriteria2.xml");

        MessageHandlerResults results = null;
        PrintCustomerSalesOrderApiHandler handler = new PrintCustomerSalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload()
                .toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_MISSING_CUSTOMER_STRUCTURE, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Transaction_Criteria() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderPrintRequestMissingTransactionCriteria.xml");

        MessageHandlerResults results = null;
        PrintCustomerSalesOrderApiHandler handler = new PrintCustomerSalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload()
                .toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_MISSING_XACT_STRUCTURE, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Transaction_Criteria2() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderPrintRequestMissingTransactionCriteria2.xml");

        MessageHandlerResults results = null;
        PrintCustomerSalesOrderApiHandler handler = new PrintCustomerSalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload()
                .toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_MISSING_XACT_STRUCTURE, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_SalesOrderId_Criteria() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderPrintRequestMissingSalesOrderId.xml");

        MessageHandlerResults results = null;
        PrintCustomerSalesOrderApiHandler handler = new PrintCustomerSalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload()
                .toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_MISSING_PRINT_PARAMETERS, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_CustomerId_Criteria() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderPrintRequestMissingCustomerId.xml");

        MessageHandlerResults results = null;
        PrintCustomerSalesOrderApiHandler handler = new PrintCustomerSalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload()
                .toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_MISSING_PRINT_PARAMETERS, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_XactId_Criteria() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderPrintRequestMissingXactId.xml");

        MessageHandlerResults results = null;
        PrintCustomerSalesOrderApiHandler handler = new PrintCustomerSalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_PRINT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload()
                .toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_MISSING_PRINT_PARAMETERS, actualRepsonse.getReplyStatus().getMessage());
    }
}

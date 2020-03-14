package org.rmt2.api.handler.transaction.sales;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.SalesInvoiceDto;
import org.dto.SalesOrderItemDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.transaction.sales.SalesApi;
import org.modules.transaction.sales.SalesApiException;
import org.modules.transaction.sales.SalesApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handlers.transaction.receipts.CashReceiptsApiHandler;
import org.rmt2.api.handlers.transaction.sales.QuerySalesOrderApiHandler;
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
 * Tests the sales order query API message handler
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, QuerySalesOrderApiHandler.class, SalesApiFactory.class,
        SystemConfigurator.class })
public class SalesOrderQueryMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    protected static final double TEST_ORDER_TOTAL = 300;
    protected static final int EXPECTED_REC_TOTAL = 5;

    private SalesApi mockApi;

    /**
     * 
     */
    public SalesOrderQueryMessageHandlerTest() {
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
        mockApi = Mockito.mock(SalesApi.class);
        PowerMockito.mockStatic(SalesApiFactory.class);
        PowerMockito.when(SalesApiFactory.createApi()).thenReturn(this.mockApi);
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

    /**
     * Test the ReceivePayment method successfully
     */
    @Test
    public void testSuccess_Query_Full() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderQueryFullRequest.xml");

        List<SalesInvoiceDto> mockSalesOrderDtoList = SalesOrderMockData.createMockSalesInvoices();
        List<SalesOrderItemDto> mockSalesOrderItems1000 = SalesOrderMockData.createMockSalesOrderItems(1000);
        List<SalesOrderItemDto> mockSalesOrderItems1001 = SalesOrderMockData.createMockSalesOrderItems(1001);
        List<SalesOrderItemDto> mockSalesOrderItems1002 = SalesOrderMockData.createMockSalesOrderItems(1002);
        List<SalesOrderItemDto> mockSalesOrderItems1003 = SalesOrderMockData.createMockSalesOrderItems(1003);
        List<SalesOrderItemDto> mockSalesOrderItems1004 = SalesOrderMockData.createMockSalesOrderItems(1004);

        try {
            when(this.mockApi.getInvoice(isA(SalesInvoiceDto.class))).thenReturn(mockSalesOrderDtoList);
        } catch (Exception e) {
            Assert.fail("Unable to setup mock stub for fetching sales order DTO list");
        }

        try {
            when(this.mockApi.getLineItems(eq(1000))).thenReturn(mockSalesOrderItems1000);
        } catch (Exception e) {
            Assert.fail("Unable to setup mock stub for fetching sales order item DTO list for Sales Order Id 1000");
        }

        try {
            when(this.mockApi.getLineItems(eq(1001))).thenReturn(mockSalesOrderItems1001);
        } catch (Exception e) {
            Assert.fail("Unable to setup mock stub for fetching sales order item DTO list for Sales Order Id 1001");
        }

        try {
            when(this.mockApi.getLineItems(eq(1002))).thenReturn(mockSalesOrderItems1002);
        } catch (Exception e) {
            Assert.fail("Unable to setup mock stub for fetching sales order item DTO list for Sales Order Id 1002");
        }

        try {
            when(this.mockApi.getLineItems(eq(1003))).thenReturn(mockSalesOrderItems1003);
        } catch (Exception e) {
            Assert.fail("Unable to setup mock stub for fetching sales order item DTO list for Sales Order Id 1003");
        }

        try {
            when(this.mockApi.getLineItems(eq(1004))).thenReturn(mockSalesOrderItems1004);
        } catch (Exception e) {
            Assert.fail("Unable to setup mock stub for fetching sales order item DTO list for Sales Order Id 1004");
        }


        MessageHandlerResults results = null;
        QuerySalesOrderApiHandler handler = new QuerySalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_GET, request);
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

        String expectedMsg = EXPECTED_REC_TOTAL + " sales order(s) were found";
        Assert.assertEquals(expectedMsg, actualRepsonse.getReplyStatus().getMessage());

        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getSalesOrders());
        Assert.assertEquals(EXPECTED_REC_TOTAL, actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size(), 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size(); ndx++) {
            SalesOrderType a = actualRepsonse.getProfile().getSalesOrders().getSalesOrder().get(ndx);
            Assert.assertNotNull(a.getSalesOrderId());
            Assert.assertEquals((1000 + ndx), a.getSalesOrderId().intValue());
            Assert.assertNotNull(a.getCustomerId());
            Assert.assertEquals(SalesOrderMockData.CUSTOMER_ID, a.getCustomerId().intValue());
            Assert.assertEquals((TEST_ORDER_TOTAL * (ndx + 1)), a.getOrderTotal().doubleValue(), 0);
            Assert.assertNotNull(a.getSalesOrderItems());
            Assert.assertNotNull(a.getSalesOrderItems().getSalesOrderItem());
            Assert.assertTrue(a.getSalesOrderItems().getSalesOrderItem().size() > 0);
        }
    }

    @Test
    public void testSuccess_Query_Header() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderQueryHeaderRequest.xml");

        List<SalesInvoiceDto> mockSalesOrderDtoList = SalesOrderMockData.createMockSalesInvoices();

        try {
            when(this.mockApi.getInvoice(isA(SalesInvoiceDto.class))).thenReturn(mockSalesOrderDtoList);
        } catch (Exception e) {
            Assert.fail("Unable to setup mock stub for fetching sales order DTO list");
        }

        MessageHandlerResults results = null;
        QuerySalesOrderApiHandler handler = new QuerySalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload()
                .toString());
        Assert.assertEquals(EXPECTED_REC_TOTAL, actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size(), 0);
        Assert.assertEquals(EXPECTED_REC_TOTAL, actualRepsonse.getReplyStatus().getRecordCount().intValue(), 0);
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String expectedMsg = EXPECTED_REC_TOTAL + " sales order(s) were found";
        Assert.assertEquals(expectedMsg, actualRepsonse.getReplyStatus().getMessage());

        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getSalesOrders());
        Assert.assertEquals(EXPECTED_REC_TOTAL, actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size(), 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size(); ndx++) {
            SalesOrderType a = actualRepsonse.getProfile().getSalesOrders().getSalesOrder().get(ndx);
            Assert.assertNotNull(a.getSalesOrderId());
            Assert.assertEquals((1000 + ndx), a.getSalesOrderId().intValue());
            Assert.assertNotNull(a.getCustomerId());
            Assert.assertEquals(SalesOrderMockData.CUSTOMER_ID, a.getCustomerId().intValue());
            Assert.assertEquals((TEST_ORDER_TOTAL * (ndx + 1)), a.getOrderTotal().doubleValue(), 0);
            Assert.assertNotNull(a.getSalesOrderItems());
            Assert.assertNotNull(a.getSalesOrderItems().getSalesOrderItem());
            Assert.assertEquals(0, a.getSalesOrderItems().getSalesOrderItem().size());
        }

    }

    @Test
    public void test_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderQueryFullRequest.xml");

        try {
            when(this.mockApi.getInvoice(isA(SalesInvoiceDto.class))).thenThrow(
                    new SalesApiException("A Sales order API test error occurred"));
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for sales order transaction");
        }

        MessageHandlerResults results = null;
        QuerySalesOrderApiHandler handler = new QuerySalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_CREATE_FAILURE, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("A Sales order API test error occurred", actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderQueryFullRequest.xml");

        MessageHandlerResults results = null;
        QuerySalesOrderApiHandler handler = new QuerySalesOrderApiHandler();
        try {
            results = handler.processMessage("INCORRECT_TRAN_CODE", request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(CashReceiptsApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "INCORRECT_TRAN_CODE", actualRepsonse.getReplyStatus().getMessage());
    }



    @Test
    public void testValidation_Missing_Criteria() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderQueryMissingCriteriaRequest.xml");

        MessageHandlerResults results = null;
        QuerySalesOrderApiHandler handler = new QuerySalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_MISSING_GENERAL_CRITERIA, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_TargetLevel() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderQueryMissingTargetLevelRequest.xml");

        MessageHandlerResults results = null;
        QuerySalesOrderApiHandler handler = new QuerySalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_MISSING_TARGET_LEVEL, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Unsupported_TargetLevel() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderQueryUnsupportedTargetLevelRequest.xml");

        MessageHandlerResults results = null;
        QuerySalesOrderApiHandler handler = new QuerySalesOrderApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());

        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(SalesOrderHandlerConst.MSG_TARGET_LEVEL_DETAILS_NOT_SUPPORTED, actualRepsonse.getReplyStatus()
                .getMessage());
    }
}

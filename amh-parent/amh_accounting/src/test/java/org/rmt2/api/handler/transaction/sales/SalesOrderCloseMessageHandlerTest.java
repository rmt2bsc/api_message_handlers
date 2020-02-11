package org.rmt2.api.handler.transaction.sales;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.XactDto;
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
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handlers.transaction.XactApiHandler;
import org.rmt2.api.handlers.transaction.sales.CloseSalesOrderWithPaymentApiHandler;
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
import com.api.util.RMT2String;

/**
 * Tests the closing a sales order with payment API message handler
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, CloseSalesOrderWithPaymentApiHandler.class,
        SalesApiFactory.class,
        SystemConfigurator.class })
public class SalesOrderCloseMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    protected static final double TEST_ORDER_TOTAL = 755.94;

    private SalesApi mockApi;

    /**
     * 
     */
    public SalesOrderCloseMessageHandlerTest() {
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
    public void testSuccess_create() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderCloseWithPaymentRequest.xml");

        try {
            when(this.mockApi.closeSalesOrderForPayment(isA(List.class), isA(XactDto.class))).thenReturn(
                    SalesOrderMockData.TOTAL_SALES_ORDERS_CLOSED);
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for closing a sales order");
        }

        MessageHandlerResults results = null;
        CloseSalesOrderWithPaymentApiHandler handler = new CloseSalesOrderWithPaymentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_CLOSE_WITH_PAYMENT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String expectedMsg = RMT2String.replace(SalesOrderHandlerConst.MSG_CLOSE_SUCCESS, String.valueOf(SalesOrderMockData.TOTAL_SALES_ORDERS_CLOSED), "%s");
        Assert.assertEquals(expectedMsg, actualRepsonse.getReplyStatus().getMessage());

        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getSalesOrders());
        Assert.assertTrue(actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size() > 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size(); ndx++) {
            SalesOrderType a = actualRepsonse.getProfile().getSalesOrders().getSalesOrder().get(ndx);
            Assert.assertNotNull(a.getSalesOrderId());
            Assert.assertEquals(SalesOrderMockData.NEW_SALES_ORDER_ID, a.getSalesOrderId().intValue());
        }
    }

    @Test
    public void test_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderCloseWithPaymentRequest.xml");

        try {
            when(this.mockApi.closeSalesOrderForPayment(isA(List.class), isA(XactDto.class))).thenThrow(
                    new SalesApiException("A Close sales order API test error occurred"));
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for sales order transaction");
        }

        MessageHandlerResults results = null;
        CloseSalesOrderWithPaymentApiHandler handler = new CloseSalesOrderWithPaymentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_CLOSE_WITH_PAYMENT, request);
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
        Assert.assertEquals("A Close sales order API test error occurred", actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderCloseWithPaymentRequest.xml");

        MessageHandlerResults results = null;
        CloseSalesOrderWithPaymentApiHandler handler = new CloseSalesOrderWithPaymentApiHandler();
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
        Assert.assertEquals(CloseSalesOrderWithPaymentApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "INCORRECT_TRAN_CODE",
                actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Profile() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderCloseWithPaymentMissingProfileRequest.xml");

        MessageHandlerResults results = null;
        CloseSalesOrderWithPaymentApiHandler handler = new CloseSalesOrderWithPaymentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_CLOSE_WITH_PAYMENT, request);
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
        Assert.assertEquals(ApiMessageHandlerConst.MSG_MISSING_PROFILE_DATA, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_SalesOrder_Structure() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderCloseWithPaymentMissingSalesOrderStructureRequest.xml");

        MessageHandlerResults results = null;
        CloseSalesOrderWithPaymentApiHandler handler = new CloseSalesOrderWithPaymentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_CLOSE_WITH_PAYMENT, request);
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
        Assert.assertEquals(SalesOrderHandlerConst.MSG_MISSING_SALESORDER_STRUCTURE, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_SalesOrder_List() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderCloseWithPaymentMissingSalesOrderListRequest.xml");

        MessageHandlerResults results = null;
        CloseSalesOrderWithPaymentApiHandler handler = new CloseSalesOrderWithPaymentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_CLOSE_WITH_PAYMENT, request);
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
        Assert.assertEquals(SalesOrderHandlerConst.MSG_SALESORDER_LIST_EMPTY, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Transaction() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderCloseWithPaymentMissingTransactionRequest.xml");

        MessageHandlerResults results = null;
        CloseSalesOrderWithPaymentApiHandler handler = new CloseSalesOrderWithPaymentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_CLOSE_WITH_PAYMENT, request);
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
        Assert.assertEquals(XactApiHandler.MSG_MISSING_TRANSACTION_SECTION, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Too_Many_Transactions() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/sales/SalesOrderCloseWithPaymentTooManyTransactionsRequest.xml");

        MessageHandlerResults results = null;
        CloseSalesOrderWithPaymentApiHandler handler = new CloseSalesOrderWithPaymentApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_CLOSE_WITH_PAYMENT, request);
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
        Assert.assertEquals(SalesOrderHandlerConst.MSG_SALESORDER_CLOSE_TOO_MANY_TRANSACTIONS, actualRepsonse.getReplyStatus()
                .getMessage());
    }
}

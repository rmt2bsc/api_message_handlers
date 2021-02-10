package org.rmt2.api.handler.transaction.sales;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dao.mapping.orm.rmt2.SalesOrderStatus;
import org.dao.mapping.orm.rmt2.SalesOrderStatusHist;
import org.dto.SalesInvoiceDto;
import org.dto.SalesOrderDto;
import org.dto.SalesOrderStatusDto;
import org.dto.SalesOrderStatusHistDto;
import org.dto.adapter.orm.transaction.sales.Rmt2SalesOrderDtoFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.transaction.sales.SalesApi;
import org.modules.transaction.sales.SalesApiConst;
import org.modules.transaction.sales.SalesApiException;
import org.modules.transaction.sales.SalesApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handlers.transaction.sales.SalesOrderHandlerConst;
import org.rmt2.api.handlers.transaction.sales.UpdateSalesOrderAutoInvoiceApiHandler;
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
 * Tests updating and invoicing the sales order as one transaction for the Sales
 * Order message handler
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, UpdateSalesOrderAutoInvoiceApiHandler.class, SalesApiFactory.class,
        SystemConfigurator.class })
public class SalesOrderUpdateAndInvoiceMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    protected static final double TEST_ORDER_TOTAL = 755.94;
    protected static final double TEST_INVOICE_ID = 7000;

    private SalesApi mockApi;

    /**
     * 
     */
    public SalesOrderUpdateAndInvoiceMessageHandlerTest() {
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
     * Test the sales order update and invoice method successfully
     */
    @Test
    public void testSuccess_update() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderUpdateAndInvoiceRequest.xml");

        SalesOrderStatusHist ormStatusHist = new SalesOrderStatusHist();
        ormStatusHist.setSoStatusId(SalesApiConst.STATUS_CODE_INVOICED);
        SalesOrderStatus ormStatus = new SalesOrderStatus();
        ormStatus.setSoStatusId(SalesApiConst.STATUS_CODE_INVOICED);
        ormStatus.setDescription("Invoice");
        SalesOrderStatusHistDto mockStatusHistDto = Rmt2SalesOrderDtoFactory.createSalesOrderStatusHistoryInstance(ormStatusHist);
        SalesOrderStatusDto mockStatusDto = Rmt2SalesOrderDtoFactory.createSalesOrderStatusInstance(ormStatus);

        try {
            when(this.mockApi.updateSalesOrder(isA(SalesOrderDto.class), isA(List.class))).thenReturn(1);
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for creating a sales order");
        }

        try {
            when(this.mockApi.invoiceSalesOrder(isA(SalesOrderDto.class), isA(List.class), eq(false))).thenReturn(SalesOrderMockData.NEW_INVOICE_ID);
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for invoicing a sales order");
        }

        try {
            when(this.mockApi.getCurrentStatus(isA(Integer.class))).thenReturn(mockStatusHistDto);
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for creating a sales order status history DTO object");
        }

        try {
            when(this.mockApi.getStatus(isA(Integer.class))).thenReturn(mockStatusDto);
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for creating a sales order status DTO object");
        }

        try {
            List<SalesInvoiceDto> dto = SalesOrderMockData.createMockSalesInvoice();
            dto.get(0).setSalesOrderId(SalesOrderMockData.NEW_XACT_ID);
            dto.get(0).setOrderTotal(TEST_ORDER_TOTAL);
            dto.get(0).setInvoiced(true);
            dto.get(0).setInvoiceNo(SalesOrderMockData.NEW_INVOICE_NO);
            dto.get(0).setSoStatusDescription("Invoice");
            dto.get(0).setSoStatusId(SalesOrderMockData.SALES_ORDER_STAT_INVOICES);
            when(this.mockApi.getInvoice(isA(Integer.class))).thenReturn(dto.get(0));
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for creating a sales invoice DTO object");
        }

        MessageHandlerResults results = null;
        UpdateSalesOrderAutoInvoiceApiHandler handler = new UpdateSalesOrderAutoInvoiceApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_UPDATE, request);
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
        Assert.assertEquals(SalesOrderHandlerConst.MSG_INVOICED_SUCCESS, actualRepsonse.getReplyStatus().getMessage());

        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getSalesOrders());
        Assert.assertTrue(actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size() > 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getSalesOrders().getSalesOrder().size(); ndx++) {
            SalesOrderType a = actualRepsonse.getProfile().getSalesOrders().getSalesOrder().get(ndx);
            Assert.assertNotNull(a.getSalesOrderId());
            Assert.assertEquals(SalesOrderMockData.NEW_XACT_ID, a.getSalesOrderId().intValue());
            Assert.assertEquals(TEST_ORDER_TOTAL, a.getOrderTotal().doubleValue(), 0);
            Assert.assertEquals("Invoice", a.getStatus().getDescription());
            Assert.assertEquals(TEST_ORDER_TOTAL, a.getOrderTotal().doubleValue(), 0);
            Assert.assertNotNull(a.getInvoiceDetails());
            Assert.assertNotNull(a.getInvoiceDetails().getInvoiceId());
            Assert.assertEquals(SalesOrderMockData.NEW_INVOICE_ID, a.getInvoiceDetails().getInvoiceId().intValue(), 0);
        }
    }

    @Test
    public void test_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderUpdateAndInvoiceRequest.xml");

        try {
            when(this.mockApi.updateSalesOrder(isA(SalesOrderDto.class), isA(List.class))).thenThrow(
                    new SalesApiException("A Sales order API test error occurred"));
        } catch (SalesApiException e) {
            Assert.fail("Unable to setup mock stub for sales order transaction");
        }

        MessageHandlerResults results = null;
        UpdateSalesOrderAutoInvoiceApiHandler handler = new UpdateSalesOrderAutoInvoiceApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_SALESORDER_INVOICE_UPDATE, request);
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
        Assert.assertEquals(SalesOrderHandlerConst.MSG_INVOICE_FAILURE, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("A Sales order API test error occurred", actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/sales/SalesOrderUpdateAndInvoiceRequest.xml");

        MessageHandlerResults results = null;
        UpdateSalesOrderAutoInvoiceApiHandler handler = new UpdateSalesOrderAutoInvoiceApiHandler();
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
        Assert.assertEquals(UpdateSalesOrderAutoInvoiceApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "INCORRECT_TRAN_CODE", actualRepsonse.getReplyStatus().getMessage());
    }
}

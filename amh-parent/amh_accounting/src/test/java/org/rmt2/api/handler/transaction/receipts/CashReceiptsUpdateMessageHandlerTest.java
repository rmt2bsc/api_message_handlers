package org.rmt2.api.handler.transaction.receipts;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.XactDto;
import org.dto.XactTypeDto;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.transaction.XactApiException;
import org.modules.transaction.XactConst;
import org.modules.transaction.receipts.CashReceiptApi;
import org.modules.transaction.receipts.CashReceiptApiException;
import org.modules.transaction.receipts.CashReceiptApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handler.HandlerCacheMockData;
import org.rmt2.api.handlers.transaction.receipts.CreateCashReceiptsApiHandler;
import org.rmt2.api.handlers.transaction.receipts.QueryCashReceiptsApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.XactType;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2File;
import com.api.util.RMT2String;

/**
 * Tests the creation and reversal of cash receipts API message handler
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, QueryCashReceiptsApiHandler.class,
        CreateCashReceiptsApiHandler.class, CashReceiptApiFactory.class,
        SystemConfigurator.class })
public class CashReceiptsUpdateMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private CashReceiptApi mockApi;

    /**
     * 
     */
    public CashReceiptsUpdateMessageHandlerTest() {
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
        mockApi = Mockito.mock(CashReceiptApi.class);
        PowerMockito.mockStatic(CashReceiptApiFactory.class);
        PowerMockito.when(CashReceiptApiFactory.createApi()).thenReturn(this.mockApi);
        doNothing().when(this.mockApi).close();

        List<XactTypeDto> mockXactTypeListData = HandlerCacheMockData.createMockXactTypes();
        try {
            when(this.mockApi.getXactTypes(null)).thenReturn(mockXactTypeListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a cash receipt treansaction type data");
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

    /**
     * Test the ReceivePayment method successfully
     */
    @Test
    public void testSuccess_ReceivePayment() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/receipts/CashReceiptCreateRequest.xml");

        try {
            when(this.mockApi.receivePayment(isA(XactDto.class), isA(Integer.class))).thenReturn(CashReceiptsMockData.NEW_XACT_ID);
        } catch (CashReceiptApiException e) {
            Assert.fail("Unable to setup mock stub for creating a cash receipt transactions");
        }

        XactDto mockNewXactDto = Rmt2XactDtoFactory.createXactBaseInstance(null);
        mockNewXactDto.setXactId(CashReceiptsMockData.NEW_XACT_ID);
        mockNewXactDto.setXactTypeId(XactConst.XACT_TYPE_CASHRECEIPT);
        mockNewXactDto.setXactAmount(100.00);
        try {
            when(this.mockApi.getXactById(eq(CashReceiptsMockData.NEW_XACT_ID))).thenReturn(mockNewXactDto);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for creating a cash receipt transactions");
        }

        MessageHandlerResults results = null;
        CreateCashReceiptsApiHandler handler = new CreateCashReceiptsApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHRECEIPT_CREATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getTransactions().getTransaction().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String expectedMsg = RMT2String.replace(CreateCashReceiptsApiHandler.MSG_CREATE_SUCCESS,
                String.valueOf(CashReceiptsMockData.NEW_XACT_ID), "%s");
        Assert.assertEquals(expectedMsg, actualRepsonse.getReplyStatus().getMessage());

        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTransactions());
        Assert.assertTrue(actualRepsonse.getProfile().getTransactions().getTransaction().size() > 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTransactions().getTransaction().size(); ndx++) {
            XactType a = actualRepsonse.getProfile().getTransactions().getTransaction().get(ndx);
            Assert.assertNotNull(a.getXactId());
            Assert.assertEquals(CashReceiptsMockData.NEW_XACT_ID, a.getXactId().intValue());
            Assert.assertNotNull(a.getXactType().getXactTypeId());
            Assert.assertEquals(XactConst.XACT_TYPE_CASHRECEIPT, a.getXactType().getXactTypeId().intValue());
            Assert.assertEquals(100.00, a.getXactAmount().doubleValue(), 0);
        }
    }

    /**
     * Test the ReceivePayment reverse method successfully
     */
    @Test
    public void testSuccess_Reverse() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/receipts/CashReceiptReversalRequest.xml");

        try {
            when(this.mockApi.receivePayment(isA(XactDto.class), isA(Integer.class))).thenReturn(CashReceiptsMockData.NEW_REVERSE_XACT_ID);
        } catch (CashReceiptApiException e) {
            Assert.fail("Unable to setup mock stub for creating a cash receipt transactions");
        }

        XactDto mockNewXactDto = Rmt2XactDtoFactory.createXactBaseInstance(null);
        mockNewXactDto.setXactId(CashReceiptsMockData.NEW_XACT_ID);
        mockNewXactDto.setXactTypeId(XactConst.XACT_TYPE_CASHRECEIPT);
        mockNewXactDto.setXactAmount(100.00);
        try {
            when(this.mockApi.getXactById(eq(CashReceiptsMockData.NEW_XACT_ID))).thenReturn(mockNewXactDto);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for creating a cash receipt transactions");
        }

        MessageHandlerResults results = null;
        CreateCashReceiptsApiHandler handler = new CreateCashReceiptsApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHRECEIPT_REVERSE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(0, actualRepsonse.getProfile().getTransactions().getTransaction().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String expectedMsg = RMT2String.replace(CreateCashReceiptsApiHandler.MSG_REVERSE_SUCCESS,
                String.valueOf(CashReceiptsMockData.NEW_REVERSE_XACT_ID), "%s");
        Assert.assertEquals(expectedMsg, actualRepsonse.getReplyStatus().getMessage());

        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTransactions());
        Assert.assertTrue(actualRepsonse.getProfile().getTransactions().getTransaction().size() < 1);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTransactions().getTransaction().size(); ndx++) {
            XactType a = actualRepsonse.getProfile().getTransactions().getTransaction().get(ndx);
            Assert.assertNotNull(a.getXactId());
            Assert.assertEquals(CashReceiptsMockData.NEW_REVERSE_XACT_ID, a.getXactId().intValue());
            Assert.assertNotNull(a.getXactType().getXactTypeId());
            Assert.assertEquals(XactConst.XACT_TYPE_CASHRECEIPT, a.getXactType().getXactTypeId().intValue());
            Assert.assertEquals(100.00, a.getXactAmount().doubleValue(), 0);
        }
    }
    
    @Test
    public void testError_ReceivePayment_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/receipts/CashReceiptCreateRequest.xml");
        List<XactDto> mockListData = CashReceiptsMockData.createMockSingleTransaction();

        try {
            when(this.mockApi.receivePayment(isA(XactDto.class), isA(Integer.class))).thenThrow(
                    new CashReceiptApiException("An Xact API test error occurred"));
        } catch (CashReceiptApiException e) {
            Assert.fail("Unable to setup mock stub for receive cash payment transaction");
        }

        MessageHandlerResults results = null;
        CreateCashReceiptsApiHandler handler = new CreateCashReceiptsApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHRECEIPT_CREATE, request);
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
        Assert.assertEquals(CreateCashReceiptsApiHandler.MSG_FAILURE, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("An Xact API test error occurred", actualRepsonse.getReplyStatus().getExtMessage());
    }

}

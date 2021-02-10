package org.rmt2.api.handler.transaction.disbursements;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.XactCustomCriteriaDto;
import org.dto.XactDto;
import org.dto.XactTypeDto;
import org.dto.XactTypeItemActivityDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.transaction.XactApiException;
import org.modules.transaction.disbursements.DisbursementsApi;
import org.modules.transaction.disbursements.DisbursementsApiException;
import org.modules.transaction.disbursements.DisbursementsApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handler.HandlerCacheMockData;
import org.rmt2.api.handlers.transaction.XactApiHandler;
import org.rmt2.api.handlers.transaction.cashdisbursement.QueryDisbursementApiHandler;
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

/**
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class,
        QueryDisbursementApiHandler.class, DisbursementsApiFactory.class, SystemConfigurator.class })
public class CashDisbursementQueryMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private DisbursementsApi mockApi;

    /**
     * 
     */
    public CashDisbursementQueryMessageHandlerTest() {
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
        mockApi = Mockito.mock(DisbursementsApi.class);
        PowerMockito.mockStatic(DisbursementsApiFactory.class);
        PowerMockito.when(DisbursementsApiFactory.createApi()).thenReturn(this.mockApi);
        doNothing().when(this.mockApi).close();
        
        List<XactTypeDto> mockXactTypeListData = HandlerCacheMockData.createMockXactTypes();
        try {
            when(this.mockApi.getXactTypes(null)).thenReturn(mockXactTypeListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a treansaction type data");
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
    public void testSuccess_Fetch_BasicCriteria_Header() {
        String request = RMT2File.getFileContentsAsString(
                "xml/transaction/disbursements/cash/CashDisbursementBasicQueryRequestHeader.xml");
        List<XactDto> mockListData = CashDisbursementMockData.createMockSingleTransactions();

        try {
            when(this.mockApi.get(isA(XactDto.class), eq((XactCustomCriteriaDto) null))).thenReturn(mockListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a cash disbursement transaction");
        }
        
        MessageHandlerResults results = null;
        QueryDisbursementApiHandler handler = new QueryDisbursementApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getTransactions().getTransaction().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Cash disbursement record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTransactions());
        Assert.assertTrue(actualRepsonse.getProfile().getTransactions().getTransaction().size() > 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTransactions().getTransaction().size(); ndx++) {
            XactType a = actualRepsonse.getProfile().getTransactions().getTransaction().get(ndx);
            Assert.assertNotNull(a.getXactId());
            Assert.assertEquals(111111, a.getXactId().intValue());
        }
    }
    
    @Test
    public void testSuccess_Fetch_BasicCriteria_Full() {
        String request = RMT2File.getFileContentsAsString(
                "xml/transaction/disbursements/cash/CashDisbursementBasicQueryRequestFull.xml");
        List<XactDto> mockListData = CashDisbursementMockData.createMockSingleTransactions();
        List<XactTypeItemActivityDto> mockItemListData = CashDisbursementMockData.createMockXactItems();

        try {
            when(this.mockApi.get(isA(XactDto.class), eq((XactCustomCriteriaDto) null))).thenReturn(mockListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a cash disbursement transaction");
        }
        
        try {
            when(this.mockApi.getItems(isA(XactTypeItemActivityDto.class), eq((XactCustomCriteriaDto) null))).thenReturn(mockItemListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching cash disbursement transaction line items");
        }
        
        MessageHandlerResults results = null;
        QueryDisbursementApiHandler handler = new QueryDisbursementApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getTransactions().getTransaction().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Cash disbursement record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTransactions());
        Assert.assertTrue(actualRepsonse.getProfile().getTransactions().getTransaction().size() > 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTransactions().getTransaction().size(); ndx++) {
            XactType a = actualRepsonse.getProfile().getTransactions().getTransaction().get(ndx);
            Assert.assertNotNull(a.getXactId());
            Assert.assertEquals(111111, a.getXactId().intValue());
            Assert.assertNotNull(a.getLineitems());
            Assert.assertNotNull(a.getLineitems().getLineitem());
            Assert.assertTrue(a.getLineitems().getLineitem().size() > 0);
        }
    }
    
    @Test
    public void testSuccess_Fetch_CustomCriteria_Header() {
        String request = RMT2File.getFileContentsAsString(
                "xml/transaction/disbursements/cash/CashDisbursementCustomQueryRequestHeader.xml");
        List<XactDto> mockListData = CashDisbursementMockData.createMockSingleTransactions();

        try {
            when(this.mockApi.get(isA(XactDto.class), isA(XactCustomCriteriaDto.class))).thenReturn(mockListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a cash disbursement transaction");
        }
        
        MessageHandlerResults results = null;
        QueryDisbursementApiHandler handler = new QueryDisbursementApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getTransactions().getTransaction().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Cash disbursement record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTransactions());
        Assert.assertTrue(actualRepsonse.getProfile().getTransactions().getTransaction().size() > 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTransactions().getTransaction().size(); ndx++) {
            XactType a = actualRepsonse.getProfile().getTransactions().getTransaction().get(ndx);
            Assert.assertNotNull(a.getXactId());
            Assert.assertEquals(111111, a.getXactId().intValue());
        }
    }
    
    @Test
    public void testSuccess_Fetch_CustomCriteria_Full() {
        String request = RMT2File.getFileContentsAsString(
                "xml/transaction/disbursements/cash/CashDisbursementCustomQueryRequestHeader.xml");
        List<XactDto> mockListData = CashDisbursementMockData.createMockSingleTransactions();
        List<XactTypeItemActivityDto> mockItemListData = CashDisbursementMockData.createMockXactItems();

        try {
            when(this.mockApi.get(isA(XactDto.class), isA(XactCustomCriteriaDto.class))).thenReturn(mockListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a cash disbursement transaction");
        }
        
        try {
            when(this.mockApi.getItems(isA(XactTypeItemActivityDto.class), isA(XactCustomCriteriaDto.class))).thenReturn(mockItemListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching cash disbursement transaction line items");
        }
        
        MessageHandlerResults results = null;
        QueryDisbursementApiHandler handler = new QueryDisbursementApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getTransactions().getTransaction().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Cash disbursement record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTransactions());
        Assert.assertTrue(actualRepsonse.getProfile().getTransactions().getTransaction().size() > 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTransactions().getTransaction().size(); ndx++) {
            XactType a = actualRepsonse.getProfile().getTransactions().getTransaction().get(ndx);
            Assert.assertNotNull(a.getXactId());
            Assert.assertEquals(111111, a.getXactId().intValue());
            Assert.assertNotNull(a.getLineitems());
            Assert.assertNotNull(a.getLineitems().getLineitem());
            Assert.assertTrue(a.getLineitems().getLineitem().size() > 0);
        }
    }
    
 
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString(
                "xml/transaction/disbursements/cash/CashDisbursementBasicQueryRequestHeader.xml");
        List<XactDto> mockListData = CashDisbursementMockData.createMockSingleTransactions();

        try {
            when(this.mockApi.get(isA(XactDto.class), eq((XactCustomCriteriaDto) null))).thenReturn(null);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a cash disbursement transaction");
        }
        
        MessageHandlerResults results = null;
        QueryDisbursementApiHandler handler = new QueryDisbursementApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_GET, request);
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
        Assert.assertEquals(QueryDisbursementApiHandler.MSG_DATA_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_Details_TargetLevel_Error() {
        String request = RMT2File.getFileContentsAsString(
                "xml/transaction/disbursements/cash/CashDisbursementBasicQueryRequestDetails.xml");
        
        MessageHandlerResults results = null;
        QueryDisbursementApiHandler handler = new QueryDisbursementApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_GET, request);
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
        Assert.assertEquals(XactApiHandler.MSG_DETAILS_NOT_SUPPORTED, actualRepsonse.getReplyStatus().getMessage());
    }
    

    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString(
                "xml/transaction/disbursements/cash/CashDisbursementBasicQueryRequestHeader.xml");
        List<XactDto> mockListData = CashDisbursementMockData.createMockSingleTransactions();

        try {
            when(this.mockApi.get(isA(XactDto.class), eq((XactCustomCriteriaDto) null)))
                 .thenThrow(new DisbursementsApiException("An Xact API test error occurred"));
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching cash disbursement transaction");
        }
        
        MessageHandlerResults results = null;
        QueryDisbursementApiHandler handler = new QueryDisbursementApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_GET, request);
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
        Assert.assertEquals(QueryDisbursementApiHandler.MSG_FAILURE, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("An Xact API test error occurred",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString(
                "xml/transaction/common/TransactionQueryInvalidTranCodeRequest.xml");
        
        MessageHandlerResults results = null;
        QueryDisbursementApiHandler handler = new QueryDisbursementApiHandler();
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
        Assert.assertEquals(QueryDisbursementApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "INCORRECT_TRAN_CODE", actualRepsonse
                .getReplyStatus().getMessage());
    }

}

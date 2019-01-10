package org.rmt2.api.handler.transaction.purchases;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.XactCreditChargeDto;
import org.dto.XactCustomCriteriaDto;
import org.dto.XactTypeDto;
import org.dto.XactTypeItemActivityDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.transaction.XactApiException;
import org.modules.transaction.purchases.creditor.CreditorPurchasesApi;
import org.modules.transaction.purchases.creditor.CreditorPurchasesApiException;
import org.modules.transaction.purchases.creditor.CreditorPurchasesApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handler.HandlerCacheMockData;
import org.rmt2.api.handlers.transaction.XactApiHandler;
import org.rmt2.api.handlers.transaction.purchases.CreditorPurchasesApiHandler;
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
    CreditorPurchasesApiHandler.class, CreditorPurchasesApiFactory.class, SystemConfigurator.class })
public class CreditorPurchasesQueryMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private CreditorPurchasesApi mockApi;

    /**
     * 
     */
    public CreditorPurchasesQueryMessageHandlerTest() {
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
        mockApi = Mockito.mock(CreditorPurchasesApi.class);
        PowerMockito.mockStatic(CreditorPurchasesApiFactory.class);
        PowerMockito.when(CreditorPurchasesApiFactory.createApi()).thenReturn(this.mockApi);
        doNothing().when(this.mockApi).close();
        
        List<XactTypeDto> mockXactTypeListData = HandlerCacheMockData.createMockXactTypes();
        try {
            when(this.mockApi.getXactTypes(null)).thenReturn(mockXactTypeListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor purchase treansaction type data");
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
    public void testSuccess_Fetch_BasicCriteria_Full() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/purchases/CreditorPurchasesBasicQueryRequestFull.xml");
        List<XactCreditChargeDto> mockListData = CreditorPurchasesMockData.createMockCreditPurchaseHeader();
        List<XactTypeItemActivityDto> mockItemListData = CreditorPurchasesMockData.createMockCreditPurchaseDetails();

        try {
            when(this.mockApi.get(isA(XactCreditChargeDto.class), eq((XactCustomCriteriaDto) null))).thenReturn(mockListData);
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor purchases transaction");
        }
        
        try {
            when(this.mockApi.getItems(isA(Integer.class))).thenReturn(mockItemListData);
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for fetching creditor purchases transaction line items");
        }
        
        MessageHandlerResults results = null;
        CreditorPurchasesApiHandler handler = new CreditorPurchasesApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_GET, request);
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
        Assert.assertEquals(CreditorPurchasesApiHandler.MSG_DATA_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTransactions());
        Assert.assertTrue(actualRepsonse.getProfile().getTransactions().getTransaction().size() > 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTransactions().getTransaction().size(); ndx++) {
            XactType a = actualRepsonse.getProfile().getTransactions().getTransaction().get(ndx);
            Assert.assertNotNull(a.getXactId());
            Assert.assertEquals(CreditorPurchasesMockData.NEW_XACT_ID, a.getXactId().intValue());
            Assert.assertNotNull(a.getLineitems());
            Assert.assertNotNull(a.getLineitems().getLineitem());
            Assert.assertTrue(a.getLineitems().getLineitem().size() > 0);
        }
    }
    
    @Test
    public void testSuccess_Fetch_CustomCriteria_Full() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/purchases/CreditorPurchasesCustomCriteriaQueryRequestFull.xml");
        List<XactCreditChargeDto> mockListData = CreditorPurchasesMockData.createMockCreditPurchaseHeader();
        List<XactTypeItemActivityDto> mockItemListData = CreditorPurchasesMockData.createMockCreditPurchaseDetails();

        try {
            when(this.mockApi.get(isA(XactCreditChargeDto.class), isA(XactCustomCriteriaDto.class)))
                     .thenReturn(mockListData);
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor purchases transaction");
        }
        
        try {
            when(this.mockApi.getItems(isA(Integer.class))).thenReturn(mockItemListData);
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for fetching creditor purchases transaction line items");
        }
        
        MessageHandlerResults results = null;
        CreditorPurchasesApiHandler handler = new CreditorPurchasesApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_GET, request);
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
        Assert.assertEquals(CreditorPurchasesApiHandler.MSG_DATA_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTransactions());
        Assert.assertTrue(actualRepsonse.getProfile().getTransactions().getTransaction().size() > 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTransactions().getTransaction().size(); ndx++) {
            XactType a = actualRepsonse.getProfile().getTransactions().getTransaction().get(ndx);
            Assert.assertNotNull(a.getXactId());
            Assert.assertEquals(CreditorPurchasesMockData.NEW_XACT_ID, a.getXactId().intValue());
            Assert.assertNotNull(a.getLineitems());
            Assert.assertNotNull(a.getLineitems().getLineitem());
            Assert.assertTrue(a.getLineitems().getLineitem().size() > 0);
        }
    }
    
 
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/purchases/CreditorPurchasesCustomCriteriaQueryRequestFull.xml");

        try {
            when(this.mockApi.get(isA(XactCreditChargeDto.class), isA(XactCustomCriteriaDto.class)))
                     .thenReturn(null);
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor purchases transaction");
        }
        
        MessageHandlerResults results = null;
        CreditorPurchasesApiHandler handler = new CreditorPurchasesApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_GET, request);
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
        Assert.assertEquals(CreditorPurchasesApiHandler.MSG_DATA_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());
    }
        
    @Test
    public void testSuccess_Fetch_BasicCriteria_Header() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/purchases/CreditorPurchasesBasicQueryRequestHeader.xml");
        List<XactCreditChargeDto> mockListData = CreditorPurchasesMockData.createMockCreditPurchaseHeader();

        try {
            when(this.mockApi.get(isA(XactCreditChargeDto.class), eq((XactCustomCriteriaDto) null)))
                             .thenReturn(mockListData);
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor purchases transaction");
        }
        
        MessageHandlerResults results = null;
        CreditorPurchasesApiHandler handler = new CreditorPurchasesApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_GET, request);
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
        Assert.assertEquals(CreditorPurchasesApiHandler.MSG_DATA_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTransactions());
        Assert.assertTrue(actualRepsonse.getProfile().getTransactions().getTransaction().size() > 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTransactions().getTransaction().size(); ndx++) {
            XactType a = actualRepsonse.getProfile().getTransactions().getTransaction().get(ndx);
            Assert.assertNotNull(a.getXactId());
            Assert.assertEquals(CreditorPurchasesMockData.NEW_XACT_ID, a.getXactId().intValue());
            Assert.assertNull(a.getLineitems());
        }
    }
    
    @Test
    public void testSuccess_Fetch_CustomCriteria_Header() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/purchases/CreditorPurchasesCustomQueryRequestHeader.xml");
        List<XactCreditChargeDto> mockListData = CreditorPurchasesMockData.createMockCreditPurchaseHeader();

        try {
            when(this.mockApi.get(isA(XactCreditChargeDto.class), isA(XactCustomCriteriaDto.class)))
                    .thenReturn(mockListData);
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor purchases transaction");
        }
        
        MessageHandlerResults results = null;
        CreditorPurchasesApiHandler handler = new CreditorPurchasesApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_GET, request);
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
        Assert.assertEquals(CreditorPurchasesApiHandler.MSG_DATA_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTransactions());
        Assert.assertTrue(actualRepsonse.getProfile().getTransactions().getTransaction().size() > 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTransactions().getTransaction().size(); ndx++) {
            XactType a = actualRepsonse.getProfile().getTransactions().getTransaction().get(ndx);
            Assert.assertNotNull(a.getXactId());
            Assert.assertEquals(CreditorPurchasesMockData.NEW_XACT_ID, a.getXactId().intValue());
            Assert.assertNull(a.getLineitems());
        }
    }
    
    

    @Test
    public void testError_Fetch_Details_TargetLevel_Error() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/purchases/CreditorPurchasesBasicQueryRequestDetails.xml");
                
        MessageHandlerResults results = null;
        CreditorPurchasesApiHandler handler = new CreditorPurchasesApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_GET, request);
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
        String request = RMT2File.getFileContentsAsString("xml/transaction/purchases/CreditorPurchasesCustomQueryRequestHeader.xml");

        try {
            when(this.mockApi.get(isA(XactCreditChargeDto.class), isA(XactCustomCriteriaDto.class)))
                 .thenThrow(new CreditorPurchasesApiException("An Xact API test error occurred"));
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for fetching cash disbursement transaction");
        }
        
        MessageHandlerResults results = null;
        CreditorPurchasesApiHandler handler = new CreditorPurchasesApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_GET, request);
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
        Assert.assertEquals(CreditorPurchasesApiHandler.MSG_FAILURE, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("An Xact API test error occurred",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionQueryInvalidTranCodeRequest.xml");
        
        MessageHandlerResults results = null;
        CreditorPurchasesApiHandler handler = new CreditorPurchasesApiHandler();
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
        Assert.assertEquals(CreditorPurchasesApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "INCORRECT_TRAN_CODE", actualRepsonse
                .getReplyStatus().getMessage());
    }

}

package org.rmt2.api.handler.transaction.common;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dao.transaction.XactDao;
import org.dao.transaction.XactDaoFactory;
import org.dto.XactDto;
import org.dto.XactTypeDto;
import org.dto.XactTypeItemActivityDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiException;
import org.modules.transaction.XactApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handler.HandlerCacheMockData;
import org.rmt2.api.handlers.transaction.XactApiHandler;
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
    XactApiHandler.class, XactApiFactory.class, SystemConfigurator.class })
public class XactQueryMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private XactApi mockApi;

    /**
     * 
     */
    public XactQueryMessageHandlerTest() {
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
        XactDaoFactory mockXactDaoFactory = Mockito.mock(XactDaoFactory.class);
        XactDao mockDao = Mockito.mock(XactDao.class);
        mockApi = Mockito.mock(XactApi.class);
        PowerMockito.mockStatic(XactApiFactory.class);
        when(mockXactDaoFactory.createRmt2OrmXactDao(isA(String.class))).thenReturn(mockDao);
        PowerMockito.when(XactApiFactory.createDefaultXactApi()).thenReturn(this.mockApi);
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
    public void testSuccess_Fetch_Header() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionQueryRequestHeader.xml");
        List<XactDto> mockListData = CommonXactMockData.createMockSingleCommonTransactions();

        try {
            when(this.mockApi.getXact(isA(XactDto.class))).thenReturn(mockListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a BASE transaction");
        }
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_GET, request);
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
        Assert.assertEquals("Transaction record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
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
    public void testSuccess_Fetch_Full() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionQueryRequestFull.xml");
        List<XactDto> mockListData = CommonXactMockData.createMockSingleCommonTransactions();
        List<XactTypeItemActivityDto> mockItemListData = CommonXactMockData.createMockXactItems();

        try {
            when(this.mockApi.getXact(isA(XactDto.class))).thenReturn(mockListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a BASE transaction");
        }
        
        try {
            when(this.mockApi.getXactTypeItemActivityExt(isA(Integer.class))).thenReturn(mockItemListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a transaction line items");
        }
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_GET, request);
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
        Assert.assertEquals("Transaction record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
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
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionQueryRequestFull.xml");

        try {
            when(this.mockApi.getXact(isA(XactDto.class))).thenReturn(null);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a BASE transaction");
        }
        
        try {
            when(this.mockApi.getXactTypeItemActivityExt(isA(Integer.class))).thenReturn(null);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a transaction line items");
        }
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_GET, request);
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
        Assert.assertEquals("Transaction data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_FetchCustomer_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionQueryRequestFull.xml");

        try {
            when(this.mockApi.getXact(isA(XactDto.class)))
                 .thenThrow(new XactApiException("An Xact API test error occurred"));
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a transaction groups");
        }
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_GET, request);
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
        Assert.assertEquals("Failure to retrieve Transaction(s)", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("An Xact API test error occurred",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionQueryInvalidTranCodeRequest.xml");
        try {
            when(this.mockApi.getXact(isA(XactDto.class)))
               .thenThrow(new XactApiException("Test validation error: selection criteria is required"));
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a transaction");
        }
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
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
        Assert.assertEquals(XactApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "INCORRECT_TRAN_CODE", actualRepsonse
                .getReplyStatus().getMessage());
    }
    

    @Test
    public void testValidation_Fetch_General_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionQueryMissingCriteriaRequest.xml");
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_GET, request);
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
        Assert.assertEquals(XactApiHandler.MSG_MISSING_GENERAL_CRITERIA,
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_Fetch_Subject_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionQueryMissingCriteriaRequest2.xml");
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_GET, request);
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
        Assert.assertEquals(XactApiHandler.MSG_MISSING_SUBJECT_CRITERIA,
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_Fetch_TargetLevel_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionQueryMissingTargetLevelRequest.xml");
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_GET, request);
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
        Assert.assertEquals(XactApiHandler.MSG_MISSING_TARGET_LEVEL,
                actualRepsonse.getReplyStatus().getMessage());
    }
}

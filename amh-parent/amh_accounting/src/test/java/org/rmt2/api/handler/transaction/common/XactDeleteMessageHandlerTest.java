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
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handler.HandlerCacheMockData;
import org.rmt2.api.handlers.transaction.XactApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionRequest;
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
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class,
    XactApiHandler.class, XactApiFactory.class, SystemConfigurator.class })
public class XactDeleteMessageHandlerTest extends BaseAccountingMessageHandlerTest {

	private static final int NOT_FOUND_COUNT = 0;
	private static final String API_ERROR = "An Xact API test error occurred";
    private XactApi mockApi;

    /**
     * 
     */
    public XactDeleteMessageHandlerTest() {
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
    public void testSuccess_Delete() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionDeleteRequest.xml");
        AccountingTransactionRequest reqObj =  (AccountingTransactionRequest) this.jaxb.unMarshalMessage(request);
        List<Integer> xactIdList =  reqObj.getCriteria().getXactCriteria().getBasicCriteria().getXactIdList().getXactId();
        
        try {
            when(this.mockApi.deleteXact(isA(List.class))).thenReturn(xactIdList.size());
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for deleting accounting transactions");
        }
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(xactIdList.size(), actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(XactApiHandler.MSG_DELETE_SUCCESS, actualRepsonse.getReplyStatus().getMessage());
        
        // Verify that all xact id's in the request exists in the response's ExtMessage element
        for (Integer xactId : xactIdList) {
        	String item = String.valueOf(xactId);
        	Assert.assertTrue(actualRepsonse.getReplyStatus().getExtMessage().contains(item));
        }
    }
    
    @Test
    public void testSuccess_DeleteTransactionsNotFound() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionDeleteRequest.xml");
        AccountingTransactionRequest reqObj =  (AccountingTransactionRequest) this.jaxb.unMarshalMessage(request);
        List<Integer> xactIdList =  reqObj.getCriteria().getXactCriteria().getBasicCriteria().getXactIdList().getXactId();
        
        try {
            when(this.mockApi.deleteXact(isA(List.class))).thenReturn(NOT_FOUND_COUNT);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for deleting accounting transactions not found");
        }
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(NOT_FOUND_COUNT, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(XactApiHandler.MSG_DELETE_XACT_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        // Verify that all xact id's in the request exists in the response's ExtMessage element
        for (Integer xactId : xactIdList) {
        	String item = String.valueOf(xactId);
        	Assert.assertTrue(actualRepsonse.getReplyStatus().getExtMessage().contains(item));
        }
    }
   
    
    @Test
    public void testError_Delete_API_Error() {
    	String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionDeleteRequest.xml");
        AccountingTransactionRequest reqObj =  (AccountingTransactionRequest) this.jaxb.unMarshalMessage(request);
        List<Integer> xactIdList =  reqObj.getCriteria().getXactCriteria().getBasicCriteria().getXactIdList().getXactId();

        try {
            when(this.mockApi.deleteXact(isA(List.class))).thenThrow(new XactApiException(API_ERROR));
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for deleting transaction API error");
        }
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_DELETE, request);
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
        
        // Verify that all xact id's in the request exists in the response's Message element
        for (Integer xactId : xactIdList) {
        	String item = String.valueOf(xactId);
        	Assert.assertTrue(actualRepsonse.getReplyStatus().getMessage().contains(item));
        }
        
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
      
    }
    

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionDeleteInvalidTranRequest.xml");
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
    public void testValidation_General_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionDeleteMissingCriteriaRequest.xml");
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_DELETE, request);
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
    public void testValidation_Fetch_TargetLevel_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionDeleteMissingTargetLevelRequest.xml");
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_DELETE, request);
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

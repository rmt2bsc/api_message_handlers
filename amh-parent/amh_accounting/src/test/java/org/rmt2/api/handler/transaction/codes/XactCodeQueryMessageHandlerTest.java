package org.rmt2.api.handler.transaction.codes;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dao.transaction.XactDao;
import org.dao.transaction.XactDaoFactory;
import org.dto.XactCodeDto;
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
import org.rmt2.api.handlers.transaction.XactCodeApiHandler;
import org.rmt2.api.handlers.transaction.XactGroupApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.XactCodeType;

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
    XactCodeApiHandler.class, XactApiFactory.class, SystemConfigurator.class })
public class XactCodeQueryMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private XactApi mockApi;

    /**
     * 
     */
    public XactCodeQueryMessageHandlerTest() {
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
    public void testSuccess_Fetch() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/codes/TransactionCodeQueryRequest.xml");
        List<XactCodeDto> mockListData = XactCodesMockData.createMockXactCode();

        try {
            when(this.mockApi.getCodes(isA(XactCodeDto.class))).thenReturn(mockListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a transaction codes");
        }
        
        MessageHandlerResults results = null;
        XactCodeApiHandler handler = new XactCodeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_CODE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getXactCodes().getXactCode().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Transaction Code record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getXactCodes());
        Assert.assertTrue(actualRepsonse.getProfile().getXactCodes().getXactCode().size() > 0);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getXactCodes().getXactCode().size(); ndx++) {
            XactCodeType a = actualRepsonse.getProfile().getXactCodes().getXactCode().get(ndx);
            Assert.assertNotNull(a.getXactCodeGrp());
            Assert.assertNotNull(a.getXactCodeGrp().getXactCodeGrpId());
            Assert.assertEquals(101 + ndx, a.getXactCodeGrp().getXactCodeGrpId().intValue());
            Assert.assertNotNull(a.getXactCodeId());
            Assert.assertEquals(201 + ndx, a.getXactCodeId().intValue());
            Assert.assertNotNull(a.getDescription());
            Assert.assertEquals("Code " + (ndx + 1), a.getDescription());
            
        }
    }
    
 
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/codes/TransactionCodeQueryRequest.xml");

        try {
            when(this.mockApi.getCodes(isA(XactCodeDto.class))).thenReturn(null);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a transaction codes");
        }
        
        MessageHandlerResults results = null;
        XactCodeApiHandler handler = new XactCodeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_CODE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Transaction Code data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_FetchCustomer_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/codes/TransactionCodeQueryRequest.xml");

        try {
            when(this.mockApi.getCodes(isA(XactCodeDto.class)))
                 .thenThrow(new XactApiException("An Xact API test error occurred"));
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a transaction groups");
        }
        
        MessageHandlerResults results = null;
        XactCodeApiHandler handler = new XactCodeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_CODE_GET, request);
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
        Assert.assertEquals("Failure to retrieve Transaction Code(s)", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("An Xact API test error occurred",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/codes/TransactionCodeQueryInvalidTransCodeRequest.xml");
        try {
            when(this.mockApi.getCodes(isA(XactCodeDto.class)))
               .thenThrow(new XactApiException("Test validation error: selection criteria is required"));
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a transaction groups");
        }
        
        MessageHandlerResults results = null;
        XactCodeApiHandler handler = new XactCodeApiHandler();
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
        Assert.assertEquals(XactGroupApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "INCORRECT_TRAN_CODE", actualRepsonse
                .getReplyStatus().getMessage());
    }
    

    @Test
    public void testValidation_Fetch_General_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/codes/TransactionCodeQueryMissingCriteriaRequest.xml");
        
        MessageHandlerResults results = null;
        XactCodeApiHandler handler = new XactCodeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_CODE_GET, request);
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
        Assert.assertEquals(XactCodeApiHandler.MSG_MISSING_GENERAL_CRITERIA,
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_Fetch_Subject_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/codes/TransactionCodeQueryMissingCriteriaRequest2.xml");
        
        MessageHandlerResults results = null;
        XactCodeApiHandler handler = new XactCodeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_CODE_GET, request);
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
        Assert.assertEquals(XactCodeApiHandler.MSG_MISSING_SUBJECT_CRITERIA,
                actualRepsonse.getReplyStatus().getMessage());
    }
}

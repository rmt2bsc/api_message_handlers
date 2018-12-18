package org.rmt2.api.handler.transaction.common;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dao.transaction.XactDao;
import org.dao.transaction.XactDaoFactory;
import org.dto.XactDto;
import org.dto.XactTypeDto;
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
import com.api.util.RMT2String;

/**
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class,
    XactApiHandler.class, XactApiFactory.class, SystemConfigurator.class })
public class XactReverseMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private XactApi mockApi;

    /**
     * 
     */
    public XactReverseMessageHandlerTest() {
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
    public void testSuccess_Reverse_Transs() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionReverseRequest.xml");

        try {
            when(this.mockApi.reverse(isA(XactDto.class), isA(List.class)))
                    .thenReturn(CommonXactMockData.NEW_XACT_ID);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for reversing a transaction");
        }
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_REVERSE, request);
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
        String msg = RMT2String.replace(XactApiHandler.MSG_REVERSE_SUCCESS,
                String.valueOf(CommonXactMockData.EXISTING_XACT_ID), "%s1");
        msg = RMT2String.replace(msg, String.valueOf(CommonXactMockData.NEW_XACT_ID), "%s2");
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());
        
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getTransactions());
        Assert.assertTrue(actualRepsonse.getProfile().getTransactions().getTransaction().size() == 1);
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getTransactions().getTransaction().size(); ndx++) {
            XactType a = actualRepsonse.getProfile().getTransactions().getTransaction().get(ndx);
            Assert.assertNotNull(a.getXactId());
            Assert.assertEquals(CommonXactMockData.NEW_XACT_ID, a.getXactId().intValue());
        }
    }
    
  
    
    @Test
    public void testError_FetchCustomer_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionReverseRequest.xml");

        try {
            when(this.mockApi.reverse(isA(XactDto.class), isA(List.class)))
                 .thenThrow(new XactApiException("An Xact API test error occurred"));
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for creating a transaction");
        }
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_REVERSE, request);
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
        Assert.assertEquals("Failure to reverse Transaction: "
                        + String.valueOf(CommonXactMockData.EXISTING_XACT_ID),
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("An Xact API test error occurred",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testValidation_Missing_Profile() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionReverseMissingProfileRequest.xml");
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_REVERSE, request);
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
        Assert.assertEquals(XactApiHandler.MSG_MISSING_PROFILE_DATA,
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_Missing_Transaction_Section() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionReverseMissingTransactionSectionRequest.xml");
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_REVERSE, request);
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
        Assert.assertEquals(XactApiHandler.MSG_MISSING_PROFILE_DATA,
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_Zero_Transactions_Exist() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionReverseZeroTransactionsRequest.xml");
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_REVERSE, request);
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
        Assert.assertEquals(XactApiHandler.MSG_REQUIRED_NO_TRANSACTIONS_INCORRECT,
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_Too_Many_Transactions_Exist() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/common/TransactionReverseTooManyTransactionsRequest.xml");
        
        MessageHandlerResults results = null;
        XactApiHandler handler = new XactApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_TRANSACTION_REVERSE, request);
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
        Assert.assertEquals(XactApiHandler.MSG_REQUIRED_NO_TRANSACTIONS_INCORRECT,
                actualRepsonse.getReplyStatus().getMessage());
    }
}

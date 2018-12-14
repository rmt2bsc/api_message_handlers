package org.rmt2.api.handler.subsidiary.creditor;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dao.transaction.XactDao;
import org.dao.transaction.XactDaoFactory;
import org.dto.CreditorDto;
import org.dto.CreditorXactHistoryDto;
import org.dto.XactDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.subsidiary.CreditorApi;
import org.modules.subsidiary.CreditorApiException;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiException;
import org.modules.transaction.XactApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handler.subsidiary.SubsidiaryMockData;
import org.rmt2.api.handlers.subsidiary.CreditorApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.CreditorActivityType;
import org.rmt2.jaxb.CreditorType;

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
    CreditorApiHandler.class, SubsidiaryApiFactory.class, XactApiFactory.class, SystemConfigurator.class })
public class CreditorQueryMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private SubsidiaryApiFactory mockApiFactory;
    private CreditorApi mockApi;
    private XactApi mockXactApi;

    /**
     * 
     */
    public CreditorQueryMessageHandlerTest() {
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
        mockApiFactory = Mockito.mock(SubsidiaryApiFactory.class);        
        try {
            PowerMockito.whenNew(SubsidiaryApiFactory.class)
                    .withNoArguments().thenReturn(this.mockApiFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mockApi = Mockito.mock(CreditorApi.class);
        when(mockApiFactory.createCreditorApi(isA(String.class))).thenReturn(mockApi);
        doNothing().when(this.mockApi).close();
        
        XactDaoFactory mockXactDaoFactory = Mockito.mock(XactDaoFactory.class);
        XactDao mockDao = Mockito.mock(XactDao.class);
        mockXactApi = Mockito.mock(XactApi.class);
        PowerMockito.mockStatic(XactApiFactory.class);
        when(mockXactDaoFactory.createRmt2OrmXactDao(isA(String.class))).thenReturn(mockDao);
        PowerMockito.when(XactApiFactory.createDefaultXactApi()).thenReturn(this.mockXactApi);
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
    public void testSuccess_FetchCreditor() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorQueryRequest.xml");
        List<CreditorDto> mockListData = SubsidiaryMockData.createMockCreditors();

        try {
            when(this.mockApi.getExt(isA(CreditorDto.class))).thenReturn(mockListData);
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor");
        }
        
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getCreditors().getCreditor().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Creditor record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getCreditors().getCreditor().size(); ndx++) {
            CreditorType a = actualRepsonse.getProfile().getCreditors().getCreditor().get(ndx);
            Assert.assertNotNull(a.getCreditorId());
            Assert.assertEquals(200 + ndx, a.getCreditorId().intValue());
            Assert.assertNotNull(a.getContactDetails());
            Assert.assertNotNull(a.getContactDetails().getBusinessId());
            Assert.assertEquals(1351 + ndx, a.getContactDetails().getBusinessId().intValue());
            Assert.assertEquals(330 + ndx, a.getAcctId().intValue());
            Assert.assertNotNull(a.getAccountNo());
            Assert.assertEquals("C123458" + ndx, a.getAccountNo());
            Assert.assertNotNull(a.getExtAccountNo());
            Assert.assertEquals("7437437JDJD848" + ndx, a.getExtAccountNo());
            
        }
    }
    
 
    @Test
    public void testSuccess_FetchCreditor_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorQueryRequest.xml");
        try {
            when(this.mockApi.getExt(isA(CreditorDto.class))).thenReturn(null);
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor");
        }
        
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_GET, request);
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
        Assert.assertEquals("Creditor data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_FetchCreditor_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorQueryRequest.xml");
        try {
            when(this.mockApi.getExt(isA(CreditorDto.class)))
               .thenThrow(new CreditorApiException("Test API Error"));
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor");
        }
        
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_GET, request);
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
        Assert.assertEquals("Failure to retrieve creditor(s)", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test API Error", actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testSuccess_FetchTransactionHistory() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorTransHistQueryRequest.xml");
        List<CreditorDto> mockCredData = SubsidiaryMockData.createMockCreditor();
        List<CreditorXactHistoryDto> mockListData = SubsidiaryMockData.createMockCreditorXactHistory();
        List<XactDto> mockXactDetailsData = SubsidiaryMockData.createMockCreditorXactHistoryDetails();
        
        try {
            when(this.mockApi.getExt(isA(CreditorDto.class))).thenReturn(mockCredData);
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor");
        }
        
        try {
            when(this.mockApi.getTransactionHistory(isA(Integer.class))).thenReturn(mockListData);
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor transaction history");
        }
        
        try {
            when(this.mockXactApi.getXactById(isA(Integer.class))).thenReturn(
                    mockXactDetailsData.get(0), mockXactDetailsData.get(1),
                    mockXactDetailsData.get(2), mockXactDetailsData.get(3),
                    mockXactDetailsData.get(4));
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor transaction history details");
        }
        
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_TRAN_HIST_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getCreditors().getCreditor().size());
        Assert.assertEquals(5, actualRepsonse.getProfile().getCreditors().getCreditor().get(0)
                .getTransactions().getTransaction().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Creditor transaction history record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getCreditors().getCreditor().size(); ndx++) {
            CreditorType a = actualRepsonse.getProfile().getCreditors().getCreditor().get(ndx);
            Assert.assertNotNull(a.getCreditorId());
            Assert.assertEquals(100, a.getCreditorId().intValue());
            int ndx2 = 0;
            for (CreditorActivityType tran : a.getTransactions().getTransaction()) {
                Assert.assertNotNull(tran.getXactDetails());
                Assert.assertNotNull(tran.getXactId());
                Assert.assertEquals(tran.getXactId(), tran.getXactId());
                Assert.assertEquals(1200 + ndx2++, tran.getXactId().intValue());
            }
            Assert.assertEquals(8580.26, a.getBalance().doubleValue(), 0);
        }
    }
    
 
    @Test
    public void testSuccess_FetchTransactionHistory_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorTransHistQueryRequest.xml");
        List<CreditorDto> mockCredData = SubsidiaryMockData.createMockCreditor();
        List<CreditorXactHistoryDto> mockListData = SubsidiaryMockData.createMockCreditorXactHistory();

        try {
            when(this.mockApi.getExt(isA(CreditorDto.class))).thenReturn(mockCredData);
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor");
        }
        
        try {
            when(this.mockApi.getTransactionHistory(isA(Integer.class))).thenReturn(null);
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor transaction history");
        }
        
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_TRAN_HIST_GET, request);
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
        Assert.assertEquals("Creditor transaction history data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testSuccess_FetchTransactionHistory_TooManyCreditorsFetched() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorTransHistQueryRequest.xml");
        List<CreditorDto> mockCredData = SubsidiaryMockData.createMockCreditors();

        try {
            when(this.mockApi.getExt(isA(CreditorDto.class))).thenReturn(mockCredData);
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor");
        }
        
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_TRAN_HIST_GET, request);
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
        Assert.assertEquals("Creditor data not found or too many creditors were fetched", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_FetchTransactionHistory_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorTransHistQueryRequest.xml");
        List<CreditorDto> mockCredData = SubsidiaryMockData.createMockCreditor();
        
        try {
            when(this.mockApi.getExt(isA(CreditorDto.class))).thenReturn(mockCredData);
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor");
        }
        try {
            when(this.mockApi.getTransactionHistory(isA(Integer.class)))
               .thenThrow(new CreditorApiException("Test API Error"));
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a customer transaction history");
        }
        
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_TRAN_HIST_GET, request);
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
        Assert.assertEquals("Failure to retrieve creditor transaction history", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test API Error", actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorQueryRequest.xml");
        
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
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
        Assert.assertEquals(CreditorApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "INCORRECT_TRAN_CODE", actualRepsonse
                .getReplyStatus().getMessage());
    }
    

    @Test
    public void testValidation_FetchCreditor_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorQueryMissingCriteriaRequest.xml");
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_GET, request);
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
        Assert.assertEquals(CreditorApiHandler.MSG_UPDATE_MISSING_CRITERIA,
                actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_FetchCreditor_CreditorCriteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorQueryMissingCreditorCriteriaRequest.xml");
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_GET, request);
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
        Assert.assertEquals(CreditorApiHandler.MSG_UPDATE_MISSING_CRITERIA,
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_InvalidRequest() {
        String request = RMT2File.getFileContentsAsString("xml/InvalidRequest.xml");
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_GET, request);
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
        Assert.assertEquals("An invalid request message was encountered.  Please payload.", actualRepsonse
                .getReplyStatus().getMessage());
    }
}

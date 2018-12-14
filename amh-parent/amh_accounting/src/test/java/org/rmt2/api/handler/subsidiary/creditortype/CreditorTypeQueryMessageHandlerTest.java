package org.rmt2.api.handler.subsidiary.creditortype;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.CreditorTypeDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.subsidiary.CreditorApi;
import org.modules.subsidiary.CreditorApiException;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handler.subsidiary.SubsidiaryMockData;
import org.rmt2.api.handlers.subsidiary.CreditorApiHandler;
import org.rmt2.api.handlers.subsidiary.CreditorTypeApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionResponse;
import org.rmt2.jaxb.CreditortypeType;

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
    CreditorTypeApiHandler.class, SubsidiaryApiFactory.class, SystemConfigurator.class })
public class CreditorTypeQueryMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private SubsidiaryApiFactory mockApiFactory;
    private CreditorApi mockApi;


    /**
     * 
     */
    public CreditorTypeQueryMessageHandlerTest() {
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
    public void testSuccess_FetchCreditorTypes() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditortype/CreditorTypeQueryRequest.xml");
        List<CreditorTypeDto> mockListData = SubsidiaryMockData.createMockCreditorTypes();

        try {
            when(this.mockApi.getCreditorType(isA(CreditorTypeDto.class))).thenReturn(mockListData);
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor type");
        }
        
        MessageHandlerResults results = null;
        CreditorTypeApiHandler handler = new CreditorTypeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_TYPE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getCreditorTypes().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Creditor Type record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getCreditorTypes().size(); ndx++) {
            CreditortypeType a = actualRepsonse.getProfile().getCreditorTypes().get(ndx);
            Assert.assertNotNull(a.getCreditorTypeId());
            Assert.assertEquals(100 * (ndx + 1), a.getCreditorTypeId().intValue());
            Assert.assertNotNull(a.getDescription());
            Assert.assertEquals("Creditor Type " + (ndx + 1), a.getDescription());
        }
    }
    
 
    @Test
    public void testSuccess_FetchCreditorType_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditortype/CreditorTypeQueryRequest.xml");
        try {
            when(this.mockApi.getCreditorType(isA(CreditorTypeDto.class))).thenReturn(null);
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor type");
        }
        
        MessageHandlerResults results = null;
        CreditorTypeApiHandler handler = new CreditorTypeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_TYPE_GET, request);
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
        Assert.assertEquals("Creditor Type data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_FetchCreditorType_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditortype/CreditorTypeQueryRequest.xml");
        try {
            when(this.mockApi.getCreditorType(isA(CreditorTypeDto.class)))
               .thenThrow(new CreditorApiException("Test API Error"));
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor");
        }
        
        MessageHandlerResults results = null;
        CreditorTypeApiHandler handler = new CreditorTypeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_TYPE_GET, request);
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
        Assert.assertEquals("Failure to retrieve creditor type(s)", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test API Error", actualRepsonse.getReplyStatus().getExtMessage());
    }
    
   

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditortype/CreditorTypeQueryRequest.xml");
        
        MessageHandlerResults results = null;
        CreditorTypeApiHandler handler = new CreditorTypeApiHandler();
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
    public void testValidation_FetchCreditorType_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditortype/CreditorTypeQueryMissingCriteriaRequest.xml");
        
        MessageHandlerResults results = null;
        CreditorTypeApiHandler handler = new CreditorTypeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_TYPE_GET, request);
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
        Assert.assertEquals(CreditorTypeApiHandler.MSG_UPDATE_MISSING_CRITERIA,
                actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_FetchCreditor_CreditorTypeCriteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditortype/CreditorTypeQueryMissingCreditorTypeCriteriaRequest.xml");
        
        MessageHandlerResults results = null;
        CreditorTypeApiHandler handler = new CreditorTypeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_TYPE_GET, request);
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
        Assert.assertEquals(CreditorTypeApiHandler.MSG_UPDATE_MISSING_CRITERIA,
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_InvalidRequest() {
        String request = RMT2File.getFileContentsAsString("xml/InvalidRequest.xml");
        MessageHandlerResults results = null;
        CreditorTypeApiHandler handler = new CreditorTypeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_TYPE_GET, request);
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

package org.rmt2.api.handler.generalledger;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.AccountTypeDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.generalledger.GeneralLedgerApiException;
import org.modules.generalledger.GeneralLedgerApiFactory;
import org.modules.generalledger.GlAccountApi;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.AccountingMockData;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handlers.generalledger.GlAccountApiHandler;
import org.rmt2.api.handlers.generalledger.GlAccountTypeApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingGeneralLedgerResponse;
import org.rmt2.jaxb.GlAccounttypeType;

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
    GlAccountTypeApiHandler.class, GeneralLedgerApiFactory.class, SystemConfigurator.class })
public class GlAccountTypeMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private GeneralLedgerApiFactory mockApiFactory;
    private GlAccountApi mockApi;


    /**
     * 
     */
    public GlAccountTypeMessageHandlerTest() {
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
        mockApiFactory = Mockito.mock(GeneralLedgerApiFactory.class);        
        try {
            PowerMockito.whenNew(GeneralLedgerApiFactory.class)
                    .withNoArguments().thenReturn(this.mockApiFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mockApi = Mockito.mock(GlAccountApi.class);
        when(mockApiFactory.createApi(isA(String.class))).thenReturn(mockApi);
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
        String request = RMT2File.getFileContentsAsString("xml/generalledger/accounttype/AccountTypeFetchRequest.xml");
        List<AccountTypeDto> mockListData = AccountingMockData.createMockGlAccountTypes();

        try {
            when(this.mockApi.getAccountType(isA(AccountTypeDto.class))).thenReturn(mockListData);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a GL Account Type");
        }
        
        MessageHandlerResults results = null;
        GlAccountTypeApiHandler handler = new GlAccountTypeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_TYPE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingGeneralLedgerResponse actualRepsonse = 
                (AccountingGeneralLedgerResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getAccountType().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("GL Account Type record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getAccount().size(); ndx++) {
            GlAccounttypeType a = actualRepsonse.getProfile().getAccountType().get(ndx);
            Assert.assertNotNull(a.getAcctTypeId());
            Assert.assertEquals(100 + ndx, a.getAcctTypeId().intValue());
            Assert.assertEquals("AccountType" + (ndx + 1), a.getDescription());
            Assert.assertNotNull(a.getBalanceType());
            Assert.assertNotNull(a.getBalanceType().getAccountBaltypeId());
            Assert.assertEquals(1, a.getBalanceType().getAccountBaltypeId().intValue());
        }
    }
    
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/accounttype/AccountTypeFetchRequest.xml");
        try {
            when(this.mockApi.getAccountType(isA(AccountTypeDto.class))).thenReturn(null);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a GL Account Type");
        }
        
        MessageHandlerResults results = null;
        GlAccountTypeApiHandler handler = new GlAccountTypeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_TYPE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingGeneralLedgerResponse actualRepsonse = 
                (AccountingGeneralLedgerResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("GL Account Type data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/accounttype/AccountTypeFetchRequest.xml");
        try {
            when(this.mockApi.getAccountType(isA(AccountTypeDto.class)))
                .thenThrow(new GeneralLedgerApiException("Test validation error: selection criteria is required"));
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a GL Accounts");
        }
        
        MessageHandlerResults results = null;
        GlAccountTypeApiHandler handler = new GlAccountTypeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_TYPE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingGeneralLedgerResponse actualRepsonse = 
                (AccountingGeneralLedgerResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals("Failure to retrieve GL Account Type(s)", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test validation error: selection criteria is required",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/accounttype/AccountTypeFetchIncorrectTransCodeRequest.xml");
        
        MessageHandlerResults results = null;
        GlAccountTypeApiHandler handler = new GlAccountTypeApiHandler();
        try {
            results = handler.processMessage("INCORRECT_TRAN_CODE", request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingGeneralLedgerResponse actualRepsonse = 
                (AccountingGeneralLedgerResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(GlAccountApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "INCORRECT_TRAN_CODE", actualRepsonse
                .getReplyStatus().getMessage());
    }
    

    @Test
    public void testValidation_Fetch_AcctType_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/accounttype/AccountTypeFetchMissingCriteriaRequest.xml");
        MessageHandlerResults results = null;
        GlAccountTypeApiHandler handler = new GlAccountTypeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_TYPE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingGeneralLedgerResponse actualRepsonse = (AccountingGeneralLedgerResponse) jaxb
                .unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals("GL Account Type criteria is required", actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_InvalidRequest() {
        String request = RMT2File.getFileContentsAsString("xml/InvalidRequest.xml");
        MessageHandlerResults results = null;
        GlAccountTypeApiHandler handler = new GlAccountTypeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingGeneralLedgerResponse actualRepsonse = (AccountingGeneralLedgerResponse) jaxb
                .unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals("An invalid request message was encountered.  Please payload.", actualRepsonse
                .getReplyStatus().getMessage());
    }
}

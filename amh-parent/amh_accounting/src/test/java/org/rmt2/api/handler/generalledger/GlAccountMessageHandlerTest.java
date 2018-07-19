package org.rmt2.api.handler.generalledger;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.AccountDto;
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
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingGeneralLedgerResponse;
import org.rmt2.jaxb.GlAccountType;

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
    GlAccountApiHandler.class, GeneralLedgerApiFactory.class, SystemConfigurator.class })
public class GlAccountMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private GeneralLedgerApiFactory mockApiFactory;
    private GlAccountApi mockApi;


    /**
     * 
     */
    public GlAccountMessageHandlerTest() {
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
        String request = RMT2File.getFileContentsAsString("xml/generalledger/account/AccountFetchRequest.xml");
        List<AccountDto> mockListData = AccountingMockData.createMockGlAccounts();

        try {
            when(this.mockApi.getAccount(isA(AccountDto.class))).thenReturn(mockListData);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a GL Accounts");
        }
        
        MessageHandlerResults results = null;
        GlAccountApiHandler handler = new GlAccountApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingGeneralLedgerResponse actualRepsonse = 
                (AccountingGeneralLedgerResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(4, actualRepsonse.getProfile().getAccount().size());
        Assert.assertEquals(4, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("GL Account record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getAccount().size(); ndx++) {
            GlAccountType a = actualRepsonse.getProfile().getAccount().get(ndx);
            Assert.assertNotNull(a.getAcctId());
            Assert.assertEquals(100 + ndx, a.getAcctId().intValue());
            Assert.assertEquals("AccountName" + (ndx + 1), a.getAccountName());
            Assert.assertEquals("AccountNo" + (ndx + 1), a.getAccountNo());
            Assert.assertEquals("AccountCode" + (ndx + 1), a.getAccountCode());
            Assert.assertEquals("AccountDescription" + (ndx + 1), a.getAccountDescription());
            Assert.assertNotNull(a.getAcctType());
            Assert.assertNotNull(a.getAcctType().getAcctTypeId());
            Assert.assertEquals((ndx + 1), a.getAcctType().getAcctTypeId().intValue());
            Assert.assertNotNull(a.getBalanceType());
            Assert.assertNotNull(a.getBalanceType().getAccountBaltypeId());
            Assert.assertEquals(1, a.getBalanceType().getAccountBaltypeId().intValue());
            Assert.assertNotNull(a.getAcctCatg());
            Assert.assertNotNull(a.getAcctCatg().getAcctCatgId());
            Assert.assertEquals(120 + ndx, a.getAcctCatg().getAcctCatgId().intValue());
            Assert.assertNotNull(a.getAcctSeq());
            Assert.assertEquals(1, a.getAcctSeq().intValue());
        }
    }
    
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/account/AccountFetchRequest.xml");
        try {
            when(this.mockApi.getAccount(isA(AccountDto.class))).thenReturn(null);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a GL Accounts");
        }
        
        MessageHandlerResults results = null;
        GlAccountApiHandler handler = new GlAccountApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_GET, request);
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
        Assert.assertEquals("GL Account data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/account/AccountFetchRequest.xml");
        try {
            when(this.mockApi.getAccount(isA(AccountDto.class)))
                .thenThrow(new GeneralLedgerApiException("Test validation error: selection criteria is required"));
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a GL Accounts");
        }
        
        MessageHandlerResults results = null;
        GlAccountApiHandler handler = new GlAccountApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_GET, request);
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
        Assert.assertEquals("Failure to retrieve GL Account(s)", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test validation error: selection criteria is required",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testSuccess_Update() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/account/AccountUpdateRequest.xml");
        try {
            when(this.mockApi.updateAccount(isA(AccountDto.class))).thenReturn(1);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for updating a GL Account");
        }
        
        MessageHandlerResults results = null;
        GlAccountApiHandler handler = new GlAccountApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingGeneralLedgerResponse actualRepsonse = 
                (AccountingGeneralLedgerResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getAccount().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("GL Account was modified successfully", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Total number of rows modified: " + actualRepsonse.getReplyStatus().getReturnCode().intValue(),
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testError_Update_Account_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/account/AccountUpdateRequest.xml");
        try {
            when(this.mockApi.updateAccount(isA(AccountDto.class))).thenReturn(0);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for updating a GL Account");
        }
        
        MessageHandlerResults results = null;
        GlAccountApiHandler handler = new GlAccountApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingGeneralLedgerResponse actualRepsonse = 
                (AccountingGeneralLedgerResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getAccount().size());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, 
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("GL Account was modified successfully",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertTrue(actualRepsonse.getReplyStatus().getExtMessage()
                .contains("Total number of rows modified: 0"));
    }

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/account/AccountIncorrectTransCodeRequest.xml");
        try {
            when(this.mockApi.updateAccount(isA(AccountDto.class))).thenReturn(1);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for updating a GL Account");
        }
        
        MessageHandlerResults results = null;
        GlAccountApiHandler handler = new GlAccountApiHandler();
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
    public void testSuccess_Delete() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/account/AccountDeleteRequest.xml");
        try {
            when(this.mockApi.deleteAccount(isA(Integer.class))).thenReturn(1);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for deleting a GL Account");
        }
        
        MessageHandlerResults results = null;
        GlAccountApiHandler handler = new GlAccountApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingGeneralLedgerResponse actualRepsonse = 
                (AccountingGeneralLedgerResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("GL Account was deleted successfully", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Delete_AcctId_Zero() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/account/AccountDeleteAcctIdZeroRequest.xml");
    try {
        when(this.mockApi.deleteAccount(isA(Integer.class))).thenReturn(1);
    } catch (GeneralLedgerApiException e) {
        Assert.fail("Unable to setup mock stub for deleting a GL Account");
    }
    
    MessageHandlerResults results = null;
    GlAccountApiHandler handler = new GlAccountApiHandler();
    try {
        results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_DELETE, request);
    } catch (MessageHandlerCommandException e) {
        e.printStackTrace();
        Assert.fail("An unexpected exception was thrown");
    }
    Assert.assertNotNull(results);
    Assert.assertNotNull(results.getPayload());

    AccountingGeneralLedgerResponse actualRepsonse = 
            (AccountingGeneralLedgerResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("A valid account id is required when deleting a GL Account from the database",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
}

package org.rmt2.api.handler.generalledger;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.AccountCategoryDto;
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
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handlers.generalledger.GlAccountCategoryApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingGeneralLedgerResponse;
import org.rmt2.jaxb.GlAccountcatgType;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
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
    GlAccountCategoryApiHandler.class, GeneralLedgerApiFactory.class, SystemConfigurator.class })
public class GlAccountCategoryMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private GeneralLedgerApiFactory mockApiFactory;
    private GlAccountApi mockApi;


    /**
     * 
     */
    public GlAccountCategoryMessageHandlerTest() {
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
        String request = RMT2File.getFileContentsAsString("xml/generalledger/accountcategory/AccountCategoryFetchRequest.xml");
        List<AccountCategoryDto> mockListData = GlAccountMockData.createMockGlAccountCategories();

        try {
            when(this.mockApi.getAccountCategory(isA(AccountCategoryDto.class))).thenReturn(mockListData);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a GL Account Categories");
        }
        
        MessageHandlerResults results = null;
        GlAccountCategoryApiHandler handler = new GlAccountCategoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_CATG_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingGeneralLedgerResponse actualRepsonse = 
                (AccountingGeneralLedgerResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getAccountCategory().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("GL Account Category record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getAccountCategory().size(); ndx++) {
            GlAccountcatgType a = actualRepsonse.getProfile().getAccountCategory().get(ndx);
            Assert.assertNotNull(a.getAcctCatgId());
            Assert.assertEquals(100 + ndx, a.getAcctCatgId().intValue());
            Assert.assertEquals("Category" + (ndx + 1), a.getDescription());
            Assert.assertNotNull(a.getAcctType());
            Assert.assertNotNull(a.getAcctType().getAcctTypeId());
            Assert.assertEquals((1 + ndx), a.getAcctType().getAcctTypeId().intValue());
        }
    }
    
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/accountcategory/AccountCategoryFetchRequest.xml");
        try {
            when(this.mockApi.getAccountCategory(isA(AccountCategoryDto.class))).thenReturn(null);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a GL Account Categories");
        }
        
        MessageHandlerResults results = null;
        GlAccountCategoryApiHandler handler = new GlAccountCategoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_CATG_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        AccountingGeneralLedgerResponse actualRepsonse = 
                (AccountingGeneralLedgerResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("GL Account Category data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/accountcategory/AccountCategoryFetchRequest.xml");
        try {
            when(this.mockApi.getAccountCategory(isA(AccountCategoryDto.class)))
                .thenThrow(new GeneralLedgerApiException("Test validation error: selection criteria is required"));
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a GL Accounts");
        }
        
        MessageHandlerResults results = null;
        GlAccountCategoryApiHandler handler = new GlAccountCategoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_CATG_GET, request);
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
        Assert.assertEquals("Failure to retrieve GL Account Category(s)", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test validation error: selection criteria is required",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testSuccess_Update() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/accountcategory/AccountCategoryUpdateRequest.xml");
        try {
            when(this.mockApi.updateCategory(isA(AccountCategoryDto.class))).thenReturn(1);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for updating a GL Account");
        }
        
        MessageHandlerResults results = null;
        GlAccountCategoryApiHandler handler = new GlAccountCategoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_CATG_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingGeneralLedgerResponse actualRepsonse = 
                (AccountingGeneralLedgerResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getAccountCategory().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("GL Account Category was modified successfully", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Total number of rows modified: " + actualRepsonse.getReplyStatus().getReturnCode().intValue(),
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testError_Update_Account_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/accountcategory/AccountCategoryUpdateRequest.xml");
        try {
            when(this.mockApi.updateCategory(isA(AccountCategoryDto.class))).thenReturn(0);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for updating a GL Account");
        }
        
        MessageHandlerResults results = null;
        GlAccountCategoryApiHandler handler = new GlAccountCategoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_CATG_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingGeneralLedgerResponse actualRepsonse = 
                (AccountingGeneralLedgerResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getAccountCategory().size());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, 
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("GL Account Category was modified successfully",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertTrue(actualRepsonse.getReplyStatus().getExtMessage()
                .contains("Total number of rows modified: 0"));
    }

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/accountcategory/AccountCategoryFetchIncorrectTransCodeRequest.xml");
        try {
            when(this.mockApi.updateCategory(isA(AccountCategoryDto.class))).thenReturn(1);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for updating a GL Account");
        }
        
        MessageHandlerResults results = null;
        GlAccountCategoryApiHandler handler = new GlAccountCategoryApiHandler();
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
        Assert.assertEquals(GlAccountCategoryApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "INCORRECT_TRAN_CODE", actualRepsonse
                .getReplyStatus().getMessage());
    }
    
    
    @Test
    public void testSuccess_Delete() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/accountcategory/AccountCategoryDeleteRequest.xml");
        try {
            when(this.mockApi.deleteCategory(isA(Integer.class))).thenReturn(1);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for deleting a GL Account Category");
        }
        
        MessageHandlerResults results = null;
        GlAccountCategoryApiHandler handler = new GlAccountCategoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_CATG_DELETE, request);
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
        Assert.assertEquals("GL Account Category was deleted successfully", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_Delete_Id_Zero() {
        String request = RMT2File.getFileContentsAsString("xml/generalledger/accountcategory/AccountCategoryDeleteKeyZeroRequest.xml");
        MessageHandlerResults results = null;
        GlAccountCategoryApiHandler handler = new GlAccountCategoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_CATG_DELETE, request);
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
            Assert.assertEquals("A valid account category id is required when deleting a GL Account Category from the database",
                    actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_Delete_Id_Blank() {
        String request = RMT2File
                .getFileContentsAsString("xml/generalledger/accountcategory/AccountCategoryDeleteKeyBlankRequest.xml");
        try {
            when(this.mockApi.deleteCategory(isA(Integer.class))).thenReturn(1);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for deleting a GL Account");
        }

        MessageHandlerResults results = null;
        GlAccountCategoryApiHandler handler = new GlAccountCategoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_CATG_DELETE, request);
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
        Assert.assertEquals("A valid account category id is required when deleting a GL Account Category from the database",
                actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Delete_Id_Missing() {
        String request = RMT2File
                .getFileContentsAsString("xml/generalledger/accountcategory/AccountCategoryDeleteKeyMissingRequest.xml");
        try {
            when(this.mockApi.deleteCategory(isA(Integer.class))).thenReturn(1);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for deleting a GL Account");
        }

        MessageHandlerResults results = null;
        GlAccountCategoryApiHandler handler = new GlAccountCategoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_CATG_DELETE, request);
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
        Assert.assertEquals("A valid account category id is required when deleting a GL Account Category from the database",
                actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_InvalidRequest() {
        String request = RMT2File.getFileContentsAsString("xml/InvalidRequest.xml");
        try {
            when(this.mockApi.deleteCategory(isA(Integer.class))).thenReturn(1);
        } catch (GeneralLedgerApiException e) {
            Assert.fail("Unable to setup mock stub for deleting a GL Account");
        }

        MessageHandlerResults results = null;
        GlAccountCategoryApiHandler handler = new GlAccountCategoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.GL_ACCOUNT_CATG_DELETE, request);
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
        Assert.assertEquals(AbstractJaxbMessageHandler.ERROR_MSG_INVALID_TRANS, actualRepsonse
                .getReplyStatus().getMessage());
    }
}

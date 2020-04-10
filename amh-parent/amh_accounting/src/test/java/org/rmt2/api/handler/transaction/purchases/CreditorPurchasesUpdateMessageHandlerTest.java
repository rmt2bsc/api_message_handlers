package org.rmt2.api.handler.transaction.purchases;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.XactCreditChargeDto;
import org.dto.XactTypeDto;
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
import org.rmt2.api.handlers.transaction.purchases.CreateCreditorPurchasesApiHandler;
import org.rmt2.api.handlers.transaction.purchases.QueryCreditorPurchasesApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionResponse;

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
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, QueryCreditorPurchasesApiHandler.class, CreditorPurchasesApiFactory.class,
        SystemConfigurator.class })
public class CreditorPurchasesUpdateMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private CreditorPurchasesApi mockApi;

    /**
     * 
     */
    public CreditorPurchasesUpdateMessageHandlerTest() {
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

        API_ERROR_MESSAGE = "A Creditor Purchases general API test error occurred";
        VALIDATION_ERROR_MESSAGE = "A Creditor Purchases validation test error occurred";
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
    public void testSuccess_Create() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/purchases/creditor/CreditorPurchasesUpdateRequest.xml");

        try {
            when(this.mockApi.update(isA(XactCreditChargeDto.class), isA(List.class))).thenReturn(CreditorPurchasesMockData.NEW_XACT_ID);
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for updating a creditor purchases transaction");
        }

        MessageHandlerResults results = null;
        CreateCreditorPurchasesApiHandler handler = new CreateCreditorPurchasesApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_CREATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        String msg = RMT2String.replace(CreateCreditorPurchasesApiHandler.MSG_CREATE_SUCCESS,
                String.valueOf(CreditorPurchasesMockData.NEW_XACT_ID), "%s");
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testError_Create_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/purchases/creditor/CreditorPurchasesUpdateRequest.xml");

        try {
            when(this.mockApi.update(isA(XactCreditChargeDto.class), isA(List.class)))
                    .thenThrow(new CreditorPurchasesApiException(API_ERROR_MESSAGE));
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for fetching cash disbursement transaction");
        }

        MessageHandlerResults results = null;
        CreateCreditorPurchasesApiHandler handler = new CreateCreditorPurchasesApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_CREATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(CreateCreditorPurchasesApiHandler.MSG_CREATE_FAILURE, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR_MESSAGE, actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testValidation_Create_Missing_Profile() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/purchases/creditor/CreditorPurchasesUpdateRequest_MissingProfile.xml");

        try {
            when(this.mockApi.update(isA(XactCreditChargeDto.class), isA(List.class))).thenReturn(CreditorPurchasesMockData.NEW_XACT_ID);
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for updating a creditor purchases transaction");
        }

        MessageHandlerResults results = null;
        CreateCreditorPurchasesApiHandler handler = new CreateCreditorPurchasesApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_CREATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(CreateCreditorPurchasesApiHandler.MSG_MISSING_PROFILE_DATA, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Create_Missing_Transaction_Section() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/purchases/creditor/CreditorPurchasesUpdateRequest_MissingTransactionSection.xml");

        try {
            when(this.mockApi.update(isA(XactCreditChargeDto.class), isA(List.class))).thenReturn(CreditorPurchasesMockData.NEW_XACT_ID);
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for updating a creditor purchases transaction");
        }

        MessageHandlerResults results = null;
        CreateCreditorPurchasesApiHandler handler = new CreateCreditorPurchasesApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_CREATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(CreateCreditorPurchasesApiHandler.MSG_MISSING_TRANSACTION_SECTION, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Create_Zero_Transactions() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/purchases/creditor/CreditorPurchasesUpdateRequest_ZeroTransactions.xml");

        try {
            when(this.mockApi.update(isA(XactCreditChargeDto.class), isA(List.class))).thenReturn(CreditorPurchasesMockData.NEW_XACT_ID);
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for updating a creditor purchases transaction");
        }

        MessageHandlerResults results = null;
        CreateCreditorPurchasesApiHandler handler = new CreateCreditorPurchasesApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_CREATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(CreateCreditorPurchasesApiHandler.MSG_REQUIRED_NO_TRANSACTIONS_INCORRECT, actualRepsonse
                .getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Create_TooMany_Transactions() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/purchases/creditor/CreditorPurchasesUpdateRequest_TooManyTransactions.xml");

        try {
            when(this.mockApi.update(isA(XactCreditChargeDto.class), isA(List.class))).thenReturn(CreditorPurchasesMockData.NEW_XACT_ID);
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for updating a creditor purchases transaction");
        }

        MessageHandlerResults results = null;
        CreateCreditorPurchasesApiHandler handler = new CreateCreditorPurchasesApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_CREATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(CreateCreditorPurchasesApiHandler.MSG_REQUIRED_NO_TRANSACTIONS_INCORRECT, actualRepsonse
                .getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Create_Missing_Creditor_Profile() {
        String request = RMT2File
                .getFileContentsAsString("xml/transaction/purchases/creditor/CreditorPurchasesUpdateRequest_MissingCreditorProfile.xml");

        try {
            when(this.mockApi.update(isA(XactCreditChargeDto.class), isA(List.class))).thenReturn(CreditorPurchasesMockData.NEW_XACT_ID);
        } catch (CreditorPurchasesApiException e) {
            Assert.fail("Unable to setup mock stub for updating a creditor purchases transaction");
        }

        MessageHandlerResults results = null;
        CreateCreditorPurchasesApiHandler handler = new CreateCreditorPurchasesApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CREDITPURCHASE_CREATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }

        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(CreateCreditorPurchasesApiHandler.MSG_MISSING_CREDITOR_PROFILE_DATA, actualRepsonse.getReplyStatus()
                .getMessage());
    }


}

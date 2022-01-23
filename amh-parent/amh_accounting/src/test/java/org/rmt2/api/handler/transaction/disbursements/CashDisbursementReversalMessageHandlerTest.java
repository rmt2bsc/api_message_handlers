package org.rmt2.api.handler.transaction.disbursements;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.dto.XactDto;
import org.dto.XactTypeDto;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.transaction.XactApiException;
import org.modules.transaction.XactConst;
import org.modules.transaction.disbursements.DisbursementsApi;
import org.modules.transaction.disbursements.DisbursementsApiException;
import org.modules.transaction.disbursements.DisbursementsApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handler.HandlerCacheMockData;
import org.rmt2.api.handler.transaction.common.CommonXactMockData;
import org.rmt2.api.handler.transaction.purchases.CreditorPurchasesMockData;
import org.rmt2.api.handlers.transaction.XactApiHandler;
import org.rmt2.api.handlers.transaction.cashdisbursement.CreateCashDisbursementApiHandler;
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
    CreateCashDisbursementApiHandler.class, DisbursementsApiFactory.class, SystemConfigurator.class })
public class CashDisbursementReversalMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private DisbursementsApi mockApi;

    /**
     * 
     */
    public CashDisbursementReversalMessageHandlerTest() {
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
        mockApi = Mockito.mock(DisbursementsApi.class);
        PowerMockito.mockStatic(DisbursementsApiFactory.class);
        PowerMockito.when(DisbursementsApiFactory.createApi()).thenReturn(this.mockApi);
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
    public void testSuccess_Reverse_Trans() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/disbursements/cash/CashDisbursementReverseRequest.xml");

        try {
            when(this.mockApi.updateTrans(isA(XactDto.class), isA(List.class), isA(Integer.class)))
                    .thenReturn(CommonXactMockData.NEW_XACT_ID);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for creating a cash disbursement transaction");
        }
        
        XactDto newXactDto = Rmt2XactDtoFactory.createXactBaseInstance(null);
        newXactDto.setXactId(CommonXactMockData.NEW_XACT_ID);
        newXactDto.setXactId(CreditorPurchasesMockData.NEW_XACT_ID);
        newXactDto.setXactTypeId(XactConst.XACT_TYPE_CASH_DISBURSE_ACCOUNT);
        List<XactDto> mockVerifyList = new ArrayList<>();
        mockVerifyList.add(newXactDto);
        try {
            when(this.mockApi.get(isA(XactDto.class), eq(null))).thenReturn(mockVerifyList);
        } catch (Exception e) {
            Assert.fail("Unable to setup mock stub for reversing a cash disbursment transaction");
        }

        MessageHandlerResults results = null;
        CreateCashDisbursementApiHandler handler = new CreateCashDisbursementApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_CREDITOR_REVERSE, request);
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
        
        String msg = RMT2String.replace(CreateCashDisbursementApiHandler.MSG_REVERSE_CREDITOR_SUCCESS,
                String.valueOf(CommonXactMockData.NEW_XACT_ID), "%s");
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
    public void testError_Reverse_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/transaction/disbursements/cash/CashDisbursementReverseRequest.xml");

        try {
            when(this.mockApi.updateTrans(isA(XactDto.class), isA(List.class), isA(Integer.class)))
                    .thenThrow(new DisbursementsApiException("An Xact API test error occurred"));
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for creating a transaction");
        }

        MessageHandlerResults results = null;
        CreateCashDisbursementApiHandler handler = new CreateCashDisbursementApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_CREDITOR_REVERSE, request);
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
        Assert.assertEquals(CreateCashDisbursementApiHandler.MSG_FAILURE, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("An Xact API test error occurred", actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testValidation_Missing_Profile() {
        String request = RMT2File.getFileContentsAsString(
                "xml/transaction/disbursements/cash/CashDisbursementReverse_MissingProfileRequest.xml");


        MessageHandlerResults results = null;
        CreateCashDisbursementApiHandler handler = new CreateCashDisbursementApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_CREDITOR_REVERSE, request);
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
        Assert.assertEquals(XactApiHandler.MSG_MISSING_PROFILE_DATA, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Transaction_Section() {
        String request = RMT2File.getFileContentsAsString(
                "xml/transaction/disbursements/cash/CashDisbursementReverse_MissingTransactionSectionRequest.xml");

        MessageHandlerResults results = null;
        CreateCashDisbursementApiHandler handler = new CreateCashDisbursementApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_CREDITOR_REVERSE, request);
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
        Assert.assertEquals(XactApiHandler.MSG_MISSING_TRANSACTION_SECTION, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Missing_Transaction_Parent_Element() {
        String request = RMT2File.getFileContentsAsString(
                "xml/transaction/disbursements/cash/CashDisbursementReverse_ZeroTransactionsRequest.xml");

        MessageHandlerResults results = null;
        CreateCashDisbursementApiHandler handler = new CreateCashDisbursementApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_CREDITOR_REVERSE, request);
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
        Assert.assertEquals(XactApiHandler.MSG_REQUIRED_NO_TRANSACTIONS_INCORRECT, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Too_Many_Transactions() {
        String request = RMT2File.getFileContentsAsString(
                "xml/transaction/disbursements/cash/CashDisbursementReverse_TooManyTransactionsRequest.xml");

        MessageHandlerResults results = null;
        CreateCashDisbursementApiHandler handler = new CreateCashDisbursementApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_CREDITOR_REVERSE, request);
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
        Assert.assertEquals(XactApiHandler.MSG_REQUIRED_NO_TRANSACTIONS_INCORRECT, actualRepsonse.getReplyStatus().getMessage());
    }
    
    
  
    
    @Test
    public void testValidation_Reverse_Trans_For_Creditor_Missing_Creditor_Profile() {
        String request = RMT2File.getFileContentsAsString(
                "xml/transaction/disbursements/cash/CashDisbursementReverse_MissingCreditorProfileRequest.xml");

        try {
            when(this.mockApi.updateTrans(isA(XactDto.class), isA(List.class), isA(Integer.class)))
                    .thenReturn(CommonXactMockData.NEW_XACT_ID);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for creating a creditor cash disbursement transaction");
        }
        
        MessageHandlerResults results = null;
        CreateCashDisbursementApiHandler handler = new CreateCashDisbursementApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ACCOUNTING_CASHDISBURSE_CREDITOR_REVERSE, request);
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
        Assert.assertEquals(CreateCashDisbursementApiHandler.MSG_MISSING_CREDITOR_PROFILE_DATA,
                actualRepsonse.getReplyStatus().getMessage());
    }
}

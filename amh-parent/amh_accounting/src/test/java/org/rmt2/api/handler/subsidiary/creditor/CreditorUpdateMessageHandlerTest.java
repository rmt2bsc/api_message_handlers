package org.rmt2.api.handler.subsidiary.creditor;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.dto.CreditorDto;
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
import org.rmt2.api.handlers.subsidiary.CreditorApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AccountingTransactionResponse;

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
    CreditorApiHandler.class, SubsidiaryApiFactory.class, SystemConfigurator.class })
public class CreditorUpdateMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private SubsidiaryApiFactory mockApiFactory;
    private CreditorApi mockApi;


    /**
     * 
     */
    public CreditorUpdateMessageHandlerTest() {
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
    public void testSuccess_UpdateCreditor() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorUpdateRequest.xml");
        try {
            when(this.mockApi.update(isA(CreditorDto.class))).thenReturn(1);
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for updating a creditor");
        }
        
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getCreditors().getCreditor().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Creditor record(s) updated successfully", actualRepsonse.getReplyStatus().getMessage());
    }
    
 
    @Test
    public void testSuccess_UpdateCreditor_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorUpdateRequest.xml");
        try {
            when(this.mockApi.update(isA(CreditorDto.class))).thenReturn(0);
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for updating a creditor");
        }
        
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AccountingTransactionResponse actualRepsonse = 
                (AccountingTransactionResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Creditor data not found for update",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Creditor Id: 3333, Creditor Name: Business Type Name",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testError_FetchCreditor_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorUpdateRequest.xml");
        try {
            when(this.mockApi.update(isA(CreditorDto.class)))
               .thenThrow(new CreditorApiException("Test API Error"));
        } catch (CreditorApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a creditor");
        }
        
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_UPDATE, request);
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
        Assert.assertEquals("Failure to update creditor(s)", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test API Error", actualRepsonse.getReplyStatus().getExtMessage());
    }
    
 
    @Test
    public void testValidation_UpdateCreditor_Profile_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorUpdateMissingProfileRequest.xml");
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_UPDATE, request);
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
        Assert.assertEquals(CreditorApiHandler.MSG_UPDATE_MISSING_PROFILE,
                actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_UpdateCreditor_CreditorProfile_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/subsidiary/creditor/CreditorUpdateMissingCreditorProfileRequest.xml");
        MessageHandlerResults results = null;
        CreditorApiHandler handler = new CreditorApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.SUBSIDIARY_CREDITOR_UPDATE, request);
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
        Assert.assertEquals(CreditorApiHandler.MSG_UPDATE_MISSING_PROFILE,
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

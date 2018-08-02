package org.rmt2.api.handler.inventory;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.ItemMasterStatusDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.inventory.InventoryApi;
import org.modules.inventory.InventoryApiException;
import org.modules.inventory.InventoryApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseAccountingMessageHandlerTest;
import org.rmt2.api.handlers.generalledger.GlAccountApiHandler;
import org.rmt2.api.handlers.inventory.ItemStatusApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.InventoryItemStatusType;
import org.rmt2.jaxb.InventoryResponse;

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
    ItemStatusApiHandler.class, InventoryApiFactory.class, SystemConfigurator.class })
public class ItemStatusTypeMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private InventoryApiFactory mockApiFactory;
    private InventoryApi mockApi;


    /**
     * 
     */
    public ItemStatusTypeMessageHandlerTest() {
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
        mockApiFactory = Mockito.mock(InventoryApiFactory.class);        
        try {
            PowerMockito.whenNew(InventoryApiFactory.class)
                    .withNoArguments().thenReturn(this.mockApiFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mockApi = Mockito.mock(InventoryApi.class);
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
        String request = RMT2File.getFileContentsAsString("xml/inventory/itemstatus/ItemStatusFetchRequest.xml");
        List<ItemMasterStatusDto> mockListData = InventoryMockData.createMockItemStatus();

        try {
            when(this.mockApi.getItemStatus(isA(ItemMasterStatusDto.class))).thenReturn(mockListData);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a Inventory item status Type");
        }
        
        MessageHandlerResults results = null;
        ItemStatusApiHandler handler = new ItemStatusApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_STATUS_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getInvItemStatus().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item  status record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getInvItemStatus().size(); ndx++) {
            InventoryItemStatusType a = actualRepsonse.getProfile().getInvItemStatus().get(ndx);
            Assert.assertNotNull(a.getItemStatusId());
            Assert.assertEquals(100 + ndx, a.getItemStatusId().intValue());
            Assert.assertNotNull(a.getDescription());
            Assert.assertEquals("Item Status #" + (ndx + 1), a.getDescription());
        }
    }
    
 
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/itemstatus/ItemStatusFetchRequest.xml");
        try {
            when(this.mockApi.getItemStatus(isA(ItemMasterStatusDto.class))).thenReturn(null);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a Inventory item status Type");
        }
        
        MessageHandlerResults results = null;
        ItemStatusApiHandler handler = new ItemStatusApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_STATUS_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item status data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/itemstatus/ItemStatusFetchRequest.xml");
        try {
            when(this.mockApi.getItemStatus(isA(ItemMasterStatusDto.class)))
                .thenThrow(new InventoryApiException("Test validation error: selection criteria is required"));
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a Inventory item status Type");
        }
        
        MessageHandlerResults results = null;
        ItemStatusApiHandler handler = new ItemStatusApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_STATUS_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals("Failure to retrieve inventory item status", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test validation error: selection criteria is required",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/itemstatus/ItemStatusFetchIncorrectTransCodeRequest.xml");
        
        MessageHandlerResults results = null;
        ItemStatusApiHandler handler = new ItemStatusApiHandler();
        try {
            results = handler.processMessage("INCORRECT_TRAN_CODE", request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(GlAccountApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "INCORRECT_TRAN_CODE", actualRepsonse
                .getReplyStatus().getMessage());
    }
    

    @Test
    public void testValidation_Fetch_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/itemstatus/ItemStatusFetchMissingCriteriaRequest.xml");
        
        MessageHandlerResults results = null;
        ItemStatusApiHandler handler = new ItemStatusApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_STATUS_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals("Inventory item status selection criteria is required for query operation",
                actualRepsonse.getReplyStatus().getMessage());
        
    }

    @Test
    public void testValidation_InvalidRequest() {
        String request = RMT2File.getFileContentsAsString("xml/InvalidRequest.xml");
        MessageHandlerResults results = null;
        ItemStatusApiHandler handler = new ItemStatusApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_STATUS_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus()
                .getReturnStatus());
        Assert.assertEquals("An invalid request message was encountered.  Please payload.", actualRepsonse
                .getReplyStatus().getMessage());
    }
}

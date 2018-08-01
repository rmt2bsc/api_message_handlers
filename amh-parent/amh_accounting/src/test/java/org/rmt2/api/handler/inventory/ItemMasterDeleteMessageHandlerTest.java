package org.rmt2.api.handler.inventory;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
import org.rmt2.api.handlers.inventory.ItemApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
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
    ItemApiHandler.class, InventoryApiFactory.class, SystemConfigurator.class })
public class ItemMasterDeleteMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private static final int ITEM_ID = 100;
    private static final int RC_EXISTING = 1;
    private static final int RC_NEW = 1234567;
    private InventoryApiFactory mockApiFactory;
    private InventoryApi mockApi;


    /**
     * 
     */
    public ItemMasterDeleteMessageHandlerTest() {
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
    public void testSuccess() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemDeleteRequest.xml");

        try {
            when(this.mockApi.deleteItemMaster(isA(Integer.class))).thenReturn(RC_EXISTING);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for update a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(RC_EXISTING, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item was deleted successfully",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("The inventory item id deleted was " + ITEM_ID,
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
       
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemDeleteRequest.xml");

        try {
            when(this.mockApi.deleteItemMaster(isA(Integer.class))).thenReturn(0);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for update a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item was not deleted",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("The inventory item id was not found: " + ITEM_ID,
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemDeleteRequest.xml");
        try {
            when(this.mockApi.deleteItemMaster(isA(Integer.class)))
                 .thenThrow(new InventoryApiException("Test validation error: delete system error"));    
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for update a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals("Failure to delete inventory item record",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test validation error: delete system error",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
  

    @Test
    public void testValidation_Missing_ItemCriteria() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemDeleteMissingCriteriaRequest1.xml");
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_DELETE, request);
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
        Assert.assertEquals("Inventory item selection criteria is required for query/delete operation",
                actualRepsonse.getReplyStatus().getMessage());
        
    }
    
    @Test
    public void testValidation_Missing_Criteria() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemDeleteMissingCriteriaRequest2.xml");
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_DELETE, request);
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
        Assert.assertEquals("Inventory item selection criteria is required for query/delete operation",
                actualRepsonse.getReplyStatus().getMessage());
        
    }

   
  
    
    @Test
    public void testValidation_InvalidRequest() {
        String request = RMT2File.getFileContentsAsString("xml/InvalidRequest.xml");
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_DELETE, request);
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

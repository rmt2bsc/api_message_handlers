package org.rmt2.api.handler.inventory;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.dto.ItemMasterDto;
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
    ItemApiHandler.class, InventoryApiFactory.class, SystemConfigurator.class })
public class ItemMasterUpdateMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private static final int UPDATE_RC_EXISTING = 1;
    private static final int UPDATE_RC_NEW = 1234567;
    private InventoryApiFactory mockApiFactory;
    private InventoryApi mockApi;


    /**
     * 
     */
    public ItemMasterUpdateMessageHandlerTest() {
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
    public void testSuccess_Update_Existing() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemUpdateExistingRequest.xml");

        try {
            when(this.mockApi.updateItemMaster(isA(ItemMasterDto.class)))
                  .thenReturn(UPDATE_RC_EXISTING);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for update a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(UPDATE_RC_EXISTING, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item was modified successfully",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testSuccess_Update_New() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemUpdateNewRequest.xml");

        try {
            when(this.mockApi.updateItemMaster(isA(ItemMasterDto.class)))
                  .thenReturn(UPDATE_RC_NEW);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for update a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(UPDATE_RC_NEW, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item was created successfully",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemUpdateExistingRequest.xml");

        try {
            when(this.mockApi.updateItemMaster(isA(ItemMasterDto.class)))
                  .thenReturn(0);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for update a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item was modified successfully",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemUpdateExistingRequest.xml");
        try {
            when(this.mockApi.updateItemMaster(isA(ItemMasterDto.class)))
                 .thenThrow(new InventoryApiException("Test validation error: update system error"));    
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for update a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals("Failure to update existing inventory item",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test validation error: update system error",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
  

    @Test
    public void testValidation_Update_Missing_InvItem() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemUpdateMissingProfileRequest1.xml");
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_UPDATE, request);
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
        Assert.assertEquals("Inventory item data is required for update operation",
                actualRepsonse.getReplyStatus().getMessage());
        
    }

    @Test
    public void testValidation_Update_Missing_Profile() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemUpdateMissingProfileRequest2.xml");
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_UPDATE, request);
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
        Assert.assertEquals("Inventory item data is required for update operation",
                actualRepsonse.getReplyStatus().getMessage());
        
    }
    
    @Test
    public void testValidation_Update_Too_Many_Items_In_Profile() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemUpdateExistingTooManyRequest.xml");
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_UPDATE, request);
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
        Assert.assertEquals("Only one (1) inventory item record is required for update operation",
                actualRepsonse.getReplyStatus().getMessage());
        
    }
    
    @Test
    public void testValidation_InvalidRequest() {
        String request = RMT2File.getFileContentsAsString("xml/InvalidRequest.xml");
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_UPDATE, request);
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
        Assert.assertEquals(AbstractJaxbMessageHandler.ERROR_MSG_INVALID_TRANS, actualRepsonse
                .getReplyStatus().getMessage());
    }
    
    @Test
    public void testSuccess_Activate() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemActivateRequest.xml");

        try {
            when(this.mockApi.activateItemMaster(isA(Integer.class))).thenReturn(UPDATE_RC_EXISTING);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for activate a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_ACTIVATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(UPDATE_RC_EXISTING, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item was activated successfully",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Activate_Item_Already_Activated() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemActivateRequest.xml");

        try {
            when(this.mockApi.activateItemMaster(isA(Integer.class)))
                .thenThrow(new InventoryApiException("Item is already activated"));
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for activate a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_ACTIVATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Failure to activate inventory item, 100",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Item is already activated", actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testSuccess_Deactivate() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemDeactivateRequest.xml");

        try {
            when(this.mockApi.deactivateItemMaster(isA(Integer.class))).thenReturn(UPDATE_RC_EXISTING);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for deactivate a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_DEACTIVATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(UPDATE_RC_EXISTING, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item was deactivated successfully",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Deactivate_Item_Already_Activated() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemDeactivateRequest.xml");

        try {
            when(this.mockApi.deactivateItemMaster(isA(Integer.class)))
                .thenThrow(new InventoryApiException("Item is already deactivated"));
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for deactivate a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_DEACTIVATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Failure to deactivate inventory item, 100",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Item is already deactivated", actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    
    @Test
    public void testSuccess_Add_Inventory_Retail_Override() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemRetailOverrideAddRequest.xml");

        try {
            when(this.mockApi.addInventoryOverride(isA(Integer.class),
                    isA(Integer[].class))).thenReturn(UPDATE_RC_EXISTING);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for adding a Inventory item retail override");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_RETAIL_OVERRIDE_ADD, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(UPDATE_RC_EXISTING, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item retail override was applied",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testSuccess_Remove_Inventory_Retail_Override() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemRetailOverrideRemoveRequest.xml");

        try {
            when(this.mockApi.removeInventoryOverride(isA(Integer.class),
                    isA(Integer[].class))).thenReturn(UPDATE_RC_EXISTING);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for adding a Inventory item retail override");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_RETAIL_OVERRIDE_REMOVE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(UPDATE_RC_EXISTING, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item retail override was removed",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_Add_Inventory_Retail_Override_Items_Empty() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemRetailOverrideAddEmptyItemsRequest.xml");
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_RETAIL_OVERRIDE_ADD, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE,
                actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("A valid list of item id's is required when adding or removing item retail override",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_Add_Inventory_Retail_Override_Items_Null() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemRetailOverrideAddNullItemsRequest.xml");
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_RETAIL_OVERRIDE_ADD, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE,
                actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("A valid list of item id's is required when adding or removing item retail override",
                actualRepsonse.getReplyStatus().getMessage());
    }
}

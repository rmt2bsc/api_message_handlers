package org.rmt2.api.handler.inventory;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

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
import org.rmt2.jaxb.InventoryItemType;
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
public class ItemMasterQueryMessageHandlerTest extends BaseAccountingMessageHandlerTest {
    private static final int CREDITOR_ID = 1234567;
    private InventoryApiFactory mockApiFactory;
    private InventoryApi mockApi;


    /**
     * 
     */
    public ItemMasterQueryMessageHandlerTest() {
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
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemFetchRequest.xml");
        List<ItemMasterDto> mockListData = InventoryMockData.createMockItemMasterList();

        try {
            when(this.mockApi.getItem(isA(ItemMasterDto.class))).thenReturn(mockListData);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getInvItem().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item record(s) found",
                actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getInvItem().size(); ndx++) {
            InventoryItemType a = actualRepsonse.getProfile().getInvItem().get(ndx);
            Assert.assertNotNull(a.getItemId());
            Assert.assertEquals(100 + ndx, a.getItemId().intValue());
            Assert.assertNotNull(a.getCreditor());
            Assert.assertNotNull(a.getCreditor().getCreditorId());
            Assert.assertEquals(1351 + ndx, a.getCreditor().getCreditorId().intValue());
            Assert.assertEquals("Item" + (ndx + 1), a.getDescription());
            Assert.assertEquals("10" + ndx + "-111-111", a.getItemSerialNo());
            Assert.assertEquals("1111111" + ndx, a.getVendorItemNo());
            Assert.assertNotNull(a.getItemType());
            Assert.assertNotNull(a.getItemType().getItemTypeId());
            Assert.assertEquals(1, a.getItemType().getItemTypeId().intValue());
            Assert.assertEquals(0, a.getOverrideRetail().intValue());
            Assert.assertEquals(1, a.getActive().intValue());
            Assert.assertEquals((a.getQtyOnHand().intValue() * a.getUnitCost().doubleValue())
                            * a.getMarkup().doubleValue(), a.getRetailPrice().doubleValue(), 0);
            Assert.assertEquals(1 + ndx, a.getQtyOnHand().intValue());
            Assert.assertEquals(1.23, a.getUnitCost().doubleValue(), 0);
            Assert.assertEquals(5, a.getMarkup().doubleValue(), 0);
        }
    }
    
    @Test
    public void testSuccess_Fetch_VendorUnassignedItems() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/VendorUnassignedItemFetchRequest.xml");
        List<ItemMasterDto> mockListData = InventoryMockData.createMockItemMasterList();
        
        // Set creditor id the same for all items.
        for (ItemMasterDto item : mockListData) {
            item.setVendorId(CREDITOR_ID);
        }

        try {
            when(this.mockApi.getVendorUnassignItems(isA(Integer.class))).thenReturn(mockListData);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a Vendor Unassigned items");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_UNASSIGNED_ITEMS_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getInvItem().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Vendor unassigned item record(s) found for vendor id, " + CREDITOR_ID,
                actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getInvItem().size(); ndx++) {
            InventoryItemType a = actualRepsonse.getProfile().getInvItem().get(ndx);
            Assert.assertNotNull(a.getItemId());
            Assert.assertEquals(100 + ndx, a.getItemId().intValue());
            Assert.assertNotNull(a.getCreditor());
            Assert.assertNotNull(a.getCreditor().getCreditorId());
            Assert.assertEquals(CREDITOR_ID, a.getCreditor().getCreditorId().intValue());
            Assert.assertEquals("Item" + (ndx + 1), a.getDescription());
            Assert.assertEquals("10" + ndx + "-111-111", a.getItemSerialNo());
            Assert.assertEquals("1111111" + ndx, a.getVendorItemNo());
            Assert.assertNotNull(a.getItemType());
            Assert.assertNotNull(a.getItemType().getItemTypeId());
            Assert.assertEquals(1, a.getItemType().getItemTypeId().intValue());
            Assert.assertEquals(0, a.getOverrideRetail().intValue());
            Assert.assertEquals(1, a.getActive().intValue());
            Assert.assertEquals((a.getQtyOnHand().intValue() * a.getUnitCost().doubleValue())
                            * a.getMarkup().doubleValue(), a.getRetailPrice().doubleValue(), 0);
            Assert.assertEquals(1 + ndx, a.getQtyOnHand().intValue());
            Assert.assertEquals(1.23, a.getUnitCost().doubleValue(), 0);
            Assert.assertEquals(5, a.getMarkup().doubleValue(), 0);
        }
    }
    
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemFetchRequest.xml");
        try {
            when(this.mockApi.getItem(isA(ItemMasterDto.class))).thenReturn(null);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item data not found!",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemFetchRequest.xml");
        try {
            when(this.mockApi.getItem(isA(ItemMasterDto.class)))
                 .thenThrow(new InventoryApiException("Test validation error: selection criteria is required"));    
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_GET, request);
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
        Assert.assertEquals("Failure to retrieve inventory item(s)",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test validation error: selection criteria is required",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemIncorrectTransCodeRequest.xml");
        List<ItemMasterDto> mockListData = InventoryMockData.createMockItemMasterList();

        try {
            when(this.mockApi.getItem(isA(ItemMasterDto.class))).thenReturn(mockListData);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a Inventory item master Type");
        }
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
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
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(ItemApiHandler.ERROR_MSG_TRANS_NOT_FOUND
                        + "INCORRECT_TRAN_CODE",
                actualRepsonse.getReplyStatus().getMessage());
    }
    

    @Test
    public void testValidation_Fetch_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/item/ItemFetchMissingCriteriaRequest.xml");
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_GET, request);
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
    public void testValidation_Fetch_VendorUnassignedItems_CreditorId_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/VendorUnassignedItemFetchMissingCreditorIdRequest.xml");
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_UNASSIGNED_ITEMS_GET, request);
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
        Assert.assertEquals("Vendor Id is required for vendor unassigned item query operation",
                actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_Fetch_VendorUnassignedItems_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/VendorUnassignedItemFetchMissingCriteriaRequest.xml");
        
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_UNASSIGNED_ITEMS_GET, request);
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
        Assert.assertEquals("Vendor item selection criteria is required",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_InvalidRequest() {
        String request = RMT2File.getFileContentsAsString("xml/InvalidRequest.xml");
        MessageHandlerResults results = null;
        ItemApiHandler handler = new ItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_MASTER_GET, request);
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
}

package org.rmt2.api.handler.inventory;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.dto.VendorItemDto;
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
import org.rmt2.api.handlers.inventory.VendorItemApiHandler;
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
    VendorItemApiHandler.class, InventoryApiFactory.class, SystemConfigurator.class })
public class VendorItemUpdateMessageHandlerTest extends BaseAccountingMessageHandlerTest {
    private static final int UPDATE_RC = 3;
    private static final int UPDATE_VENDOR_ITEM_RC = 1;
    private static final int TEST_VENDOR_ID = 1234567;
    private InventoryApiFactory mockApiFactory;
    private InventoryApi mockApi;


    /**
     * 
     */
    public VendorItemUpdateMessageHandlerTest() {
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
    public void testSuccess_AssignVendorItems() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/AssignVendorItemsRequest.xml");

        try {
            when(this.mockApi.assignVendorItems(isA(Integer.class), isA(Integer[].class)))
                  .thenReturn(UPDATE_RC);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for assigning inventory items to vendor");
        }
        
        MessageHandlerResults results = null;
        VendorItemApiHandler handler = new VendorItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_ITEM_ASSIGN, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(UPDATE_RC + " inventory items were assigned to vendor, " + TEST_VENDOR_ID,
                actualRepsonse.getReplyStatus().getMessage());
    }
   
    @Test
    public void testError_AssignVendorItems_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/AssignVendorItemsRequest.xml");
        try {
            when(this.mockApi.assignVendorItems(isA(Integer.class), isA(Integer[].class)))
               .thenThrow(new InventoryApiException("Test validation error: selection criteria is required"));
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for assigning inventory items to vendor");
        }
        
        MessageHandlerResults results = null;
        VendorItemApiHandler handler = new VendorItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_ITEM_ASSIGN, request);
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
        Assert.assertEquals("Failure to assign inventory items to vendor, "
                        + TEST_VENDOR_ID, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test validation error: selection criteria is required",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testValidation_AssigneVendorItems_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/AssignVendorItemsMissingCriteriaRequest.xml");
        MessageHandlerResults results = null;
        VendorItemApiHandler handler = new VendorItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_ITEM_ASSIGN, request);
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
        Assert.assertEquals("Vendor item selection criteria is required for query operation",
                actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_AssigneVendorItems_ItemCriteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/AssignVendorItemsMissingItemCriteriaRequest.xml");
        MessageHandlerResults results = null;
        VendorItemApiHandler handler = new VendorItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_ITEM_ASSIGN, request);
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
        Assert.assertEquals("A valid list of item id's is required when assigning/removing items to a vendor",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_AssigneVendorItems_ItemCriteria_Empty() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/AssignVendorItemsItemCriteriaEmptyRequest.xml");
        MessageHandlerResults results = null;
        VendorItemApiHandler handler = new VendorItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_ITEM_ASSIGN, request);
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
        Assert.assertEquals("A valid list of item id's is required when assigning/removing items to a vendor",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_InvalidRequest() {
        String request = RMT2File.getFileContentsAsString("xml/InvalidRequest.xml");
        MessageHandlerResults results = null;
        VendorItemApiHandler handler = new VendorItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_ITEM_ASSIGN, request);
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
    public void testSuccess_RemoveVendorItems() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/RemoveVendorItemsRequest.xml");

        try {
            when(this.mockApi.removeVendorItems(isA(Integer.class), isA(Integer[].class)))
                  .thenReturn(UPDATE_RC);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for removing inventory items from vendor");
        }
        
        MessageHandlerResults results = null;
        VendorItemApiHandler handler = new VendorItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_ITEM_REMOVE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(UPDATE_RC + " inventory items were removed from vendor, " + TEST_VENDOR_ID,
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_RemoveVendorItems_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/RemoveVendorItemsRequest.xml");
        try {
            when(this.mockApi.removeVendorItems(isA(Integer.class), isA(Integer[].class)))
               .thenThrow(new InventoryApiException("Test validation error: selection criteria is required"));
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for assigning inventory items to vendor");
        }
        
        MessageHandlerResults results = null;
        VendorItemApiHandler handler = new VendorItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_ITEM_REMOVE, request);
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
        Assert.assertEquals("Failure to remove inventory items from vendor, "
                        + TEST_VENDOR_ID, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test validation error: selection criteria is required",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testValidation_RemoveVendorItems_Criteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/RemoveVendorItemsMissingCriteriaRequest.xml");
        MessageHandlerResults results = null;
        VendorItemApiHandler handler = new VendorItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_ITEM_REMOVE, request);
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
        Assert.assertEquals("Vendor item selection criteria is required for query operation",
                actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testValidation_RemoveVendorItems_ItemCriteria_Missing() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/RemoveVendorItemsMissingItemCriteriaRequest.xml");
        MessageHandlerResults results = null;
        VendorItemApiHandler handler = new VendorItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_ITEM_REMOVE, request);
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
        Assert.assertEquals("A valid list of item id's is required when assigning/removing items to a vendor",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testValidation_RemoveVendorItems_ItemCriteria_Empty() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/RemoveVendorItemsItemCriteriaEmptyRequest.xml");
        MessageHandlerResults results = null;
        VendorItemApiHandler handler = new VendorItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_ITEM_REMOVE, request);
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
        Assert.assertEquals("A valid list of item id's is required when assigning/removing items to a vendor",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testSuccess_UpdateVendorItem() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/UpdateVendorItemRequest.xml");

        try {
            when(this.mockApi.updateVendorItem(isA(VendorItemDto.class))).thenReturn(UPDATE_VENDOR_ITEM_RC);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for updating vendor inventory item");
        }
        
        MessageHandlerResults results = null;
        VendorItemApiHandler handler = new VendorItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_ITEM_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(UPDATE_VENDOR_ITEM_RC,
                actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Vendor inventory item was updated",
                actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_UpdateVendorItem_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/vendoritem/UpdateVendorItemRequest.xml");
        try {
            when(this.mockApi.updateVendorItem(isA(VendorItemDto.class)))
               .thenThrow(new InventoryApiException("Test API error"));
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for updating inventory vendor item");
        }
        
        MessageHandlerResults results = null;
        VendorItemApiHandler handler = new VendorItemApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_VENDOR_ITEM_UPDATE, request);
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
        Assert.assertEquals("Failure to update vendor inventory item",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test API error", actualRepsonse.getReplyStatus().getExtMessage());
    }
}

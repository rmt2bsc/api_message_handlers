package org.rmt2.api.handler.inventory;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import org.dto.ItemMasterStatusHistDto;
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
import org.rmt2.api.handlers.inventory.ItemStatusHistoryApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.InventoryResponse;
import org.rmt2.jaxb.InventoryStatusHistoryType;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2Date;
import com.api.util.RMT2File;

/**
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class,
    ItemStatusHistoryApiHandler.class, InventoryApiFactory.class, SystemConfigurator.class })
public class ItemStatusHistoryTypeMessageHandlerTest extends BaseAccountingMessageHandlerTest {

    private InventoryApiFactory mockApiFactory;
    private InventoryApi mockApi;


    /**
     * 
     */
    public ItemStatusHistoryTypeMessageHandlerTest() {
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
        String request = RMT2File.getFileContentsAsString("xml/inventory/ItemStatusHist/ItemStatusHistoryFetchRequest.xml");
        List<ItemMasterStatusHistDto> mockListData = InventoryMockData.createMockItemStatusHistoryList();

        try {
            when(this.mockApi.getItemStatusHist(isA(ItemMasterStatusHistDto.class))).thenReturn(mockListData);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a Inventory item status history Type");
        }
        
        MessageHandlerResults results = null;
        ItemStatusHistoryApiHandler handler = new ItemStatusHistoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_STATUS_HIST_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getInvItemStatusHist().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item  status history record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getInvItemStatusHist().size(); ndx++) {
            InventoryStatusHistoryType a = actualRepsonse.getProfile().getInvItemStatusHist().get(ndx);
            Assert.assertNotNull(a.getStatusHistId());
            Assert.assertEquals(10 + ndx, a.getStatusHistId().intValue());
            Assert.assertNotNull(a.getItem());
            Assert.assertNotNull(a.getItem().getItemId());
            Assert.assertEquals(100 + ndx, a.getItem().getItemId().intValue());
            Assert.assertNotNull(a.getStatus());
            Assert.assertNotNull(a.getStatus().getItemStatusId());
            Assert.assertEquals(1000 + ndx, a.getStatus().getItemStatusId().intValue());
            Assert.assertEquals(12.50 + ndx, a.getUnitCost().doubleValue(), 0);
            Assert.assertEquals(3, a.getMarkup().doubleValue(), 0);
            Date dt = RMT2Date.stringToDate("2017-01-0" + (ndx + 1));
            Assert.assertEquals(dt, a.getEffectiveDate().toGregorianCalendar().getTime());
            dt = RMT2Date.stringToDate("2017-03-0" + (ndx + 1));
            Assert.assertEquals(dt, a.getEndDate().toGregorianCalendar().getTime());
            Assert.assertEquals("Item Status History Description " + (ndx + 1), a.getReason());
        }
    }
    
    @Test
    public void testSuccess_Fetch_Current() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/ItemStatusHist/CurrentItemStatusHistoryFetchRequest.xml");
        List<ItemMasterStatusHistDto> mockListData = InventoryMockData.createMockItemStatusHistoryList();

        try {
            when(this.mockApi.getCurrentItemStatusHist(isA(Integer.class))).thenReturn(mockListData.get(0));
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a Inventory item status history Type");
        }
        
        MessageHandlerResults results = null;
        ItemStatusHistoryApiHandler handler = new ItemStatusHistoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_CURRENT_STATUS_HIST_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        InventoryResponse actualRepsonse = 
                (InventoryResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getInvItemStatusHist().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Current inventory item status history record found", actualRepsonse.getReplyStatus().getMessage());
        
        InventoryStatusHistoryType a = actualRepsonse.getProfile().getInvItemStatusHist().get(0);
        Assert.assertNotNull(a.getStatusHistId());
        Assert.assertEquals(10, a.getStatusHistId().intValue());
        Assert.assertNotNull(a.getItem());
        Assert.assertNotNull(a.getItem().getItemId());
        Assert.assertEquals(100, a.getItem().getItemId().intValue());
        Assert.assertNotNull(a.getStatus());
        Assert.assertNotNull(a.getStatus().getItemStatusId());
        Assert.assertEquals(1000, a.getStatus().getItemStatusId().intValue());
        Assert.assertEquals(12.50, a.getUnitCost().doubleValue(), 0);
        Assert.assertEquals(3, a.getMarkup().doubleValue(), 0);
        Date dt = RMT2Date.stringToDate("2017-01-01");
        Assert.assertEquals(dt, a.getEffectiveDate().toGregorianCalendar().getTime());
        dt = RMT2Date.stringToDate("2017-03-01");
        Assert.assertEquals(dt, a.getEndDate().toGregorianCalendar().getTime());
        Assert.assertEquals("Item Status History Description 1", a.getReason());
    }
    
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/ItemStatusHist/ItemStatusHistoryFetchRequest.xml");
        List<ItemMasterStatusHistDto> mockListData = InventoryMockData.createMockItemStatusHistoryList();

        try {
            when(this.mockApi.getItemStatusHist(isA(ItemMasterStatusHistDto.class))).thenReturn(null);
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a Inventory item status history Type");
        }
        
        MessageHandlerResults results = null;
        ItemStatusHistoryApiHandler handler = new ItemStatusHistoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_STATUS_HIST_GET, request);
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
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Inventory item status history data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/ItemStatusHist/ItemStatusHistoryFetchRequest.xml");
        List<ItemMasterStatusHistDto> mockListData = InventoryMockData.createMockItemStatusHistoryList();

        try {
            when(this.mockApi.getItemStatusHist(isA(ItemMasterStatusHistDto.class)))
                 .thenThrow(new InventoryApiException("Test validation error: selection criteria is required"));
        } catch (InventoryApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a Inventory item status history Type");
        }
        
        MessageHandlerResults results = null;
        ItemStatusHistoryApiHandler handler = new ItemStatusHistoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_STATUS_HIST_GET, request);
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
        Assert.assertEquals("Failure to retrieve inventory item status history", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Test validation error: selection criteria is required",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/inventory/ItemStatusHist/ItemStatusHistoryIncorrectTransCodeRequest.xml");
        
        MessageHandlerResults results = null;
        ItemStatusHistoryApiHandler handler = new ItemStatusHistoryApiHandler();
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
        String request = RMT2File.getFileContentsAsString("xml/inventory/ItemStatusHist/ItemStatusHistoryFetchMissingCriteriaRequest.xml");
        
        MessageHandlerResults results = null;
        ItemStatusHistoryApiHandler handler = new ItemStatusHistoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_STATUS_HIST_GET, request);
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
        Assert.assertEquals("Inventory item status history selection criteria is required for query operation", actualRepsonse.getReplyStatus().getMessage());
        
    }

    @Test
    public void testValidation_InvalidRequest() {
        String request = RMT2File.getFileContentsAsString("xml/InvalidRequest.xml");
        MessageHandlerResults results = null;
        ItemStatusHistoryApiHandler handler = new ItemStatusHistoryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.INVENTORY_ITEM_STATUS_HIST_GET, request);
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

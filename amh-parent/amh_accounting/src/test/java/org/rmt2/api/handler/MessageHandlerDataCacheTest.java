package org.rmt2.api.handler;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.List;

import org.dao.transaction.XactDao;
import org.dao.transaction.XactDaoFactory;
import org.dto.XactTypeDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.transaction.XactApi;
import org.modules.transaction.XactApiException;
import org.modules.transaction.XactApiFactory;
import org.modules.transaction.XactConst;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handlers.AccountingtMsgHandlerUtility;

import com.api.config.SystemConfigurator;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;

/**
 * Tests the Message handler cache mechanisms
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class,
    XactApiFactory.class, SystemConfigurator.class })
public class MessageHandlerDataCacheTest {
    
    private static final int TOTAL_RECORDS = 12;

    private XactApi mockApi;

    /**
     * 
     */
    public MessageHandlerDataCacheTest() {
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see testcases.messaging.MessageToListenerToHandlerTest#setUp()
     */
    @Before
    public void setUp() throws Exception {
        XactDaoFactory mockXactDaoFactory = Mockito.mock(XactDaoFactory.class);
        XactDao mockDao = Mockito.mock(XactDao.class);
        mockApi = Mockito.mock(XactApi.class);
        PowerMockito.mockStatic(XactApiFactory.class);
        when(mockXactDaoFactory.createRmt2OrmXactDao(isA(String.class))).thenReturn(mockDao);
        PowerMockito.when(XactApiFactory.createDefaultXactApi()).thenReturn(this.mockApi);
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

    @Before
    public void resetProgramState() throws Exception {
        Field field = AccountingtMsgHandlerUtility.class.getDeclaredField("XACTTYPE_CACHE");
        field.setAccessible(true);
        field.set(null, null);
    }
    
    @Test
    public void testSuccess_Initial_Load() {
        List<XactTypeDto> mockListData = HandlerCacheMockData.createMockXactTypes();

        try {
            when(this.mockApi.getXactTypes(null)).thenReturn(mockListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a treansaction type data");
        }
        
        int rc = AccountingtMsgHandlerUtility.loadXactTypeCache(false);
        
        Assert.assertEquals(TOTAL_RECORDS, rc);
    }
    
    @Test
    public void testSuccess_Previously_Initialized_Cache() {
        List<XactTypeDto> mockListData = HandlerCacheMockData.createMockXactTypes();

        try {
            when(this.mockApi.getXactTypes(null)).thenReturn(mockListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a treansaction type data");
        }
        
        int rc = AccountingtMsgHandlerUtility.loadXactTypeCache(false);
        Assert.assertEquals(TOTAL_RECORDS, rc);
        rc = AccountingtMsgHandlerUtility.loadXactTypeCache(false);
        Assert.assertEquals(0, rc);
    }
    
    @Test
    public void testSuccess_Override_Previously_Initialized_Cache() {
        List<XactTypeDto> mockListData = HandlerCacheMockData.createMockXactTypes();

        try {
            when(this.mockApi.getXactTypes(null)).thenReturn(mockListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a treansaction type data");
        }
        
        int rc = AccountingtMsgHandlerUtility.loadXactTypeCache(false);
        Assert.assertEquals(TOTAL_RECORDS, rc);
        rc = AccountingtMsgHandlerUtility.loadXactTypeCache(true);
        Assert.assertEquals(TOTAL_RECORDS, rc);
    }
    
    
    @Test
    public void testSuccess_Fetch_Code() {
        List<XactTypeDto> mockListData = HandlerCacheMockData.createMockXactTypes();

        try {
            when(this.mockApi.getXactTypes(null)).thenReturn(mockListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a treansaction type data");
        }
        
        int rc = AccountingtMsgHandlerUtility.loadXactTypeCache(false);
        Assert.assertEquals(TOTAL_RECORDS, rc);
        
        String result = AccountingtMsgHandlerUtility.getXactTypeCodeFromCache(XactConst.XACT_TYPE_CASH_DISBURSE);
        Assert.assertNotNull(result);
        Assert.assertEquals("CASH_DISBURSE", result);
    }
    
    @Test
    public void testSuccess_Fetch_Description() {
        List<XactTypeDto> mockListData = HandlerCacheMockData.createMockXactTypes();

        try {
            when(this.mockApi.getXactTypes(null)).thenReturn(mockListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a treansaction type data");
        }
        
        int rc = AccountingtMsgHandlerUtility.loadXactTypeCache(false);
        Assert.assertEquals(TOTAL_RECORDS, rc);
        
        String result = AccountingtMsgHandlerUtility.getXactTypeDescriptionFromCache(XactConst.XACT_TYPE_CASH_DISBURSE);
        Assert.assertNotNull(result);
        Assert.assertEquals("XACT_TYPE_CASH_DISBURSE", result);
    }
    
    @Test
    public void testError_Fetch_Code_Cache_Not_Initialized() {
        List<XactTypeDto> mockListData = HandlerCacheMockData.createMockXactTypes();

        try {
            when(this.mockApi.getXactTypes(null)).thenReturn(mockListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a treansaction type data");
        }
        
        String result = AccountingtMsgHandlerUtility.getXactTypeCodeFromCache(XactConst.XACT_TYPE_CASH_DISBURSE);
        Assert.assertNull(result);
    }
    
    @Test
    public void testError_Fetch_Description_Cache_Not_Initialized() {
        List<XactTypeDto> mockListData = HandlerCacheMockData.createMockXactTypes();

        try {
            when(this.mockApi.getXactTypes(null)).thenReturn(mockListData);
        } catch (XactApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a treansaction type data");
        }
        
        String result = AccountingtMsgHandlerUtility.getXactTypeDescriptionFromCache(XactConst.XACT_TYPE_CASH_DISBURSE);
        Assert.assertNull(result);
    }
    
}

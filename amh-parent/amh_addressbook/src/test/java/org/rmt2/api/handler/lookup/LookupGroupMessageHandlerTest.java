package org.rmt2.api.handler.lookup;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.dao.lookup.LookupDaoException;
import org.dao.mapping.orm.rmt2.GeneralCodesGroup;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.lookup.LookupDataApi;
import org.modules.lookup.LookupDataApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseMessageHandlerTest;
import org.rmt2.api.handlers.lookup.LookupGroupApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.LookupCodesResponse;

import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.messaging.webservice.WebServiceConstants;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.DatabaseException;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2File;

/**
 * Tests the API Handler for Lookup Group API functionality.
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class })
public class LookupGroupMessageHandlerTest extends BaseMessageHandlerTest {

    private static final int GROUP_ID = 555;
    private LookupDataApiFactory mockApiFactory;
    private LookupDataApi mockApi;

    /**
     * 
     */
    public LookupGroupMessageHandlerTest() {
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
    }

    private void setupMockApiCall() {
      this.mockApiFactory = Mockito.mock(LookupDataApiFactory.class);
      this.mockApi = Mockito.mock(LookupDataApi.class);
      try {
          PowerMockito.whenNew(LookupDataApiFactory.class).withNoArguments().thenReturn(this.mockApiFactory);
      } catch (Exception e) {
          e.printStackTrace();
      }
      when(this.mockApiFactory.createApi()).thenReturn(this.mockApi);
    }
    
    private GeneralCodesGroup createMockFetchSingleResponse() {
        GeneralCodesGroup p = new GeneralCodesGroup();
        p.setCodeGrpId(100);
        p.setDescription("Group1");
        return p;
    }
    
    private List<GeneralCodesGroup> createMockFetchAllResponse() {
        List<GeneralCodesGroup> list = new ArrayList<GeneralCodesGroup>();
        GeneralCodesGroup p = new GeneralCodesGroup();
        p.setCodeGrpId(100);
        p.setDescription("Group1");
        list.add(p);

        p = new GeneralCodesGroup();
        p.setCodeGrpId(101);
        p.setDescription("Group2");
        list.add(p);

        p = new GeneralCodesGroup();
        p.setCodeGrpId(102);
        p.setDescription("Group3");
        list.add(p);

        p = new GeneralCodesGroup();
        p.setCodeGrpId(103);
        p.setDescription("Group4");
        list.add(p);
        return list;
    }
    
    private void setupMockForFetch() {
        try {
            when(this.mockPersistenceClient.retrieveList(any(GeneralCodesGroup.class)))
                    .thenReturn(this.createMockFetchAllResponse());
        } catch (LookupDaoException e) {
            e.printStackTrace();
            Assert.fail("All lookup code group fetch test case failed");
        }
    }
    
    private void setupMockForInsert() {
        try {
            when(this.mockPersistenceClient.insertRow(any(GeneralCodesGroup.class), any(Boolean.class)))
                    .thenReturn(GROUP_ID);
        } catch (LookupDaoException e) {
            e.printStackTrace();
            Assert.fail("Lookup Code Group insert test case failed setting up mock insert call");
        }
    }
    
    private void setupMockForUpdate() {
        try {
            when(this.mockPersistenceClient.retrieveObject(isA(GeneralCodesGroup.class)))
                            .thenReturn(this.createMockFetchSingleResponse());
        } catch (LookupDaoException e) {
            e.printStackTrace();
            Assert.fail("Lookup Code Group update test case failed setting up mock retrieve call");
        }
        try {
            when(this.mockPersistenceClient.updateRow(any(GeneralCodesGroup.class))).thenReturn(1);
        } catch (LookupDaoException e) {
            e.printStackTrace();
            Assert.fail("Lookup Code Group update test case failed setting up mock update call");
        }
    }
    
    private void setupMockForDelete() {
        try {
            when(this.mockPersistenceClient.deleteRow(isA(GeneralCodesGroup.class))).thenReturn(1);
        } catch (LookupDaoException e) {
            e.printStackTrace();
            Assert.fail("Lookup Code Group update test case failed setting up mock retrieve call");
        }
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
    public void testSuccess_FetchMultiple() {
        String request = RMT2File.getFileContentsAsString("xml/lookup/LookupCodeSearchRequest.xml");
        this.setupMockForFetch();
        
        MessageHandlerResults results = null;
        LookupGroupApiHandler handler = new LookupGroupApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.LOOKUP_GROUP_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        LookupCodesResponse actualRepsonse = 
                (LookupCodesResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getGroupCodes());
        Assert.assertNotNull(actualRepsonse.getDetailCodes());
        Assert.assertTrue(actualRepsonse.getDetailCodes().isEmpty());
        Assert.assertEquals(4, actualRepsonse.getGroupCodes().size());
        Assert.assertEquals(4, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals("SUCCESS", actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Group Lookup record(s) found", actualRepsonse.getReplyStatus().getMessage());
        for (int ndx = 0; ndx < actualRepsonse.getGroupCodes().size(); ndx++) {
            Assert.assertEquals(100 + (ndx),
                    actualRepsonse.getGroupCodes().get(ndx).getGroupId().intValue());
            Assert.assertEquals("Group" + (ndx+1),
                    actualRepsonse.getGroupCodes().get(ndx).getGroupDesc());
        }
    }
    
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/lookup/LookupCodeSearchRequest.xml");
        try {
            when(this.mockPersistenceClient.retrieveList(any(GeneralCodesGroup.class))).thenReturn(null);
        } catch (LookupDaoException e) {
            e.printStackTrace();
            Assert.fail("All lookup code group fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        LookupGroupApiHandler handler = new LookupGroupApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.LOOKUP_GROUP_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        LookupCodesResponse actualRepsonse = 
                (LookupCodesResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getGroupCodes());
        Assert.assertNotNull(actualRepsonse.getDetailCodes());
        Assert.assertTrue(actualRepsonse.getGroupCodes().isEmpty());
        Assert.assertTrue(actualRepsonse.getDetailCodes().isEmpty());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals("SUCCESS", actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Group Lookup data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/lookup/LookupCodeSearchRequest.xml");
        try {
            when(this.mockPersistenceClient.retrieveList(any(GeneralCodesGroup.class)))
                    .thenThrow(new DatabaseException("DB error occurred fetching Lookup Groups"));
        } catch (LookupDaoException e) {
            e.printStackTrace();
            Assert.fail("All lookup code group fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        LookupGroupApiHandler handler = new LookupGroupApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.LOOKUP_GROUP_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        LookupCodesResponse actualRepsonse = 
                (LookupCodesResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getGroupCodes());
        Assert.assertNotNull(actualRepsonse.getDetailCodes());
        Assert.assertTrue(actualRepsonse.getGroupCodes().isEmpty());
        Assert.assertTrue(actualRepsonse.getDetailCodes().isEmpty());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals("ERROR", actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Failure to retrieve Lookup Group(s)", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Unable to fetch List of Code Groups using selection criteria contained in DTO",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testSuccess_Update() {
        String request = RMT2File.getFileContentsAsString("xml/lookup/LookupGroupUpdateRequest.xml");
        this.setupMockForUpdate();
        
        MessageHandlerResults results = null;
        LookupGroupApiHandler handler = new LookupGroupApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.LOOKUP_GROUP_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        LookupCodesResponse actualRepsonse = 
                (LookupCodesResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getGroupCodes());
        Assert.assertNotNull(actualRepsonse.getDetailCodes());
        Assert.assertTrue(!actualRepsonse.getGroupCodes().isEmpty());
        Assert.assertTrue(actualRepsonse.getDetailCodes().isEmpty());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals("SUCCESS", actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Lookup Group was modified successfully", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Total number of rows modified: " + actualRepsonse.getReplyStatus().getReturnCode().intValue(),
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testError_Update_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/lookup/LookupGroupUpdateRequest.xml");
        this.setupMockForUpdate();
        try {
            when(this.mockPersistenceClient.retrieveObject(isA(GeneralCodesGroup.class))).thenReturn(null);
        } catch (LookupDaoException e) {
            e.printStackTrace();
            Assert.fail("Lookup Code Group update test case failed setting up mock retrieve call");
        }
        
        MessageHandlerResults results = null;
        LookupGroupApiHandler handler = new LookupGroupApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.LOOKUP_GROUP_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        LookupCodesResponse actualRepsonse = 
                (LookupCodesResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getGroupCodes());
        Assert.assertNotNull(actualRepsonse.getDetailCodes());
        Assert.assertTrue(!actualRepsonse.getGroupCodes().isEmpty());
        Assert.assertTrue(actualRepsonse.getDetailCodes().isEmpty());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_ERROR, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Failure to update existing Lookup Group", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertTrue(actualRepsonse.getReplyStatus().getExtMessage().contains(
                        "Lookup group targeted for update does not exist [group id="));
    }
    
    @Test
    public void testSuccess_Insert() {
        String request = RMT2File.getFileContentsAsString("xml/lookup/LookupGroupInsertRequest.xml");
        this.setupMockForInsert();
        
        MessageHandlerResults results = null;
        LookupGroupApiHandler handler = new LookupGroupApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.LOOKUP_GROUP_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        LookupCodesResponse actualRepsonse = 
                (LookupCodesResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getGroupCodes());
        Assert.assertNotNull(actualRepsonse.getDetailCodes());
        Assert.assertTrue(!actualRepsonse.getGroupCodes().isEmpty());
        Assert.assertTrue(actualRepsonse.getDetailCodes().isEmpty());
        Assert.assertEquals(GROUP_ID, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Lookup Group was created successfully", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("The new group id is " + actualRepsonse.getReplyStatus().getReturnCode().intValue(),
                actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/lookup/LookupGroupIncorrectTransRequest.xml");

        MessageHandlerResults results = null;
        LookupGroupApiHandler handler = new LookupGroupApiHandler();
        try {
            results = handler.processMessage("INCORRECT_TRANS_CODE", request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        LookupCodesResponse actualRepsonse = 
                (LookupCodesResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getGroupCodes());
        Assert.assertNotNull(actualRepsonse.getDetailCodes());
        Assert.assertTrue(actualRepsonse.getGroupCodes().isEmpty());
        Assert.assertTrue(actualRepsonse.getDetailCodes().isEmpty());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_ERROR, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Unable to identify transaction code: INCORRECT_TRANS_CODE", actualRepsonse.getReplyStatus().getMessage());
    }
    
    
    @Test
    public void testSuccess_Delete() {
        String request = RMT2File.getFileContentsAsString("xml/lookup/LookupGroupDeleteRequest.xml");
        this.setupMockForDelete();
        
        MessageHandlerResults results = null;
        LookupGroupApiHandler handler = new LookupGroupApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.LOOKUP_GROUP_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        LookupCodesResponse actualRepsonse = 
                (LookupCodesResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getGroupCodes());
        Assert.assertNotNull(actualRepsonse.getDetailCodes());
        Assert.assertTrue(actualRepsonse.getDetailCodes().isEmpty());
        Assert.assertTrue(actualRepsonse.getGroupCodes().isEmpty());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Lookup Group was deleted successfully", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Lookup Group Id deleted was 200", actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testError_Delete_Invalid_GroupId() {
        String request = RMT2File.getFileContentsAsString("xml/lookup/LookupGroupDeleteInvalidGroupIdRequest.xml");
        
        this.setupMockForDelete();
        
        MessageHandlerResults results = null;
        LookupGroupApiHandler handler = new LookupGroupApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.LOOKUP_GROUP_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        LookupCodesResponse actualRepsonse = 
                (LookupCodesResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getGroupCodes());
        Assert.assertNotNull(actualRepsonse.getDetailCodes());
        Assert.assertTrue(actualRepsonse.getDetailCodes().isEmpty());
        Assert.assertTrue(actualRepsonse.getGroupCodes().isEmpty());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_ERROR, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Failure to delelte Lookup Group by group id, 0", actualRepsonse.getReplyStatus().getMessage());
    }
    
}

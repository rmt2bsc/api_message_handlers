package org.rmt2.api.handler.postal;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.CountryRegionDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.AddressBookConstants;
import org.modules.postal.PostalApi;
import org.modules.postal.PostalApiException;
import org.modules.postal.PostalApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.AddressBookMockData;
import org.rmt2.api.handler.BaseMessageHandlerTest;
import org.rmt2.api.handlers.postal.RegionApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.PostalResponse;

import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.messaging.webservice.WebServiceConstants;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2File;

/**
 * Tests the API Handler for Region/State/Province API functionality.
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, PostalApiFactory.class })
public class RegionMessageHandlerTest extends BaseMessageHandlerTest {

    private PostalApi mockApi;
  

    /**
     * 
     */
    public RegionMessageHandlerTest() {
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
      this.mockApi = Mockito.mock(PostalApi.class);
      PowerMockito.mockStatic(PostalApiFactory.class);
      when(PostalApiFactory.createApi(eq(AddressBookConstants.APP_NAME))).thenReturn(this.mockApi);
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
    public void testSuccess_Fetch_Multiple() {
        this.setupMockApiCall();
        
        String request = RMT2File.getFileContentsAsString("xml/postal/RegionSearchRequest.xml");
        
        try {
            List<CountryRegionDto> apiResults = AddressBookMockData.createMockCountryRegionDto();
            when(this.mockApi.getCountryRegion(isA(CountryRegionDto.class))).thenReturn(apiResults);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All Region/State/Province fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        RegionApiHandler handler = new RegionApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.REGION_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        Assert.assertEquals(5, results.getReturnCode());

        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getStates());
        Assert.assertEquals(5, actualRepsonse.getStates().size());
        Assert.assertEquals(actualRepsonse.getReplyStatus().getReturnCode().intValue(),
                actualRepsonse.getStates().size());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Region/State/Province record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getStates().size(); ndx++) {
            Assert.assertEquals(10 + (ndx), actualRepsonse.getStates().get(ndx).getStateId().intValue());
            Assert.assertEquals(100, actualRepsonse.getStates().get(ndx).getCountryId().intValue());
            Assert.assertEquals("CountryName1", actualRepsonse.getStates().get(ndx).getCountryName());
            Assert.assertEquals("StateName" + (ndx+1), actualRepsonse.getStates().get(ndx).getStateName());
            Assert.assertEquals("AbbrCode" + (ndx+1), actualRepsonse.getStates().get(ndx).getStateCode());
        }
    }
    
    
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        this.setupMockApiCall();
        String request = RMT2File.getFileContentsAsString("xml/postal/RegionSearchRequest.xml");
        try {
            when(this.mockApi.getCountryRegion(isA(CountryRegionDto.class))).thenReturn(null);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All Region fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        RegionApiHandler handler = new RegionApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.REGION_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getStates());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("No Region/State/Province data found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        this.setupMockApiCall();
        String request = RMT2File.getFileContentsAsString("xml/postal/RegionSearchRequest.xml");
        try {
            when(this.mockApi.getCountryRegion(isA(CountryRegionDto.class)))
                .thenThrow(new PostalApiException("A region API error occurred"));
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All Region fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        RegionApiHandler handler = new RegionApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.REGION_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_ERROR,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Failure to retrieve Region/State/Province data", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("A region API error occurred", actualRepsonse.getReplyStatus().getExtMessage());
    }
 
}

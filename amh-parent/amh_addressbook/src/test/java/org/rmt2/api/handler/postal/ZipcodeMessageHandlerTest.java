package org.rmt2.api.handler.postal;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.ZipcodeDto;
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
import org.rmt2.api.handler.BaseAddressBookMessageHandlerTest;
import org.rmt2.api.handlers.postal.ZipCodeApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.PostalResponse;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.messaging.webservice.WebServiceConstants;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2File;

/**
 * Tests the API Handler for Zipcode API functionality.
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, PostalApiFactory.class, SystemConfigurator.class })
public class ZipcodeMessageHandlerTest extends BaseAddressBookMessageHandlerTest {

    private PostalApi mockApi;
  

    /**
     * 
     */
    public ZipcodeMessageHandlerTest() {
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
    public void testSuccess_Fetch_FullFormat_Multiple() {
        this.setupMockApiCall();
        
        String request = RMT2File.getFileContentsAsString("xml/postal/ZipcodeSearchFullRequest.xml");
        
        try {
            List<ZipcodeDto> apiResults = AddressBookMockData.createMockFetchZipDtoResults();
            when(this.mockApi.getZipCode(isA(ZipcodeDto.class))).thenReturn(apiResults);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All zipcode fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        ZipCodeApiHandler handler = new ZipCodeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ZIPCODE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        Assert.assertEquals(5, results.getReturnCode());

        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getZipFull());
        Assert.assertTrue(!actualRepsonse.getZipFull().isEmpty());
        Assert.assertEquals(5, actualRepsonse.getZipFull().size());
        Assert.assertEquals(actualRepsonse.getReplyStatus().getReturnCode().intValue(),
                actualRepsonse.getZipFull().size());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Zipcode record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getZipFull().size(); ndx++) {
            Assert.assertEquals(75231 + (ndx), actualRepsonse.getZipFull().get(ndx).getZipId().intValue());
            Assert.assertEquals(75231 + (ndx), actualRepsonse.getZipFull().get(ndx).getZipcode().intValue());
            Assert.assertEquals("City" + (ndx+1), actualRepsonse.getZipFull().get(ndx).getCity());
            Assert.assertEquals("State" + (ndx+1), actualRepsonse.getZipFull().get(ndx).getState());
            Assert.assertEquals("AreaCode" + (ndx+1), actualRepsonse.getZipFull().get(ndx).getAreaCode());
            Assert.assertEquals("County" + (ndx+1), actualRepsonse.getZipFull().get(ndx).getCountyName());
            Assert.assertEquals(382372382323.3883828, actualRepsonse.getZipFull().get(ndx).getLatitude(), 0);
            Assert.assertEquals(48484848.4843949, actualRepsonse.getZipFull().get(ndx).getLongitude(), 0);
            Assert.assertEquals(239000, actualRepsonse.getZipFull().get(ndx).getBlackPopulation(), 0);
            Assert.assertEquals(10000000, actualRepsonse.getZipFull().get(ndx).getWhitePopulation(), 0);
            Assert.assertEquals(30000, actualRepsonse.getZipFull().get(ndx).getHispanicPopulation(), 0);
        }
    }
    
    @Test
    public void testSuccess_Fetch_ShortFormat_Multiple() {
        this.setupMockApiCall();
        
        String request = RMT2File.getFileContentsAsString("xml/postal/ZipcodeSearchShortRequest.xml");
        
        try {
            List<ZipcodeDto> apiResults = AddressBookMockData.createMockFetchZipDtoResults();
            when(this.mockApi.getZipCode(isA(ZipcodeDto.class))).thenReturn(apiResults);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All zipcode fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        ZipCodeApiHandler handler = new ZipCodeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ZIPCODE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        Assert.assertEquals(5, results.getReturnCode());

        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getZipFull());
        Assert.assertTrue(!actualRepsonse.getZipShort().isEmpty());
        Assert.assertEquals(5, actualRepsonse.getZipShort().size());
        Assert.assertEquals(actualRepsonse.getReplyStatus().getReturnCode().intValue(),
                actualRepsonse.getZipShort().size());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Zipcode record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getZipShort().size(); ndx++) {
            Assert.assertEquals(75231 + (ndx), actualRepsonse.getZipShort().get(ndx).getZipId().intValue());
            Assert.assertEquals(75231 + (ndx), actualRepsonse.getZipShort().get(ndx).getZipcode().intValue());
            Assert.assertEquals("City" + (ndx+1), actualRepsonse.getZipShort().get(ndx).getCity());
            Assert.assertEquals("State" + (ndx+1), actualRepsonse.getZipShort().get(ndx).getState());
            Assert.assertEquals("AreaCode" + (ndx+1), actualRepsonse.getZipShort().get(ndx).getAreaCode());
            Assert.assertEquals("County" + (ndx+1), actualRepsonse.getZipShort().get(ndx).getCountyName());
        }
    }
    
    @Test
    public void testSuccess_Fetch_Result_Format_Default() {
        this.setupMockApiCall();
        
        String request = RMT2File.getFileContentsAsString("xml/postal/ZipcodeSearchResultFormatDefaultRequest.xml");
        
        try {
            List<ZipcodeDto> apiResults = AddressBookMockData.createMockFetchZipDtoResults();
            when(this.mockApi.getZipCode(isA(ZipcodeDto.class))).thenReturn(apiResults);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All zipcode fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        ZipCodeApiHandler handler = new ZipCodeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ZIPCODE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getZipFull());
        Assert.assertTrue(!actualRepsonse.getZipShort().isEmpty());
        Assert.assertEquals(5, actualRepsonse.getZipShort().size());
        Assert.assertEquals(actualRepsonse.getReplyStatus().getReturnCode().intValue(),
                actualRepsonse.getZipShort().size());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Zipcode record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getZipShort().size(); ndx++) {
            Assert.assertEquals(75231 + (ndx), actualRepsonse.getZipShort().get(ndx).getZipId().intValue());
            Assert.assertEquals(75231 + (ndx), actualRepsonse.getZipShort().get(ndx).getZipcode().intValue());
            Assert.assertEquals("City" + (ndx+1), actualRepsonse.getZipShort().get(ndx).getCity());
            Assert.assertEquals("State" + (ndx+1), actualRepsonse.getZipShort().get(ndx).getState());
            Assert.assertEquals("AreaCode" + (ndx+1), actualRepsonse.getZipShort().get(ndx).getAreaCode());
            Assert.assertEquals("County" + (ndx+1), actualRepsonse.getZipShort().get(ndx).getCountyName());
        }
    }
    
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        this.setupMockApiCall();
        String request = RMT2File.getFileContentsAsString("xml/postal/ZipcodeSearchShortRequest.xml");
        try {
            when(this.mockApi.getZipCode(isA(ZipcodeDto.class))).thenReturn(null);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All zipcode fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        ZipCodeApiHandler handler = new ZipCodeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ZIPCODE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getZipFull());
        Assert.assertTrue(actualRepsonse.getZipShort().isEmpty());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("No Zipcode data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        this.setupMockApiCall();
        String request = RMT2File.getFileContentsAsString("xml/postal/ZipcodeSearchShortRequest.xml");
        try {
            when(this.mockApi.getZipCode(isA(ZipcodeDto.class))).thenThrow(new PostalApiException("Postal API error occurred"));
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All zipcode fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        ZipCodeApiHandler handler = new ZipCodeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ZIPCODE_GET, request);
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
        Assert.assertEquals("Failure to retrieve Zipcode data", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Postal API error occurred", actualRepsonse.getReplyStatus().getExtMessage());
    }
 
    @Test
    public void testError_Fetch_Result_Format_Not_Included() {
        this.setupMockApiCall();
        
        String request = RMT2File.getFileContentsAsString("xml/postal/ZipcodeSearchResultFormatNotIncludedRequest.xml");
        
        try {
            List<ZipcodeDto> apiResults = AddressBookMockData.createMockFetchZipDtoResults();
            when(this.mockApi.getZipCode(isA(ZipcodeDto.class))).thenReturn(apiResults);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All zipcode fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        ZipCodeApiHandler handler = new ZipCodeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ZIPCODE_GET, request);
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
        Assert.assertEquals("Failure to retrieve Zipcode data", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("The Result Format indicator is required and must be valid", actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testError_Fetch_Result_Format_Invalid_Value() {
        this.setupMockApiCall();
        
        String request = RMT2File.getFileContentsAsString("xml/postal/ZipcodeSearchInvalidResultFormatRequest.xml");
        
        try {
            List<ZipcodeDto> apiResults = AddressBookMockData.createMockFetchZipDtoResults();
            when(this.mockApi.getZipCode(isA(ZipcodeDto.class))).thenReturn(apiResults);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All zipcode fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        ZipCodeApiHandler handler = new ZipCodeApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.ZIPCODE_GET, request);
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
        Assert.assertEquals("Failure to retrieve Zipcode data", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("The Result Format indicator is required and must be valid", actualRepsonse.getReplyStatus().getExtMessage());
    }
}

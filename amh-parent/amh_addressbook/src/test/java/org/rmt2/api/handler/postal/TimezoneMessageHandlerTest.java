package org.rmt2.api.handler.postal;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.TimeZoneDto;
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
import org.rmt2.api.handlers.postal.TimezoneApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.PostalResponse;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.messaging.webservice.WebServiceConstants;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2File;

/**
 * Tests the API Handler for Timezone API functionality.
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class,
        PostalApiFactory.class, SystemConfigurator.class })
public class TimezoneMessageHandlerTest extends BaseAddressBookMessageHandlerTest {

    private PostalApi mockApi;
  

    /**
     * 
     */
    public TimezoneMessageHandlerTest() {
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
    public void testSuccess_Fetch_With_Criteria() {
        this.setupMockApiCall();
        
        String request = RMT2File.getFileContentsAsString("xml/postal/TimezoneSearchRequest.xml");
        
        try {
            List<TimeZoneDto> apiResults = AddressBookMockData.createMockTimezoneDto();
            when(this.mockApi.getTimezone(isA(TimeZoneDto.class))).thenReturn(apiResults);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All Timezone fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        TimezoneApiHandler handler = new TimezoneApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.TIMEZONE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        Assert.assertEquals(5, results.getReturnCode());

        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getTimezones());
        Assert.assertEquals(results.getReturnCode(), actualRepsonse.getTimezones().size());
        Assert.assertEquals(actualRepsonse.getReplyStatus().getReturnCode().intValue(),
                actualRepsonse.getTimezones().size());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Timezone record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getTimezones().size(); ndx++) {
            Assert.assertEquals(1000 + (ndx), actualRepsonse.getTimezones().get(ndx).getTimezoneId().intValue());
            Assert.assertEquals("Timezone" + (ndx+1), actualRepsonse.getTimezones().get(ndx).getTimeszoneDesc());
        }
    }
    
    @Test
    public void testSuccess_Fetch_With_No_Criteria() {
        this.setupMockApiCall();
        
        String request = RMT2File.getFileContentsAsString("xml/postal/TimezoneSearchAllRequest.xml");
        
        try {
            List<TimeZoneDto> apiResults = AddressBookMockData.createMockTimezoneDto();
            when(this.mockApi.getTimezone(isA(TimeZoneDto.class))).thenReturn(apiResults);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All Timezone fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        TimezoneApiHandler handler = new TimezoneApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.TIMEZONE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        Assert.assertEquals(5, results.getReturnCode());

        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getTimezones());
        Assert.assertEquals(results.getReturnCode(), actualRepsonse.getTimezones().size());
        Assert.assertEquals(actualRepsonse.getReplyStatus().getReturnCode().intValue(),
                actualRepsonse.getTimezones().size());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Timezone record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getTimezones().size(); ndx++) {
            Assert.assertEquals(1000 + (ndx), actualRepsonse.getTimezones().get(ndx).getTimezoneId().intValue());
            Assert.assertEquals("Timezone" + (ndx+1), actualRepsonse.getTimezones().get(ndx).getTimeszoneDesc());
        }
    }
    
    
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        this.setupMockApiCall();
        String request = RMT2File.getFileContentsAsString("xml/postal/TimezoneSearchRequest.xml");
        
        try {
            when(this.mockApi.getTimezone(isA(TimeZoneDto.class))).thenReturn(null);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All Timezone fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        TimezoneApiHandler handler = new TimezoneApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.TIMEZONE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
                
        Assert.assertNotNull(results);
        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getTimezones());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("No Timezone data found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        this.setupMockApiCall();
        String request = RMT2File.getFileContentsAsString("xml/postal/TimezoneSearchRequest.xml");
        
        try {
            when(this.mockApi.getTimezone(isA(TimeZoneDto.class)))
                .thenThrow(new PostalApiException("A Timezone API error occurred"));
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All Timezone fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        TimezoneApiHandler handler = new TimezoneApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.TIMEZONE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Failure to retrieve Timezone data", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("A Timezone API error occurred", actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testError_Fetch_Bad_Trans_Code() {
        this.setupMockApiCall();
        String request = RMT2File.getFileContentsAsString("xml/postal/TimezoneSearchInvalidTransCodeRequest.xml");
        
        try {
            when(this.mockApi.getTimezone(isA(TimeZoneDto.class)))
                .thenThrow(new PostalApiException("A Timezone API error occurred"));
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All Timezone fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        TimezoneApiHandler handler = new TimezoneApiHandler();
        try {
            results = handler.processMessage("INVALID_TRANS_CODE", request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_BAD_REQUEST,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Unable to identify transaction code: INVALID_TRANS_CODE", actualRepsonse.getReplyStatus().getMessage());
    }
 
    
    @Test
    public void testValidation_Fetch_Null_Criteria() {
        this.setupMockApiCall();
        String request = RMT2File.getFileContentsAsString("xml/postal/TimezoneSearchNullCriteriaRequest.xml");
        
        try {
            when(this.mockApi.getTimezone(isA(TimeZoneDto.class)))
                .thenThrow(new PostalApiException("A Timezone API error occurred"));
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All Timezone fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        TimezoneApiHandler handler = new TimezoneApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.TIMEZONE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        
        Assert.assertNotNull(results);
        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_BAD_REQUEST,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("PostalRequest Timezone criteria element is required", actualRepsonse.getReplyStatus().getMessage());
    }
    
}

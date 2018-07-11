package org.rmt2.api.handler.postal;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.CountryDto;
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
import org.rmt2.api.handlers.postal.CountryApiHandler;
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
 * Tests the API Handler for Country API functionality.
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, PostalApiFactory.class, SystemConfigurator.class })
public class CountryMessageHandlerTest extends BaseMessageHandlerTest {

    private PostalApi mockApi;
  

    /**
     * 
     */
    public CountryMessageHandlerTest() {
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
        
        String request = RMT2File.getFileContentsAsString("xml/postal/CountrySearchRequest.xml");
        
        try {
            List<CountryDto> apiResults = AddressBookMockData.createMockCountryDto();
            when(this.mockApi.getCountry(isA(CountryDto.class))).thenReturn(apiResults);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All Country fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        CountryApiHandler handler = new CountryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.COUNTRY_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        Assert.assertEquals(4, results.getReturnCode());

        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getCountries());
        Assert.assertEquals(4, actualRepsonse.getCountries().size());
        Assert.assertEquals(actualRepsonse.getReplyStatus().getReturnCode().intValue(),
                actualRepsonse.getCountries().size());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Country record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getCountries().size(); ndx++) {
            Assert.assertEquals(100 + (ndx), actualRepsonse.getCountries().get(ndx).getCountryId().intValue());
            Assert.assertEquals("CountryName" + (ndx+1), actualRepsonse.getCountries().get(ndx).getCountryName());
            Assert.assertEquals("CountryCode" + (ndx+1), actualRepsonse.getCountries().get(ndx).getCountryCode());
        }
    }
    
    
    @Test
    public void testSuccess_Fetch_NoDataFound() {
        this.setupMockApiCall();
        String request = RMT2File.getFileContentsAsString("xml/postal/CountrySearchRequest.xml");
        try {
            List<CountryDto> apiResults = AddressBookMockData.createMockCountryDto();
            when(this.mockApi.getCountry(isA(CountryDto.class))).thenReturn(null);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All Country fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        CountryApiHandler handler = new CountryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.COUNTRY_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getCountries());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("No Country data found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_API_Error() {
        this.setupMockApiCall();
        String request = RMT2File.getFileContentsAsString("xml/postal/CountrySearchRequest.xml");
        try {
            List<CountryDto> apiResults = AddressBookMockData.createMockCountryDto();
            when(this.mockApi.getCountry(isA(CountryDto.class)))
                .thenThrow(new PostalApiException("A country API error occurred"));
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All Country fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        CountryApiHandler handler = new CountryApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.COUNTRY_GET, request);
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
        Assert.assertEquals("Failure to retrieve Country data", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("A country API error occurred", actualRepsonse.getReplyStatus().getExtMessage());
    }
 
}

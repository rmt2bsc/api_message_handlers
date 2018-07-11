package org.rmt2.api.handler.postal;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

import org.dto.IpLocationDto;
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
import org.rmt2.api.handlers.postal.IpInfoApiHandler;
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
 * Tests the API Handler for IP API functionality.
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class,
        PostalApiFactory.class, SystemConfigurator.class })
public class IpInfoMessageHandlerTest extends BaseMessageHandlerTest {

    private PostalApi mockApi;
  

    /**
     * 
     */
    public IpInfoMessageHandlerTest() {
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
    public void testSuccess_Fetch_With_Standard_IP() {
        this.setupMockApiCall();
        
        String request = RMT2File.getFileContentsAsString("xml/postal/IpInfoStandardSearchRequest.xml");
        
        try {
            IpLocationDto apiResults = AddressBookMockData.createMockIpLocationDto(null, "111.222.333.444", 90333.333,
                            29393.392838, "United States", "TX", "Dallas", "75240", "214");
            when(this.mockApi.getIpInfo(isA(String.class))).thenReturn(apiResults);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All IP fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        IpInfoApiHandler handler = new IpInfoApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.IP_INFO_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        Assert.assertEquals(1, results.getReturnCode());

        PostalResponse actualRepsonse = 
                (PostalResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getIpData());
        Assert.assertEquals(actualRepsonse.getReplyStatus().getReturnCode().intValue(), 1);
        Assert.assertEquals(WebServiceConstants.RETURN_STATUS_SUCCESS,
                actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("IP record(s) found", actualRepsonse.getReplyStatus().getMessage());
        
        Assert.assertEquals(actualRepsonse.getIpData().getCity(), "Dallas");
        Assert.assertEquals(actualRepsonse.getIpData().getCountryName(), "United States");
        Assert.assertEquals(actualRepsonse.getIpData().getRegion(), "TX");
        Assert.assertEquals(actualRepsonse.getIpData().getZip(), "75240");
        Assert.assertEquals(actualRepsonse.getIpData().getLongitude(), "90333.333");
        Assert.assertEquals(actualRepsonse.getIpData().getLatitude(), "29393.392838");
    }
    
    
    @Test
    public void testSuccess_Fetch_With_Standard_IP_NoDataFound() {
        this.setupMockApiCall();
        String request = RMT2File.getFileContentsAsString("xml/postal/IpInfoStandardSearchRequest.xml");
        try {
            when(this.mockApi.getIpInfo(isA(String.class))).thenReturn(null);
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All IP fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        IpInfoApiHandler handler = new IpInfoApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.IP_INFO_GET, request);
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
        Assert.assertEquals("No IP data found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Fetch_With_Standard_IP_API_Error() {
        this.setupMockApiCall();
        String request = RMT2File.getFileContentsAsString("xml/postal/IpInfoStandardSearchRequest.xml");
        try {
            when(this.mockApi.getIpInfo(isA(String.class)))
                .thenThrow(new PostalApiException("A IP API error occurred"));
        } catch (PostalApiException e) {
            e.printStackTrace();
            Assert.fail("All IP fetch test case failed");
        }
        
        MessageHandlerResults results = null;
        IpInfoApiHandler handler = new IpInfoApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.IP_INFO_GET, request);
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
        Assert.assertEquals("Failure to retrieve IP data", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("A IP API error occurred", actualRepsonse.getReplyStatus().getExtMessage());
    }
}

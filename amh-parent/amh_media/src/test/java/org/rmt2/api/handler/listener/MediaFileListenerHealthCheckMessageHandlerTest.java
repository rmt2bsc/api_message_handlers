package org.rmt2.api.handler.listener;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.document.DocumentContentApi;
import org.modules.document.DocumentContentApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseMediaMessageHandlerTest;
import org.rmt2.api.handlers.listener.MediaFileListenerApiHandlerConst;
import org.rmt2.api.handlers.listener.MediaFileListenerHealthCheckApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.MediaFileListenerDetailsType;
import org.rmt2.jaxb.MultimediaResponse;

import com.api.config.SystemConfigurator;
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
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, MediaFileListenerHealthCheckApiHandler.class,
        DocumentContentApiFactory.class,
        SystemConfigurator.class })
public class MediaFileListenerHealthCheckMessageHandlerTest extends BaseMediaMessageHandlerTest {
    public static final String API_ERROR = "Test validation error: API Error occurred";

    private DocumentContentApi mockApi;


    /**
     * 
     */
    public MediaFileListenerHealthCheckMessageHandlerTest() {
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
        mockApi = Mockito.mock(DocumentContentApi.class);
        PowerMockito.mockStatic(DocumentContentApiFactory.class);
        when(DocumentContentApiFactory.createMediaContentApi()).thenReturn(mockApi);
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
    public void testSuccess_Operation() {
        String request = RMT2File.getFileContentsAsString("xml/listener/MediaFileListenerHealthCheckRequest.xml");

        when(this.mockApi.getMediaFileListenerStatus()).thenReturn("Running");
        
        MessageHandlerResults results = null;
        MediaFileListenerHealthCheckApiHandler handler = new MediaFileListenerHealthCheckApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_FILE_LISTENER_HEALTH, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getMediaFileListenerDetails());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(MediaFileListenerApiHandlerConst.MESSAGE_STATUS, actualRepsonse.getReplyStatus().getMessage());

        MediaFileListenerDetailsType m = actualRepsonse.getProfile().getMediaFileListenerDetails();
        Assert.assertNotNull(m.getStatus());
        Assert.assertEquals("Running", m.getStatus());
    }

}

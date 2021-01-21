package org.rmt2.api.handler.maint;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.MediaModuleException;
import org.modules.document.DocumentContentApi;
import org.modules.document.DocumentContentApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseMediaMessageHandlerTest;
import org.rmt2.api.handlers.maint.DocumentDeleteApiHandler;
import org.rmt2.api.handlers.maint.MediaContentApiHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
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
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, DocumentDeleteApiHandler.class,
        DocumentContentApiFactory.class, SystemConfigurator.class })
public class DocumentDeleteMessageHandlerTest extends BaseMediaMessageHandlerTest {
    public static final String API_ERROR = "Test validation error: API Error occurred";

    private DocumentContentApi mockApi;


    /**
     * 
     */
    public DocumentDeleteMessageHandlerTest() {
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
    public void testSuccess_Delete() {
        String request = RMT2File.getFileContentsAsString("xml/maint/MediaContentDeleteRequest.xml");

        try {
            when(this.mockApi.delete(isA(Integer.class))).thenReturn(1);
        } catch (MediaModuleException e) {
            Assert.fail("Unable to setup mock stub for deleting media content records");
        }
        
        MessageHandlerResults results = null;
        DocumentDeleteApiHandler handler = new DocumentDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(MediaContentApiHandlerConst.MESSAGE_DELETED, actualRepsonse.getReplyStatus().getMessage());
    }
    

    @Test
    public void testSuccess_Delete_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/maint/MediaContentDeleteRequest.xml");

        try {
            when(this.mockApi.delete(isA(Integer.class))).thenReturn(0);
        } catch (MediaModuleException e) {
            Assert.fail("Unable to setup mock stub for fetching media content records");
        }

        MessageHandlerResults results = null;
        DocumentDeleteApiHandler handler = new DocumentDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(MediaContentApiHandlerConst.MESSAGE_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());
    }

    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/maint/MediaContentDeleteRequest.xml");
        try {
            when(this.mockApi.delete(isA(Integer.class))).thenThrow(new MediaModuleException(API_ERROR));
        } catch (MediaModuleException e) {
            Assert.fail("Unable to setup mock stub for deleting media content with an API Error");
        }
        
        MessageHandlerResults results = null;
        DocumentDeleteApiHandler handler = new DocumentDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MediaContentApiHandlerConst.MESSAGE_DELETE_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testValidation_Missing_Criteria() {
        String request = RMT2File.getFileContentsAsString("xml/maint/DocumentDeleteRequest_MissingCriteria.xml");

        MessageHandlerResults results = null;
        DocumentDeleteApiHandler handler = new DocumentDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(MediaContentApiHandlerConst.MESSAGE_DELETE_MISSING_CRITERIA, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Missing_Content_Criteria() {
        String request = RMT2File.getFileContentsAsString("xml/maint/DocumentDeleteRequest_MissingContentCriteria.xml");

        MessageHandlerResults results = null;
        DocumentDeleteApiHandler handler = new DocumentDeleteApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(MediaContentApiHandlerConst.MESSAGE_DELETE_MISSING_CRITERIA, actualRepsonse.getReplyStatus()
                .getMessage());
    }
}

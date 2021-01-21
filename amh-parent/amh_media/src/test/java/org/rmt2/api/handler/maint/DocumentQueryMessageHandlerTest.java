package org.rmt2.api.handler.maint;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.dto.ContentDto;
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
import org.rmt2.api.handler.MediaMockDtoFactory;
import org.rmt2.api.handler.MediaMockOrmFactory;
import org.rmt2.api.handlers.maint.DocumentFetchApiHandler;
import org.rmt2.api.handlers.maint.MediaContentApiHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.MimeContentType;
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
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, DocumentFetchApiHandler.class,
        DocumentContentApiFactory.class, SystemConfigurator.class })
public class DocumentQueryMessageHandlerTest extends BaseMediaMessageHandlerTest {
    public static final String API_ERROR = "Test validation error: API Error occurred";

    private DocumentContentApi mockApi;


    /**
     * 
     */
    public DocumentQueryMessageHandlerTest() {
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
    public void testSuccess_Fetch() {
        String request = RMT2File.getFileContentsAsString("xml/maint/DocumentFetchRequest.xml");
        ContentDto mockListData = MediaMockDtoFactory.createMediaContentMockData();

        try {
            when(this.mockApi.get(isA(Integer.class))).thenReturn(mockListData);
        } catch (MediaModuleException e) {
            Assert.fail("Unable to setup mock stub for fetching media content records");
        }
        
        MessageHandlerResults results = null;
        DocumentFetchApiHandler handler = new DocumentFetchApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getAudioVideoContent());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(MediaContentApiHandlerConst.MESSAGE_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        MimeContentType mct = actualRepsonse.getProfile().getAudioVideoContent();
        Assert.assertNotNull(mct.getContentId());
        Assert.assertEquals(MediaMockDtoFactory.TEST_CONTENT_ID, mct.getContentId().intValue());
        Assert.assertNotNull(mct.getAppCode());
        Assert.assertNotNull(mct.getModuleCode());
        Assert.assertNotNull(mct.getFilename());
        Assert.assertEquals(MediaMockDtoFactory.TEST_FILENAME, mct.getFilename());
        Assert.assertNotNull(mct.getFilepath());
        Assert.assertEquals(MediaMockDtoFactory.TEST_FILEPATH, mct.getFilepath());
        Assert.assertNotNull(mct.getTextData());
        Assert.assertNotNull(mct.getBinaryData());
        Assert.assertNotNull(mct.getFilesize());
        Assert.assertTrue(mct.getFilesize() > 13000);
        Assert.assertNotNull(mct.getMimeType());
        Assert.assertNotNull(mct.getMimeType().getMimeTypeId());
        Assert.assertEquals(MediaMockOrmFactory.TEST_MIMETYPE_ID, mct.getMimeType().getMimeTypeId().intValue());

    }
    

    @Test
    public void testSuccess_Fetch_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/maint/DocumentFetchRequest.xml");

        try {
            when(this.mockApi.get(isA(Integer.class))).thenReturn(null);
        } catch (MediaModuleException e) {
            Assert.fail("Unable to setup mock stub for fetching media content records");
        }

        MessageHandlerResults results = null;
        DocumentFetchApiHandler handler = new DocumentFetchApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_GET, request);
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
        String request = RMT2File.getFileContentsAsString("xml/maint/DocumentFetchRequest.xml");
        try {
            when(this.mockApi.get(isA(Integer.class))).thenThrow(new MediaModuleException(API_ERROR));
        } catch (MediaModuleException e) {
            Assert.fail("Unable to setup mock stub for fetching media content with an API Error");
        }
        
        MessageHandlerResults results = null;
        DocumentFetchApiHandler handler = new DocumentFetchApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_GET, request);
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
        Assert.assertEquals(MediaContentApiHandlerConst.MESSAGE_FETCH_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }

    @Test
    public void testValidation_Fetch_Missing_Criteria() {
        String request = RMT2File.getFileContentsAsString("xml/maint/DocumentFetchRequest_MissingCriteria.xml");

        MessageHandlerResults results = null;
        DocumentFetchApiHandler handler = new DocumentFetchApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_GET, request);
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
        Assert.assertEquals(MediaContentApiHandlerConst.MESSAGE_FETCH_MISSING_CRITERIA, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_Fetch_Missing_Content_Criteria() {
        String request = RMT2File.getFileContentsAsString("xml/maint/DocumentFetchRequest_MissingContentCriteria.xml");

        MessageHandlerResults results = null;
        DocumentFetchApiHandler handler = new DocumentFetchApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONTENT_GET, request);
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
        Assert.assertEquals(MediaContentApiHandlerConst.MESSAGE_FETCH_MISSING_CRITERIA, actualRepsonse.getReplyStatus()
                .getMessage());
    }
}

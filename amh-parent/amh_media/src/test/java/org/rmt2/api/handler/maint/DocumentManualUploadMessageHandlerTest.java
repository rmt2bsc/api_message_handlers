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
import org.modules.audiovideo.AudioVideoApiException;
import org.modules.document.DocumentContentApi;
import org.modules.document.DocumentContentApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handler.BaseMediaMessageHandlerTest;
import org.rmt2.api.handlers.maint.DocumentManualUploadApiHandler;
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
import com.api.util.RMT2String;

/**
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, DocumentManualUploadApiHandler.class,
        DocumentContentApiFactory.class, SystemConfigurator.class })
public class DocumentManualUploadMessageHandlerTest extends BaseMediaMessageHandlerTest {
    public static final String API_ERROR = "Test validation error: API Error occurred";
    private static final int NEW_CONTENT_ID = 12345;

    private DocumentContentApi mockApi;


    /**
     * 
     */
    public DocumentManualUploadMessageHandlerTest() {
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
    public void testSuccess_Manual_Upload() {
        String request = RMT2File.getFileContentsAsString("xml/maint/DocumentManualUploadRequest.xml");

        try {
            when(this.mockApi.add(isA(ContentDto.class))).thenReturn(NEW_CONTENT_ID);
        } catch (MediaModuleException e) {
            Assert.fail("Unable to setup mock stub for manual upload of media content records");
        }
        
        MessageHandlerResults results = null;
        DocumentManualUploadApiHandler handler = new DocumentManualUploadApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_MANUAL_UPLOAD_CONTENT, request);
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

        String msg = RMT2String.replace(MediaContentApiHandlerConst.MESSAGE_UPLOAD_SUCCESS, "MSWord.docx",
                ApiMessageHandlerConst.MSG_PLACEHOLDER);
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());
    }
    

    @Test
    public void testError_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/maint/DocumentManualUploadRequest.xml");
        try {
            when(this.mockApi.add(isA(ContentDto.class))).thenThrow(new AudioVideoApiException(API_ERROR));
        } catch (MediaModuleException e) {
            Assert.fail("Unable to setup mock stub for manual upload of media content with an API Error");
        }
        
        MessageHandlerResults results = null;
        DocumentManualUploadApiHandler handler = new DocumentManualUploadApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_MANUAL_UPLOAD_CONTENT, request);
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
        Assert.assertEquals(MediaContentApiHandlerConst.MESSAGE_UPLOAD_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }


    @Test
    public void testValidation_MissingProfile() {
        String request = RMT2File.getFileContentsAsString("xml/maint/DocumentManualUploadRequest_MissingProfile.xml");

        MessageHandlerResults results = null;
        DocumentManualUploadApiHandler handler = new DocumentManualUploadApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_MANUAL_UPLOAD_CONTENT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(MediaContentApiHandlerConst.MESSAGE_UPLOAD_MISSING_PROFILE_ERROR, actualRepsonse
                .getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_MissingAudioVideoContentSection() {
        String request = RMT2File
                .getFileContentsAsString("xml/maint/DocumentManualUploadRequest_MissingAudioVideoContentSection.xml");

        MessageHandlerResults results = null;
        DocumentManualUploadApiHandler handler = new DocumentManualUploadApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_MANUAL_UPLOAD_CONTENT, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_FAILURE, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(MediaContentApiHandlerConst.MESSAGE_UPLOAD_MISSING_PROFILE_AUDIOVIDEOCONTENT, actualRepsonse
                .getReplyStatus()
                .getMessage());
    }

}

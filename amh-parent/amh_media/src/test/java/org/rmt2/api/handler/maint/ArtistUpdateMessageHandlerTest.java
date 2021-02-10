package org.rmt2.api.handler.maint;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.dto.ArtistDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.audiovideo.AudioVideoApi;
import org.modules.audiovideo.AudioVideoApiException;
import org.modules.audiovideo.AudioVideoFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handler.BaseMediaMessageHandlerTest;
import org.rmt2.api.handlers.maint.ArtistApiHandlerConst;
import org.rmt2.api.handlers.maint.ArtistUpdateApiHandler;
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
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, ArtistUpdateApiHandler.class, AudioVideoFactory.class,
        SystemConfigurator.class })
public class ArtistUpdateMessageHandlerTest extends BaseMediaMessageHandlerTest {
    public static final String API_ERROR = "Test validation error: API Error occurred";
    private static final int NEW_ARTIST_ID = 12345;

    private AudioVideoApi mockApi;


    /**
     * 
     */
    public ArtistUpdateMessageHandlerTest() {
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
        mockApi = Mockito.mock(AudioVideoApi.class);
        PowerMockito.mockStatic(AudioVideoFactory.class);
        when(AudioVideoFactory.createApi()).thenReturn(mockApi);
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
    public void testSuccess_Update() {
        String request = RMT2File.getFileContentsAsString("xml/maint/ArtistUpdateRequest.xml");

        try {
            when(this.mockApi.updateArtist(isA(ArtistDto.class))).thenReturn(1);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for updating artist records");
        }
        
        MessageHandlerResults results = null;
        ArtistUpdateApiHandler handler = new ArtistUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_ARTIST_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNull(actualRepsonse.getProfile().getAudioVideoDetails());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String msg = RMT2String.replace(ArtistApiHandlerConst.MESSAGE_UPDATE_EXISTING_SUCCESS, "10",
                ApiMessageHandlerConst.MSG_PLACEHOLDER);
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testSuccess_Insert() {
        String request = RMT2File.getFileContentsAsString("xml/maint/ArtistInsertRequest.xml");

        try {
            when(this.mockApi.updateArtist(isA(ArtistDto.class))).thenReturn(NEW_ARTIST_ID);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for updating artist records");
        }

        MessageHandlerResults results = null;
        ArtistUpdateApiHandler handler = new ArtistUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_ARTIST_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNull(actualRepsonse.getProfile().getAudioVideoDetails());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        String msg = RMT2String.replace(ArtistApiHandlerConst.MESSAGE_UPDATE_NEW_SUCCESS, String.valueOf(NEW_ARTIST_ID),
                ApiMessageHandlerConst.MSG_PLACEHOLDER);
        Assert.assertEquals(msg, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/maint/ArtistUpdateRequest.xml");
        try {
            when(this.mockApi.updateArtist(isA(ArtistDto.class))).thenThrow(new AudioVideoApiException(API_ERROR));
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for updating artist with an API Error");
        }
        
        MessageHandlerResults results = null;
        ArtistUpdateApiHandler handler = new ArtistUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_ARTIST_UPDATE, request);
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
        Assert.assertEquals(ArtistApiHandlerConst.MESSAGE_UPDATE_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }


    @Test
    public void testValidation_MissingProfile() {
        String request = RMT2File.getFileContentsAsString("xml/maint/ArtistUpdateValidationRequest_MissingProfile.xml");

        MessageHandlerResults results = null;
        ArtistUpdateApiHandler handler = new ArtistUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_ARTIST_UPDATE, request);
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
        Assert.assertEquals(ArtistApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_MissingAudioVideoDetails() {
        String request = RMT2File.getFileContentsAsString("xml/maint/ArtistUpdateValidationRequest_MissingAudioVideoDetails.xml");

        MessageHandlerResults results = null;
        ArtistUpdateApiHandler handler = new ArtistUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_ARTIST_UPDATE, request);
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
        Assert.assertEquals(ArtistApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_AUDIOVIDEODETAILS, actualRepsonse
                .getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_MissingArtist() {
        String request = RMT2File.getFileContentsAsString("xml/maint/ArtistUpdateValidationRequest_MissingArtist.xml");

        MessageHandlerResults results = null;
        ArtistUpdateApiHandler handler = new ArtistUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_ARTIST_UPDATE, request);
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
        Assert.assertEquals(ArtistApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_ARTIST, actualRepsonse
                .getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_TooManyArtist() {
        String request = RMT2File.getFileContentsAsString("xml/maint/ArtistUpdateValidationRequest_TooManyArtist.xml");

        MessageHandlerResults results = null;
        ArtistUpdateApiHandler handler = new ArtistUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_ARTIST_UPDATE, request);
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
        Assert.assertEquals(ArtistApiHandlerConst.MESSAGE_UPDATE_TOO_MANY_ARTIST, actualRepsonse.getReplyStatus().getMessage());
    }

}

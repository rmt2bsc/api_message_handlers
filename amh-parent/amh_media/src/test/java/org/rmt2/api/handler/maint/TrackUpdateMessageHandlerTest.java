package org.rmt2.api.handler.maint;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.ArtistDto;
import org.dto.ProjectDto;
import org.dto.TracksDto;
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
import org.rmt2.api.handler.BaseMediaMessageHandlerTest;
import org.rmt2.api.handler.MediaMockDtoFactory;
import org.rmt2.api.handlers.maint.TrackApiHandlerConst;
import org.rmt2.api.handlers.maint.TrackUpdateApiHandler;
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
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, TrackUpdateApiHandler.class,
        AudioVideoFactory.class,
        SystemConfigurator.class })
public class TrackUpdateMessageHandlerTest extends BaseMediaMessageHandlerTest {
    public static final String API_ERROR = "Test validation error: API Error occurred";
    private static final int NEW_TRACK_ID = 12345;

    private AudioVideoApi mockApi;


    /**
     * 
     */
    public TrackUpdateMessageHandlerTest() {
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

        List<ArtistDto> mockListData1 = MediaMockDtoFactory.createArtistMockData();
        try {
            when(this.mockApi.getArtist(isA(ArtistDto.class))).thenReturn(mockListData1);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for fetching artist records");
        }

        List<ProjectDto> mockListData2 = MediaMockDtoFactory.createProjectMockData();
        try {
            when(this.mockApi.getProject(isA(ProjectDto.class))).thenReturn(mockListData2);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for fetching project records");
        }

        List<TracksDto> mockListData3 = MediaMockDtoFactory.createTrackMockData();
        try {
            when(this.mockApi.getTracks(isA(TracksDto.class))).thenReturn(mockListData3);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for fetching track records");
        }
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
        String request = RMT2File.getFileContentsAsString("xml/maint/TrackUpdateRequest.xml");

        try {
            when(this.mockApi.updateTrack(isA(TracksDto.class))).thenReturn(1);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for updating track records");
        }
        
        MessageHandlerResults results = null;
        TrackUpdateApiHandler handler = new TrackUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_TRACK_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getAudioVideoDetails());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        Assert.assertEquals(TrackApiHandlerConst.MESSAGE_UPDATE_EXISTING_SUCCESS, actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testSuccess_Insert() {
        String request = RMT2File.getFileContentsAsString("xml/maint/TrackInsertRequest.xml");

        try {
            when(this.mockApi.updateTrack(isA(TracksDto.class))).thenReturn(NEW_TRACK_ID);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for inserting media track records");
        }

        MessageHandlerResults results = null;
        TrackUpdateApiHandler handler = new TrackUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_TRACK_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile());
        Assert.assertNotNull(actualRepsonse.getProfile().getAudioVideoDetails());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());

        Assert.assertEquals(TrackApiHandlerConst.MESSAGE_UPDATE_NEW_SUCCESS, actualRepsonse.getReplyStatus().getMessage());
    }

    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/maint/TrackUpdateRequest.xml");
        try {
            when(this.mockApi.updateTrack(isA(TracksDto.class))).thenThrow(new AudioVideoApiException(API_ERROR));
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for updating media track with an API Error");
        }
        
        MessageHandlerResults results = null;
        TrackUpdateApiHandler handler = new TrackUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_TRACK_UPDATE, request);
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
        Assert.assertEquals(TrackApiHandlerConst.MESSAGE_UPDATE_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }


    @Test
    public void testValidation_MissingProfile() {
        String request = RMT2File.getFileContentsAsString("xml/maint/TrackUpdateValidationRequest_MissingProfile.xml");

        MessageHandlerResults results = null;
        TrackUpdateApiHandler handler = new TrackUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_TRACK_UPDATE, request);
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
        Assert.assertEquals(TrackApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_ERROR, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_MissingAudioVideoDetails() {
        String request = RMT2File
                .getFileContentsAsString("xml/maint/TrackUpdateValidationRequest_MissingAudioVideoDetails.xml");

        MessageHandlerResults results = null;
        TrackUpdateApiHandler handler = new TrackUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_TRACK_UPDATE, request);
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
        Assert.assertEquals(TrackApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_AUDIOVIDEODETAILS, actualRepsonse
                .getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_MissingArtist() {
        String request = RMT2File.getFileContentsAsString("xml/maint/TrackUpdateValidationRequest_MissingArtist.xml");

        MessageHandlerResults results = null;
        TrackUpdateApiHandler handler = new TrackUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_TRACK_UPDATE, request);
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
        Assert.assertEquals(TrackApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_TRACKS, actualRepsonse
                .getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_MissingProjects() {
        String request = RMT2File.getFileContentsAsString("xml/maint/TrackUpdateValidationRequest_MissingProjects.xml");

        MessageHandlerResults results = null;
        TrackUpdateApiHandler handler = new TrackUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_TRACK_UPDATE, request);
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
        Assert.assertEquals(TrackApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_TRACKS, actualRepsonse
                .getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_MissingProject() {
        String request = RMT2File.getFileContentsAsString("xml/maint/TrackUpdateValidationRequest_MissingProject.xml");

        MessageHandlerResults results = null;
        TrackUpdateApiHandler handler = new TrackUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_TRACK_UPDATE, request);
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
        Assert.assertEquals(TrackApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_TRACKS, actualRepsonse
                .getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_TooManyArtists() {
        String request = RMT2File.getFileContentsAsString("xml/maint/TrackUpdateValidationRequest_TooManyArtist.xml");

        MessageHandlerResults results = null;
        TrackUpdateApiHandler handler = new TrackUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_TRACK_UPDATE, request);
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
        Assert.assertEquals(TrackApiHandlerConst.MESSAGE_UPDATE_TOO_MANY_TRACKS, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_TooManyProjects() {
        String request = RMT2File.getFileContentsAsString("xml/maint/TrackUpdateValidationRequest_TooManyProjects.xml");

        MessageHandlerResults results = null;
        TrackUpdateApiHandler handler = new TrackUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_TRACK_UPDATE, request);
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
        Assert.assertEquals(TrackApiHandlerConst.MESSAGE_UPDATE_TOO_MANY_TRACKS, actualRepsonse.getReplyStatus()
                .getMessage());
    }

    @Test
    public void testValidation_TooManyTracks() {
        String request = RMT2File.getFileContentsAsString("xml/maint/TrackUpdateValidationRequest_TooManyTracks.xml");

        MessageHandlerResults results = null;
        TrackUpdateApiHandler handler = new TrackUpdateApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_TRACK_UPDATE, request);
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
        Assert.assertEquals(TrackApiHandlerConst.MESSAGE_UPDATE_TOO_MANY_TRACKS, actualRepsonse.getReplyStatus()
                .getMessage());
    }
}

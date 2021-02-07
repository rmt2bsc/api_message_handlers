package org.rmt2.api.handler.maint;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.VwArtistDto;
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
//import org.rmt2.api.audiovideo.AvMediaMockDataFactory;
import org.rmt2.api.handler.BaseMediaMessageHandlerTest;
import org.rmt2.api.handler.MediaMockDtoFactory;
import org.rmt2.api.handler.MediaMockOrmFactory;
import org.rmt2.api.handlers.maint.ConsolidatedMediaApiHandlerConst;
import org.rmt2.api.handlers.maint.ConsolidatedMediaFetchApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ArtistType;
import org.rmt2.jaxb.AvProjectType;
import org.rmt2.jaxb.MultimediaResponse;
import org.rmt2.jaxb.TrackType;

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
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, ConsolidatedMediaFetchApiHandler.class,
        AudioVideoFactory.class,
        SystemConfigurator.class })
public class ConsolidatedMediaQueryMessageHandlerTest extends BaseMediaMessageHandlerTest {
    public static final String API_ERROR = "Test validation error: API Error occurred";

    private AudioVideoApi mockApi;


    /**
     * 
     */
    public ConsolidatedMediaQueryMessageHandlerTest() {
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
    public void testSuccess_Fetch_Unique_Media() {
        String request = RMT2File.getFileContentsAsString("xml/maint/ConsolidateMediaQueryRequest.xml");
        List<VwArtistDto> mockListData = MediaMockDtoFactory.createConsolidatedMediaDistinctMockData();

        try {
            when(this.mockApi.getConsolidatedArtist(isA(VwArtistDto.class))).thenReturn(mockListData);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for fetching vw_audio_video_artist records");
        }
        
        MessageHandlerResults results = null;
        ConsolidatedMediaFetchApiHandler handler = new ConsolidatedMediaFetchApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getAudioVideoDetails().getArtist().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ConsolidatedMediaApiHandlerConst.MESSAGE_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getAudioVideoDetails().getArtist().size(); ndx++) {
            ArtistType a = actualRepsonse.getProfile().getAudioVideoDetails().getArtist().get(ndx);
            Assert.assertNotNull(a.getArtistId());
            Assert.assertEquals(MediaMockOrmFactory.TEST_ARTIST_ID + ndx, a.getArtistId().intValue());
            Assert.assertEquals("Artist" + ndx, a.getArtistName());

            Assert.assertNotNull(a.getProjects());
            Assert.assertNotNull(a.getProjects().getProject());
            Assert.assertEquals(1, a.getProjects().getProject().size());
            for (AvProjectType item : a.getProjects().getProject()) {
                Assert.assertEquals(MediaMockOrmFactory.TEST_PROJECT_ID + ndx, item.getProjectId(), 0);
                Assert.assertEquals("Project Name" + ndx, item.getTitle());
            }
        }
    }
    
    @Test
    public void testSuccess_Fetch_Like_Media() {
        String request = RMT2File.getFileContentsAsString("xml/maint/ConsolidateMediaQueryRequest.xml");
        List<VwArtistDto> mockListData = MediaMockDtoFactory.createConsolidatedMediaLikeMockData();

        try {
            when(this.mockApi.getConsolidatedArtist(isA(VwArtistDto.class))).thenReturn(mockListData);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for fetching vw_audio_video_artist records");
        }

        MessageHandlerResults results = null;
        ConsolidatedMediaFetchApiHandler handler = new ConsolidatedMediaFetchApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getAudioVideoDetails().getArtist().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(ConsolidatedMediaApiHandlerConst.MESSAGE_FOUND, actualRepsonse.getReplyStatus().getMessage());

        for (int ndx = 0; ndx < actualRepsonse.getProfile().getAudioVideoDetails().getArtist().size(); ndx++) {
            ArtistType a = actualRepsonse.getProfile().getAudioVideoDetails().getArtist().get(ndx);
            Assert.assertNotNull(a.getArtistId());
            Assert.assertEquals(MediaMockOrmFactory.TEST_ARTIST_ID, a.getArtistId().intValue());
            Assert.assertEquals("Artist", a.getArtistName());

            Assert.assertNotNull(a.getProjects());
            Assert.assertNotNull(a.getProjects().getProject());
            Assert.assertEquals(1, a.getProjects().getProject().size());
            for (AvProjectType item : a.getProjects().getProject()) {
                Assert.assertEquals(MediaMockOrmFactory.TEST_PROJECT_ID, item.getProjectId(), 0);
                Assert.assertEquals("Project Name", item.getTitle());
                Assert.assertNotNull(a.getProjects().getProject().get(0).getTracks());
                Assert.assertNotNull(a.getProjects().getProject().get(0).getTracks().getTrack());
                Assert.assertEquals(5, a.getProjects().getProject().get(0).getTracks().getTrack().size());
                int ndx3 = 0;
                List<TrackType> tracks = a.getProjects().getProject().get(0).getTracks().getTrack();
                for (TrackType track : tracks) {
                    Assert.assertEquals(MediaMockOrmFactory.TEST_TRACK_ID + ndx3, track.getTrackId().intValue());
                    Assert.assertEquals("Track Name" + ndx3, track.getTrackName());
                    Assert.assertEquals(ndx3 + 1, track.getTrackNumber().intValue());
                    ndx3++;
                }
            }
        }
    }

    @Test
    public void testSuccess_Fetch_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/maint/ConsolidateMediaQueryRequest.xml");

        try {
            when(this.mockApi.getConsolidatedArtist(isA(VwArtistDto.class))).thenReturn(null);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for fetching vw_audio_video_artist records");
        }

        MessageHandlerResults results = null;
        ConsolidatedMediaFetchApiHandler handler = new ConsolidatedMediaFetchApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH, request);
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
        Assert.assertEquals(ConsolidatedMediaApiHandlerConst.MESSAGE_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());
    }

    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/maint/ConsolidateMediaQueryRequest.xml");
        try {
            when(this.mockApi.getConsolidatedArtist(isA(VwArtistDto.class))).thenThrow(new AudioVideoApiException(API_ERROR));
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for fetching vw_audio_video_artist with an API Error");
        }
        
        MessageHandlerResults results = null;
        ConsolidatedMediaFetchApiHandler handler = new ConsolidatedMediaFetchApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_CONSOLIDATED_SEARCH, request);
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
        Assert.assertEquals(ConsolidatedMediaApiHandlerConst.MESSAGE_FETCH_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }
}

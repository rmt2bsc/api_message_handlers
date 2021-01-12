package org.rmt2.api.handler.maint;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

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
//import org.rmt2.api.audiovideo.AvMediaMockDataFactory;
import org.rmt2.api.handler.BaseMediaMessageHandlerTest;
import org.rmt2.api.handler.MediaMockDtoFactory;
import org.rmt2.api.handler.MediaMockOrmFactory;
import org.rmt2.api.handlers.maint.TrackApiHandlerConst;
import org.rmt2.api.handlers.maint.TrackFetchApiHandler;
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
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, TrackFetchApiHandler.class,
        AudioVideoFactory.class,
        SystemConfigurator.class })
public class TrackQueryMessageHandlerTest extends BaseMediaMessageHandlerTest {
    public static final String API_ERROR = "Test validation error: API Error occurred";

    private AudioVideoApi mockApi;


    /**
     * 
     */
    public TrackQueryMessageHandlerTest() {
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
    public void testSuccess_Fetch_All() {
        String request = RMT2File.getFileContentsAsString("xml/maint/TrackQueryRequest.xml");
        List<TracksDto> mockListData = MediaMockDtoFactory.createTrackMockData();

        try {
            when(this.mockApi.getTracks(isA(TracksDto.class))).thenReturn(mockListData);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for fetching track records");
        }
        
        MessageHandlerResults results = null;
        TrackFetchApiHandler handler = new TrackFetchApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_TRACK_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNotNull(actualRepsonse.getProfile().getAudioVideoDetails());
        Assert.assertNotNull(actualRepsonse.getProfile().getAudioVideoDetails().getArtist());
        Assert.assertEquals(5, actualRepsonse.getProfile().getAudioVideoDetails().getArtist().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(TrackApiHandlerConst.MESSAGE_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getAudioVideoDetails().getArtist().size(); ndx++) {
            ArtistType at = actualRepsonse.getProfile().getAudioVideoDetails().getArtist().get(ndx);
            Assert.assertNotNull(at.getProjects());
            Assert.assertEquals(1, at.getProjects().getProject().size());
            for (int ndx2 = 0; ndx2 < at.getProjects().getProject().size(); ndx2++) {
                AvProjectType apt = at.getProjects().getProject().get(ndx2);
                Assert.assertNotNull(apt.getTracks());
                Assert.assertEquals(1, apt.getTracks().getTrack().size());
                for (int ndx3 = 0; ndx3 < apt.getTracks().getTrack().size(); ndx3++) {
                    TrackType tt = apt.getTracks().getTrack().get(ndx3);
                    Assert.assertNotNull(tt.getTrackId());
                    // Since each artist has one project and each project has
                    // one track, use the outer most index value to track each
                    // artsit/project track
                    int currentTrackId = MediaMockOrmFactory.TEST_TRACK_ID + ndx;
                    Assert.assertEquals(currentTrackId, tt.getTrackId().intValue());
                    Assert.assertNotNull(tt.getTrackName());
                    Assert.assertEquals("Track" + tt.getTrackNumber(), tt.getTrackName());
                    Assert.assertNotNull(tt.getLocationPath());
                    Assert.assertEquals("/FilePath/" + currentTrackId, tt.getLocationPath());
                    Assert.assertNotNull(tt.getLocationPath());
                    Assert.assertEquals("ProjectFileName" + currentTrackId, tt.getLocationFilename());
                }
            }
        }
    }
    

    @Test
    public void testSuccess_Fetch_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/maint/TrackQueryRequest.xml");

        try {
            when(this.mockApi.getTracks(isA(TracksDto.class))).thenReturn(null);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for fetching track records");
        }

        MessageHandlerResults results = null;
        TrackFetchApiHandler handler = new TrackFetchApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_TRACK_GET, request);
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
        Assert.assertEquals(TrackApiHandlerConst.MESSAGE_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());
    }

    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/maint/TrackQueryRequest.xml");
        try {
            when(this.mockApi.getTracks(isA(TracksDto.class))).thenThrow(new AudioVideoApiException(API_ERROR));
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for fetching track with an API Error");
        }
        
        MessageHandlerResults results = null;
        TrackFetchApiHandler handler = new TrackFetchApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_TRACK_GET, request);
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
        Assert.assertEquals(TrackApiHandlerConst.MESSAGE_FETCH_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }
}

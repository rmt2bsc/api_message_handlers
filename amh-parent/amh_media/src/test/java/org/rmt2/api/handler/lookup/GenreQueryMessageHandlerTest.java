package org.rmt2.api.handler.lookup;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;

import org.dto.GenreDto;
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
import org.rmt2.api.handler.MediaMockOrmFactory;
import org.rmt2.api.handlers.lookup.genre.GenreApiHandler;
import org.rmt2.api.handlers.lookup.genre.GenreApiHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.GenreType;
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
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, GenreApiHandler.class, AudioVideoFactory.class,
        SystemConfigurator.class })
public class GenreQueryMessageHandlerTest extends BaseMediaMessageHandlerTest {
    public static final int PROJECT_COUNT = 28;
    public static final int TASK_ID_SEED = 1112220;
    public static final String TASK_DESCRIPTION_SEED = "Task Description ";
    public static final String API_ERROR = "Test validation error: API Error occurred";

    private AudioVideoApi mockApi;


    /**
     * 
     */
    public GenreQueryMessageHandlerTest() {
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
        String request = RMT2File.getFileContentsAsString("xml/genre/GenreQueryRequest.xml");
        List<GenreDto> mockListData = MediaMockDtoFactory.createGenreMockData();

        try {
            when(this.mockApi.getGenre(isA(GenreDto.class))).thenReturn(mockListData);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for fetching Genre records");
        }
        
        MessageHandlerResults results = null;
        GenreApiHandler handler = new GenreApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_GENRE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(5, actualRepsonse.getProfile().getGenres().getGenre().size());
        Assert.assertEquals(5, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(GenreApiHandlerConst.MESSAGE_FOUND, actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getGenres().getGenre().size(); ndx++) {
            GenreType a = actualRepsonse.getProfile().getGenres().getGenre().get(ndx);
            Assert.assertNotNull(a.getGenreId());
            Assert.assertEquals(MediaMockOrmFactory.TEST_GENRE_ID + ndx, a.getGenreId().intValue());
            Assert.assertEquals("Genre" + String.valueOf(MediaMockOrmFactory.TEST_GENRE_ID + ndx), a.getGenreName());
        }
    }
    
    @Test
    public void testSuccess_Fetch_Single() {
        String request = RMT2File.getFileContentsAsString("xml/genre/GenreQueryRequest.xml");
        List<GenreDto> mockListData = MediaMockDtoFactory.createGenreSingleMockData();

        try {
            when(this.mockApi.getGenre(isA(GenreDto.class))).thenReturn(mockListData);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for fetching Genre records");
        }

        MessageHandlerResults results = null;
        GenreApiHandler handler = new GenreApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_GENRE_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getGenres().getGenre().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(GenreApiHandlerConst.MESSAGE_FOUND, actualRepsonse.getReplyStatus().getMessage());

        for (int ndx = 0; ndx < actualRepsonse.getProfile().getGenres().getGenre().size(); ndx++) {
            GenreType a = actualRepsonse.getProfile().getGenres().getGenre().get(ndx);
            Assert.assertNotNull(a.getGenreId());
            Assert.assertEquals(MediaMockOrmFactory.TEST_GENRE_ID + ndx, a.getGenreId().intValue());
            Assert.assertEquals("Genre" + String.valueOf(MediaMockOrmFactory.TEST_GENRE_ID + ndx), a.getGenreName());
        }
    }

    @Test
    public void testSuccess_Fetch_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/genre/GenreQueryRequest.xml");

        try {
            when(this.mockApi.getGenre(isA(GenreDto.class))).thenReturn(null);
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for fetching Genre records");
        }

        MessageHandlerResults results = null;
        GenreApiHandler handler = new GenreApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_GENRE_GET, request);
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
        Assert.assertEquals(GenreApiHandlerConst.MESSAGE_NOT_FOUND, actualRepsonse.getReplyStatus().getMessage());
    }

    
    @Test
    public void testError_Fetch_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/genre/GenreQueryRequest.xml");
        try {
            when(this.mockApi.getGenre(isA(GenreDto.class))).thenThrow(new AudioVideoApiException(API_ERROR));
        } catch (AudioVideoApiException e) {
            Assert.fail("Unable to setup mock stub for fetching genres with an API Error");
        }
        
        MessageHandlerResults results = null;
        GenreApiHandler handler = new GenreApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_GENRE_GET, request);
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
        Assert.assertEquals(GenreApiHandlerConst.MESSAGE_FETCH_ERROR, actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(API_ERROR, actualRepsonse.getReplyStatus().getExtMessage());
    }
}

package org.rmt2.api.handler.batch;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.audiovideo.batch.AvBatchFileFactory;
import org.modules.audiovideo.batch.AvBatchFileProcessorApi;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.BaseMediaMessageHandlerTest;
import org.rmt2.api.handlers.batch.AudioMetadataBatchImportApiHandler;
import org.rmt2.api.handlers.batch.BatchImportConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.MultimediaResponse;

import com.api.BatchFileException;
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
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class, AudioMetadataBatchImportApiHandler.class,
        AvBatchFileFactory.class,
        SystemConfigurator.class })
public class AudioBatchImportMessageHandlerTest extends BaseMediaMessageHandlerTest {
    public static final String API_ERROR = "Test validation error: API Error occurred";

    private AvBatchFileProcessorApi mockApi;


    /**
     * 
     */
    public AudioBatchImportMessageHandlerTest() {
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
        mockApi = Mockito.mock(AvBatchFileProcessorApi.class);
        PowerMockito.mockStatic(AvBatchFileFactory.class);
        when(AvBatchFileFactory.createMediaFileBatchImportApiInstance(isA(String.class))).thenReturn(mockApi);
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
    public void testSuccess() {
        String request = RMT2File.getFileContentsAsString("xml/batch/AudioMetadataImportBatchRequest.xml");
        try {
            when(this.mockApi.processBatch()).thenReturn(100);
        } catch (BatchFileException e) {
            Assert.fail("Unable to setup mock stub for audio batch file import");
        }
        
        MessageHandlerResults results = null;
        AudioMetadataBatchImportApiHandler handler = new AudioMetadataBatchImportApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.MEDIA_AUDIO_METADATA_IMPORT_BATCH, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        MultimediaResponse actualRepsonse = (MultimediaResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(100, actualRepsonse.getReplyStatus().getRecordCount().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_CODE_SUCCESS, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(BatchImportConst.MESSAGE_SUCCESS, actualRepsonse.getReplyStatus().getMessage());
    }
    
}

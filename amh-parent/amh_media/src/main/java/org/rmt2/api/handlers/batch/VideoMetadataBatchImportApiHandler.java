package org.rmt2.api.handlers.batch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.modules.audiovideo.batch.AvBatchFileFactory;
import org.modules.audiovideo.batch.AvBatchFileProcessorApi;
import org.modules.audiovideo.batch.AvBatchImportParameters;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.api.handlers.lookup.genre.GenreApiHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.BatchImportType;
import org.rmt2.jaxb.MultimediaRequest;
import org.rmt2.jaxb.MultimediaResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.util.media.BatchImportTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.messaging.webservice.WebServiceConstants;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * A message handler for importing data into the MIME database system using an
 * external file containing the metadata that describes the individual video
 * files by invoking the Media API.
 * <p>
 * The following input parameters are expected to be retrieved from the request
 * and passed to the media API operation for processing:
 * <ol>
 * <li>Server Name: The name of the server where the audio files reside.</li>
 * <li>Share Name: Serves as the base directory to server name for locating
 * media files. For Windows systems, the share name should be suffixed with a
 * "$".</li>
 * <li>Root Path: Can be used as the context part of the path. If the share name
 * is not used, then this will typically be a drive letter (including the colon)
 * for Windows systems. Otherwise, it is an additional segment of the path.</li>
 * <li>Path/Location: The relative path where the media files live on the
 * server. Alternatively, this parameter could serve as the full path to where
 * the media files live and ignores server_name, share_name, and root_path
 * parameters. This parameter is required. When this parameter is used, the the
 * actual input file (<i>data/video_batch_import.txt</i>) is expected to live
 * relative to this location.
 * <li>Import File Path: The full path to the external import file containing
 * the media metadata to import into the system. The parameter must include the
 * actual name of the actual import file containing the video metadata to import
 * into the system. All other paramters are ignored. This parameter is required.
 * </li>
 * </ol>
 * 
 * @author roy.terrell
 *
 */
public class VideoMetadataBatchImportApiHandler extends 
        AbstractJaxbMessageHandler<MultimediaRequest, MultimediaResponse, List<BatchImportType>> {
    
    private static final Logger logger = Logger.getLogger(VideoMetadataBatchImportApiHandler.class);

    private ObjectFactory jaxbObjFactory;

    /**
     * @param payload
     */
    public VideoMetadataBatchImportApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createMultimediaResponse();
        logger.info(VideoMetadataBatchImportApiHandler.class.getName() + " was instantiated successfully");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.api.messaging.jms.handler.AbstractMessageHandler#processRequest(java
     * .lang.String, java.io.Serializable)
     */
    @Override
    public MessageHandlerResults processMessage(String command, Serializable payload) throws MessageHandlerCommandException {
        MessageHandlerResults r = super.processMessage(command, payload);

        if (r != null) {
            // This means an error occurred.
            return r;
        }
        switch (command) {
            case ApiTransactionCodes.MEDIA_VIDEO_METADATA_IMPORT_BATCH:
                r = this.doOperation(this.requestObj);
                break;
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to import video
     * metadata fetch one or more Genere objects.
     * 
     * @param req
     *            an instance of {@link MultimediaRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(MultimediaRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<BatchImportType> b = null;

        try {
            rs.setRecordCount(0);
            this.responseObj.setHeader(req.getHeader());
            // Set reply status
            rs.setReturnStatus(WebServiceConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            
            AvBatchImportParameters parms = new AvBatchImportParameters();
            parms.setServerName(req.getCriteria().getAudioBatchImportCriteria().getServerName());
            parms.setShareName(req.getCriteria().getAudioBatchImportCriteria().getShareName());
            parms.setRootPath(req.getCriteria().getAudioBatchImportCriteria().getRootPath());

            // Required. Path that points to the acutal video files and their
            // respective .jpg's. Can be a full path (ignore the previous parms)
            // or can be relative to the previous parms
            parms.setPath(req.getCriteria().getAudioBatchImportCriteria().getLocation());
            // Required. Points to the import file containing the metatdata for
            // each video file.
            parms.setImportFilePath(req.getCriteria().getAudioBatchImportCriteria().getImportFilePath());

            int rc = 0;
            AvBatchFileProcessorApi api = AvBatchFileFactory.createCsvBatchImportApiInstance(parms);

            // UI-37: Added for capturing the update user id
            api.setApiUser(this.userId);

            rc = api.processBatch();
            b = this.buildBatchResults(api);
            rs.setMessage(BatchImportConst.MESSAGE_SUCCESS);
            rs.setRecordCount(rc);
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(GenreApiHandlerConst.MESSAGE_FETCH_ERROR);
            rs.setExtMessage(e.getMessage());
        }
        String xml = this.buildResponse(b, rs);
        results.setPayload(xml);
        return results;
    }
    

    
    @Override
    protected void validateRequest(MultimediaRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Genre message request element is invalid");
        }
    }

    private List<BatchImportType> buildBatchResults(AvBatchFileProcessorApi api) {
        BatchImportType bit = BatchImportTypeBuilder.Builder.create()
                .withStartTime(api.getStartTime())
                .withEndTime(api.getEndTime())
                .withSuccessTotal(api.getSuccessCount())
                .withFailureTotal(api.getErrorCount())
                .withProcessTotal(api.getTotCnt())
                .withNonAudioFilesEncountered(api.getNonAvFileCnt())
                .build();

        List<BatchImportType> list = new ArrayList<>();
        list.add(bit);
        return list;
    }

    @Override
    protected String buildResponse(List<BatchImportType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            this.responseObj.setProfile(this.jaxbObjFactory.createMimeDetailGroup());
            this.responseObj.getProfile().setBatchImportResults(payload.get(0));
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }


}

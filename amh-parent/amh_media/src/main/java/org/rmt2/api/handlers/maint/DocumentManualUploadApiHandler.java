package org.rmt2.api.handlers.maint;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.dto.ContentDto;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.MultimediaRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Message handler for manually uploading media content via the Media API.
 * 
 * @author roy.terrell
 *
 */
public class DocumentManualUploadApiHandler extends MediaContentApiHandler {
    
    private static final Logger logger = Logger.getLogger(DocumentManualUploadApiHandler.class);

    /**
     * @param payload
     */
    public DocumentManualUploadApiHandler() {
        super();
        logger.info(DocumentManualUploadApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.MEDIA_MANUAL_UPLOAD_CONTENT:
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
     * Handler for invoking the appropriate API in order to create a new or
     * update an existing artist object.
     * 
     * @param req
     *            an instance of {@link MultimediaRequest}
     */
    @Override
    protected void processTransactionCode(MultimediaRequest req) {
        try {
            // Get artist profile data
            ContentDto contentDto = MediaContentJaxbDtoFactory.createMediaContentDtoInstance(req.getProfile()
                    .getAudioVideoContent());

            // Make API call
            int rc = this.api.add(contentDto);
            if (rc > 0) {
                this.rs.setMessage(MediaContentApiHandlerConst.MESSAGE_UPLOAD_SUCCESS);
            }
            this.rs.setRecordCount(1);

            this.jaxbResults = this.jaxbObjFactory.createMimeContentType();
            this.jaxbResults.setContentId(rc);
            this.jaxbResults.setFilename(contentDto.getFilename());
            this.jaxbResults.setFilesize(contentDto.getSize());
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(MediaContentApiHandlerConst.MESSAGE_UPLOAD_ERROR);
            rs.setExtMessage(e.getMessage());
            this.jaxbResults = null;
        }
    }

    /**
     * Verifies the multimedia request payload exist and that it contains a
     * valid media content profile for updating.
     * 
     * @param req
     *            instance of {@link MultimediaRequest}
     * @throws {@link InvalidDataException} when <i>req</i> is null, transaction
     *         code is incorrect, does not contain a profile section, profile
     *         section does not contain an audio video details section, the
     *         audio video details section does not contatin an artist, or
     *         contains two or more artists to be processed.
     */
    @Override
    protected void validateRequest(MultimediaRequest req) throws InvalidDataException {
        super.validateRequest(req);
        try {
            Verifier.verify(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.MEDIA_MANUAL_UPLOAD_CONTENT));
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Invalid transaction code for this message handler: "
                    + req.getHeader().getTransaction());
        }
        
        try {
            Verifier.verifyNotNull(req.getProfile());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(MediaContentApiHandlerConst.MESSAGE_UPLOAD_MISSING_PROFILE_ERROR);
        }
        
        try {
            Verifier.verifyNotNull(req.getProfile().getAudioVideoContent());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(
                    MediaContentApiHandlerConst.MESSAGE_UPLOAD_MISSING_PROFILE_AUDIOVIDEOCONTENT);
        }
    }

}

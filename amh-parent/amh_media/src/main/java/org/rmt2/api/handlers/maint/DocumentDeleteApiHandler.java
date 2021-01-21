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
 * Message handler for deleting media content via the Media API.
 * 
 * @author roy.terrell
 *
 */
public class DocumentDeleteApiHandler extends MediaContentApiHandler {
    
    private static final Logger logger = Logger.getLogger(DocumentDeleteApiHandler.class);

    /**
     * @param payload
     */
    public DocumentDeleteApiHandler() {
        super();
        logger.info(DocumentDeleteApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.MEDIA_CONTENT_DELETE:
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
     * Handler for invoking the appropriate API in order to delete the content
     * record
     * 
     * @param req
     *            an instance of {@link MultimediaRequest}
     */
    @Override
    protected void processTransactionCode(MultimediaRequest req) {
        this.jaxbResults = null;
        try {
            // Get artist profile data
            ContentDto criteria = MediaContentJaxbDtoFactory
                    .createMediaContentDtoInstance(req.getCriteria().getContentCriteria());

            // Make API call
            int rc = this.api.delete(criteria.getContentId());
            if (rc == 1) {
                this.rs.setMessage(MediaContentApiHandlerConst.MESSAGE_DELETED);
                this.rs.setRecordCount(rc);
            }
            else {
                this.rs.setMessage(MediaContentApiHandlerConst.MESSAGE_NOT_FOUND);
                this.rs.setRecordCount(0);
            }

            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(MediaContentApiHandlerConst.MESSAGE_DELETE_ERROR);
            rs.setExtMessage(e.getMessage());
        }
    }

    /**
     * Verifies the multimedia request payload exist and that it contains a
     * valid criteria section.
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
            Verifier.verify(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.MEDIA_CONTENT_DELETE));
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Invalid transaction code for this message handler: "
                    + req.getHeader().getTransaction());
        }
        
        try {
            Verifier.verifyNotNull(req.getCriteria());
            Verifier.verifyNotNull(req.getCriteria().getContentCriteria());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(MediaContentApiHandlerConst.MESSAGE_DELETE_MISSING_CRITERIA);
        }
    }

}

package org.rmt2.api.handlers.listener;

import java.io.Serializable;

import org.apache.log4j.Logger;
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
 * Message handler for checking the health of the media file listener via the
 * Media API.
 * 
 * @author roy.terrell
 *
 */
public class MediaFileListenerHealthCheckApiHandler extends MediaFileListenerApiHandler {
    
    private static final Logger logger = Logger.getLogger(MediaFileListenerHealthCheckApiHandler.class);

    /**
     * @param payload
     */
    public MediaFileListenerHealthCheckApiHandler() {
        super();
        logger.info(MediaFileListenerHealthCheckApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.MEDIA_FILE_LISTENER_HEALTH:
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
     * Handler for invoking the appropriate API in order to stop the media file
     * listener
     * 
     * @param req
     *            an instance of {@link MultimediaRequest}
     */
    @Override
    protected void processTransactionCode(MultimediaRequest req) {
        try {
            // Make API call
            String status = this.api.getMediaFileListenerStatus();
            this.jaxbResults = this.jaxbObjFactory.createMediaFileListenerDetailsType();
            this.jaxbResults.setStatus(status);
            this.rs.setMessage(MediaFileListenerApiHandlerConst.MESSAGE_STATUS);
            this.rs.setRecordCount(1);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(MediaFileListenerApiHandlerConst.MESSAGE_ERROR);
            rs.setExtMessage(e.getMessage());
        }
    }

    
    @Override
    protected void validateRequest(MultimediaRequest req) throws InvalidDataException {
        super.validateRequest(req);
        try {
            Verifier.verify(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.MEDIA_FILE_LISTENER_HEALTH));
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Invalid transaction code for this message handler: "
                    + req.getHeader().getTransaction());
        }
    }

}

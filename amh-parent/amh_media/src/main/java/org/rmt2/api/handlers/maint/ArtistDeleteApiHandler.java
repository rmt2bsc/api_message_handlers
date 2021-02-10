package org.rmt2.api.handlers.maint;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.dto.ArtistDto;
import org.modules.audiovideo.AudioVideoApi;
import org.modules.audiovideo.AudioVideoFactory;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.MultimediaRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Message handler for deleting artist related messages for the Media API.
 * <p>
 * Currently, the delete operation at the API level is not supported. Whenever a
 * delete artist request is made to the server, that Media API will send back an
 * error stating that the operation is not supported.
 * 
 * @author roy.terrell
 *
 */
public class ArtistDeleteApiHandler extends AudioVideoApiHandler {
    
    private static final Logger logger = Logger.getLogger(ArtistDeleteApiHandler.class);

    /**
     * @param payload
     */
    public ArtistDeleteApiHandler() {
        super();
        logger.info(ArtistDeleteApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.MEDIA_ARTIST_DELETE:
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
     * Handler for invoking the appropriate API in order to delete an existing
     * artist object.
     * 
     * @param req
     *            an instance of {@link MultimediaRequest}
     */
    @Override
    protected void processTransactionCode(MultimediaRequest req) {
        try {
            // Get artist profile data. Artist criteria should possess an artist
            // id. Otherwise, API will reject request.
            ArtistDto artistDto = ArtistJaxbDtoFactory.createArtistDtoInstance(req.getProfile().getAudioVideoDetails()
                    .getArtist().get(0));

            // Make API call
            AudioVideoApi api = AudioVideoFactory.createApi();
            int rc = api.deleteArtist(artistDto);
            String msg = null;
            msg = RMT2String.replace(ArtistApiHandlerConst.MESSAGE_DELETE_SUCCESS, String.valueOf(rc),
                    ApiMessageHandlerConst.MSG_PLACEHOLDER);
            this.rs.setMessage(msg);
            this.rs.setRecordCount(rc);
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            this.rs.setMessage(ArtistApiHandlerConst.MESSAGE_DELETE_ERROR);
            this.rs.setExtMessage(e.getMessage());
        }
        this.jaxbResults.add(null);
        this.responseObj.setHeader(req.getHeader());
    }

    /**
     * Verifies the multimedia request payload exist and that it contains a
     * valid artist profile for deleting.
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
            Verifier.verify(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.MEDIA_ARTIST_DELETE));
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Invalid transaction code for this message handler: "
                    + req.getHeader().getTransaction());
        }
        
        try {
            Verifier.verifyNotNull(req.getProfile());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(ArtistApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_ERROR);
        }
        
        try {
            Verifier.verifyNotNull(req.getProfile().getAudioVideoDetails());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(ArtistApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_AUDIOVIDEODETAILS);
        }
        
        try {
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().size() > 0);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(ArtistApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_ARTIST);
        }
        
        try {
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().size() == 1);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(ArtistApiHandlerConst.MESSAGE_UPDATE_TOO_MANY_ARTIST);
        }
    }

}

package org.rmt2.api.handlers.maint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ArtistDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;
import org.modules.audiovideo.AudioVideoApi;
import org.modules.audiovideo.AudioVideoFactory;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ArtistType;
import org.rmt2.jaxb.AudioVideoType;
import org.rmt2.jaxb.MultimediaRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Message handler for updating existing artist related messages for the Media
 * API.
 * 
 * @author roy.terrell
 *
 */
public class ArtistUpdateApiHandler extends AudioVideoApiHandler {
    
    private static final Logger logger = Logger.getLogger(ArtistUpdateApiHandler.class);

    /**
     * @param payload
     */
    public ArtistUpdateApiHandler() {
        super();
        logger.info(ArtistUpdateApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.MEDIA_ARTIST_UPDATE:
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
            ArtistDto artistDto = ArtistJaxbDtoFactory.createArtistDtoInstance(req.getProfile().getAudioVideoDetails()
                    .getArtist().get(0));

            boolean isNew = artistDto.getId() == 0;

            // Make API call
            AudioVideoApi api = AudioVideoFactory.createApi();

            // UI-37: Added for capturing the update user id
            api.setApiUser(this.userId);

            int rc = api.updateArtist(artistDto);
            String msg = null;
            int artistId = 0;
            if (rc > 0) {
                if (isNew) {
                    this.rs.setMessage(ArtistApiHandlerConst.MESSAGE_UPDATE_NEW_SUCCESS);
                    artistId = rc;
                }
                else {
                    this.rs.setMessage(ArtistApiHandlerConst.MESSAGE_UPDATE_EXISTING_SUCCESS);
                    artistId = artistDto.getId();
                }
            }
            else {
                msg = RMT2String.replace(ArtistApiHandlerConst.MESSAGE_UPDATE_NO_CHANGE, String.valueOf(artistDto.getId()),
                        ApiMessageHandlerConst.MSG_PLACEHOLDER);
                this.rs.setMessage(msg);
            }
            this.rs.setRecordCount(1);

            // Confirm artist change
            ArtistDto criteria = Rmt2MediaDtoFactory.getAvArtistInstance(null);
            criteria.setId(artistId);
            List<ArtistDto> confirmDto = api.getArtist(criteria);
            List<ArtistType> confirmJaxb = ArtistJaxbDtoFactory.createArtistJaxbInstance(confirmDto);
            AudioVideoType avt = this.jaxbObjFactory.createAudioVideoType();
            avt.getArtist().addAll(confirmJaxb);
            List<AudioVideoType> list = new ArrayList<>();
            list.add(avt);
            this.jaxbResults.add(avt);

            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(ArtistApiHandlerConst.MESSAGE_UPDATE_ERROR);
            rs.setExtMessage(e.getMessage());
        }
    }

    /**
     * Verifies the multimedia request payload exist and that it contains a
     * valid artist profile for updating.
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
            Verifier.verify(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.MEDIA_ARTIST_UPDATE));
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

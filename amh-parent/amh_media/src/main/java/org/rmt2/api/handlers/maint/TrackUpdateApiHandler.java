package org.rmt2.api.handlers.maint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ArtistDto;
import org.dto.ProjectDto;
import org.dto.TracksDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;
import org.modules.audiovideo.AudioVideoApi;
import org.modules.audiovideo.AudioVideoFactory;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ArtistType;
import org.rmt2.jaxb.AudioVideoType;
import org.rmt2.jaxb.AvProjectType;
import org.rmt2.jaxb.MultimediaRequest;
import org.rmt2.jaxb.TrackType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Message handler for updating media track related messages for the Media API.
 * 
 * @author roy.terrell
 *
 */
public class TrackUpdateApiHandler extends AudioVideoApiHandler {
    
    private static final Logger logger = Logger.getLogger(TrackUpdateApiHandler.class);

    /**
     * @param payload
     */
    public TrackUpdateApiHandler() {
        super();
        logger.info(TrackUpdateApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.MEDIA_TRACK_UPDATE:
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
     * Handler for invoking the appropriate API in order to update a media track
     * object.
     * 
     * @param req
     *            an instance of {@link MultimediaRequest}
     */
    @Override
    protected void processTransactionCode(MultimediaRequest req) {
        int rc = 0;
        try {
            int trackId = 0;
            // Get criteria data
            TracksDto criteriaDto = TrackJaxbDtoFactory.createTracksDtoInstance(req.getProfile().getAudioVideoDetails()
                    .getArtist().get(0).getProjects().getProject().get(0).getTracks().getTrack().get(0));

            boolean isNew = criteriaDto.getTrackId() == 0;

            // Make API call
            AudioVideoApi api = AudioVideoFactory.createApi();
            rc = api.updateTrack(criteriaDto);
            String msg = null;
            if (rc > 0) {
                if (isNew) {
                    this.rs.setMessage(TrackApiHandlerConst.MESSAGE_UPDATE_NEW_SUCCESS);
                    trackId = rc;
                }
                else {
                    this.rs.setMessage(TrackApiHandlerConst.MESSAGE_UPDATE_EXISTING_SUCCESS);
                    trackId = criteriaDto.getTrackId();
                }
            }
            else {
                msg = RMT2String.replace(TrackApiHandlerConst.MESSAGE_UPDATE_NO_CHANGE, String.valueOf(criteriaDto.getTrackId()),
                        ApiMessageHandlerConst.MSG_PLACEHOLDER);
                this.rs.setMessage(msg);
            }
            this.rs.setRecordCount(1);

            // Confirm project change
            // Get Track data
            TracksDto criteria1 = Rmt2MediaDtoFactory.getAvTrackInstance(null);
            criteria1.setTrackId(trackId);
            List<TracksDto> confirmDto1 = api.getTracks(criteria1);
            List<TrackType> confirmJaxb1 = TrackJaxbDtoFactory.createTrackJaxbInstance(confirmDto1);

            // Get Project data
            ProjectDto criteria2 = Rmt2MediaDtoFactory.getAvProjectInstance(null);
            criteria2.setProjectId(confirmDto1.get(0).getProjectId());
            List<ProjectDto> confirmDto2 = api.getProject(criteria2);
            List<AvProjectType> confirmJaxb2 = ArtistProjectJaxbDtoFactory.createProjectJaxbInstance(confirmDto2);

            // Merge project and track data
            confirmJaxb2.get(0).setTracks(this.jaxbObjFactory.createTracksType());
            confirmJaxb2.get(0).getTracks().getTrack().addAll(confirmJaxb1);

            // Get artist data
            ArtistDto criteria3 = Rmt2MediaDtoFactory.getAvArtistInstance(null);
            criteria3.setId(confirmDto2.get(0).getArtistId());
            List<ArtistDto> confirmDto3 = api.getArtist(criteria3);
            List<ArtistType> confirmJaxb3 = ArtistJaxbDtoFactory.createArtistJaxbInstance(confirmDto3);

            // Merge artist and project data
            confirmJaxb3.get(0).setProjects(this.jaxbObjFactory.createAvProjectsType());
            confirmJaxb3.get(0).getProjects().getProject().addAll(confirmJaxb2);

            AudioVideoType avt = this.jaxbObjFactory.createAudioVideoType();
            avt.getArtist().addAll(confirmJaxb3);
            List<AudioVideoType> list = new ArrayList<>();
            list.add(avt);
            this.jaxbResults.add(avt);

            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(TrackApiHandlerConst.MESSAGE_UPDATE_ERROR);
            rs.setExtMessage(e.getMessage());
        }
    }

    
    @Override
    protected void validateRequest(MultimediaRequest req) throws InvalidDataException {
        super.validateRequest(req);
        try {
            Verifier.verify(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.MEDIA_TRACK_UPDATE));
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Invalid transaction code for this message handler: "
                    + req.getHeader().getTransaction());
        }
        
        try {
            Verifier.verifyNotNull(req.getProfile());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(TrackApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_ERROR);
        }
        
        try {
            Verifier.verifyNotNull(req.getProfile().getAudioVideoDetails());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(TrackApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_AUDIOVIDEODETAILS);
        }
        
        try {
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().size() > 0);
            Verifier.verifyNotNull(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects());
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects().getProject().size() > 0);
            Verifier.verifyNotNull(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects().getProject().get(0).getTracks());
            Verifier.verifyNotNull(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects().getProject().get(0).getTracks().getTrack().size() > 0);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(TrackApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_TRACKS);
        }
        
        try {
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().size() == 1);
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects().getProject().size() == 1);
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects().getProject().get(0)
                    .getTracks().getTrack().size() == 1);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(TrackApiHandlerConst.MESSAGE_UPDATE_TOO_MANY_TRACKS);
        }
    }

}

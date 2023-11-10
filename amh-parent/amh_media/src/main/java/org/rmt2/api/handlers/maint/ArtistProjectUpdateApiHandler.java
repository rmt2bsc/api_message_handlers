package org.rmt2.api.handlers.maint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ArtistDto;
import org.dto.ProjectDto;
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

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Message handler for updating existing artist album/movie project related
 * messages for the Media API.
 * 
 * @author roy.terrell
 *
 */
public class ArtistProjectUpdateApiHandler extends AudioVideoApiHandler {
    
    private static final Logger logger = Logger.getLogger(ArtistProjectUpdateApiHandler.class);

    /**
     * @param payload
     */
    public ArtistProjectUpdateApiHandler() {
        super();
        logger.info(ArtistProjectUpdateApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.MEDIA_ARTIST_PROJECT_UPDATE:
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
            int projectId = 0;
            // Get artist profile data
            ProjectDto projectDto = ArtistProjectJaxbDtoFactory.createProjectDtoInstance(req.getProfile().getAudioVideoDetails()
                    .getArtist().get(0).getProjects().getProject().get(0));

            boolean isNew = projectDto.getProjectId() == 0;

            // Make API call
            AudioVideoApi api = AudioVideoFactory.createApi();

            // UI-37: Added for capturing the update user id
            api.setApiUser(this.userId);

            int rc = api.updateProject(projectDto);
            String msg = null;
            if (rc > 0) {
                if (isNew) {
                    this.rs.setMessage(ArtistProjectApiHandlerConst.MESSAGE_UPDATE_NEW_SUCCESS);
                    projectId = rc;
                }
                else {
                    this.rs.setMessage(ArtistProjectApiHandlerConst.MESSAGE_UPDATE_EXISTING_SUCCESS);
                    projectId = projectDto.getProjectId();
                }
            }
            else {
                msg = RMT2String.replace(ArtistProjectApiHandlerConst.MESSAGE_UPDATE_NO_CHANGE,
                        String.valueOf(projectDto.getProjectId()),
                        ApiMessageHandlerConst.MSG_PLACEHOLDER);
                this.rs.setMessage(msg);
            }
            this.rs.setRecordCount(1);

            // Confirm project change
            // Get Project data
            ProjectDto criteria = Rmt2MediaDtoFactory.getAvProjectInstance(null);
            criteria.setProjectId(projectId);
            List<ProjectDto> confirmDto = api.getProject(criteria);
            List<AvProjectType> confirmJaxb = ArtistProjectJaxbDtoFactory.createProjectJaxbInstance(confirmDto);
            
            // Get artist data
            ArtistDto criteria2 = Rmt2MediaDtoFactory.getAvArtistInstance(null);
            criteria2.setId(confirmDto.get(0).getArtistId());
            List<ArtistDto> confirmDto2 = api.getArtist(criteria2);
            List<ArtistType> confirmJaxb2 = ArtistJaxbDtoFactory.createArtistJaxbInstance(confirmDto2);
            
            // Merge artist and project data
            confirmJaxb2.get(0).setProjects(this.jaxbObjFactory.createAvProjectsType());
            confirmJaxb2.get(0).getProjects().getProject().addAll(confirmJaxb);
            
            AudioVideoType avt = this.jaxbObjFactory.createAudioVideoType();
            avt.getArtist().addAll(confirmJaxb2);
            List<AudioVideoType> list = new ArrayList<>();
            list.add(avt);
            this.jaxbResults.add(avt);

            this.jaxbResults.add(null);
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(ArtistProjectApiHandlerConst.MESSAGE_UPDATE_ERROR);
            rs.setExtMessage(e.getMessage());
        }
    }

    /**
     * Verifies the multimedia request payload exist and that it contains a
     * valid artist/project profile structure for updating.
     * 
     * @param req
     *            instance of {@link MultimediaRequest}
     * @throws {@link InvalidDataException} when <i>req</i> is null, transaction
     *         code is incorrect, does not contain a profile section, profile
     *         section does not contain an audio video details section, the
     *         audio video details section does not contatin an artist an its
     *         project, or contains multiple artist/projects to be processed.
     */
    @Override
    protected void validateRequest(MultimediaRequest req) throws InvalidDataException {
        super.validateRequest(req);
        try {
            Verifier.verify(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.MEDIA_ARTIST_PROJECT_UPDATE));
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Invalid transaction code for this message handler: "
                    + req.getHeader().getTransaction());
        }
        
        try {
            Verifier.verifyNotNull(req.getProfile());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(ArtistProjectApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_ERROR);
        }
        
        try {
            Verifier.verifyNotNull(req.getProfile().getAudioVideoDetails());
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(ArtistProjectApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_AUDIOVIDEODETAILS);
        }
        
        try {
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().size() > 0);
            Verifier.verifyNotNull(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects());
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects().getProject().size() > 0);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(ArtistProjectApiHandlerConst.MESSAGE_UPDATE_MISSING_PROFILE_PROJECT);
        }
        
        try {
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().size() == 1);
            Verifier.verifyTrue(req.getProfile().getAudioVideoDetails().getArtist().get(0).getProjects().getProject().size() == 1);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException(ArtistProjectApiHandlerConst.MESSAGE_UPDATE_TOO_MANY_ARTIST_PROJECTS);
        }
    }

}

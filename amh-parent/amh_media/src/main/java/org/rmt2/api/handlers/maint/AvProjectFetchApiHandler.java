package org.rmt2.api.handlers.maint;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ProjectDto;
import org.modules.audiovideo.AudioVideoApi;
import org.modules.audiovideo.AudioVideoFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AudioVideoType;
import org.rmt2.jaxb.MultimediaRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Message handler for fetching audio/video project related messages for the
 * Media API.
 * 
 * @author roy.terrell
 *
 */
public class AvProjectFetchApiHandler extends AudioVideoApiHandler {
    
    private static final Logger logger = Logger.getLogger(AvProjectFetchApiHandler.class);

    /**
     * @param payload
     */
    public AvProjectFetchApiHandler() {
        super();
        logger.info(AvProjectFetchApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.MEDIA_ARTIST_PROJECT_GET:
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
     * Handler for invoking the appropriate API in order to fetch one or more
     * artist objects.
     * 
     * @param req
     *            an instance of {@link MultimediaRequest}
     */
    @Override
    protected void processTransactionCode(MultimediaRequest req) {
        try {
            // Get criteria data
            ProjectDto criteriaDto = ArtistProjectJaxbDtoFactory.createProjectDtoInstance(req.getCriteria()
                    .getAudioVideoCriteria());

            // Make API call
            AudioVideoApi api = AudioVideoFactory.createApi();

            // UI-37: Added for capturing the update user id
            api.setApiUser(this.userId);

            List<ProjectDto> dtoList = api.getProject(criteriaDto);
            if (dtoList == null) {
                this.rs.setMessage(ArtistProjectApiHandlerConst.MESSAGE_NOT_FOUND);
                this.rs.setRecordCount(0);
            }
            else {
                // Package API results into JAXB objects
                AudioVideoType avt = this.buildProjecttOnly(dtoList);
                this.jaxbResults.add(avt);
                this.rs.setMessage(ArtistProjectApiHandlerConst.MESSAGE_FOUND);
                this.rs.setRecordCount(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(ArtistProjectApiHandlerConst.MESSAGE_FETCH_ERROR);
            rs.setExtMessage(e.getMessage());
        }
    }
    
    @Override
    protected void validateRequest(MultimediaRequest req) throws InvalidDataException {
        super.validateRequest(req);
        try {
            Verifier.verify(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.MEDIA_ARTIST_PROJECT_GET));
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Invalid transaction code for this message handler: "
                    + req.getHeader().getTransaction());
        }
    }

}

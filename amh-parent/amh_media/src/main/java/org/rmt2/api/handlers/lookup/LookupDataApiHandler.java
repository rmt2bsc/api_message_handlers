package org.rmt2.api.handlers.lookup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.GenreDto;
import org.dto.MediaTypeDto;
import org.dto.ProjectTypeDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;
import org.modules.audiovideo.AudioVideoApi;
import org.modules.audiovideo.AudioVideoFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.api.handlers.lookup.genre.GenreApiHandlerConst;
import org.rmt2.api.handlers.lookup.genre.GenreJaxbDtoFactory;
import org.rmt2.api.handlers.lookup.mediatype.MediaTypeApiHandlerConst;
import org.rmt2.api.handlers.lookup.mediatype.MediaTypeJaxbDtoFactory;
import org.rmt2.api.handlers.lookup.projecttype.ProjectTypeJaxbDtoFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.GenreType;
import org.rmt2.jaxb.MediatypeType;
import org.rmt2.jaxb.MimeDetailGroup;
import org.rmt2.jaxb.MultimediaRequest;
import org.rmt2.jaxb.MultimediaResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ProjecttypeType;
import org.rmt2.jaxb.ReplyStatusType;

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
 * Handles and routes all lookup codes related messages for the Media API.
 * 
 * @author roy.terrell
 *
 */
public class LookupDataApiHandler extends 
        AbstractJaxbMessageHandler<MultimediaRequest, MultimediaResponse, List<MimeDetailGroup>> {
    
    private static final Logger logger = Logger.getLogger(LookupDataApiHandler.class);

    private ObjectFactory jaxbObjFactory;

    /**
     * @param payload
     */
    public LookupDataApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createMultimediaResponse();
        logger.info(LookupDataApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.MEDIA_LOOKUP_VALUES_GET:
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
     * Handler for invoking the appropriate API in order to fetch all media
     * lookup data objects.
     * 
     * @param req
     *            an instance of {@link MultimediaRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(MultimediaRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        int totalRecordsFetchted = 0;
        MimeDetailGroup mdg = this.jaxbObjFactory.createMimeDetailGroup();

        try {
            // Set reply status
            rs.setReturnStatus(WebServiceConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            
            AudioVideoApi api = AudioVideoFactory.createApi();

            // Get Genre data
            List<GenreType> genreListJaxb = null;
            GenreDto genreCriteriaDto = Rmt2MediaDtoFactory.getAvGenreInstance(null);
            List<GenreDto> genreListDto = api.getGenre(genreCriteriaDto);
            if (genreListDto != null) {
                genreListJaxb = GenreJaxbDtoFactory.createGenreJaxbInstance(genreListDto);
                rs.setMessage(GenreApiHandlerConst.MESSAGE_FOUND);
                totalRecordsFetchted += genreListDto.size();
                mdg.setGenres(this.jaxbObjFactory.createGenresType());
                mdg.getGenres().getGenre().addAll(genreListJaxb);
            }

            // Get Media Type data
            List<MediatypeType> mediaTypeListJaxb = null;
            MediaTypeDto mediaTypeCriteriaDto = Rmt2MediaDtoFactory.getAvMediaTypeInstance(null);
            List<MediaTypeDto> mediaTypeListDto = api.getMediaType(mediaTypeCriteriaDto);
            if (mediaTypeListDto != null) {
                mediaTypeListJaxb = MediaTypeJaxbDtoFactory.createMediaTypeJaxbInstance(mediaTypeListDto);
                rs.setMessage(MediaTypeApiHandlerConst.MESSAGE_FOUND);
                totalRecordsFetchted += mediaTypeListDto.size();
                mdg.setMediatypes(this.jaxbObjFactory.createMediaTypes());
                mdg.getMediatypes().getMediaType().addAll(mediaTypeListJaxb);
            }

            // Get Project Type data
            List<ProjecttypeType> projectTypeListJaxb = null;
            ProjectTypeDto projectTypeCriteriaDto = Rmt2MediaDtoFactory.getAvProjectTypeInstance(null);
            List<ProjectTypeDto> projectTypeListDto = api.getProjectType(projectTypeCriteriaDto);
            if (projectTypeListDto != null) {
                projectTypeListJaxb = ProjectTypeJaxbDtoFactory.createProjectTypeJaxbInstance(projectTypeListDto);
                totalRecordsFetchted += projectTypeListDto.size();
                mdg.setProjecttypes(this.jaxbObjFactory.createProjectTypes());
                mdg.getProjecttypes().getProjectType().addAll(projectTypeListJaxb);
            }

            // Repoort count of total items fetched
            if (totalRecordsFetchted > 0) {
                rs.setMessage(LookupDataApiHandlerConst.MESSAGE_FOUND);
            }
            else {
                rs.setMessage(LookupDataApiHandlerConst.MESSAGE_NOT_FOUND);
            }

            rs.setRecordCount(totalRecordsFetchted);
            this.responseObj.setHeader(req.getHeader());
            
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(LookupDataApiHandlerConst.MESSAGE_FETCH_ERROR);
            rs.setExtMessage(e.getMessage());
        }

        List<MimeDetailGroup> mdgList = new ArrayList<>();
        mdgList.add(mdg);
        String xml = this.buildResponse(mdgList, rs);
        results.setPayload(xml);
        return results;
    }
    

    
    @Override
    protected void validateRequest(MultimediaRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Media lookup data query message request element is invalid");
        }
    }

    @Override
    protected String buildResponse(List<MimeDetailGroup> payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            this.responseObj.setProfile(payload.get(0));
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }


}

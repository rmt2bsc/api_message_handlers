package org.rmt2.api.handlers.maint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ArtistDto;
import org.dto.ProjectDto;
import org.dto.TracksDto;
import org.dto.VwArtistDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;
import org.modules.audiovideo.AudioVideoApi;
import org.modules.audiovideo.AudioVideoApiException;
import org.modules.audiovideo.AudioVideoFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ArtistType;
import org.rmt2.jaxb.AudioVideoType;
import org.rmt2.jaxb.AvProjectType;
import org.rmt2.jaxb.MimeContentType;
import org.rmt2.jaxb.MultimediaRequest;
import org.rmt2.jaxb.MultimediaResponse;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.jaxb.TrackType;
import org.rmt2.util.media.AVProjectTypeBuilder;
import org.rmt2.util.media.ArtistTypeBuilder;

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
 * An abstract message handler for processing audio and video related messages
 * for the Media API.
 * 
 * @author roy.terrell
 *
 */
public abstract class AudioVideoApiHandler extends
        AbstractJaxbMessageHandler<MultimediaRequest, MultimediaResponse, List<AudioVideoType>> {
    
    private static final Logger logger = Logger.getLogger(AudioVideoApiHandler.class);

    protected ObjectFactory jaxbObjFactory;
    protected AudioVideoApi api;
    protected MessageHandlerCommonReplyStatus rs;
    List<AudioVideoType> jaxbResults;

    /**
     * @param payload
     */
    public AudioVideoApiHandler() {
        super();
        this.api = AudioVideoFactory.createApi();

        // UI-37: Added for capturing the update user id
        this.transApi = this.api;

        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createMultimediaResponse();
        this.rs = new MessageHandlerCommonReplyStatus();
        this.jaxbResults = new ArrayList<>();
        logger.info(AudioVideoApiHandler.class.getName() + " was instantiated successfully");
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
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to fetch, update, or
     * delete audio/video related objects.
     * 
     * @param req
     *            an instance of {@link MultimediaRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(MultimediaRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();

        // Set reply status
        rs.setReturnStatus(WebServiceConstants.RETURN_STATUS_SUCCESS);
        rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);

        // Process transaction specifically
        this.processTransactionCode(req);

        String xml = this.buildResponse(this.jaxbResults, this.rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Implement this method to perform specific operations based on the
     * transaction code contained in the request
     * 
     * @param req
     *            instance of {@link MultimediaRequest}
     */
    protected abstract void processTransactionCode(MultimediaRequest req);
    
    /**
     * Verifies the multimedia request payload exists.
     * 
     * @param req
     *            instance of {@link MultimediaRequest}
     */
    @Override
    protected void validateRequest(MultimediaRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("Genre message request element is invalid");
        }
    }


    @Override
    protected String buildResponse(List<AudioVideoType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null && payload.size() > 0) {
            this.responseObj.setProfile(this.jaxbObjFactory.createMimeDetailGroup());
            this.responseObj.getProfile().setAudioVideoDetails(payload.get(0));
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }

    /**
     * 
     * @param payload
     * @param replyStatus
     * @return
     */
    protected String buildResponse(MimeContentType payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);
        }

        if (payload != null) {
            this.responseObj.setProfile(this.jaxbObjFactory.createMimeDetailGroup());
            this.responseObj.getProfile().setAudioVideoContent(payload);
        }

        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }

    /**
     * Builds an AudioVideoType object graph of artist objects excluding project
     * and track details
     * 
     * @param artistDtoList
     *            a List of {@link ArtistDto} instances
     * @return {@link AudioVideoType}
     * @throws AudioVideoApiException
     */
    protected AudioVideoType buildArtistOnly(List<ArtistDto> artistDtoList) throws AudioVideoApiException {
        List<ArtistType> jaxbArtists = ArtistJaxbDtoFactory.createArtistJaxbInstance(artistDtoList);
        AudioVideoType avt = this.jaxbObjFactory.createAudioVideoType();
        avt.getArtist().addAll(jaxbArtists);
        return avt;
    }

    /**
     * Builds an AudioVideoType object graph of project objects excluding track
     * details
     * 
     * @param artistDtoList
     *            a List of {@link ProjectDto} instances
     * @return {@link AudioVideoType}
     * @throws AudioVideoApiException
     */
    protected AudioVideoType buildProjecttOnly(List<ProjectDto> projectDtoList) throws AudioVideoApiException {
        List<AvProjectType> jaxbProjects = ArtistProjectJaxbDtoFactory.createProjectJaxbInstance(projectDtoList);
        AudioVideoType avt = this.jaxbObjFactory.createAudioVideoType();

        for (AvProjectType jaxbProject : jaxbProjects) {
            ArtistType jaxbArtist = ArtistTypeBuilder.Builder.create()
                    .withArtistId(jaxbProject.getArtistId())
                    .withProject(jaxbProject)
                    .build();
            avt.getArtist().add(jaxbArtist);
        }
        return avt;
    }

    /**
     * Builds an AudioVideoType object graph of track objects.
     * 
     * @param trackDtoList
     *            a List of {@link TracksDto} instances
     * @return {@link AudioVideoType}
     * @throws AudioVideoApiException
     */
    protected AudioVideoType buildTrackOnly(List<TracksDto> trackDtoList) throws AudioVideoApiException {
        List<TrackType> jaxbList = TrackJaxbDtoFactory.createTrackJaxbInstance(trackDtoList);
        AudioVideoType avt = this.jaxbObjFactory.createAudioVideoType();

        for (TrackType jaxb : jaxbList) {
            AvProjectType apt = AVProjectTypeBuilder.Builder.create()
                    .withArtistId(0)
                    .withProjectId(jaxb.getProjectId())
                    .withTrack(jaxb)
                    .build();
            ArtistType jaxbArtist = ArtistTypeBuilder.Builder.create()
                    .withArtistId(0)
                    .withArtistName(jaxb.getArtist())
                    .withProject(apt)
                    .build();
            avt.getArtist().add(jaxbArtist);
        }
        return avt;
    }

    /**
     * Builds an AudioVideoType object graph of list of VwArtistDto objects.
     * 
     * @param projectDtoList
     *            a List of {@link VwArtistDto} instances
     * @return {@link AudioVideoType}
     * @throws AudioVideoApiException
     */
    protected AudioVideoType buildConsolidatedMedia(List<VwArtistDto> mediaList) throws AudioVideoApiException {
        AudioVideoType avt = this.jaxbObjFactory.createAudioVideoType();

        List<ArtistType> results = ConsolidatedMediaJaxbDtoFactory.createMediaJaxbInstance(mediaList);
        if (results != null) {
            avt.getArtist().addAll(results);
        }
        return avt;
    }

    /**
     * Builds an AudioVideoType object graph of list of VwArtistDto objects.
     * 
     * @param mediaList
     *            a List of {@link VwArtistDto} instances
     * @param searhTerm
     *            used to filter out duplicate records based on track, projects
     *            and/or artists
     * @return {@link AudioVideoType}
     * @throws AudioVideoApiException
     */
    protected AudioVideoType buildConsolidatedMedia(List<VwArtistDto> mediaList, String searhTerm) throws AudioVideoApiException {
        AudioVideoType avt = this.jaxbObjFactory.createAudioVideoType();

        List<ArtistType> results = ConsolidatedMediaJaxbDtoFactory.createMediaJaxbInstance(mediaList, searhTerm);
        if (results != null) {
            avt.getArtist().addAll(results);
        }
        return avt;
    }

    /**
     * Dynamically builds a full AudioVideoType object graph beggining with a
     * list of artist objects.
     * <p>
     * Using the artist id contained in each ArtistDto, it fetches all related
     * projects and tracks from an underlying datasource via the AudioVideoApi.
     * 
     * @param artistDtoList
     *            a List of {@link ArtistDto} instances. Each instance is
     *            required to have an artist id.
     * @return {@link AudioVideoType}
     * @throws AudioVideoApiException
     */
    protected AudioVideoType buildDBAudioVideoType(List<ArtistDto> artistDtoList) throws AudioVideoApiException {
        List<ArtistType> jaxbArtists = ArtistJaxbDtoFactory.createArtistJaxbInstance(artistDtoList);
        AudioVideoType avt = this.jaxbObjFactory.createAudioVideoType();
        avt.getArtist().addAll(jaxbArtists);

        // Attach the projects and tracks of each artist
        for (ArtistType item : jaxbArtists) {
            List<AvProjectType> projects = this.buildArtistProjects(item);
            item.getProjects().getProject().addAll(projects);
        }
        return avt;
    }

    private List<AvProjectType> buildArtistProjects(ArtistType artist) throws AudioVideoApiException {
        ProjectDto criteria = Rmt2MediaDtoFactory.getAvProjectInstance(null);
        criteria.setArtistId(artist.getArtistId());
        List<ProjectDto> projects = this.api.getProject(criteria);
        List<AvProjectType> list = ArtistProjectJaxbDtoFactory.createProjectJaxbInstance(projects);
        
        // Attach the tracks to each project
        for (AvProjectType item : list) {
            List<TrackType> tracks = this.buildProjectTracks(item);
            item.getTracks().getTrack().addAll(tracks);
        }
        return list;
    }

    private List<TrackType> buildProjectTracks(AvProjectType proj) throws AudioVideoApiException {
        TracksDto criteria = Rmt2MediaDtoFactory.getAvTrackInstance(null);
        criteria.setProjectId(proj.getProjectId());
        List<TracksDto> tracks = this.api.getTracks(criteria);
        List<TrackType> list = TrackJaxbDtoFactory.createTrackJaxbInstance(tracks);

        return list;
    }
}

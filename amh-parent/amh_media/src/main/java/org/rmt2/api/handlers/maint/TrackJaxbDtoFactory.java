package org.rmt2.api.handlers.maint;

import java.util.ArrayList;
import java.util.List;

import org.dto.TracksDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;
import org.rmt2.jaxb.AudioVideoCriteriaType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.TrackType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.media.TrackTypeBuilder;

import com.RMT2Base;
import com.api.util.RMT2Money;

/**
 * A factory for creating DTO and JAXB instances for the audio/video track
 * related message handlers.
 * 
 * @author Roy Terrell.
 * 
 */
public class TrackJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>TracksDto</i> using a valid
     * <i>AudioVideoCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link AudioVideoCriteriaType}
     * @return an instance of {@link TracksDto}
     */
    public static final TracksDto createTracksDtoInstance(AudioVideoCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        TracksDto dto = Rmt2MediaDtoFactory.getAvTrackInstance(null);
        if (jaxbObj.getTrackId() != null) {
            dto.setTrackId(jaxbObj.getTrackId());
        }
        if (jaxbObj.getProjectId() != null) {
            dto.setProjectId(jaxbObj.getProjectId());
        }
        if (jaxbObj.getTrackTitle() != null) {
            dto.setTrackTitle(jaxbObj.getTrackTitle());
        }
        if (jaxbObj.getArtistName() != null) {
            dto.setTrackArtist(jaxbObj.getArtistName());
        }
        if (jaxbObj.getGenreId() != null) {
            dto.setGenreId(jaxbObj.getGenreId());
        }

        return dto;
    }

    /**
     * Creates an instance of <i>TracksDto</i> using a valid <i>TrackType</i>
     * JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link TrackType}
     * @return an instance of {@link TracksDto}
     */
    public static final TracksDto createTracksDtoInstance(TrackType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        TracksDto dto = Rmt2MediaDtoFactory.getAvTrackInstance(null);
        dto.setTrackId(jaxbObj.getTrackId());
        dto.setProjectId(jaxbObj.getProjectId());
        dto.setTrackTitle(jaxbObj.getTrackName());
        dto.setTrackNumber(jaxbObj.getTrackNumber());
        dto.setTrackHours(jaxbObj.getHours());
        dto.setTrackMinutes(jaxbObj.getMinutes());
        dto.setTrackSeconds(jaxbObj.getSeconds());
        if (jaxbObj.getDiscNumber() != null) {
            dto.setTrackDisc(jaxbObj.getDiscNumber().toString());
        }
        dto.setTrackProducer(jaxbObj.getProducer());
        dto.setTrackComposer(jaxbObj.getComposer());
        dto.setTrackLyricist(jaxbObj.getLyricist());
        dto.setLocServername(jaxbObj.getLocationServername());
        dto.setLocSharename(jaxbObj.getLocationSharename());
        dto.setLocRootPath(jaxbObj.getLocationRootPath());
        dto.setLocPath(jaxbObj.getLocationPath());
        dto.setLocFilename(jaxbObj.getLocationFilename());
        dto.setComments(jaxbObj.getComments());
        dto.setGenreId(jaxbObj.getGenreId());

        if (jaxbObj.getTracking() != null) {
            if (jaxbObj.getTracking().getDateCreated() != null) {
                dto.setDateCreated(jaxbObj.getTracking().getDateCreated().toGregorianCalendar().getTime());
            }
            if (jaxbObj.getTracking().getDateUpdated() != null) {
                dto.setDateUpdated(jaxbObj.getTracking().getDateUpdated().toGregorianCalendar().getTime());
            }
            dto.setUpdateUserId(jaxbObj.getTracking().getUserId());
        }

        return dto;
    }

    /**
     * Creates an instance of <i>TrackType</i> using a valid <i>TracksDto</i>
     * JAXB object.
     * 
     * @param dto
     *            an instance of {@link TracksDto}
     * @return an instance of {@link TrackType}
     */
    public static final TrackType createTrackJaxbInstance(TracksDto dto) {
        if (dto == null) {
            return null;
        }

        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated())
                .build();

        TrackType obj = TrackTypeBuilder.Builder.create()
                .withTrackId(dto.getTrackId())
                .withProjectId(dto.getProjectId())
                .withTrackName(dto.getTrackTitle())
                .withTrackNumber(dto.getTrackNumber())
                .withHours(dto.getTrackHours())
                .withMinutes(dto.getTrackMinutes())
                .withSeconds(dto.getTrackSeconds())
                .withDiscNumber(RMT2Money.isNumeric(dto.getTrackDisc()) ? Integer.valueOf(dto.getTrackDisc()) : 1)
                .withProducer(dto.getTrackProducer())
                .withComposer(dto.getTrackComposer())
                .withLyrisist(dto.getTrackLyricist())
                .withLocationServerName(dto.getLocServername())
                .withLocationShareName(dto.getLocSharename())
                .withLocationRootPath(dto.getLocRootPath())
                .withLocationPath(dto.getLocPath())
                .withLocationFilename(dto.getLocFilename())
                .withComments(dto.getComments())
                .withGenreId(dto.getGenreId())
                .withRecordTracking(rtt)
                .build();
        return obj;
    }

    /**
     * Creates a List instance of <i>TrackType</i> using a valid List of
     * <i>TracksDto</i> DTO objects.
     * 
     * @param results
     *            List of {@link TracksDto}
     * @return List of {@link TrackType} objects
     */
    public static final List<TrackType> createTrackJaxbInstance(List<TracksDto> results) {
        List<TrackType> list = new ArrayList<>();
        if (results == null) {
            return list;
        }
        for (TracksDto item : results) {
            list.add(TrackJaxbDtoFactory.createTrackJaxbInstance(item));
        }
        return list;
    }
}

package org.rmt2.api.handlers.maint;

import java.util.ArrayList;
import java.util.List;

import org.dto.ArtistDto;
import org.dto.VwArtistDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;
import org.rmt2.jaxb.ArtistType;
import org.rmt2.jaxb.AudioVideoCriteriaType;
import org.rmt2.jaxb.AvProjectType;
import org.rmt2.util.media.AVProjectTypeBuilder;
import org.rmt2.util.media.ArtistTypeBuilder;

import com.RMT2Base;

/**
 * A factory for creating DTO and JAXB instances for the audio/video artist
 * related message handlers.
 * 
 * @author Roy Terrell.
 * 
 */
public class ArtistJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>ArtistDto</i> using a valid
     * <i>AudioVideoCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link AudioVideoCriteriaType}
     * @return an instance of {@link ArtistDto}
     */
    public static final ArtistDto createArtistDtoInstance(AudioVideoCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ArtistDto dto = Rmt2MediaDtoFactory.getAvArtistInstance(null);
        if (jaxbObj.getArtistId() != null) {
            dto.setId(jaxbObj.getArtistId());
        }
        dto.setName(jaxbObj.getArtistName());
        return dto;
    }


    /**
     * Creates an instance of <i>ArtistDto</i> using a valid <i>ArtistType</i>
     * JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ArtistType}
     * @return an instance of {@link ArtistDto}
     */
    public static final ArtistDto createArtistDtoInstance(ArtistType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ArtistDto dto = Rmt2MediaDtoFactory.getAvArtistInstance(null);
        if (jaxbObj.getArtistId() != null) {
            dto.setId(jaxbObj.getArtistId());
        }
        else {
            dto.setId(0);
        }
        dto.setName(jaxbObj.getArtistName());
        return dto;
    }

    /**
     * Creates an instance of <i>ArtistType</i> using a valid <i>ArtistDto</i>
     * JAXB object.
     * 
     * @param dto
     *            an instance of {@link ArtistDto}
     * @return an instance of {@link ArtistType}
     */
    public static final ArtistType createArtistJaxbInstance(ArtistDto dto) {
        if (dto == null) {
            return null;
        }
        ArtistType obj = ArtistTypeBuilder.Builder.create()
                .withArtistName(dto.getName())
                .withArtistId(dto.getId())
                .build();
        return obj;
    }

    /**
     * Creates a List instance of <i>ArtistType</i> using a valid List of
     * <i>ArtistDto</i> DTO objects.
     * 
     * @param results
     *            List of {@link ArtistDto}
     * @return List of {@link ArtistType} objects
     */
    public static final List<ArtistType> createArtistJaxbInstance(List<ArtistDto> dto) {
        if (dto == null) {
            return null;
        }
        List<ArtistType> list = new ArrayList<>();
        for (ArtistDto item : dto) {
            list.add(ArtistJaxbDtoFactory.createArtistJaxbInstance(item));
        }
        return list;
    }

    /**
     * Creates an instance of <i>VwArtistDto</i> using a valid
     * <i>AudioVideoCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link AudioVideoCriteriaType}
     * @return an instance of {@link VwArtistDto}
     */
    public static final VwArtistDto createVwArtistProjectDtoInstance(AudioVideoCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        VwArtistDto dto = Rmt2MediaDtoFactory.getVwAudioVideoArtistsInstance(null);
        if (jaxbObj.getArtistId() != null) {
            // Get specific artist by id
            dto.setArtistId(jaxbObj.getArtistId());
        }
        if (!jaxbObj.getArtistName().isEmpty()) {
            dto.setArtistName(jaxbObj.getArtistName());
        }

        if (jaxbObj.getProjectId() != null) {
            dto.setProjectId(jaxbObj.getProjectId());
        }

        dto.setProjectName(jaxbObj.getProjectTitle());

        if (jaxbObj.getTrackId() != null) {
            dto.setTrackId(jaxbObj.getTrackId());
        }

        dto.setTrackName(jaxbObj.getTrackTitle());

        if (jaxbObj.getProjectTypeId() != null) {
            dto.setProjectTypeId(jaxbObj.getProjectTypeId());
        }

        dto.setProjectTypeName(jaxbObj.getProjectTypeName());

        if (jaxbObj.getYear() != null) {
            dto.setYear(jaxbObj.getYear());
        }

        if (jaxbObj.getRipped() != null) {
            dto.setRippedInd(jaxbObj.getRipped());
        }

        if (jaxbObj.getGenreId() != null) {
            dto.setGenreId(jaxbObj.getGenreId());
        }

        if (jaxbObj.getMediaTypeId() != null) {
            dto.setMediaTypeId(jaxbObj.getMediaTypeId());
        }

        return dto;
    }

    /**
     * Creates a List instance of <i>ArtistType</i> using a valid List of
     * <i>VwArtistDto</i> DTO objects.
     * 
     * @param dto
     *            List of {@link VwArtistDto}
     * @return List of {@link ArtistType} objects
     */
    public static final List<ArtistType> createVwArtistProjectJaxbInstance(List<VwArtistDto> dto) {
        if (dto == null) {
            return null;
        }
        List<ArtistType> list = new ArrayList<>();
        for (VwArtistDto item : dto) {
            list.add(ArtistJaxbDtoFactory.createVwArtistProjectJaxbInstance(item));
        }
        return list;
    }

    /**
     * Creates an instance of <i>ArtistType</i> using a valid <i>VwArtistDto</i>
     * JAXB object.
     * 
     * @param dto
     *            an instance of {@link VwArtistDto}
     * @return an instance of {@link ArtistType}
     */
    public static final ArtistType createVwArtistProjectJaxbInstance(VwArtistDto dto) {
        if (dto == null) {
            return null;
        }

        AvProjectType pt = AVProjectTypeBuilder.Builder.create()
                .withProjectId(dto.getProjectId())
                .withArtistId(dto.getArtistId())
                .withComments(dto.getProjectComments())
                .withTitle(dto.getProjectName())
                .build();

        ArtistType obj = ArtistTypeBuilder.Builder.create()
                .withArtistName(dto.getArtistName())
                .withArtistId(dto.getArtistId())
                .withProject(pt)
                .build();
        return obj;
    }

}

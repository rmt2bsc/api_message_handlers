package org.rmt2.api.handlers.maint;

import java.util.ArrayList;
import java.util.List;

import org.dto.ProjectDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;
import org.rmt2.jaxb.AudioVideoCriteriaType;
import org.rmt2.jaxb.AvProjectType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.media.AVProjectTypeBuilder;

import com.RMT2Base;

/**
 * A factory for creating DTO and JAXB instances for the audio/video project
 * related message handlers.
 * 
 * @author Roy Terrell.
 * 
 */
public class ProjectJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>ProjectDto</i> using a valid
     * <i>AudioVideoCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link AudioVideoCriteriaType}
     * @return an instance of {@link ProjectDto}
     */
    public static final ProjectDto createProjectDtoInstance(AudioVideoCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ProjectDto dto = Rmt2MediaDtoFactory.getAvProjectInstance(null);
        if (jaxbObj.getProjectId() != null) {
            dto.setProjectId(jaxbObj.getProjectId());
        }
        if (jaxbObj.getProjectTitle() != null) {
            dto.setTitle(jaxbObj.getProjectTitle());
        }
        if (jaxbObj.getArtistId() != null) {
            dto.setArtistId(jaxbObj.getArtistId());
        }
        if (jaxbObj.getProjectTypeId() != null) {
            dto.setProjectTypeId(jaxbObj.getProjectTypeId());
        }
        if (jaxbObj.getGenreId() != null) {
            dto.setGenreId(jaxbObj.getGenreId());
        }
        if (jaxbObj.getMediaTypeId() != null) {
            dto.setMediaTypeId(jaxbObj.getMediaTypeId());
        }
        if (jaxbObj.getYear() != null) {
            dto.setYear(jaxbObj.getYear());
        }
        if (jaxbObj.getRipped() != null) {
            dto.setRippedInd(jaxbObj.getRipped());
        }

        return dto;
    }

    /**
     * Creates an instance of <i>ProjectDto</i> using a valid
     * <i>AvProjectType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link AvProjectType}
     * @return an instance of {@link ProjectDto}
     */
    public static final ProjectDto createProjectDtoInstance(AvProjectType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ProjectDto dto = Rmt2MediaDtoFactory.getAvProjectInstance(null);
        dto.setProjectId(jaxbObj.getProjectId());
        dto.setTitle(jaxbObj.getTitle());
        dto.setArtistId(jaxbObj.getArtistId());
        dto.setProjectTypeId(jaxbObj.getProjectTypeId());
        dto.setGenreId(jaxbObj.getGenreId());
        dto.setMediaTypeId(jaxbObj.getMediaTypeId());
        dto.setYear(jaxbObj.getYear());
        dto.setRippedInd(jaxbObj.getRipped());
        dto.setContentId(jaxbObj.getContentId());
        dto.setMasterDupId(jaxbObj.getMasterDupId());
        dto.setCost(jaxbObj.getCost());
        dto.setContentPath(jaxbObj.getContentPath());
        dto.setContentFilename(jaxbObj.getContentFilename());
        dto.setArtWorkPath(jaxbObj.getArtWorkPath());
        dto.setArtWorkFilename(jaxbObj.getArtWorkFilename());
        dto.setComments(jaxbObj.getComments());
        dto.setTotalTime(jaxbObj.getTotalTime());
        dto.setProducer(jaxbObj.getProducer());

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
     * Creates an instance of <i>AvProjectType</i> using a valid
     * <i>ProjectDto</i> JAXB object.
     * 
     * @param dto
     *            an instance of {@link ProjectDto}
     * @return an instance of {@link AvProjectType}
     */
    public static final AvProjectType createProjectJaxbInstance(ProjectDto dto) {
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

        AvProjectType obj = AVProjectTypeBuilder.Builder.create()
                .withProjectId(dto.getProjectId())
                .withTitle(dto.getTitle())
                .withArtistId(dto.getArtistId())
                .withMediaTypeId(dto.getMediaTypeId())
                .withGenreTypeId(dto.getGenreId())
                .withProjectTypeId(dto.getProjectTypeId())
                .withYearId(dto.getYear())
                .withContentId(dto.getContentId())
                .withMasterDupId(dto.getMasterDupId())
                .withRipped(dto.getRippedInd())
                .withCost(dto.getCost())
                .withContentPath(dto.getContentPath())
                .withContentFilename(dto.getContentFilename())
                .withArtWorkPath(dto.getArtWorkPath())
                .withArtWorkFilename(dto.getArtWorkFilename())
                .withComments(dto.getComments())
                .withTotalTime(dto.getTotalTime())
                .withProducer(dto.getProducer())
                .withRecordTracking(rtt)
                .build();
        return obj;
    }

    /**
     * Creates a List instance of <i>AvProjectType</i> using a valid List of
     * <i>ProjectDto</i> DTO objects.
     * 
     * @param results
     *            List of {@link ProjectDto}
     * @return List of {@link AvProjectType} objects
     */
    public static final List<AvProjectType> createProjectJaxbInstance(List<ProjectDto> results) {
        List<AvProjectType> list = new ArrayList<>();
        for (ProjectDto item : results) {
            list.add(ProjectJaxbDtoFactory.createProjectJaxbInstance(item));
        }
        return list;
    }
}

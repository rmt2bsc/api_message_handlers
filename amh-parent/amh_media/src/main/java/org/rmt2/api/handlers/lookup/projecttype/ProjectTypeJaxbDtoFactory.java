package org.rmt2.api.handlers.lookup.projecttype;

import java.util.ArrayList;
import java.util.List;

import org.dto.ProjectTypeDto;
import org.dto.adapter.orm.Rmt2MediaDtoFactory;
import org.rmt2.jaxb.AudioVideoCriteriaType;
import org.rmt2.jaxb.ProjecttypeType;
import org.rmt2.util.media.ProjecttypeTypeBuilder;

import com.RMT2Base;

/**
 * A factory for creating DTO and JAXB instances for the Project-Type Media
 * message handler project.
 * 
 * @author Roy Terrell.
 * 
 */
public class ProjectTypeJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>ProjectTypeDto</i> using a valid
     * <i>AudioVisualCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link AudioVisualCriteriaType}
     * @return an instance of {@link ProjectTypeDto}
     */
    public static final ProjectTypeDto createProjectTypeDtoInstance(AudioVideoCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ProjectTypeDto dto = Rmt2MediaDtoFactory.getAvProjectTypeInstance(null);
        if (jaxbObj.getProjectTypeId() != null) {
            dto.setUid(jaxbObj.getProjectTypeId());
        }
        dto.setDescription(jaxbObj.getProjectTypeName());
        return dto;
    }

    /**
     * Creates an instance of <i>ProjectTypeDto</i> using a valid
     * <i>ProjecttypeType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ProjecttypeType}
     * @return an instance of {@link ProjectTypeDto}
     */
    public static final ProjectTypeDto createProjectTypeDtoInstance(ProjecttypeType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ProjectTypeDto dto = Rmt2MediaDtoFactory.getAvProjectTypeInstance(null);
        if (jaxbObj.getProjectTypeId() != null) {
            dto.setUid(jaxbObj.getProjectTypeId());
        }
        dto.setDescription(jaxbObj.getProjectTypeName());
        return dto;
    }

    /**
     * Creates an instance of <i>ProjecttypeType</i> using a valid
     * <i>ProjectTypeDto</i> JAXB object.
     * 
     * @param dto
     *            an instance of {@link ProjectTypeDto}
     * @return an instance of {@link ProjecttypeType}
     */
    public static final ProjecttypeType createProjectTypeJaxbInstance(ProjectTypeDto dto) {
        if (dto == null) {
            return null;
        }
        ProjecttypeType obj = ProjecttypeTypeBuilder.Builder.create()
                .withName(dto.getDescritpion())
                .withUID(dto.getUid())
                .build();
        return obj;
    }

    /**
     * Creates a List instance of <i>ProjecttypeType</i> using a valid List of
     * <i>ProjectTypeDto</i> DTO objects.
     * 
     * @param results
     *            List of {@link ProjectTypeDto}
     * @return List of {@link ProjecttypeType} objects
     */
    public static final List<ProjecttypeType> createProjectTypeJaxbInstance(List<ProjectTypeDto> results) {
        List<ProjecttypeType> list = new ArrayList<>();
        for (ProjectTypeDto item : results) {
            ProjecttypeType jaxbObj = ProjecttypeTypeBuilder.Builder.create()
                    .withUID(item.getUid())
                    .withName(item.getDescritpion())
                    .build();

            list.add(jaxbObj);
        }
        return list;
    }
}

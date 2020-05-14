package org.rmt2.api.handlers.admin.project;

import org.dto.Project2Dto;
import org.dto.ProjectClientDto;
import org.dto.adapter.orm.ProjectObjectFactory;
import org.rmt2.jaxb.ProjectCriteriaType;
import org.rmt2.jaxb.ProjectType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.projecttracker.admin.ProjectTypeBuilder;

import com.RMT2Base;

/**
 * A factory for converting project related JAXB objects to DTO and vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class ProjectJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>ProjectClientDto</i> using a valid
     * <i>ProjectCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link ProjectCriteriaType}
     * @return an instance of {@link ProjectClientDto}
     */
    public static final ProjectClientDto createProjectClientDtoCriteriaInstance(ProjectCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ProjectClientDto dto = ProjectObjectFactory.createProjectClientDtoInstance(null);
        if (jaxbObj.getClientId() != null) {
            dto.setClientId(jaxbObj.getClientId().intValue());
        }
        if (jaxbObj.getClientBusinessId() != null) {
            dto.setBusinessId(jaxbObj.getClientBusinessId().intValue());
        }
        if (jaxbObj.getClientName() != null) {
            dto.setClientName(jaxbObj.getClientName());
        }
        if (jaxbObj.getProjectId() != null) {
            dto.setProjId(jaxbObj.getProjectId().intValue());
        }
        if (jaxbObj.getProjectName() != null) {
            dto.setProjectDescription(jaxbObj.getProjectName());
        }
        if (jaxbObj.getProjectEffectiveDate() != null) {
            dto.setProjectEffectiveDate(jaxbObj.getProjectEffectiveDate().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getProjectEndDate() != null) {
            dto.setProjectEndDate(jaxbObj.getProjectEndDate().toGregorianCalendar().getTime());
        }
        return dto;
    }
    
    /**
     * Created an instance of Project2Dto from an ProjectType object
     * 
     * @param jaxbObj
     *            an instance of {@link ProjectType}
     * @return an instance of {@link Project2Dto}
     */
    public static final Project2Dto createProjetDtoInstance(ProjectType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        Project2Dto dto = ProjectObjectFactory.createProjectDtoInstance(null);
        if (jaxbObj.getClient() != null) {
            if (jaxbObj.getClient().getClientId() != null) {
                dto.setClientId(jaxbObj.getClient().getClientId().intValue());
            }
        }
        if (jaxbObj.getProjectId() != null) {
            dto.setProjId(jaxbObj.getProjectId().intValue());
        }
        else {
            dto.setProjId(0);
        }
        dto.setProjectDescription(jaxbObj.getDescription());
        if (jaxbObj.getEffectiveDate() != null) {
            dto.setProjectEffectiveDate(jaxbObj.getEffectiveDate().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getEndDate() != null) {
            dto.setProjectEndDate(jaxbObj.getEndDate().toGregorianCalendar().getTime());
        }

        return dto;
    }
    
    /**
     * Created an instance of ProjectType from an ProjectClientDto object
     * 
     * @param dto
     *            an instance of {@link ProjectClientDto}
     * @return an instance of {@link ProjectType}
     */
    public static final ProjectType createProjectJaxbInstance(ProjectClientDto dto) {
        if (dto == null) {
            return null;
        }
        RecordTrackingType tracking = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .build();

        ProjectType client = ProjectTypeBuilder.Builder.create()
                .withClientId(dto.getClientId())
                .withClientName(dto.getClientName())
                .withClientBusinessId(dto.getBusinessId())
                .withBillRate(dto.getClientBillRate())
                .withOvertimeBillRate(dto.getClientOtBillRate())
                .withProjectId(dto.getProjId())
                .withProjectName(dto.getProjectDescription())
                .withEffectiveDate(dto.getProjectEffectiveDate())
                .withEndDate(dto.getProjectEndDate())
                .withRecordTracking(tracking)
                .build();

        return client;
    }

    /**
     * Created an instance of ProjectType from an Project2Dto object
     * 
     * @param dto
     *            an instance of {@link Project2Dto}
     * @return an instance of {@link ProjectType}
     */
    public static final ProjectType createProjectJaxbInstance(Project2Dto dto) {
        if (dto == null) {
            return null;
        }
        RecordTrackingType tracking = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .build();

        ProjectType client = ProjectTypeBuilder.Builder.create()
                .withClientId(dto.getClientId())
                .withProjectId(dto.getProjId())
                .withProjectName(dto.getProjectDescription())
                .withEffectiveDate(dto.getProjectEffectiveDate())
                .withEndDate(dto.getProjectEndDate())
                .withRecordTracking(tracking)
                .build();

        return client;
    }
}

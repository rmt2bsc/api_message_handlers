package org.rmt2.api.handlers.employee;

import org.dto.ProjectEmployeeDto;
import org.dto.adapter.orm.ProjectObjectFactory;
import org.rmt2.jaxb.EmployeeProjectCriteriaType;
import org.rmt2.jaxb.EmployeeProjectType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeeProjectTypeBuilder;

import com.RMT2Base;

/**
 * A factory for converting Employee/Project project tracker administration
 * related JAXB objects to DTO and vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class EmployeeProjectJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>ProjectEmployeeDto</i> using a valid
     * <i>EmployeeProjectCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link EmployeeProjectCriteriaType}
     * @return an instance of {@link ProjectEmployeeDto}
     */
    public static final ProjectEmployeeDto createDtoCriteriaInstance(EmployeeProjectCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ProjectEmployeeDto dto = ProjectObjectFactory.createEmployeeProjectDtoInstance(null);
        if (jaxbObj.getEmpProjId() != null) {
            dto.setEmpProjId(jaxbObj.getEmpProjId().intValue());
        }
        if (jaxbObj.getEmpId() != null) {
            dto.setEmpId(jaxbObj.getEmpId().intValue());
        }
        if (jaxbObj.getProjId() != null) {
            dto.setProjId(jaxbObj.getProjId().intValue());
        }
        if (jaxbObj.getEffectiveDate() != null) {
            dto.setProjEmpEffectiveDate(jaxbObj.getEffectiveDate().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getEndDate() != null) {
            dto.setProjEmpEndDate(jaxbObj.getEndDate().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getHourlyRate() != null) {
            dto.setHourlyRate(jaxbObj.getHourlyRate().doubleValue());
        }
        if (jaxbObj.getFlatRate() != null) {
            dto.setFlatRate(jaxbObj.getFlatRate().doubleValue());
        }
        return dto;
    }
    
    /**
     * Created an instance of ProjectEmployeeDto from an EmployeeProjectType
     * object
     * 
     * @param jaxbObj
     *            an instance of {@link EmployeeProjectType}
     * @return an instance of {@link ProjectEmployeeDto}
     */
    public static final ProjectEmployeeDto createEmploiyeeDtoInstance(EmployeeProjectType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }

        ProjectEmployeeDto dto = ProjectObjectFactory.createEmployeeProjectDtoInstance(null);
        if (jaxbObj.getEmployeeProjectId() != null) {
            dto.setEmpProjId(jaxbObj.getEmployeeProjectId().intValue());
        }
        else {
            dto.setEmpProjId(0);
        }

        if (jaxbObj.getEmployee() != null && jaxbObj.getEmployee().getEmployeeId() != null) {
            dto.setEmpId(jaxbObj.getEmployee().getEmployeeId().intValue());
        }
        if (jaxbObj.getProject() != null && jaxbObj.getProject().getProjectId() != null) {
            dto.setProjId(jaxbObj.getProject().getProjectId().intValue());
        }
        if (jaxbObj.getEffectiveDate() != null) {
            dto.setProjEmpEffectiveDate(jaxbObj.getEffectiveDate().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getEndDate() != null) {
            dto.setProjectEndDate(jaxbObj.getEndDate().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getHourlyRate() != null) {
            dto.setHourlyRate(jaxbObj.getHourlyRate().doubleValue());
        }
        if (jaxbObj.getHourlyOvertimeRate() != null) {
            dto.setHourlyOverRate(jaxbObj.getHourlyOvertimeRate().doubleValue());
        }
        if (jaxbObj.getFlatRate() != null) {
            dto.setFlatRate(jaxbObj.getFlatRate().doubleValue());
        }
        dto.setComments(jaxbObj.getComments());
        return dto;
    }

    /**
     * Created an instance of EmployeeProjectType from a ProjectEmployeeDto
     * object
     * 
     * @param dto
     *            an instance of {@link ProjectEmployeeDto}
     * @return an instance of {@link EmployeeProjectType}
     */
    public static final EmployeeProjectType createEmployeeJaxbInstance(ProjectEmployeeDto dto) {
        if (dto == null) {
            return null;
        }

        RecordTrackingType tracking = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated())
                .withUserId(dto.getUpdateUserId())
                .build();

        EmployeeProjectType jaxbObj = EmployeeProjectTypeBuilder.Builder.create()
                .withEmpProjId(dto.getEmpProjId())
                .withEmployeeId(dto.getEmpId())
                .withProjectId(dto.getProjId())
                .withEmpProjEffectiveDate(dto.getProjEmpEffectiveDate())
                .withEmpProjEndDate(dto.getProjEmpEndDate())
                .withEmpProjHourlyRate(dto.getHourlyRate())
                .withEmpProjHourlyOvertimeRate(dto.getHourlyOverRate())
                .withEmpProjFlatRate(dto.getFlatRate())
                .withEmpProjComments(dto.getComments())
                .withClientId(dto.getClientId())
                .withClientName(dto.getClientName())
                .withClientAccountNo(dto.getAccountNo())
                .withClientBusinessId(dto.getBusinessId())
                .withClientBillRate(dto.getClientBillRate())
                .withClientOvertimeBillRate(dto.getClientOtBillRate())
                .withProjectName(dto.getProjectDescription())
                .withProjectEffectiveDate(dto.getProjectEffectiveDate())
                .withProjectEndDate(dto.getProjectEndDate())
                .withRecordTracking(tracking)
                .build();

        return jaxbObj;
    }

}

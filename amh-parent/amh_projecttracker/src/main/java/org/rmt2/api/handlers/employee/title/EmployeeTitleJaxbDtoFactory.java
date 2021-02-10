package org.rmt2.api.handlers.employee.title;

import org.dto.EmployeeTitleDto;
import org.dto.adapter.orm.EmployeeObjectFactory;
import org.rmt2.jaxb.EmployeeTitleCriteriaType;
import org.rmt2.jaxb.EmployeeTitleType;
import org.rmt2.util.projecttracker.employee.EmployeeTitleTypeBuilder;

import com.RMT2Base;

/**
 * A factory for converting Employee Title project tracker administration
 * related JAXB objects to DTO and vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class EmployeeTitleJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>EmployeeTitleDto</i> using a valid
     * <i>EmployeeTitleCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link EmployeeTitleCriteriaType}
     * @return an instance of {@link EmployeeTitleDto}
     */
    public static final EmployeeTitleDto createDtoCriteriaInstance(EmployeeTitleCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }

        EmployeeTitleDto dto = EmployeeObjectFactory.createEmployeeTitleDtoInstance(null);
        if (jaxbObj.getEmployeeTitleId() != null) {
            dto.setEmployeeTitleId(jaxbObj.getEmployeeTitleId().intValue());
        }
        else {
            dto.setEmployeeTitleId(0);
        }
        if (jaxbObj.getDescription() != null) {
            dto.setEmployeeTitleDescription(jaxbObj.getDescription());
        }
        return dto;
    }
    
    /**
     * Created an instance of EmployeeTitleDto from an EmployeeTitleType object
     * 
     * @param jaxbObj
     *            an instance of {@link EmployeeTitleType}
     * @return an instance of {@link EmployeeTitleDto}
     */
    public static final EmployeeTitleDto createDtoInstance(EmployeeTitleType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }

        EmployeeTitleDto dto = EmployeeObjectFactory.createEmployeeTitleDtoInstance(null);
        if (jaxbObj.getEmployeeTitleId() != null) {
            dto.setEmployeeTitleId(jaxbObj.getEmployeeTitleId().intValue());
        }
        else {
            dto.setEmployeeTitleId(0);
        }
        dto.setEmployeeTitleDescription(jaxbObj.getDescription());
        return dto;
    }

    /**
     * Created an instance of EmployeeTitleType from a EmployeeTitleDto object
     * 
     * @param dto
     *            an instance of {@link EmployeeTitleDto}
     * @return an instance of {@link EmployeeTitleType}
     */
    public static final EmployeeTitleType createJaxbInstance(EmployeeTitleDto dto) {
        if (dto == null) {
            return null;
        }

        EmployeeTitleType jaxbObj = EmployeeTitleTypeBuilder.Builder.create()
                .withEmployeeTitleId(dto.getEmployeeTitleId())
                .withDescription(dto.getEmployeeTitleDescription())
                .build();

        return jaxbObj;
    }

}

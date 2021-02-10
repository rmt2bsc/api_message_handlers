package org.rmt2.api.handlers.employee.type;

import org.dto.EmployeeTypeDto;
import org.dto.adapter.orm.EmployeeObjectFactory;
import org.rmt2.jaxb.EmployeeTypeCriteriaType;
import org.rmt2.jaxb.EmployeetypeType;
import org.rmt2.util.projecttracker.employee.EmployeetypeTypeBuilder;

import com.RMT2Base;

/**
 * A factory for converting Employee Title project tracker administration
 * related JAXB objects to DTO and vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class EmployeeTypeJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>EmployeeTypeDto</i> using a valid
     * <i>EmployeeTypeCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link EmployeeTypeCriteriaType}
     * @return an instance of {@link EmployeeTypeDto}
     */
    public static final EmployeeTypeDto createDtoCriteriaInstance(EmployeeTypeCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }

        EmployeeTypeDto dto = EmployeeObjectFactory.createEmployeeTypeDtoInstance(null);
        if (jaxbObj.getEmployeeTypeId() != null) {
            dto.setEmployeeTypeId(jaxbObj.getEmployeeTypeId().intValue());
        }
        else {
            dto.setEmployeeTypeId(0);
        }
        if (jaxbObj.getDescription() != null) {
            dto.setEmployeeTypeDescription(jaxbObj.getDescription());
        }
        return dto;
    }
    
    /**
     * Created an instance of EmployeeTypeDto from an EmployeetypeType object
     * 
     * @param jaxbObj
     *            an instance of {@link EmployeetypeType}
     * @return an instance of {@link EmployeeTypeDto}
     */
    public static final EmployeeTypeDto createDtoInstance(EmployeetypeType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }

        EmployeeTypeDto dto = EmployeeObjectFactory.createEmployeeTypeDtoInstance(null);
        if (jaxbObj.getEmployeeTypeId() != null) {
            dto.setEmployeeTypeId(jaxbObj.getEmployeeTypeId().intValue());
        }
        else {
            dto.setEmployeeTypeId(0);
        }
        dto.setEmployeeTypeDescription(jaxbObj.getDescription());
        return dto;
    }

    /**
     * Created an instance of EmployeetypeType from a EmployeeTypeDto object
     * 
     * @param dto
     *            an instance of {@link EmployeeTypeDto}
     * @return an instance of {@link EmployeetypeType}
     */
    public static final EmployeetypeType createJaxbInstance(EmployeeTypeDto dto) {
        if (dto == null) {
            return null;
        }

        EmployeetypeType jaxbObj = EmployeetypeTypeBuilder.Builder.create()
                .withEmployeeTypeId(dto.getEmployeeTypeId())
                .withDescription(dto.getEmployeeTypeDescription())
                .build();

        return jaxbObj;
    }

}

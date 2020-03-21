package org.rmt2.api.handlers.employee;

import org.dto.EmployeeDto;
import org.dto.adapter.orm.EmployeeObjectFactory;
import org.rmt2.jaxb.EmployeeCriteriaType;
import org.rmt2.jaxb.EmployeeType;
import org.rmt2.jaxb.ObjectFactory;

import com.RMT2Base;

/**
 * A factory for converting project tracker administration related JAXB objects
 * to DTO and vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class EmployeeJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>EmployeeDto</i> using a valid
     * <i>EmployeeCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link EmployeeCriteriaType}
     * @return an instance of {@link EmployeeDto}
     */
    public static final EmployeeDto createEmployeeDtoCriteriaInstance(EmployeeCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        EmployeeDto dto = EmployeeObjectFactory.createEmployeeDtoInstance(null);

        return dto;
    }
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final EmployeeDto createEmploiyeeDtoInstance(EmployeeType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        EmployeeDto dto = EmployeeObjectFactory.createEmployeeDtoInstance(null);

        return dto;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    public static final EmployeeType createEmployeeDtoInstance(EmployeeDto dto) {
        if (dto == null) {
            return null;
        }
        ObjectFactory f = new ObjectFactory();
        EmployeeType jaxb = f.createEmployeeType();
        return jaxb;
    }

}

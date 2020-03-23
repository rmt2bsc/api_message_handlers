package org.rmt2.api.handlers.employee;

import java.math.BigInteger;

import org.dto.EmployeeDto;
import org.dto.adapter.orm.EmployeeObjectFactory;
import org.rmt2.jaxb.EmployeeCriteriaType;
import org.rmt2.jaxb.EmployeeTitleType;
import org.rmt2.jaxb.EmployeeType;
import org.rmt2.jaxb.EmployeetypeType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.PersonType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.addressbook.PersonTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeeTitleTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeetypeTypeBuilder;

import com.RMT2Base;
import com.api.util.RMT2Date;

/**
 * A factory for converting Employee project tracker administration related JAXB
 * objects to DTO and vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class EmployeeJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>EmployeeDto</i> using a valid
     * <i>EmployeeCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link EmployeeCriteriaType}
     * @return an instance of {@link EmployeeDto}
     */
    public static final EmployeeDto createEmployeeDtoCriteriaInstance(EmployeeCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        EmployeeDto dto = EmployeeObjectFactory.createEmployeeDtoInstance(null);
        if (jaxbObj.getEmployeeId() != null) {
            dto.setEmployeeId(jaxbObj.getEmployeeId().intValue());
        }
        if (jaxbObj.getEmployeeTitleId() != null) {
            dto.setEmployeeTypeId(jaxbObj.getEmployeeTypeId().intValue());
        }
        if (jaxbObj.getManagerId() != null) {
            dto.setManagerId(jaxbObj.getManagerId().intValue());
        }
        if (jaxbObj.getEmployeeTitleId() != null) {
            dto.setEmployeeTitleId(jaxbObj.getEmployeeTitleId().intValue());
        }
        if (jaxbObj.getFirstName() != null) {
            dto.setEmployeeFirstname(jaxbObj.getFirstName());
        }
        if (jaxbObj.getLastName() != null) {
            dto.setEmployeeLastname(jaxbObj.getLastName());
        }
        if (jaxbObj.getCompanyName() != null) {
            dto.setEmployeeCompanyName(jaxbObj.getCompanyName());
        }
        if (jaxbObj.getSsn() != null) {
            dto.setSsn(jaxbObj.getSsn());
        }
        return dto;
    }
    
    /**
     * Created an instance of EmployeeDto from an EmployeeType object
     * 
     * @param jaxbObj
     *            an instance of {@link EmployeeType}
     * @return an instance of {@link EmployeeDto}
     */
    public static final EmployeeDto createEmploiyeeDtoInstance(EmployeeType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        EmployeeDto dto = EmployeeObjectFactory.createEmployeeDtoInstance(null);
        if (jaxbObj.getEmployeeId() != null) {
            dto.setEmployeeId(jaxbObj.getEmployeeId().intValue());
        }
        if (jaxbObj.getEmployeeType() != null) {
            if (jaxbObj.getEmployeeType().getEmployeeTypeId() != null) {
                dto.setEmployeeTypeId(jaxbObj.getEmployeeType().getEmployeeTypeId().intValue());
            }
            dto.setEmployeeType(jaxbObj.getEmployeeType().getDescription());
        }

        if (jaxbObj.getEmployeeTitle() != null) {
            dto.setEmployeeTitle(jaxbObj.getEmployeeTitle().getDescription());
            if (jaxbObj.getEmployeeTitle().getEmployeeTitleId() != null) {
                dto.setEmployeeTitleId(jaxbObj.getEmployeeTitle().getEmployeeTitleId().intValue());
            }
        }
        dto.setIsManager(jaxbObj.isIsManager() ? 1 : 0);
        if (jaxbObj.getManagerId() != null) {
            dto.setManagerId(jaxbObj.getManagerId().intValue());
        }
        if (jaxbObj.getEmployeeTitle() != null && jaxbObj.getEmployeeTitle().getEmployeeTitleId() != null) {
            dto.setEmployeeTitleId(jaxbObj.getEmployeeTitle().getEmployeeTitleId().intValue());
        }
        if (jaxbObj.getContactDetails() != null) {
            dto.setEmployeeFirstname(jaxbObj.getContactDetails().getFirstName());
            dto.setEmployeeLastname(jaxbObj.getContactDetails().getLastName());
            dto.setEmployeeEmail(jaxbObj.getContactDetails().getEmail());
            if (jaxbObj.getContactDetails().getFirstName() != null && jaxbObj.getContactDetails().getLastName() != null) {
                dto.setEmployeeFullname(jaxbObj.getContactDetails().getFirstName() + " "
                        + jaxbObj.getContactDetails().getLastName());
            }
            dto.setSsn(jaxbObj.getContactDetails().getSsn());
        }
        if (jaxbObj.getLoginId() != null) {
            dto.setLoginId(jaxbObj.getLoginId().intValue());
        }
        dto.setLoginName(jaxbObj.getLoginName());

        if (jaxbObj.getStartDate() != null) {
            dto.setStartDate(jaxbObj.getStartDate().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getTerminationDate() != null) {
            dto.setTerminationDate(jaxbObj.getTerminationDate().toGregorianCalendar().getTime());
        }

        if (jaxbObj.getProjectCount() != null) {
            dto.setProjectCount(jaxbObj.getProjectCount().intValue());
        }
        if (jaxbObj.getTracking() != null) {
            dto.setUpdateUserId(jaxbObj.getTracking().getUserId());
            if (jaxbObj.getTracking().getDateCreated() != null) {
                dto.setDateCreated(jaxbObj.getTracking().getDateCreated().toGregorianCalendar().getTime());
            }
            if (jaxbObj.getTracking().getDateUpdated() != null) {
                dto.setDateUpdated(jaxbObj.getTracking().getDateUpdated().toGregorianCalendar().getTime());
            }
            dto.setIpCreated(jaxbObj.getTracking().getIpCreated());
            dto.setIpUpdated(jaxbObj.getTracking().getIpUpdated());
        }

        return dto;
    }
    
    /**
     * Created an instance of EmployeeType from an EmployeeDto object
     * 
     * @param dto
     *            an instance of {@link EmployeeDto}
     * @return an instance of {@link EmployeeType}
     */
    public static final EmployeeType createEmployeeDtoInstance(EmployeeDto dto) {
        if (dto == null) {
            return null;
        }
        ObjectFactory f = new ObjectFactory();
        EmployeeType jaxbObj = f.createEmployeeType();

        jaxbObj.setEmployeeId(BigInteger.valueOf(dto.getEmployeeId()));

        EmployeetypeType ett = EmployeetypeTypeBuilder.Builder.create()
                .withEmployeeId(dto.getEmployeeTypeId())
                .withDescription(dto.getEmployeeType())
                .build();
        jaxbObj.setEmployeeType(ett);

        EmployeeTitleType ett2 = EmployeeTitleTypeBuilder.Builder.create()
                .withEmployeeTitleId(dto.getEmployeeTitleId())
                .withDescription(dto.getEmployeeTitle())
                .build();
        jaxbObj.setEmployeeTitle(ett2);

        jaxbObj.setIsManager(dto.getIsManager() == 1 ? true : false);
        jaxbObj.setManagerId(BigInteger.valueOf(dto.getManagerId()));

        PersonType pt = PersonTypeBuilder.Builder.create()
                .withFirstName(dto.getEmployeeFirstname())
                .withLastName(dto.getEmployeeLastname())
                .withEmail(dto.getEmployeeEmail())
                .withSocialSecurityNumber(dto.getSsn())
                .build();
        jaxbObj.setContactDetails(pt);

        jaxbObj.setLoginId(BigInteger.valueOf(dto.getLoginId()));
        jaxbObj.setLoginName(dto.getLoginName());

        jaxbObj.setStartDate(RMT2Date.toXmlDate(dto.getStartDate()));
        jaxbObj.setTerminationDate(RMT2Date.toXmlDate(dto.getTerminationDate()));
        jaxbObj.setProjectCount(BigInteger.valueOf(dto.getProjectCount()));
        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated())
                .withUserId(dto.getUpdateUserId())
                .build();
        jaxbObj.setTracking(rtt);

        return jaxbObj;
    }

}

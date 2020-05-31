package org.rmt2.api.handlers.timesheet;

import org.dto.TimesheetDto;
import org.dto.adapter.orm.TimesheetObjectFactory;
import org.rmt2.jaxb.ClientType;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.EmployeeType;
import org.rmt2.jaxb.EmployeetypeType;
import org.rmt2.jaxb.PersonType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.TimesheetCriteriaType;
import org.rmt2.jaxb.TimesheetStatusType;
import org.rmt2.jaxb.TimesheetType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CustomerTypeBuilder;
import org.rmt2.util.addressbook.PersonTypeBuilder;
import org.rmt2.util.projecttracker.admin.ClientTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeeTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeetypeTypeBuilder;
import org.rmt2.util.projecttracker.timesheet.TimesheetStatusTypeBuilder;
import org.rmt2.util.projecttracker.timesheet.TimesheetTypeBuilder;

import com.RMT2Base;
import com.api.util.RMT2Date;

/**
 * A factory for converting Employee project tracker administration related JAXB
 * objects to DTO and vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class TimesheetJaxbDtoFactory extends RMT2Base {

    /**
     * Create an instance of <i>TimesheetDto</i> using a valid
     * <i>TimesheetCriteriaType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link TimesheetCriteriaType}
     * @return an instance of {@link TimesheetDto}
     */
    public static final TimesheetDto createDtoCriteriaInstance(TimesheetCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        TimesheetDto dto = TimesheetObjectFactory.createTimesheetExtendedDtoInstance(null);
        if (jaxbObj.getTimesheetId() != null) {
            dto.setTimesheetId(jaxbObj.getTimesheetId().intValue());
        }
        if (jaxbObj.getStatusId() != null) {
            dto.setStatusId(jaxbObj.getStatusId().intValue());
        }
        if (jaxbObj.getEmployeeId() != null) {
            dto.setEmpId(jaxbObj.getEmployeeId().intValue());
        }
        if (jaxbObj.getClientId() != null) {
            dto.setClientId(jaxbObj.getClientId().intValue());
        }
        if (jaxbObj.getProjectId() != null) {
            dto.setProjId(jaxbObj.getProjectId().intValue());
        }
        if (jaxbObj.getPeriodBegin() != null) {
            dto.setBeginPeriod(jaxbObj.getPeriodBegin().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getPeriodEnd() != null) {
            if (jaxbObj.getPeriodEnd2() != null) {
                StringBuilder buf = new StringBuilder();
                buf.append("end_period between \'");
                buf.append(RMT2Date.formatDate(jaxbObj.getPeriodEnd().toGregorianCalendar().getTime(), "yyyy-MM-dd"));
                buf.append("\' and \'");
                buf.append(RMT2Date.formatDate(jaxbObj.getPeriodEnd2().toGregorianCalendar().getTime(), "yyyy-MM-dd"));
                buf.append("\'");
                String predicate = buf.toString();
                dto.setCriteria(predicate);
            }
            else {
                dto.setEndPeriod(jaxbObj.getPeriodEnd().toGregorianCalendar().getTime());
            }
        }
        return dto;
    }
    
    /**
     * Create an instance of TimesheetDto from an TimesheetType object
     * 
     * @param jaxbObj
     *            an instance of {@link TimesheetType}
     * @return an instance of {@link TimesheetDto}
     */
    public static final TimesheetDto createDtoInstance(TimesheetType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        TimesheetDto dto = TimesheetObjectFactory.createTimesheetExtendedDtoInstance(null);

        // Get timesheet info
        if (jaxbObj.getTimesheetId() != null) {
            dto.setTimesheetId(jaxbObj.getTimesheetId().intValue());
        }
        else {
            dto.setTimesheetId(0);
        }
        if (jaxbObj.getProjId() != null) {
            dto.setProjId(jaxbObj.getProjId().intValue());
        }
        if (jaxbObj.getStatusHistoryId() != null) {
            dto.setStatusHistId(jaxbObj.getStatusHistoryId().intValue());
        }
        dto.setDisplayValue(jaxbObj.getDisplayValue());
        if (jaxbObj.getPeriodBegin() != null) {
            dto.setBeginPeriod(jaxbObj.getPeriodBegin().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getPeriodEnd() != null) {
            dto.setEndPeriod(jaxbObj.getPeriodEnd().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getStatusEffectiveDate() != null) {
            dto.setStatusEffectiveDate(jaxbObj.getStatusEffectiveDate().toGregorianCalendar().getTime());
        }
        if (jaxbObj.getStatusEndDate() != null) {
            dto.setStatusEndDate(jaxbObj.getStatusEndDate().toGregorianCalendar().getTime());
        }
        dto.setInvoiceRefNo(jaxbObj.getInvoiceRefNo());
        dto.setExtRef(jaxbObj.getExternalRefNo());
        dto.setComments(jaxbObj.getComments());
        if (jaxbObj.getDocumentId() != null) {
            dto.setDocumentId(jaxbObj.getDocumentId().intValue());
        }
        if (jaxbObj.getBillableHours() != null) {
            dto.setBillHrs(jaxbObj.getBillableHours().doubleValue());
        }
        if (jaxbObj.getNonBillableHours() != null) {
            dto.setNonBillHrs(jaxbObj.getNonBillableHours().doubleValue());
        }
        if (jaxbObj.getHourlyRate() != null) {
            dto.setEmployeeHourlyRate(jaxbObj.getHourlyRate().doubleValue());
        }
        if (jaxbObj.getOvertimeHourlyRate() != null) {
            dto.setEmployeeHourlyOverRate(jaxbObj.getOvertimeHourlyRate().doubleValue());
        }

        // Get client info
        if (jaxbObj.getClient() != null) {
            if (jaxbObj.getClient().getClientId() != null) {
                dto.setClientId(jaxbObj.getClient().getClientId().intValue());
                dto.setClientName(jaxbObj.getClient().getName());
                if (jaxbObj.getClient().getCustomer() != null) {
                    dto.setClientAccountNo(jaxbObj.getClient().getCustomer().getAccountNo());
                }
            }
        }

        // Get employee info
        if (jaxbObj.getEmployee() != null) {
            if (jaxbObj.getEmployee().getEmployeeId() != null) {
                dto.setEmpId(jaxbObj.getEmployee().getEmployeeId().intValue());
            }
            if (jaxbObj.getEmployee().getContactDetails() != null) {
                dto.setEmployeeFirstname(jaxbObj.getEmployee().getContactDetails().getFirstName());
                dto.setEmployeeLastname(jaxbObj.getEmployee().getContactDetails().getLastName());
                dto.setEmployeeFullName(jaxbObj.getEmployee().getContactDetails().getShortName());
                if (jaxbObj.getEmployee().getManagerId() != null) {
                    dto.setEmployeeManagerId(jaxbObj.getEmployee().getManagerId().intValue());
                }
            }
            if (jaxbObj.getEmployee().getEmployeeType() != null) {
                if (jaxbObj.getEmployee().getEmployeeType().getEmployeeTypeId() != null) {
                    dto.setEmployeeTypeId(jaxbObj.getEmployee().getEmployeeType().getEmployeeTypeId().intValue());
                }
            }
        }

        // Get timesheet status info
        if (jaxbObj.getStatus() != null) {
            if (jaxbObj.getStatus().getTimesheetStatusId() != null) {
                dto.setStatusId(jaxbObj.getStatus().getTimesheetStatusId().intValue());
            }
            dto.setStatusName(jaxbObj.getStatus().getName());
            dto.setStatusDescription(jaxbObj.getStatus().getDescription());
        }

        return dto;
    }
    

    /**
     * Create an instance of TimesheetType from an TimesheetDto object
     * 
     * @param dto
     *            an instance of {@link TimesheetDto}
     * @return an instance of {@link TimesheetType}
     */
    public static final TimesheetType createJaxbInstance(TimesheetDto dto) {
        if (dto == null) {
            return null;
        }
        EmployeetypeType ett = EmployeetypeTypeBuilder.Builder.create()
                .withEmployeeTypeId(dto.getEmployeeTypeId())
                .build();

        PersonType pt = PersonTypeBuilder.Builder.create()
                .withFirstName(dto.getEmployeeFirstname())
                .withLastName(dto.getEmployeeLastname())
                .withShortName(dto.getEmployeeFullName())
                .build();

        EmployeeType et = EmployeeTypeBuilder.Builder.create()
                .withEmployeeId(dto.getEmpId())
                .withManagerId(dto.getEmployeeManagerId())
                .withEmployeeType(ett)
                .withContactDetails(pt)
                .build();

        CustomerType customer = CustomerTypeBuilder.Builder.create()
                .withAccountNo(dto.getClientAccountNo())
                .build();

        ClientType ct = ClientTypeBuilder.Builder.create()
                .withClientId(dto.getClientId())
                .withClientName(dto.getClientName())
                .withCustomerData(customer)
                .build();

        TimesheetStatusType tst = TimesheetStatusTypeBuilder.Builder.create()
                .withStatusId(dto.getStatusId())
                .withName(dto.getStatusName())
                .withDescription(dto.getStatusDescription())
                .build();

        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated())
                .withUserId(dto.getUpdateUserId())
                .build();

        TimesheetType jaxbObj = TimesheetTypeBuilder.Builder.create()
                .withTimesheetId(dto.getTimesheetId())
                .withProjectId(dto.getProjId())
                .withDisplayTimesheetId(dto.getDisplayValue())
                .withBeginPeriod(dto.getBeginPeriod())
                .withEndPeriod(dto.getEndPeriod())
                .withInvoiceRefNo(dto.getInvoiceRefNo())
                .withExternalRefNo(dto.getExtRef())
                .withComments(dto.getComments())
                .withDocumentId(dto.getDocumentId())
                .withStatusHistoryId(dto.getStatusHistId())
                .withStatusEffectiveDate(dto.getStatusEffectiveDate())
                .withStatusEndDate(dto.getStatusEndDate())
                .withEmployeeBillableHours(dto.getBillHrs())
                .withEmployeeNonBillableHours(dto.getNonBillHrs())
                .withHourlyRate(dto.getEmployeeHourlyRate())
                .withHourlyOvertimeRate(dto.getEmployeeHourlyOverRate())
                .withClient(ct)
                .withEmployee(et)
                .withStatus(tst)
                .withRecordTracking(rtt)
                .build();

        return jaxbObj;
    }

    /**
     * Create an abbreviated instance of TimesheetType from an TimesheetDto
     * object
     * 
     * @param dto
     *            an instance of {@link TimesheetDto}
     * @return an instance of {@link TimesheetType}
     */
    public static final TimesheetType createJaxbAbbreviatedInstance(TimesheetDto dto) {
        if (dto == null) {
            return null;
        }

        PersonType pt = PersonTypeBuilder.Builder.create()
                .withFirstName(dto.getEmployeeFirstname())
                .withLastName(dto.getEmployeeLastname())
                .withShortName(dto.getEmployeeFullName())
                .build();

        EmployeeType et = EmployeeTypeBuilder.Builder.create()
                .withEmployeeId(dto.getEmpId())
                .withManagerId(dto.getEmployeeManagerId())
                .withContactDetails(pt)
                .build();

        ClientType ct = ClientTypeBuilder.Builder.create()
                .withClientId(dto.getClientId())
                .withClientName(dto.getClientName())
                .build();

        TimesheetStatusType tst = TimesheetStatusTypeBuilder.Builder.create()
                .withName(dto.getStatusName())
                .build();

        TimesheetType jaxbObj = TimesheetTypeBuilder.Builder.create()
                .withTimesheetId(dto.getTimesheetId())
                .withDisplayTimesheetId(dto.getDisplayValue())
                .withEndPeriod(dto.getEndPeriod())
                .withClient(ct)
                .withEmployee(et)
                .withStatus(tst)
                .build();

        return jaxbObj;
    }
}

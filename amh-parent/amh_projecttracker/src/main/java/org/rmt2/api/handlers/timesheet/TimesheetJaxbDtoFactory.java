package org.rmt2.api.handlers.timesheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dto.BusinessContactDto;
import org.dto.EventDto;
import org.dto.ProjectTaskDto;
import org.dto.TimesheetDto;
import org.dto.TimesheetHoursSummaryDto;
import org.dto.adapter.orm.ProjectObjectFactory;
import org.dto.adapter.orm.TimesheetObjectFactory;
import org.rmt2.jaxb.AddressType;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.ClientType;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.EmployeeType;
import org.rmt2.jaxb.EmployeetypeType;
import org.rmt2.jaxb.EventType;
import org.rmt2.jaxb.PersonType;
import org.rmt2.jaxb.ProjectTaskType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.TimesheetCriteriaType;
import org.rmt2.jaxb.TimesheetHoursSummaryType;
import org.rmt2.jaxb.TimesheetStatusType;
import org.rmt2.jaxb.TimesheetType;
import org.rmt2.jaxb.ZipcodeType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CustomerTypeBuilder;
import org.rmt2.util.addressbook.AddressTypeBuilder;
import org.rmt2.util.addressbook.BusinessTypeBuilder;
import org.rmt2.util.addressbook.PersonTypeBuilder;
import org.rmt2.util.addressbook.ZipcodeTypeBuilder;
import org.rmt2.util.projecttracker.admin.ClientTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeeTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeetypeTypeBuilder;
import org.rmt2.util.projecttracker.timesheet.EventTypeBuilder;
import org.rmt2.util.projecttracker.timesheet.ProjectTaskTypeBuilder;
import org.rmt2.util.projecttracker.timesheet.TimesheetHoursSummaryTypeBuilder;
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
    public static final TimesheetDto createTimesheetDtoCriteriaInstance(TimesheetCriteriaType jaxbObj) {
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
    public static final TimesheetDto createTimesheetDtoInstance(TimesheetType jaxbObj) {
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
     * Builds a Map representing the timesheet work log which is is keyed by
     * ProjectTaskDto.
     * <p>
     * The map values are a list of EventDto ojects representing the daily time
     * sheet hours for the project task.
     * 
     * @param jaxbObj
     *            an instance of {@link TimesheetType}
     * @return Map<ProjectTaskDto, List<EventDto>>
     */
    public static final Map<ProjectTaskDto, List<EventDto>> createTimesheetWorkLogDtoInstance(TimesheetType jaxbObj) {
        Map<ProjectTaskDto, List<EventDto>> hours = new HashMap<>();
        for (ProjectTaskType ptt : jaxbObj.getWorkLog()) {
            ProjectTaskDto key = TimesheetJaxbDtoFactory.createProjectTaskDtoInstance(ptt);
            if (jaxbObj.getTimesheetId() != null) {
                key.setTimesheetId(jaxbObj.getTimesheetId().intValue());
            }
            else {
                key.setTimesheetId(0);
            }
            List<EventDto> events = TimesheetJaxbDtoFactory.createEventDtoInstance(ptt, key.getProjectTaskId());
            hours.put(key, events);
        }
        return hours;
    }

    private static final ProjectTaskDto createProjectTaskDtoInstance(ProjectTaskType jaxbObj) {
        ProjectTaskDto dto = ProjectObjectFactory.createProjectTaskDtoInstance(null);
        if (jaxbObj.getProjectTaskId() != null) {
            dto.setProjectTaskId(jaxbObj.getProjectTaskId().intValue());
        }
        else {
            dto.setProjectTaskId(0);
        }
        dto.setTaskId(jaxbObj.getTaskId().intValue());
        dto.setProjId(jaxbObj.getProjectId().intValue());
        dto.setDeleteFlag(jaxbObj.isDeleteProjectTask());
        return dto;
    }

    private static final List<EventDto> createEventDtoInstance(ProjectTaskType jaxbObj, int projectTaskId) {
        List<EventDto> list = new ArrayList<>();
        for (EventType et : jaxbObj.getDailyHours()) {
            EventDto dto = ProjectObjectFactory.createEventDtoInstance(null);
            if (et.getEventId() != null) {
                dto.setEventId(et.getEventId().intValue());
            }
            else {
                dto.setEventId(0);
            }
            dto.setProjectTaskId(projectTaskId);
            dto.setEventDate(et.getEventDate().toGregorianCalendar().getTime());
            dto.setEventHours(et.getHours().doubleValue());
            list.add(dto);
        }
        return list;
    }

    /**
     * Creats full TimesheetType object which includes base timesheet data and a
     * summary of work log hours
     * 
     * @param dto
     *            instance of {@link TimesheetDto}
     * @param workLogSummary
     *            an instance of {@link TimesheetHoursSummaryDto}
     * @param serviceProvider
     *            an instance of {@link BusinessContactDto} representing the
     *            company providing services to the client
     * @return {@link TimesheetType}
     */
    public static final TimesheetType createTimesheetJaxbInstance(TimesheetDto dto, TimesheetHoursSummaryDto workLogSummary,
            BusinessContactDto serviceProvider) {
        if (dto == null) {
            return null;
        }

        BusinessType bt = null;
        if (serviceProvider != null) {
            ZipcodeType zt = ZipcodeTypeBuilder.Builder.create()
                    .withCity(serviceProvider.getCity())
                    .withState(serviceProvider.getState())
                    .withZipcode(serviceProvider.getZip())
                    .build();

            AddressType at = AddressTypeBuilder.Builder.create()
                    .withAddrId(serviceProvider.getAddrId())
                    .withAddressLine1(serviceProvider.getAddr1())
                    .withAddressLine2(serviceProvider.getAddr2())
                    .withAddressLine3(serviceProvider.getAddr3())
                    .withAddressLine4(serviceProvider.getAddr4())
                    .withPhoneMain(serviceProvider.getPhoneCompany())
                    .withPhoneFax(serviceProvider.getPhoneFax())
                    .withZipcode(zt)
                    .build();

            bt = BusinessTypeBuilder.Builder.create()
                    .withBusinessId(serviceProvider.getContactId())
                    .withContactFirstname(serviceProvider.getContactFirstname())
                    .withContactLastname(serviceProvider.getContactLastname())
                    .withContactPhone(serviceProvider.getContactPhone())
                    .withLongname(serviceProvider.getContactName())
                    .withTaxId(serviceProvider.getTaxId())
                    .withWebsite(serviceProvider.getWebsite())
                    .withContactEmail(serviceProvider.getContactEmail())
                    .withAddress(at)
                    .build();
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

        TimesheetHoursSummaryType thst = TimesheetHoursSummaryTypeBuilder.Builder.create()
                .withDay1Hours(workLogSummary.getHours1())
                .withDay2Hours(workLogSummary.getHours2())
                .withDay3Hours(workLogSummary.getHours3())
                .withDay4Hours(workLogSummary.getHours4())
                .withDay5Hours(workLogSummary.getHours5())
                .withDay6Hours(workLogSummary.getHours6())
                .withDay7Hours(workLogSummary.getHours7())
                .withTotalHours(workLogSummary.getTotalHours())
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
                .withServiceProvider(bt)
                .withClient(ct)
                .withEmployee(et)
                .withStatus(tst)
                .withWorkLogSummary(thst)
                .withRecordTracking(rtt)
                .build();

        return jaxbObj;
    }

    /**
     * Creats full TimesheetType object which includes base timesheet data and
     * its complete work log of hours
     * 
     * @param dto
     *            instance of {@link TimesheetDto}
     * @param workLog
     *            an instance of {@link Map&lt;&lt;ProjectTaskDto&gt;,
     *            List&lt;EventDto&gt;&gt;}
     * @param serviceProvider
     *            an instance of {@link BusinessContactDto} representing the
     *            company providing services to the client
     * @return {@link TimesheetType}
     */
    public static final TimesheetType createTimesheetJaxbInstance(TimesheetDto dto, Map<ProjectTaskDto, List<EventDto>> workLog,
            BusinessContactDto serviceProvider) {
        if (dto == null) {
            return null;
        }

        BusinessType bt = null;
        if (serviceProvider != null) {
            ZipcodeType zt = ZipcodeTypeBuilder.Builder.create()
                    .withCity(serviceProvider.getCity())
                    .withState(serviceProvider.getState())
                    .withZipcode(serviceProvider.getZip())
                    .build();

            AddressType at = AddressTypeBuilder.Builder.create()
                    .withAddrId(serviceProvider.getAddrId())
                    .withAddressLine1(serviceProvider.getAddr1())
                    .withAddressLine2(serviceProvider.getAddr2())
                    .withAddressLine3(serviceProvider.getAddr3())
                    .withAddressLine4(serviceProvider.getAddr4())
                    .withPhoneMain(serviceProvider.getPhoneCompany())
                    .withPhoneFax(serviceProvider.getPhoneFax())
                    .withZipcode(zt)
                    .build();

            bt = BusinessTypeBuilder.Builder.create()
                    .withBusinessId(serviceProvider.getContactId())
                    .withContactFirstname(serviceProvider.getContactFirstname())
                    .withContactLastname(serviceProvider.getContactLastname())
                    .withContactPhone(serviceProvider.getContactPhone())
                    .withLongname(serviceProvider.getContactName())
                    .withTaxId(serviceProvider.getTaxId())
                    .withWebsite(serviceProvider.getWebsite())
                    .withContactEmail(serviceProvider.getContactEmail())
                    .withAddress(at)
                    .build();
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

        List<ProjectTaskType> hours = TimesheetJaxbDtoFactory.createTimesheetWorkLogJaxbInstance(workLog);

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
                .withServiceProvider(bt)
                .withClient(ct)
                .withEmployee(et)
                .withStatus(tst)
                .addWorkLog(hours)
                .withRecordTracking(rtt)
                .build();

        return jaxbObj;
    }

    /**
     * Create an instance of TimesheetType from an Timesheet DTO object
     * 
     * @param dto
     *            an instance of {@link TimesheetDto}
     * @return an instance of {@link TimesheetType}
     */
    public static final TimesheetType createTimesheetJaxbInstance(TimesheetDto dto) {
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
     * object with limited data.
     * 
     * @param dto
     *            an instance of {@link TimesheetDto}
     * @return an instance of {@link TimesheetType}
     */
    public static final TimesheetType createTimesheetJaxbAbbreviatedInstance(TimesheetDto dto) {
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
                .withEmployeeBillableHours(dto.getBillHrs())
                .withEmployeeNonBillableHours(dto.getNonBillHrs())
                .withClient(ct)
                .withEmployee(et)
                .withStatus(tst)
                .build();

        return jaxbObj;
    }

    /**
     * Create a list of ProjectTaskType from the timesheet work log
     * 
     * @param workLog
     *            an instance of {@link Map&lt;&lt;ProjectTaskDto&gt;,
     *            List&lt;EventDto&gt;&gt;}
     * @return List of {@link ProjectTaskType}
     */
    public static final List<ProjectTaskType> createTimesheetWorkLogJaxbInstance(Map<ProjectTaskDto, List<EventDto>> workLog) {
        if (workLog == null) {
            return null;
        }

        List<ProjectTaskType> jaxbObj = new ArrayList<>();

        Set<ProjectTaskDto> keys = workLog.keySet();
        Iterator<ProjectTaskDto> iter = keys.iterator();
        while (iter.hasNext()) {
            ProjectTaskDto item = iter.next();
            List<EventDto> events = workLog.get(item);
            ProjectTaskType obj = TimesheetJaxbDtoFactory.createTimesheetProjectTaskJaxbInstance(item, events);
            jaxbObj.add(obj);
        }

        return jaxbObj;
    }

    private static final ProjectTaskType createTimesheetProjectTaskJaxbInstance(ProjectTaskDto projTaskDto, List<EventDto> projTaskHours) {
        List<EventType> events = new ArrayList<>();
        for (EventDto event : projTaskHours) {
            events.add(TimesheetJaxbDtoFactory.createTimesheetEventJaxbInstance(event));
        }

        ProjectTaskType jaxbObj = ProjectTaskTypeBuilder.Builder.create()
                .withProjectTaskId(projTaskDto.getProjectTaskId())
                .withProjectId(projTaskDto.getProjId())
                .withProjectName(projTaskDto.getProjectDescription())
                .withTaskId(projTaskDto.getTaskId())
                .withTaskName(projTaskDto.getTaskDescription())
                .addHours(events)
                .build();

        return jaxbObj;
    }

    private static final EventType createTimesheetEventJaxbInstance(EventDto evt) {

        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(evt.getDateCreated())
                .withDateUpdate(evt.getDateUpdated())
                .withIpCreated(evt.getIpCreated())
                .withIpUpdate(evt.getIpUpdated())
                .withUserId(evt.getUpdateUserId())
                .build();

        EventType jaxbObj = EventTypeBuilder.Builder.create()
                .withEventId(evt.getEventId())
                .withEventDate(evt.getEventDate())
                .withHours(evt.getEventHours())
                .withRecordTracking(rtt)
                .build();

        return jaxbObj;

    }

}

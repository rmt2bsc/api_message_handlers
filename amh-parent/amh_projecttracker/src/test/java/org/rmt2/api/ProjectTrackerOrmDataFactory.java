package org.rmt2.api;

import java.util.Calendar;
import java.util.Date;

import org.dao.mapping.orm.rmt2.ProjClient;
import org.dao.mapping.orm.rmt2.ProjEmployee;
import org.dao.mapping.orm.rmt2.ProjEmployeeTitle;
import org.dao.mapping.orm.rmt2.ProjEmployeeType;
import org.dao.mapping.orm.rmt2.ProjEvent;
import org.dao.mapping.orm.rmt2.ProjProject;
import org.dao.mapping.orm.rmt2.ProjProjectTask;
import org.dao.mapping.orm.rmt2.ProjTask;
import org.dao.mapping.orm.rmt2.ProjTimesheet;
import org.dao.mapping.orm.rmt2.ProjTimesheetHist;
import org.dao.mapping.orm.rmt2.VwEmployeeExt;
import org.dao.mapping.orm.rmt2.VwEmployeeProjects;
import org.dao.mapping.orm.rmt2.VwProjectClient;
import org.dao.mapping.orm.rmt2.VwTimesheetEventList;
import org.dao.mapping.orm.rmt2.VwTimesheetHours;
import org.dao.mapping.orm.rmt2.VwTimesheetList;
import org.dao.mapping.orm.rmt2.VwTimesheetProjectTask;
import org.dao.timesheet.TimesheetConst;

import com.api.util.RMT2Date;
import com.api.util.RMT2String;

public class ProjectTrackerOrmDataFactory {
    public static final int TEST_LOGIN_ID = 999991;
    public static final int TEST_TIMESHEET_ID = 111;
    public static final int TEST_TIMESHEET_HIST_ID = 5550;
    public static final int TEST_EMPLOYEE_ID = 2220;
    public static final int TEST_MANAGER_ID = 3330;
    public static final int TEST_CLIENT_ID = 1110;
    public static final int TEST_PROJ_ID = 4440;
    public static final int TEST_TASK_ID = 1112220;
    public static final int TEST_EVENT_ID = 123401;
    public static final int TEST_PROJECT_TASK_ID = 444441;
    public static final String TEST_PROJECT_NAME = "Project 2220";
    public static final int TEST_BUSINESS_ID = 1350;
    public static final int TEST_EMPLOYEE_TITLE_ID = 101;
    public static final int TEST_EMP_PROJ_ID = 55551;
    public static final int TEST_NEW_TIMESHEET_STATUS_HIST_ID = 987654;
    public static final int TEST_INVALID_TIMESHEET_STATUS_ID = 999999;
    public static final int TEST_INVOICE_ID = 7654321;
    public static final String TEST_COMPANY_NAME = "ABC Company";
    public static final String TEST_TASK_NAMES[] = new String[]{"Design and Analysis", 
    "Development", "Meetings", "Testing", "Holiday"};
    
    /**
     * 
     * @param clientId
     * @param businessId
     * @param name
     * @param billRate
     * @param otBillRate
     * @param acctNo
     * @param firstName
     * @param lastName
     * @param phone
     * @param email
     * @return
     */
    public static final ProjClient createMockOrmProjClient(int clientId,
            int businessId, String name, double billRate, double otBillRate,
            String acctNo, String firstName, String lastName, String phone,
            String email) {
        ProjClient o = new ProjClient();
        o.setClientId(clientId);
        o.setBusinessId(businessId);
        o.setName(name);
        o.setBillRate(billRate);
        o.setOtBillRate(otBillRate);
        o.setAccountNo(acctNo);
        o.setContactFirstname(firstName);
        o.setContactLastname(lastName);
        o.setContactPhone(phone);
        o.setContactEmail(email);
        return o;
    }

    /**
     * 
     * @param projId
     * @param clientId
     * @param description
     * @param effectiveDate
     * @param endDate
     * @return
     */
    public static final ProjProject createMockOrmProjProject(int projId,
            int clientId, String description, String effectiveDate,
            String endDate) {
        ProjProject o = new ProjProject();
        o.setProjId(projId);
        o.setClientId(clientId);
        o.setDescription(description);
        o.setEffectiveDate(RMT2Date.stringToDate(effectiveDate));
        o.setEndDate(RMT2Date.stringToDate(endDate));
        return o;
    }
    
    /**
     * 
     * @param projId
     * @param clientId
     * @param projName
     * @param effectiveDate
     * @param endDate
     * @param clientName
     * @param clientBusinessId
     * @return
     */
    public static final VwProjectClient createMockOrmVwProjectClient(int projId, int clientId, String projName,
            String effectiveDate, String endDate, String clientName, int clientBusinessId) {
        VwProjectClient o = new VwProjectClient();
        o.setProjId(projId);
        o.setClientId(clientId);
        o.setDescription(projName);
        o.setEffectiveDate(RMT2Date.stringToDate(effectiveDate));
        o.setEndDate(RMT2Date.stringToDate(endDate));
        o.setName(clientName);
        o.setBusinessId(clientBusinessId);
        return o;
    }

    /**
     * 
     * @param empId
     * @param empTypeId
     * @param isManager
     * @param managerId
     * @param empTitleId
     * @param loginId
     * @param startDate
     * @param termDate
     * @param loginName
     * @param firstName
     * @param lastName
     * @param ssn
     * @param companyName
     * @return
     */
    public static final ProjEmployee createMockOrmProjEmployee(int empId,
            int empTypeId, int isManager, int managerId, int empTitleId,
            int loginId, String startDate, String termDate, String loginName,
            String firstName, String lastName, String ssn, String companyName) {
        ProjEmployee o = new ProjEmployee();
        o.setEmpId(empId);
        o.setEmpTypeId(empTypeId);
        o.setIsManager(isManager);
        o.setManagerId(managerId);
        o.setEmpTitleId(empTitleId);
        o.setLoginId(loginId);
        o.setStartDate(RMT2Date.stringToDate(startDate));
        o.setTerminationDate(termDate == null ? null : RMT2Date.stringToDate(termDate));
        o.setLoginName(loginName);
        o.setFirstname(firstName);
        o.setLastname(lastName);
        o.setSsn(ssn);
        o.setCompanyName(companyName);
        o.setEmail(firstName + "." + lastName + "@gte.net");
        return o;
    }
    
    /**
     * 
     * @param empId
     * @param empTypeDesc
     * @param isManager
     * @param managerId
     * @param empTitleDesc
     * @param loginId
     * @param startDate
     * @param termDate
     * @param loginName
     * @param firstName
     * @param lastName
     * @param ssn
     * @param companyName
     * @return
     */
    public static final VwEmployeeExt createMockOrmExtEmployee(int empId,
            String empTypeDesc, int isManager, int managerId,
            String empTitleDesc, int loginId, String startDate, String termDate,
            String loginName, String firstName, String lastName, String ssn,
            String companyName) {
        VwEmployeeExt o = new VwEmployeeExt();
        o.setEmployeeId(empId);
        o.setEmployeeType(empTypeDesc);
        o.setIsManager(isManager);
        o.setManagerId(managerId);
        o.setEmployeeTitle(empTitleDesc);
        o.setLoginId(loginId);
        o.setStartDate(RMT2Date.stringToDate(startDate));
        o.setTerminationDate(termDate == null ? null : RMT2Date.stringToDate(termDate));
        o.setLoginName(loginName);
        o.setFirstname(firstName);
        o.setLastname(lastName);
        o.setSsn(ssn);
        o.setCompanyName(companyName);
        o.setEmail(firstName + "." + lastName + "@gte.net");
        o.setProjCount(28);
        return o;
    }
    
    /**
     * 
     * @param empTitleId
     * @param description
     * @return
     */
    public static final ProjEmployeeTitle createMockOrmProjEmployeeTitle(int empTitleId, String description) {
        ProjEmployeeTitle o = new ProjEmployeeTitle();
        o.setEmpTitleId(empTitleId);
        o.setDescription(description);
        return o;
    }
    
    /**
     * 
     * @param empTypeId
     * @param description
     * @return
     */
    public static final ProjEmployeeType createMockOrmProjEmployeeType(int empTypeId, String description) {
        ProjEmployeeType o = new ProjEmployeeType();
        o.setEmpTypeId(empTypeId);
        o.setDescription(description);
        return o;
    }
    
    /**
     * 
     * @param empProjId
     * @param projId
     * @param projName
     * @param clientId
     * @param clientName
     * @param businessId
     * @param acctNo
     * @param empId
     * @param projEffectiveDate
     * @param projEndDate
     * @param projEmpEffectiveDate
     * @param projEmpEndDate
     * @param payRate
     * @param otPayRate
     * @param flatRate
     * @param clientBillRate
     * @param clientOtBillRate
     * @return
     */
    public static final VwEmployeeProjects createMockOrmVwEmployeeProjects(int empProjId, int projId, 
            String projName, int clientId, String clientName, int businessId,
            String acctNo, int empId, String projEffectiveDate,
            String projEndDate, String projEmpEffectiveDate,
            String projEmpEndDate, double payRate, double otPayRate,
            double flatRate, double clientBillRate, double clientOtBillRate) {
        VwEmployeeProjects o = new VwEmployeeProjects();
        o.setEmpProjId(empProjId);
        o.setProjId(projId);
        o.setProjectName(projName);
        o.setClientId(clientId);
        o.setClientName(clientName);
        o.setBusinessId(businessId);
        o.setAccountNo(acctNo);
        o.setEmpId(empId);
        o.setProjEffectiveDate(RMT2Date.stringToDate(projEffectiveDate));
        o.setProjEndDate(RMT2Date.stringToDate(projEndDate));
        o.setProjempEffectiveDate(RMT2Date.stringToDate(projEmpEffectiveDate));
        o.setProjempEndDate(RMT2Date.stringToDate(projEmpEndDate));
        o.setPayRate(payRate);
        o.setOtPayRate(otPayRate);
        o.setFlatRate(flatRate);
        o.setClientBillRate(clientBillRate);
        o.setClientOtBillRate(clientOtBillRate);
        o.setComments("Comments for Employee Project Id: " + empProjId);
        return o;
    }
    
    /**
     * 
     * @param timesheetId
     * @param clientId
     * @param projId
     * @param empId
     * @param invRefNo
     * @param begPeriod
     * @param endPeriod
     * @param extRefNo
     * @return
     */
    public static final ProjTimesheet createMockOrmProjTimesheet(
            int timesheetId, int clientId, int projId, int empId,
            String invRefNo, String begPeriod, String endPeriod,
            String extRefNo) {
        ProjTimesheet o = new ProjTimesheet();

        o.setTimesheetId(timesheetId);
        o.setClientId(clientId);
        o.setProjId(projId);
        o.setEmpId(empId);
        o.setInvoiceRefNo(invRefNo);
        o.setBeginPeriod(RMT2Date.stringToDate(begPeriod));
        o.setEndPeriod(RMT2Date.stringToDate(endPeriod));
        o.setExtRef(extRefNo);
        o.setComments("Comments" + timesheetId);
        o.setDocumentId(timesheetId);
        String displayValue = RMT2String.padInt(timesheetId, 10, RMT2String.PAD_LEADING);
        o.setDisplayValue(displayValue);
        o.setDateCreated(RMT2Date.stringToDate(begPeriod));
        o.setDateUpdated(RMT2Date.stringToDate(endPeriod));
        o.setUserId("testuser");
        o.setIpCreated("1.2.3.4");
        o.setIpUpdated("1.2.3.4");
        return o;
    }
    
   /**
    * 
    * @param timesheetId
    * @param clientId
    * @param projId
    * @param empId
    * @param invRefNo
    * @param begPeriod
    * @param endPeriod
    * @param extRefNo
    * @param managerId
    * @param statusName
    * @param acctNo
    * @param billableHours
    * @param nonBillableHours
    * @param hourlyRate
    * @param overtimeRate
    * @return
    */
    public static final VwTimesheetList createMockOrmVwTimesheetList(int timesheetId, int clientId, 
            int projId, int empId, String invRefNo, String begPeriod, String endPeriod,
            String extRefNo, int managerId, String statusName, String acctNo,
            double billableHours, double nonBillableHours, double hourlyRate, double overtimeRate) {
        VwTimesheetList o = new VwTimesheetList();
        o.setTimesheetId(timesheetId);
        o.setClientId(clientId);
        o.setProjId(projId);
        o.setEmpId(empId);
        o.setManagerId(managerId);
        o.setInvoiceRefNo(invRefNo);
        o.setBeginPeriod(RMT2Date.stringToDate(begPeriod));
        o.setEndPeriod(RMT2Date.stringToDate(endPeriod));
        o.setExtRef(extRefNo);
        o.setComments("Comments" + timesheetId);
        o.setDocumentId(timesheetId);
        String displayValue = RMT2String.padInt(timesheetId, 10, RMT2String.PAD_LEADING);
        o.setDisplayValue(displayValue);
        o.setClientName("ClientName" + clientId);
        o.setAccountNo(acctNo);
        o.setFirstname("FirstName" + timesheetId);
        o.setLastname("LastName" + timesheetId);
        o.setHourlyRate(hourlyRate);
        o.setHourlyOverRate(overtimeRate);
        o.setBillHrs(billableHours);
        o.setNonBillHrs(nonBillableHours);
        o.setStatusName(statusName);
        o.setTimesheetStatusId(TimesheetConst.STATUS_DRAFT);
        o.setStatusDescription(statusName + "Description");
        o.setStatusEffectiveDate(RMT2Date.stringToDate(begPeriod));
        o.setStatusEndDate(RMT2Date.stringToDate(endPeriod));
        o.setTypeId(222);
        o.setProjTimesheetHistId(5555);
        o.setLastFirstName(o.getLastname() + ", " + o.getFirstname());
        return o;
    }
    
    
    /**
     * 
     * @param eventId
     * @param projectTaskId
     * @param eventDate
     * @param hours
     * @return
     */
    public static final ProjEvent createMockOrmProjEvent(int eventId, int projectTaskId, String eventDate, double hours) {
        ProjEvent o = new ProjEvent();
        o.setEventId(eventId);
        o.setProjectTaskId(projectTaskId);
        o.setEventDate(RMT2Date.stringToDate(eventDate));
        o.setHours(hours);
        return o;
    }
    
   /**
    * 
    * @param eventId
    * @param eventDate
    * @param hours
    * @param projectTaskId
    * @param timesheetId
    * @param projectId
    * @param projectName
    * @param taskId
    * @param taskName
    * @param clientId
    * @param effectiveDate
    * @param endDate
    * @param billable
    * @return
    */
    public static final VwTimesheetEventList createMockOrmVwTimesheetEventList(int eventId, String eventDate, 
            double hours, int projectTaskId, int timesheetId, int projectId, 
            String projectName, int taskId, String taskName, int clientId, 
            String effectiveDate, String endDate, boolean billable) {
        VwTimesheetEventList o = new VwTimesheetEventList();
        o.setEventId(eventId);
        o.setProjectTaskId(projectTaskId);
        o.setEventDate(RMT2Date.stringToDate(eventDate));
        o.setEventDateCreated(RMT2Date.stringToDate(eventDate));
        o.setHours(hours);
        o.setTimesheetId(timesheetId);
        o.setProjectId(projectId);
        o.setProjectName(projectName);
        o.setTaskId(taskId);
        o.setTaskName(taskName);
        o.setClientId(clientId);
        o.setEffectiveDate(RMT2Date.stringToDate(effectiveDate));
        o.setEndDate(RMT2Date.stringToDate(endDate));
        o.setBillable(billable ? 1 : 0);
        return o;
    }
    
    /**
     * 
     * @param taskId
     * @param description
     * @param billable
     * @return
     */
    public static final ProjTask createMockOrmProjTask(int taskId, String description, boolean billable) {
        ProjTask o = new ProjTask();
       o.setTaskId(taskId);
       o.setDescription(description);
       o.setBillable(billable ? 1 : 0);
        return o;
    }
    
    /**
     * 
     * @param projectTaskId
     * @param timesheetId
     * @param projectId
     * @param taskId
     * @param clientId
     * @param projectName
     * @param effectiveDate
     * @param endDate
     * @param taskName
     * @param billable
     * @return
     */
    public static final VwTimesheetProjectTask createMockOrmVwTimesheetProjectTask(
            int projectTaskId, int timesheetId, int projectId, int taskId,
            int clientId, String projectName, String effectiveDate,
            String endDate, String taskName, boolean billable) {
        VwTimesheetProjectTask o = new VwTimesheetProjectTask();
        o.setProjectTaskId(projectTaskId);
        o.setTimesheetId(timesheetId);
        o.setProjectId(projectId);
        o.setProjectName(projectName);
        o.setTaskId(taskId);
        o.setTaskName(taskName);
        o.setClientId(clientId);
        o.setEffectiveDate(RMT2Date.stringToDate(effectiveDate));
        o.setEndDate(RMT2Date.stringToDate(endDate));
        o.setBillable(billable ? 1 : 0);
        return o;
    }
   
    /**
     * 
     * @param projectTaskId
     * @param taskId
     * @param timesheetId
     * @param projId
     * @return
     */
    public static final ProjProjectTask createMockOrmProjProjectTask(int projectTaskId, int taskId, 
            int timesheetId, int projId) {
        ProjProjectTask o = new ProjProjectTask();
        o.setProjectTaskId(projectTaskId);
        o.setTaskId(taskId);
        o.setTimesheetId(timesheetId);
        o.setProjId(projId);
        return o;
    }
    
    /**
     * 
     * @param timesheetId
     * @param clientId
     * @param projId
     * @param empId
     * @param taskId
     * @param eventId
     * @param projTaskId
     * @param eventDate
     * @param hours
     * @param billable
     * @return
     */
    public static final VwTimesheetHours createMockOrmVwTimesheetHours(int timesheetId, int clientId, 
            int projId, int empId, int taskId, int eventId, int projTaskId, 
            String eventDate, double hours, boolean billable) {
        
        VwTimesheetHours o = new VwTimesheetHours();
        o.setTimesheetId(timesheetId);
        o.setClientId(clientId);
        o.setProjectId(projId);
        o.setEmployeeId(empId);
        o.setTaskId(taskId);
        o.setEventId(eventId);
        o.setProjTaskId(projTaskId);
        o.setHours(hours);
        
        // Set derived fields
        o.setEventDate(RMT2Date.stringToDate(eventDate));
        long newDateInMillSecs = RMT2Date.incrementDate(o.getEventDate(), Calendar.DATE, 1);
        Date newEndDate = RMT2Date.toDate(newDateInMillSecs);
        o.setEffectiveDate(o.getEventDate());
        o.setTimesheetBeginPeriod(o.getEventDate());
        o.setEndDate(newEndDate);
        o.setTimesheetEndPeriod(newEndDate);
        o.setDateCreated(o.getEventDate());
        
        o.setBillable(billable ? 1 : 0);
        
        String displayValue = RMT2String.padInt(timesheetId, 10, RMT2String.PAD_LEADING);
        o.setDisplayValue(displayValue);
        o.setInvoiceRefNo("InvoiceRefNo" + o.getTimesheetId());
        o.setExtRef("ExtRefNo" + o.getTimesheetId());
        o.setDocumentId(o.getTimesheetId());
        o.setProjectName("ProjectName" + o.getProjectId());
        o.setTaskName("TaskName" + o.getTaskId());
        return o;
    }
    
    /**
     * 
     * @param timesheetHistId
     * @param timesheetId
     * @param timesheetStatusId
     * @param effectiveDate
     * @param endDate
     * @return
     */
    public static final ProjTimesheetHist createMockOrmProjTimesheetHist(int timesheetHistId, int timesheetId, 
            int timesheetStatusId, String effectiveDate, String endDate) {
        ProjTimesheetHist o = new ProjTimesheetHist();
        o.setTimesheetHistId(timesheetHistId);
        o.setTimesheetId(timesheetId);
        o.setTimesheetStatusId(timesheetStatusId);
        o.setEffectiveDate(RMT2Date.stringToDate(effectiveDate));
        if (endDate != null) {
            o.setEndDate(RMT2Date.stringToDate(endDate));
        }
        o.setIpCreated("1.2.3.4");
        o.setIpUpdated("1.2.3.4");
        o.setUserId("testuser");
        return o;
    }
}

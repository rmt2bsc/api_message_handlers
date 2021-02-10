package org.rmt2.api.handler.timesheet;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.ProjClient;
import org.dao.mapping.orm.rmt2.ProjEvent;
import org.dao.mapping.orm.rmt2.ProjProjectTask;
import org.dao.mapping.orm.rmt2.ProjTimesheet;
import org.dao.mapping.orm.rmt2.ProjTimesheetHist;
import org.dao.mapping.orm.rmt2.VwBusinessAddress;
import org.dao.mapping.orm.rmt2.VwTimesheetEventList;
import org.dao.mapping.orm.rmt2.VwTimesheetHours;
import org.dao.mapping.orm.rmt2.VwTimesheetList;
import org.dao.mapping.orm.rmt2.VwTimesheetProjectTask;
import org.dao.mapping.orm.rmt2.VwTimesheetSummary;
import org.dao.timesheet.TimesheetConst;
import org.dto.BusinessContactDto;
import org.dto.ContactDto;
import org.dto.TimesheetDto;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.dto.adapter.orm.TimesheetObjectFactory;
import org.rmt2.api.ProjectTrackerOrmDataFactory;

/**
 * Timesheet API testing facility that is mainly responsible for setting up mock
 * data.
 * <p>
 * All Timesheet API unit tests should inherit this class to prevent duplicating
 * common functionality.
 * 
 * @author rterrell
 * 
 */
public class TimesheetMockData {
    protected List<ProjProjectTask> mockProjProjectTaskSingle;
    protected List<ProjProjectTask> mockProjProjectTaskMultiple;
    protected List<ProjTimesheet> mockProjTimesheetSingle;
    protected List<ProjTimesheet> mockProjTimesheetMultiple;
    protected List<VwTimesheetList> mockVwTimesheetSingle;
    protected List<VwTimesheetList> mockVwTimesheetMultiple;   
    protected List<ProjTimesheetHist> mockProjTimesheetHistMultiple;
    protected List<ProjTimesheetHist> mockCurrentProjTimesheetHist;
    protected List<VwTimesheetEventList> mockVwTimesheetEventListFetchMultiple;
    protected List<VwTimesheetEventList> mockVwTimesheetEventListFetchSingle;
    protected List<VwTimesheetProjectTask> mockVwTimesheetProjectTaskFetchMultiple;
    protected List<VwTimesheetProjectTask> mockVwTimesheetProjectTaskFetchSingle;
    protected List<VwTimesheetHours> mockTimesheetHours;

    
    private List<ProjClient> createMockMultipleClient() {
        List<ProjClient> list = new ArrayList<ProjClient>();
        ProjClient o = ProjectTrackerOrmDataFactory.createMockOrmProjClient(
                1110, 1350, "1110 Company", 70.00, 80.00, "000-111", "firstname0",
                "lastname0", "0000000000", "firstname0lastname0@gte.net");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjClient(1111, 1351,
                "1111 Company", 80.00, 90.00, "111-111", "firstname1", "lastname1",
                "1111111111", "firstname1lastname1@gte.net");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjClient(1112, 1352,
                "1112 Company", 90.00, 100.00, "222-111", "firstname2", "lastname2",
                "2222222222", "firstname2lastname2@gte.net");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjClient(1113, 1353,
                "1113 Company", 100.00, 110.00, "333-111", "firstname3", "lastname3",
                "3333333333", "firstname3lastname3@gte.net");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjClient(1114, 1354,
                "1114 Company", 110.00, 120.00, "444-111", "firstname4", "lastname4",
                "4444444444", "firstname4lastname4@gte.net");
        list.add(o);
        return list;
    }

    public static final List<ProjTimesheetHist> createMockTimesheetCurrentStatus() {
        List<ProjTimesheetHist> list = new ArrayList<ProjTimesheetHist>();
        ProjTimesheetHist o = ProjectTrackerOrmDataFactory.createMockOrmProjTimesheetHist(
                ProjectTrackerOrmDataFactory.TEST_TIMESHEET_HIST_ID,
                ProjectTrackerOrmDataFactory.TEST_TIMESHEET_ID,
                TimesheetConst.STATUS_APPROVED, "2018-01-09", null);
        list.add(o);
        
        return list;
    }
    
    public static final List<ProjTimesheetHist> createMockTimesheetStatusHistory() {
        List<ProjTimesheetHist> list = new ArrayList<ProjTimesheetHist>();
        ProjTimesheetHist o = ProjectTrackerOrmDataFactory.createMockOrmProjTimesheetHist(
                ProjectTrackerOrmDataFactory.TEST_TIMESHEET_HIST_ID,
                ProjectTrackerOrmDataFactory.TEST_TIMESHEET_ID,
                TimesheetConst.STATUS_NEW, "2018-01-01", "2018-01-02");    
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjTimesheetHist(
                ProjectTrackerOrmDataFactory.TEST_TIMESHEET_HIST_ID,
                ProjectTrackerOrmDataFactory.TEST_TIMESHEET_ID,
                TimesheetConst.STATUS_DRAFT, "2018-01-03", "2018-01-04");
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjTimesheetHist(
                ProjectTrackerOrmDataFactory.TEST_TIMESHEET_HIST_ID,
                ProjectTrackerOrmDataFactory.TEST_TIMESHEET_ID,
                TimesheetConst.STATUS_SUBMITTED, "2018-01-05", "2018-01-06");
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjTimesheetHist(
                ProjectTrackerOrmDataFactory.TEST_TIMESHEET_HIST_ID,
                ProjectTrackerOrmDataFactory.TEST_TIMESHEET_ID,
                TimesheetConst.STATUS_RECVD, "2018-01-07", "2018-01-08");
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjTimesheetHist(
                ProjectTrackerOrmDataFactory.TEST_TIMESHEET_HIST_ID,
                ProjectTrackerOrmDataFactory.TEST_TIMESHEET_ID,
                TimesheetConst.STATUS_APPROVED, "2018-01-09", null);
        list.add(o);
        
        return list;
    }
    
    private List<ProjProjectTask> createMockSingleProjProjectTask() {
        List<ProjProjectTask> list = new ArrayList<ProjProjectTask>();
        ProjProjectTask o = ProjectTrackerOrmDataFactory.createMockOrmProjProjectTask(444441, 1112220, 111, 4440);
        list.add(o);
        return list;
    }
    
    private List<ProjProjectTask> createMockMultipleProjProjectTask() {
        List<ProjProjectTask> list = new ArrayList<ProjProjectTask>();
        ProjProjectTask o = ProjectTrackerOrmDataFactory.createMockOrmProjProjectTask(444441, 1112220, 111, 4440);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjProjectTask(444442, 1112221, 111, 4440);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjProjectTask(444443, 1112222, 111, 4440);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjProjectTask(444444, 1112223, 111, 4440);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjProjectTask(444445, 1112224, 111, 4440);
        list.add(o);
        
        return list;
    }
    
    
    
    
    private List<VwTimesheetProjectTask> createMockSingleVwTimesheetProjectTask() {
        List<VwTimesheetProjectTask> list = new ArrayList<VwTimesheetProjectTask>();
        VwTimesheetProjectTask o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetProjectTask(444441, 111, 4440,
                        1112220, 1110, "Project 2220", "2018-01-01",
                        "2018-01-07", "Design and Analysis", true);
        list.add(o);
        return list;
    }
    
    public static final List<VwTimesheetProjectTask> createMockMultipleVwTimesheetProjectTask() {
        List<VwTimesheetProjectTask> list = new ArrayList<VwTimesheetProjectTask>();
        VwTimesheetProjectTask o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetProjectTask(444441, 111, 4440,
                        1112220, 1110, "Project 2220", "2018-01-01",
                        "2018-01-07", "Design and Analysis", true);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetProjectTask(444442, 111, 4440,
                        1112221, 1110, "Project 2220", "2018-01-01",
                        "2018-01-07", "Development", true);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetProjectTask(444443, 111, 4440,
                        1112222, 1110, "Project 2220", "2018-01-01",
                        "2018-01-07", "Meetings", true);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetProjectTask(444444, 111, 4440,
                        1112223, 1110, "Project 2220", "2018-01-01",
                        "2018-01-07", "Testing", true);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetProjectTask(444445, 111, 4440,
                        1112224, 1110, "Project 2220", "2018-01-01",
                        "2018-01-07", "Holiday", false);
        list.add(o);
        
        return list;
    }
    
    private List<VwTimesheetEventList> createMockSingleVwTimesheetEventList() {
        List<VwTimesheetEventList> list = new ArrayList<VwTimesheetEventList>();
        VwTimesheetEventList o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetEventList(123401, "2018-01-01", 8,
                        444441, 111, 4440, "Project 2220", 1112220,
                        "Design and Analysis", 1110, "2018-01-01", "2018-01-07", true);
        list.add(o);
        return list;
    }
    
    private List<VwTimesheetEventList> createMockMultipleVwTimesheetEventList() {
        List<VwTimesheetEventList> list = new ArrayList<VwTimesheetEventList>();
        VwTimesheetEventList o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetEventList(123401, "2018-01-01", 8,
                        444441, 111, 4440, "Project 2220", 1112220,
                        "Design and Analysis", 1110, "2018-01-01", "2018-01-07",
                        true);
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetEventList(123402, "2018-01-02", 8, 444442, 111, 4440,
                "Project 2220", 1112221, "Development", 1110, "2018-01-01",
                "2018-01-07", true);
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetEventList(123403, "2018-01-03", 8, 444443, 111, 4440,
                "Project 2220", 1112222, "Meetings", 1110, "2018-01-01",
                "2018-01-07", true);
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetEventList(123404, "2018-01-04", 8, 444444, 111, 4440,
                "Project 2220", 1112223, "Testing", 1110, "2018-01-01",
                "2018-01-07", true);
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetEventList(123405, "2018-01-05", 8, 444445, 111, 4440,
                "Project 2220", 1112224, "Holiday", 1110, "2018-01-01",
                "2018-01-07", false);
        list.add(o);
        return list;
    }
    
    private List<ProjTimesheet> createMockSingleTimesheetList() {
        List<ProjTimesheet> list = new ArrayList<ProjTimesheet>();
        ProjTimesheet o = ProjectTrackerOrmDataFactory.createMockOrmProjTimesheet(111, 1110, 4440, 2220, "INVREF1230",
                "2018-01-01", "2018-01-07", "ExtReNo1000");
        list.add(o);
        return list;
    }
    
    public static final List<ProjTimesheet> createMockMultipleTimesheetSameClientList() {
        List<ProjTimesheet> list = new ArrayList<ProjTimesheet>();
        ProjTimesheet o = ProjectTrackerOrmDataFactory.createMockOrmProjTimesheet(111, 1110, 4440, 2220, "INVREF1230",
                "2018-01-01", "2018-01-07", "ExtReNo1000");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjTimesheet(112, 1110, 4440, 2220, "INVREF1231",
                "2018-01-08", "2018-01-14", "ExtReNo1001");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjTimesheet(113, 1110, 4440, 2220, "INVREF1232",
                "2018-01-15", "2018-01-21", "ExtReNo1002");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjTimesheet(114, 1110, 4440, 2220, "INVREF1233",
                "2018-01-22", "2018-01-28", "ExtReNo1003");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjTimesheet(115, 1110, 4440, 2220, "INVREF1234",
                "2018-01-29", "2018-02-04", "ExtReNo1004");
        list.add(o);
        return list;
    }
    
    
    public static final List<TimesheetDto> createMockExtTimesheetList() {
        List<TimesheetDto> list = new ArrayList<>();
        VwTimesheetList o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetList(111, 1110, 1234, 2220,
                "INVREF1230", "2018-01-01", "2018-01-01", "ExtReNo1000",
                3330, "DRAFT", "ACCT-111", 40, 0, 70.00, 80.00);
        TimesheetDto dto = TimesheetObjectFactory.createTimesheetExtendedDtoInstance(o);
        list.add(dto);

        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetList(112, 1110, 1234, 2220,
                "INVREF1231", "2018-01-02", "2018-01-02", "ExtReNo1001",
                3330, "DRAFT", "ACCT-111", 40, 0, 70.00, 80.00);
        dto = TimesheetObjectFactory.createTimesheetExtendedDtoInstance(o);
        list.add(dto);

        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetList(113, 1110, 1234, 2220,
                "INVREF1232", "2018-01-03", "2018-01-03", "ExtReNo1002",
                3330, "DRAFT", "ACCT-111", 40, 0, 70.00, 80.00);
        dto = TimesheetObjectFactory.createTimesheetExtendedDtoInstance(o);
        list.add(dto);

        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetList(114, 1110, 1234, 2220,
                "INVREF1233", "2018-01-04", "2018-01-04", "ExtReNo1003",
                3330, "DRAFT", "ACCT-111", 40, 0, 70.00, 80.00);
        dto = TimesheetObjectFactory.createTimesheetExtendedDtoInstance(o);
        list.add(dto);

        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetList(115, 1110, 1234, 2220,
                "INVREF1234", "2018-01-05", "2018-01-05", "ExtReNo1004",
                3330, "DRAFT", "ACCT-111", 40, 0, 70.00, 80.00);
        dto = TimesheetObjectFactory.createTimesheetExtendedDtoInstance(o);
        list.add(dto);
        return list;
    }
    
    private List<VwTimesheetHours> createMockTimesheetHours() {
        List<VwTimesheetHours> list = new ArrayList<VwTimesheetHours>();
        VwTimesheetHours o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetHours(111, 1110, 4440, 2220,
                1112220, 123401, 444441, "2018-01-01", 8, true);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetHours(111, 1110, 4440, 2220,
                1112220, 123401, 444441, "2018-01-02", 8, true);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetHours(111, 1110, 4440, 2220,
                1112220, 123401, 444441, "2018-01-03", 8, true);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetHours(111, 1110, 4440, 2220,
                1112220, 123401, 444441, "2018-01-04", 8, true);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetHours(111, 1110, 4440, 2220,
                1112220, 123401, 444441, "2018-01-05", 8, true);
        list.add(o);
        return list;
    }
    
    /**
     * 
     * @param day
     * @return
     */
    public static final List<ProjEvent> createMockMultiple_Day_Task_Events(int projectTaskId) {
        List<ProjEvent> list = new ArrayList<ProjEvent>();
        int eventId = ProjectTrackerOrmDataFactory.TEST_EVENT_ID;
        // Day 1
        ProjEvent o = ProjectTrackerOrmDataFactory.createMockOrmProjEvent(eventId, projectTaskId, "2018-01-01", 0);
        list.add(o);
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEvent(++eventId, projectTaskId, "2018-01-02", 2);
        list.add(o);
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEvent(++eventId, projectTaskId, "2018-01-03", 2);
        list.add(o);
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEvent(++eventId, projectTaskId, "2018-01-04", 2);
        list.add(o);
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEvent(++eventId, projectTaskId, "2018-01-05", 1);
        list.add(o);
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEvent(++eventId, projectTaskId, "2018-01-06", 1);
        list.add(o);
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEvent(++eventId, projectTaskId, "2018-01-07", 0);
        list.add(o);
        
       
        return list;
    }

    /**
     * 
     * @return
     */
    public static final List<VwTimesheetSummary> createMockTimesheetSummaryList() {
        List<VwTimesheetSummary> list = new ArrayList<>();
        VwTimesheetSummary o = ProjectTrackerOrmDataFactory.createMockOrmVwTimesheetSummary(111, "john", "doe", "2020-5-15", 8);
        list.add(o);
        return list;
    }

    /**
     * 
     * @return
     */
    public static final List<ContactDto> createMockSingleBusinessContactDto() {
        List<ContactDto> list = new ArrayList<ContactDto>();
        VwBusinessAddress bus = new VwBusinessAddress();
        bus.setBusinessId(1351);
        bus.setBusLongname("BusinessName_1");
        bus.setBusContactFirstname("firstname_1");
        bus.setBusContactLastname("lastname_1");
        bus.setContactEmail(bus.getBusContactFirstname() + "." + bus.getBusContactLastname() + "@gte.net");
        bus.setBusContactPhone("9999999991");
        bus.setAddrId(2001);
        bus.setBusinessId(1351);
        bus.setAddr1("address_line1_1");
        bus.setAddr2("address_line2_1");
        bus.setAddr3("address_line3_1");
        bus.setAddr4("address_line4_1");
        bus.setZipCity("Dallas");
        bus.setZipState("Tx");
        bus.setAddrZip(75232);
        bus.setAddrPhoneMain("2143738001");
        bus.setBusTaxId("750000001");
        bus.setBusWebsite("www.BusinessName_1.com");
        bus.setBusShortname("shortname");
        BusinessContactDto busDto = Rmt2AddressBookDtoFactory.getBusinessInstance(bus);
        list.add(busDto);
        return list;
    }
}
package org.rmt2.api;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.ProjClient;
import org.dao.mapping.orm.rmt2.ProjEmployee;
import org.dao.mapping.orm.rmt2.ProjEmployeeTitle;
import org.dao.mapping.orm.rmt2.ProjEmployeeType;
import org.dao.mapping.orm.rmt2.ProjEvent;
import org.dao.mapping.orm.rmt2.ProjProject;
import org.dao.mapping.orm.rmt2.ProjTask;
import org.dao.mapping.orm.rmt2.VwEmployeeExt;
import org.dao.mapping.orm.rmt2.VwEmployeeProjects;
import org.dto.EmployeeDto;
import org.dto.adapter.orm.EmployeeObjectFactory;

/**
 * Project Tracker Administration testing facility that is mainly responsible for
 * setting up mock data.
 * <p>
 * All derived project tracker related Api unit tests should inherit this class
 * to prevent duplicating common functionality.
 * 
 * @author rterrell
 * 
 */
public class ProjectTrackerMockData {

    public static final List<ProjClient> createMockSingleClient() {
        List<ProjClient> list = new ArrayList<ProjClient>();
        ProjClient o = ProjectTrackerOrmDataFactory.createMockOrmProjClient(
                1110, 1350, "1110 Company", 70.00, 80.00, "000-111", "steve",
                "gadd", "0000000000", "stevegadd@gte.net");
        list.add(o);
        return list;
    }

    public static final List<ProjClient> createMockMultipleClient() {
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

    public static final List<ProjProject> createMockSingleProject() {
        List<ProjProject> list = new ArrayList<ProjProject>();
        ProjProject o = ProjectTrackerOrmDataFactory.createMockOrmProjProject(
                2220, 1110, "Project 2220", "2018-01-01", "2018-02-01");
        list.add(o);
        return list;
    }

    public static final List<ProjProject> createMockMultipleProject() {
        List<ProjProject> list = new ArrayList<ProjProject>();
        ProjProject o = ProjectTrackerOrmDataFactory.createMockOrmProjProject(
                2220, 1110, "Project 2220", "2018-01-01", "2018-02-01");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjProject(2221, 1110,
                "Project 2221", "2018-02-01", "2018-03-01");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjProject(2222, 1110,
                "Project 2222", "2018-03-01", "2018-04-01");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjProject(2223, 1110,
                "Project 2223", "2018-04-01", "2018-05-01");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjProject(2224, 1110,
                "Project 2224", "2018-05-01", "2018-06-01");
        list.add(o);
        return list;
    }
    
    public static final List<ProjEmployeeTitle> createMockSingleEmployeeTitle() {
        List<ProjEmployeeTitle> list = new ArrayList<ProjEmployeeTitle>();
        ProjEmployeeTitle o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployeeTitle(101, "Employee Title 1");
        list.add(o);
        return list;
    }
    
    public static final List<ProjEmployeeTitle> createMockMultipleEmployeeTitle() {
        List<ProjEmployeeTitle> list = new ArrayList<ProjEmployeeTitle>();
        ProjEmployeeTitle o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployeeTitle(101, "Employee Title 1");
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployeeTitle(102, "Employee Title 2");
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployeeTitle(103, "Employee Title 3");
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployeeTitle(104, "Employee Title 4");
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployeeTitle(105, "Employee Title 5");
        list.add(o);
        return list;
    }
    
    public static final List<ProjEmployeeType> createMockSingleEmployeeType() {
        List<ProjEmployeeType> list = new ArrayList<ProjEmployeeType>();
        ProjEmployeeType o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployeeType(201, "Employee Type 1");
        list.add(o);
        return list;
    }
    
    public static final List<ProjEmployeeType> createMockMultipleEmployeeType() {
        List<ProjEmployeeType> list = new ArrayList<ProjEmployeeType>();
        ProjEmployeeType o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployeeType(201, "Employee Type 1");
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployeeType(202, "Employee Type 2");
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployeeType(203, "Employee Type 3");
        list.add(o);
        
        return list;
    }
    
    public static final List<ProjEmployee> createMockSingleManager() {
        List<ProjEmployee> list = new ArrayList<ProjEmployee>();
        ProjEmployee o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployee(5550, 201, 1, 9999, 202, 222221,
                "2010-01-01", "2018-01-01", "login_name_1", "mgr_first_name_1", "mgr_last_name_1",
                "111-11-5000", "ABC Company");
        list.add(o);
        return list;
    }
    
    public static final List<ProjEmployee> createMockSingleEmployee() {
        List<ProjEmployee> list = new ArrayList<ProjEmployee>();
        ProjEmployee o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployee(2220, 201, 0, 3330, 101, 999991,
                "2010-01-01", "2018-01-01", "login_name_1", "first_name_1", "last_name_1",
                "111-11-5000", "ABC Company");
        list.add(o);
        return list;
    }
    
    public static final List<ProjEmployee> createMockMultipleEmployee() {
        List<ProjEmployee> list = new ArrayList<ProjEmployee>();
        ProjEmployee o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployee(2220, 201, 0, 3330, 101, 999991,
                "2010-01-01", null, "login_name_1", "first_name_1", "last_name_1",
                "111-11-5000", "ABC Company");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployee(2221, 201, 0, 3330, 102, 999992,
                "2011-01-01", null, "login_name_2", "first_name_2", "last_name_2",
                "111-11-5001", "ABC Company");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployee(2222, 201, 0, 3330, 103, 999993,
                "2012-01-01", null, "login_name_3", "first_name_3", "last_name_3",
                "111-11-5002", "ABC Company");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployee(2223, 202, 1, 3330, 104, 999994,
                "2013-01-01", "2018-01-01", "login_name_4", "first_name_4", "last_name_4",
                "111-11-5003", "ABC Company");
        list.add(o);

        o = ProjectTrackerOrmDataFactory.createMockOrmProjEmployee(2224, 202, 1, 3330, 105, 999995,
                "2014-01-01", "2018-01-01", "login_name_5", "first_name_5", "last_name_5",
                "111-11-5004", "ABC Company");
        list.add(o);
        return list;
    }
    
    public static final List<EmployeeDto> createMockSingleExtEmployee() {
        List<EmployeeDto> list = new ArrayList<>();
        VwEmployeeExt o = ProjectTrackerOrmDataFactory.createMockOrmExtEmployee(2220, "EmployeeType", 0, 3330, "EmployeeTitle", 999991,
                "2010-01-01", "2018-01-01", "login_name_1", "first_name_1", "last_name_1",
                "111-11-5000", "ABC Company");
        EmployeeDto dto = EmployeeObjectFactory.createEmployeeExtendedDtoInstance(o);
        list.add(dto);
        return list;
    }
    
    public static final List<EmployeeDto> createMockMultipleExtEmployee() {
        List<EmployeeDto> list = new ArrayList<>();
        VwEmployeeExt o = ProjectTrackerOrmDataFactory.createMockOrmExtEmployee(2220, "EmployeeType", 0, 3330,
                "EmployeeTitle_1", 999991,
                "2010-01-01", "2018-01-01", "login_name_1", "first_name_1", "last_name_1",
                "111-11-5001", "ABC Company");
        EmployeeDto dto = EmployeeObjectFactory.createEmployeeExtendedDtoInstance(o);
        list.add(dto);

        o = ProjectTrackerOrmDataFactory.createMockOrmExtEmployee(2221, "EmployeeType", 0, 3330, "EmployeeTitle_2", 999992,
                "2010-01-01", "2018-01-01", "login_name_2", "first_name_2", "last_name_2",
                "111-11-5002", "ABC Company");
        dto = EmployeeObjectFactory.createEmployeeExtendedDtoInstance(o);
        list.add(dto);

        o = ProjectTrackerOrmDataFactory.createMockOrmExtEmployee(2222, "EmployeeType", 0, 3330, "EmployeeTitle_3", 999993,
                "2010-01-01", "2018-01-01", "login_name_3", "first_name_3", "last_name_3",
                "111-11-5003", "ABC Company");
        dto = EmployeeObjectFactory.createEmployeeExtendedDtoInstance(o);
        list.add(dto);

        o = ProjectTrackerOrmDataFactory.createMockOrmExtEmployee(2223, "EmployeeType", 0, 3330, "EmployeeTitle_4", 999994,
                "2010-01-01", "2018-01-01", "login_name_4", "first_name_4", "last_name_4",
                "111-11-5004", "ABC Company");
        dto = EmployeeObjectFactory.createEmployeeExtendedDtoInstance(o);
        list.add(dto);

        o = ProjectTrackerOrmDataFactory.createMockOrmExtEmployee(2224, "EmployeeType", 0, 3330, "EmployeeTitle_5", 999995,
                "2010-01-01", "2018-01-01", "login_name_5", "first_name_5", "last_name_5",
                "111-11-5005", "ABC Company");
        dto = EmployeeObjectFactory.createEmployeeExtendedDtoInstance(o);
        list.add(dto);

        return list;
    }

    public static final List<VwEmployeeProjects> createMockSingleVwEmployeeProjects() {
        List<VwEmployeeProjects> list = new ArrayList<VwEmployeeProjects>();
        VwEmployeeProjects o = ProjectTrackerOrmDataFactory.createMockOrmVwEmployeeProjects(55551, 4440, "Project 2220",
                        1110, "1110 Company", 1350, "000-111", 2220,
                        "2018-01-01", "2018-02-01", "2018-01-01", "2018-02-01",
                        50.00, 55.00, 0.00, 70.00, 80.00);
        list.add(o);
        return list;
    }
    
    public static final List<VwEmployeeProjects> createMockMultipleVwEmployeeProjects() {
        List<VwEmployeeProjects> list = new ArrayList<VwEmployeeProjects>();
        VwEmployeeProjects o = ProjectTrackerOrmDataFactory.createMockOrmVwEmployeeProjects(55551, 4440, "Project 2220",
                        1110, "1110 Company", 1350, "000-111", 2220,
                        "2018-01-01", "2018-02-01", "2018-01-01", "2018-02-01",
                        50.00, 55.00, 0.00, 70.00, 80.00);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmVwEmployeeProjects(55552,
                4441, "Project 2221", 1111, "1111 Company", 1350, "000-111",
                2220, "2018-01-01", "2018-02-01", "2018-01-01", "2018-02-01",
                50.00, 55.00, 0.00, 70.00, 80.00);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmVwEmployeeProjects(55553,
                4442, "Project 2222", 1112, "1112 Company", 1350, "000-111",
                2220, "2018-01-01", "2018-02-01", "2018-01-01", "2018-02-01",
                50.00, 55.00, 0.00, 70.00, 80.00);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmVwEmployeeProjects(55554,
                4443, "Project 2223", 1113, "1113 Company", 1350, "000-111",
                2220, "2018-01-01", "2018-02-01", "2018-01-01", "2018-02-01",
                50.00, 55.00, 0.00, 70.00, 80.00);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmVwEmployeeProjects(55555,
                4444, "Project 2224", 1114, "1114 Company", 1350, "000-111",
                2220, "2018-01-01", "2018-02-01", "2018-01-01", "2018-02-01",
                50.00, 55.00, 0.00, 70.00, 80.00);
        list.add(o);
        
        return list;
    }
    
    public static final List<ProjEvent> createMockSingleEvent() {
        List<ProjEvent> list = new ArrayList<ProjEvent>();
        ProjEvent o = ProjectTrackerOrmDataFactory.createMockOrmProjEvent(123401, 444441, "2018-01-01", 8);
        list.add(o);
        return list;
    }
    
    public static final List<ProjEvent> createMockMultipleEvent() {
        List<ProjEvent> list = new ArrayList<ProjEvent>();
        ProjEvent o = ProjectTrackerOrmDataFactory.createMockOrmProjEvent(123401, 444441, "2018-01-01", 8);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEvent(123402, 444442, "2018-01-02", 8);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEvent(123403, 444443, "2018-01-03", 8);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEvent(123404, 444444, "2018-01-04", 8);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjEvent(123405, 444445, "2018-01-05", 8);
        list.add(o);
        return list;
    }
    
    public static final List<ProjTask> createMockSingleTask() {
        List<ProjTask> list = new ArrayList<ProjTask>();
        ProjTask o = ProjectTrackerOrmDataFactory.createMockOrmProjTask(1112220, "Design and Analysis", true);
        list.add(o);
        return list;
    }
    
    public static final List<ProjTask> createMockMultipleTask() {
        List<ProjTask> list = new ArrayList<ProjTask>();
        ProjTask o = ProjectTrackerOrmDataFactory.createMockOrmProjTask(1112220, "Design and Analysis", true);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjTask(1112221, "Development", true);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjTask(1112222, "Meetings", true);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjTask(1112223, "Testing", true);
        list.add(o);
        
        o = ProjectTrackerOrmDataFactory.createMockOrmProjTask(1112224, "Holiday", false);
        list.add(o);
        return list;
    }
}
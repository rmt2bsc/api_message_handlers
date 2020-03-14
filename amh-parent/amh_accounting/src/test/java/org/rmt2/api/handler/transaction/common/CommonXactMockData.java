package org.rmt2.api.handler.transaction.common;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.VwGenericXactList;
import org.dao.mapping.orm.rmt2.VwXactList;
import org.dao.mapping.orm.rmt2.XactTypeItemActivity;
import org.dto.CommonXactDto;
import org.dto.XactDto;
import org.dto.XactTypeItemActivityDto;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.modules.transaction.XactConst;
import org.rmt2.api.AccountingMockDataFactory;
import org.rmt2.api.handler.subsidiary.SubsidiaryMockData;

import com.api.util.RMT2Date;

/**
 * @author rterrell
 *
 */
public class CommonXactMockData extends SubsidiaryMockData {
    
    public static final int NEW_XACT_ID = 1234567;
    public static final int EXISTING_XACT_ID = 7777;

    /**
     * 
     * @return
     */
    public static final List<XactDto> createMockCommonTransactions() {
        List<XactDto> list = new ArrayList<XactDto>();
        VwXactList o = AccountingMockDataFactory.createMockOrmXact(111111, XactConst.XACT_TYPE_CASH_DISBURSE,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED, RMT2Date.stringToDate("2017-01-13"), 100.00, 200, "1111-1111-1111-1111");
        XactDto d = Rmt2XactDtoFactory.createXactInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXact(222222, XactConst.XACT_TYPE_CASH_DISBURSE, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-14"), 101.00, 200,
                "2222-2222-2222-2222");
        d = Rmt2XactDtoFactory.createXactInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXact(333333, XactConst.XACT_TYPE_CASH_DISBURSE, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-15"), 102.00, 200, "3333-3333-3333-3333");
        d = Rmt2XactDtoFactory.createXactInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXact(444444, XactConst.XACT_TYPE_CASH_DISBURSE, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-16"), 103.00, 200, "4444-4444-4444-4444");
        d = Rmt2XactDtoFactory.createXactInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXact(555555, XactConst.XACT_TYPE_CASH_DISBURSE, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-17"), 104.00, 200,
                "5555-5555-5555-5555");
        d = Rmt2XactDtoFactory.createXactInstance(o);
        list.add(d);
        return list;
    }
    
    /**
     * 
     * @return
     */
    public static final List<XactDto> createMockSingleXact() {
        List<XactDto> list = new ArrayList<XactDto>();
        VwXactList o = AccountingMockDataFactory.createMockOrmXact(111111, XactConst.XACT_TYPE_CASH_DISBURSE,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED, RMT2Date.stringToDate("2017-01-13"), 100.00, 200, "1111-1111-1111-1111");
        XactDto d = Rmt2XactDtoFactory.createXactInstance(o);
        list.add(d);
        return list;
    }
    
    /**
     * 
     * @return
     */
    public static final List<XactTypeItemActivityDto> createMockXactItems() {
        List<XactTypeItemActivityDto> list = new ArrayList<XactTypeItemActivityDto>();
        XactTypeItemActivity o = AccountingMockDataFactory
                .createMockOrmXactTypeItemActivity(7001, 111111, 601, 31.11,
                        "Item1");
        XactTypeItemActivityDto d = Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(7002,
                111111, 602, 20.00, "Item2");
        d = Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(7003,
                111111, 603, 20.00, "Item3");
        d = Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(7004,
                111111, 604, 20.00, "Item4");
        d = Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(7005,
                111111, 605, 20.00, "Item5");
        d = Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
        list.add(d);
        return list;
    }
    
    public static final List<CommonXactDto> createMockGenericXactList() {
        List<CommonXactDto> list = new ArrayList<>();
        VwGenericXactList o = AccountingMockDataFactory.createMockOrmGenericXact(111111, XactConst.XACT_TYPE_CREDITOR_PURCHASE,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED, RMT2Date.stringToDate("2020-01-01"), 100.00, "Purchase Expense on Account",
                200, "XYZ Company0", "1111-1111-1111-1111", "R-20034-9382", 1351, 300,
                RMT2Date.stringToDate("2020-01-01"), 1000, RMT2Date.stringToDate("2020-01-01"));
        CommonXactDto dto = Rmt2XactDtoFactory.createGenericXactInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmGenericXact(111111, XactConst.XACT_TYPE_CASH_DISBURSE,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED, RMT2Date.stringToDate("2020-01-01"), 200.00, "Cash Disbursement",
                200, "XYZ Company1", null, null, 0, 0, null, 0, null);
        dto = Rmt2XactDtoFactory.createGenericXactInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmGenericXact(111111, XactConst.XACT_TYPE_CASH_DISBURSE,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED, RMT2Date.stringToDate("2020-01-01"), 300.00, "Cash Disbursement",
                200, "XYZ Company2", null, null, 0, 0, null, 0, null);
        dto = Rmt2XactDtoFactory.createGenericXactInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmGenericXact(111111, XactConst.XACT_TYPE_CREDITOR_PURCHASE,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED, RMT2Date.stringToDate("2020-01-01"), 400.00, "Purchase Expense on Account",
                200, "XYZ Company3", "1111-1111-1111-1111", "R-20034-9382", 1351, 300,
                RMT2Date.stringToDate("2020-01-01"), 1000, RMT2Date.stringToDate("2020-01-01"));
        dto = Rmt2XactDtoFactory.createGenericXactInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmGenericXact(111111, XactConst.XACT_TYPE_CREDITOR_PURCHASE,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED, RMT2Date.stringToDate("2020-01-01"), 500.00, "Purchase Expense on Account",
                200, "XYZ Company4", "1111-1111-1111-1111", "R-20034-9382", 1351, 300,
                RMT2Date.stringToDate("2020-01-01"), 1000, RMT2Date.stringToDate("2020-01-01"));
        dto = Rmt2XactDtoFactory.createGenericXactInstance(o);
        list.add(dto);
        return list;
    }
}

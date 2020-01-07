package org.rmt2.api.handler.transaction.receipts;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.VwXactList;
import org.dto.XactDto;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.modules.transaction.XactConst;
import org.rmt2.api.AccountingMockDataFactory;
import org.rmt2.api.handler.subsidiary.SubsidiaryMockData;

import com.api.util.RMT2Date;

/**
 * @author rterrell
 *
 */
public class CashReceiptsMockData extends SubsidiaryMockData {

    public static final int NEW_XACT_ID = 1234567;
    public static final int EXISTING_XACT_ID = 7777;
    public static final int CUSTOMER_ID = 77777777;

    /**
     * 
     * @return
     */
    public static final List<XactDto> createMockTransactions() {
        List<XactDto> list = new ArrayList<XactDto>();
        VwXactList o = AccountingMockDataFactory.createMockOrmXact(111110, XactConst.XACT_TYPE_CASHRECEIPT, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-13"), 100.00, 11, "1111-1111-1111-1111");
        XactDto d = Rmt2XactDtoFactory.createXactInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXact(111111, XactConst.XACT_TYPE_CASHRECEIPT, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-14"), 101.00, 11, "2222-2222-2222-2222");
        d = Rmt2XactDtoFactory.createXactInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXact(111112, XactConst.XACT_TYPE_CASHRECEIPT, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-15"), 102.00, 11, "3333-3333-3333-3333");
        d = Rmt2XactDtoFactory.createXactInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXact(111113, XactConst.XACT_TYPE_CASHRECEIPT, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-16"), 103.00, 11, "4444-4444-4444-4444");
        d = Rmt2XactDtoFactory.createXactInstance(o);
        list.add(d);

        o = AccountingMockDataFactory.createMockOrmXact(111114, XactConst.XACT_TYPE_CASHRECEIPT, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-17"), 104.00, 11, "5555-5555-5555-5555");
        d = Rmt2XactDtoFactory.createXactInstance(o);
        list.add(d);
        return list;
    }

    /**
     * 
     * @return
     */
    public static final List<XactDto> createMockSingleTransaction() {
        List<XactDto> list = new ArrayList<XactDto>();
        VwXactList o = AccountingMockDataFactory.createMockOrmXact(111111, XactConst.XACT_TYPE_CASHRECEIPT, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-13"), 100.00, 11, "1111-1111-1111-1111");
        XactDto d = Rmt2XactDtoFactory.createXactInstance(o);
        list.add(d);
        return list;
    }

    // /**
    // *
    // * @return
    // */
    // public static final List<XactTypeItemActivityDto> createMockXactItems() {
    // List<XactTypeItemActivityDto> list = new
    // ArrayList<XactTypeItemActivityDto>();
    // XactTypeItemActivity o = AccountingMockDataFactory
    // .createMockOrmXactTypeItemActivity(7001, 111111, 601, 31.11,
    // "Item1");
    // XactTypeItemActivityDto d =
    // Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
    // list.add(d);
    //
    // o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(7002,
    // 111111, 602, 20.00, "Item2");
    // d = Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
    // list.add(d);
    //
    // o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(7003,
    // 111111, 603, 20.00, "Item3");
    // d = Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
    // list.add(d);
    //
    // o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(7004,
    // 111111, 604, 20.00, "Item4");
    // d = Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
    // list.add(d);
    //
    // o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(7005,
    // 111111, 605, 20.00, "Item5");
    // d = Rmt2XactDtoFactory.createXactTypeItemActivityInstance(o);
    // list.add(d);
    // return list;
    // }

}

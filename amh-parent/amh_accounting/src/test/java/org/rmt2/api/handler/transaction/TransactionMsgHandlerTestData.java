package org.rmt2.api.handler.transaction;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.VwGenericXactList;
import org.dao.mapping.orm.rmt2.VwXactList;
import org.dao.mapping.orm.rmt2.VwXactTypeItemActivity;
import org.dao.mapping.orm.rmt2.XactCategory;
import org.dao.mapping.orm.rmt2.XactCodeGroup;
import org.dao.mapping.orm.rmt2.XactCodes;
import org.dao.mapping.orm.rmt2.XactType;
import org.dao.mapping.orm.rmt2.XactTypeItem;
import org.dao.mapping.orm.rmt2.XactTypeItemActivity;
import org.junit.After;
import org.junit.Before;
import org.modules.transaction.XactConst;
import org.rmt2.api.AccountingMockDataFactory;
import org.rmt2.api.handler.subsidiary.SubsidiaryMsgHandlerTestData;

import com.api.util.RMT2Date;

/**
 * Base transaction testing facility that is mainly responsible for setting up
 * mock data.
 * <p>
 * All derived transaction related Api unit tests should inherit this class to
 * prevent duplicating common functionality.
 * 
 * @author rterrell
 * 
 */
public class TransactionMsgHandlerTestData extends SubsidiaryMsgHandlerTestData {
    protected List<XactCategory> mockCategoryNotFoundFetchResponse;
    protected List<XactCategory> mockCategoryFetchAllResponse;
    protected List<XactCategory> mockCategoryFetchSingleResponse;

    protected List<XactCodes> mockCodeNotFoundFetchResponse;
    protected List<XactCodes> mockCodeFetchAllResponse;
    protected List<XactCodes> mockCodeFetchSingleResponse;

    protected List<XactCodeGroup> mockCodeGroupNotFoundFetchResponse;
    protected List<XactCodeGroup> mockCodeGroupFetchAllResponse;
    protected List<XactCodeGroup> mockCodeGroupFetchSingleResponse;

    protected List<XactType> mockXactTypeNotFoundFetchResponse;
    protected List<XactType> mockXactTypeFetchAllResponse;
    protected List<XactType> mockXactTypeFetchSingleResponse;

    protected List<VwXactList> mockXactNotFoundFetchResponse;
    protected List<VwXactList> mockXactFetchAllResponse;
    protected List<VwXactList> mockXactFetchSingleResponse;

    protected List<VwGenericXactList> mockXactFetchAllGenericResponse;

    protected List<XactTypeItem> mockXactTypeItemNotFoundFetchResponse;
    protected List<XactTypeItem> mockXactTypeItemFetchAllResponse;
    protected List<XactTypeItem> mockXactTypeItemFetchSingleResponse;

    protected List<XactTypeItemActivity> mockXactTypeItemActivityNotFoundFetchResponse;
    protected List<XactTypeItemActivity> mockXactTypeItemActivityFetchAllResponse;
    protected List<XactTypeItemActivity> mockXactTypeItemActivityFetchSingleResponse;

    protected List<VwXactTypeItemActivity> mockVwXactTypeItemActivityNotFoundFetchResponse;
    protected List<VwXactTypeItemActivity> mockVwXactTypeItemActivityFetchAllResponse;
    protected List<VwXactTypeItemActivity> mockVwXactTypeItemActivityFetchSingleResponse;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.mockCategoryFetchAllResponse = this
                .createMockXactCategoryFetchAllsponse();
        this.mockCategoryFetchSingleResponse = this
                .createMockXactCategorySingleFetchResponse();
        this.mockCategoryNotFoundFetchResponse = this
                .createMockXactCategoryNotFoundResponse();

        this.mockCodeFetchAllResponse = this.createMockXactCodeFetchAllsponse();
        this.mockCodeFetchSingleResponse = this
                .createMockXactCodeSingleFetchResponse();
        this.mockCodeNotFoundFetchResponse = this
                .createMockXactCodeNotFoundResponse();

        this.mockCodeGroupFetchAllResponse = this
                .createMockXactCodeGroupFetchAllsponse();
        this.mockCodeGroupFetchSingleResponse = this
                .createMockXactCodeGroupSingleFetchResponse();
        this.mockCodeGroupNotFoundFetchResponse = this
                .createMockXactCodeGroupNotFoundResponse();

        this.mockXactTypeFetchAllResponse = this
                .createMockXactTypeFetchAllsponse();
        this.mockXactTypeFetchSingleResponse = this
                .createMockXactTypeSingleFetchResponse();
        this.mockXactTypeNotFoundFetchResponse = this
                .createMockXactTypeNotFoundResponse();

        this.mockXactFetchAllResponse = this.createMockXactFetchAllsponse();
        this.mockXactFetchSingleResponse = this
                .createMockXactSingleFetchResponse();
        this.mockXactNotFoundFetchResponse = this
                .createMockXactNotFoundResponse();

        this.mockXactFetchAllGenericResponse = this.createMockXactFetchAllGenericResponse();

        this.mockXactTypeItemFetchAllResponse = this
                .createMockXactTypeItemFetchAllsponse();
        this.mockXactTypeItemFetchSingleResponse = this
                .createMockXactTypeItemSingleFetchResponse();
        this.mockXactTypeItemNotFoundFetchResponse = this
                .createMockXactTypeItemNotFoundResponse();

        this.mockXactTypeItemActivityFetchAllResponse = this
                .createMockXactTypeItemActivityFetchAllsponse();
        this.mockXactTypeItemActivityFetchSingleResponse = this
                .createMockXactTypeItemActivitySingleFetchResponse();
        this.mockXactTypeItemActivityNotFoundFetchResponse = this
                .createMockXactTypeItemActivityNotFoundResponse();

        this.mockVwXactTypeItemActivityFetchAllResponse = this
                .createMockVwXactTypeItemActivityFetchAllsponse();
        this.mockVwXactTypeItemActivityFetchSingleResponse = this
                .createMockVwXactTypeItemActivitySingleFetchResponse();
        this.mockVwXactTypeItemActivityNotFoundFetchResponse = this
                .createMockVwXactTypeItemActivityNotFoundResponse();

        return;
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        super.tearDown();
        return;
    }

    private List<VwXactTypeItemActivity> createMockVwXactTypeItemActivityNotFoundResponse() {
        List<VwXactTypeItemActivity> list = null;
        return list;
    }

    private List<VwXactTypeItemActivity> createMockVwXactTypeItemActivityFetchAllsponse() {
        List<VwXactTypeItemActivity> list = new ArrayList<VwXactTypeItemActivity>();
        VwXactTypeItemActivity o = AccountingMockDataFactory
                .createMockOrmVwXactTypeItemActivity(7000, 1111111, 20.00,
                        "2017-01-01", 19.00, 100, 1000, "XT-0", 200, "XC-0");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmVwXactTypeItemActivity(7001, 1111111, 21.00,
                "2017-01-01", 20.00, 101, 1001, "XT-1", 201, "XC-1");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmVwXactTypeItemActivity(7002, 1111111, 22.00,
                "2017-01-01", 21.00, 102, 1002, "XT-2", 202, "XC-2");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmVwXactTypeItemActivity(7003, 1111111, 23.00,
                "2017-01-01", 22.00, 103, 1003, "XT-3", 203, "XC-3");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmVwXactTypeItemActivity(7004, 1111111, 24.00,
                "2017-01-01", 23.00, 104, 1004, "XT-4", 204, "XC-4");
        list.add(o);
        return list;
    }

    private List<VwXactTypeItemActivity> createMockVwXactTypeItemActivitySingleFetchResponse() {
        List<VwXactTypeItemActivity> list = new ArrayList<VwXactTypeItemActivity>();
        VwXactTypeItemActivity o = AccountingMockDataFactory
                .createMockOrmVwXactTypeItemActivity(7001, 111111, 20.00,
                        "2017-01-01", 19.00, 100, 1000, "XT-1", 200, "XC-1");
        list.add(o);
        return list;
    }

    private List<XactTypeItemActivity> createMockXactTypeItemActivityNotFoundResponse() {
        List<XactTypeItemActivity> list = null;
        return list;
    }

    private List<XactTypeItemActivity> createMockXactTypeItemActivityFetchAllsponse() {
        List<XactTypeItemActivity> list = new ArrayList<XactTypeItemActivity>();
        XactTypeItemActivity o = AccountingMockDataFactory
                .createMockOrmXactTypeItemActivity(7001, 111111, 601, 31.11,
                        "Item1");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(7002,
                111111, 602, 20.00, "Item2");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(7003,
                111111, 603, 20.00, "Item3");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(7004,
                111111, 604, 20.00, "Item4");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactTypeItemActivity(7005,
                111111, 605, 20.00, "Item5");
        list.add(o);
        return list;
    }

    private List<XactTypeItemActivity> createMockXactTypeItemActivitySingleFetchResponse() {
        List<XactTypeItemActivity> list = new ArrayList<XactTypeItemActivity>();
        XactTypeItemActivity o = AccountingMockDataFactory
                .createMockOrmXactTypeItemActivity(7001, 111111, 601, 1.11,
                        "Item 1");
        list.add(o);
        return list;
    }

    private List<XactTypeItem> createMockXactTypeItemNotFoundResponse() {
        List<XactTypeItem> list = null;
        return list;
    }

    private List<XactTypeItem> createMockXactTypeItemFetchAllsponse() {
        List<XactTypeItem> list = new ArrayList<XactTypeItem>();
        XactTypeItem o = AccountingMockDataFactory
                .createMockOrmXactTypeItem(601, 301, "TransactionTypeItem1");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactTypeItem(602, 301,
                "TransactionTypeItem2");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactTypeItem(603, 301,
                "TransactionTypeItem3");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactTypeItem(604, 301,
                "TransactionTypeItem4");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactTypeItem(605, 301,
                "TransactionTypeItem5");
        list.add(o);
        return list;
    }

    private List<XactTypeItem> createMockXactTypeItemSingleFetchResponse() {
        List<XactTypeItem> list = new ArrayList<XactTypeItem>();
        XactTypeItem o = AccountingMockDataFactory
                .createMockOrmXactTypeItem(601, 301, "TransactionTypeItem1");
        list.add(o);
        return list;
    }

    private List<VwXactList> createMockXactNotFoundResponse() {
        List<VwXactList> list = null;
        return list;
    }

    private List<VwXactList> createMockXactFetchAllsponse() {
        List<VwXactList> list = new ArrayList<VwXactList>();
        VwXactList o = AccountingMockDataFactory.createMockOrmXact(111111, XactConst.XACT_TYPE_CREDITOR_PURCHASE,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED, RMT2Date.stringToDate("2017-01-13"), 100.00, 200, "1111-1111-1111-1111");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXact(222222, XactConst.XACT_TYPE_CREDITOR_PURCHASE, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-14"), 101.00, 200,
                "2222-2222-2222-2222");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXact(333333, XactConst.XACT_TYPE_CREDITOR_PURCHASE, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-15"), 102.00, 200, "3333-3333-3333-3333");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXact(444444, XactConst.XACT_TYPE_CREDITOR_PURCHASE, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-16"), 103.00, 200, "4444-4444-4444-4444");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXact(555555, XactConst.XACT_TYPE_CREDITOR_PURCHASE, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-17"), 104.00, 200,
                "5555-5555-5555-5555");
        list.add(o);
        return list;
    }

    private List<VwGenericXactList> createMockXactFetchAllGenericResponse() {
        List<VwGenericXactList> list = new ArrayList<>();
        VwGenericXactList o = AccountingMockDataFactory.createMockOrmGenericXact(111111, XactConst.XACT_TYPE_CREDITOR_PURCHASE,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED, RMT2Date.stringToDate("2020-01-01"), 100.00, "Purchase Expense on Account",
                200, "XYZ Company0", "1111-1111-1111-1111", "R-20034-9382", 1351, 300,
                RMT2Date.stringToDate("2020-01-01"), 1000, RMT2Date.stringToDate("2020-01-01"));
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmGenericXact(111111, XactConst.XACT_TYPE_CASH_DISBURSE,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED, RMT2Date.stringToDate("2020-01-01"), 200.00, "Cash Disbursement",
                200, "XYZ Company1", null, null, 0, 0, null, 0, null);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmGenericXact(111111, XactConst.XACT_TYPE_CASH_DISBURSE,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED, RMT2Date.stringToDate("2020-01-01"), 300.00, "Cash Disbursement",
                200, "XYZ Company2", null, null, 0, 0, null, 0, null);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmGenericXact(111111, XactConst.XACT_TYPE_CREDITOR_PURCHASE,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED, RMT2Date.stringToDate("2020-01-01"), 400.00, "Purchase Expense on Account",
                200, "XYZ Company3", "1111-1111-1111-1111", "R-20034-9382", 1351, 300,
                RMT2Date.stringToDate("2020-01-01"), 1000, RMT2Date.stringToDate("2020-01-01"));
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmGenericXact(111111, XactConst.XACT_TYPE_CREDITOR_PURCHASE,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED, RMT2Date.stringToDate("2020-01-01"), 500.00, "Purchase Expense on Account",
                200, "XYZ Company4", "1111-1111-1111-1111", "R-20034-9382", 1351, 300,
                RMT2Date.stringToDate("2020-01-01"), 1000, RMT2Date.stringToDate("2020-01-01"));
        list.add(o);
        return list;
    }


    protected List<VwXactList> createMockXactSingleFetchResponse() {
        List<VwXactList> list = new ArrayList<VwXactList>();
        VwXactList o = AccountingMockDataFactory.createMockOrmXact(111111,
                XactConst.XACT_TYPE_CREDITOR_PURCHASE,
                XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
                RMT2Date.stringToDate("2017-01-13"), 111.11, 200, "1111-1111-1111-1111");
        list.add(o);
        return list;
    }

    private List<XactType> createMockXactTypeNotFoundResponse() {
        List<XactType> list = null;
        return list;
    }

    private List<XactType> createMockXactTypeFetchAllsponse() {
        List<XactType> list = new ArrayList<XactType>();
        XactType o = AccountingMockDataFactory.createMockOrmXactType(300, 1000,
                "TransactionType1", "TransactionTypeCode1", 1, -1, 400, 401,
                200, 222, 1);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactType(301, 1000,
                "TransactionType2", "TransactionTypeCode2", 1, -1, 400, 401,
                200, 222, 1);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactType(302, 1000,
                "TransactionType3", "TransactionTypeCode3", 1, -1, 400, 401,
                200, 222, 1);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactType(303, 1000,
                "TransactionType4", "TransactionTypeCode4", 1, -1, 400, 401,
                200, 222, 1);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactType(304, 1000,
                "TransactionType5", "TransactionTypeCode5", 1, -1, 400, 401,
                200, 222, 1);
        list.add(o);
        return list;
    }

    private List<XactType> createMockXactTypeSingleFetchResponse() {
        List<XactType> list = new ArrayList<XactType>();
        XactType o = AccountingMockDataFactory.createMockOrmXactType(301, 1000,
                "Transaction Type 1", "transaction type code 1", 1, -1, 400,
                401, 200, 222, 1);
        list.add(o);
        return list;
    }

    private List<XactCodeGroup> createMockXactCodeGroupNotFoundResponse() {
        List<XactCodeGroup> list = null;
        return list;
    }

    private List<XactCodeGroup> createMockXactCodeGroupFetchAllsponse() {
        List<XactCodeGroup> list = new ArrayList<XactCodeGroup>();
        XactCodeGroup o = AccountingMockDataFactory
                .createMockOrmXactCodeGroup(10, "Code Group 10");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactCodeGroup(11,
                "Code Group 11");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactCodeGroup(12,
                "Code Group 12");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactCodeGroup(13,
                "Code Group 13");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactCodeGroup(14,
                "Code Group 14");
        list.add(o);
        return list;
    }

    private List<XactCodeGroup> createMockXactCodeGroupSingleFetchResponse() {
        List<XactCodeGroup> list = new ArrayList<XactCodeGroup>();
        XactCodeGroup o = AccountingMockDataFactory
                .createMockOrmXactCodeGroup(10, "Code Group 10");
        list.add(o);
        return list;
    }

    private List<XactCategory> createMockXactCategoryNotFoundResponse() {
        List<XactCategory> list = null;
        return list;
    }

    private List<XactCategory> createMockXactCategoryFetchAllsponse() {
        List<XactCategory> list = new ArrayList<XactCategory>();
        XactCategory o = AccountingMockDataFactory.createMockOrmXactCategory(
                1000, "transaction category 1", "catg code 1");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactCategory(1001,
                "transaction category 2", "catg code 2");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactCategory(1002,
                "transaction category 3", "catg code 3");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactCategory(1003,
                "transaction category 4", "catg code 4");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactCategory(1004,
                "transaction category 5", "catg code 5");
        list.add(o);
        return list;
    }

    private List<XactCategory> createMockXactCategorySingleFetchResponse() {
        List<XactCategory> list = new ArrayList<XactCategory>();
        XactCategory o = AccountingMockDataFactory.createMockOrmXactCategory(
                1000, "transaction category 1", "catg code 1");
        list.add(o);
        return list;
    }

    private List<XactCodes> createMockXactCodeNotFoundResponse() {
        List<XactCodes> list = null;
        return list;
    }

    private List<XactCodes> createMockXactCodeFetchAllsponse() {
        List<XactCodes> list = new ArrayList<XactCodes>();
        XactCodes o = AccountingMockDataFactory.createMockOrmXactCode(200, 10,
                "Transaction code 1");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactCode(201, 10,
                "Transaction code 2");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactCode(202, 10,
                "Transaction code 3");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactCode(203, 10,
                "Transaction code 4");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmXactCode(204, 10,
                "Transaction code 5");
        list.add(o);
        return list;
    }

    private List<XactCodes> createMockXactCodeSingleFetchResponse() {
        List<XactCodes> list = new ArrayList<XactCodes>();
        XactCodes o = AccountingMockDataFactory.createMockOrmXactCode(200, 10,
                "Transaction code 1");
        list.add(o);
        return list;
    }
}
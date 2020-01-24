package org.rmt2.api.handler.transaction.sales;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.SalesOrder;
import org.dao.mapping.orm.rmt2.SalesOrderItems;
import org.dto.SalesOrderDto;
import org.dto.SalesOrderItemDto;
import org.dto.adapter.orm.transaction.sales.Rmt2SalesOrderDtoFactory;
import org.rmt2.api.AccountingMockDataFactory;
import org.rmt2.api.handler.subsidiary.SubsidiaryMockData;

/**
 * @author rterrell
 *
 */
public class SalesOrderMockData extends SubsidiaryMockData {

    public static final int NEW_XACT_ID = 1234567;
    public static final int NEW_INVOICE_ID = 3786;
    public static final int NEW_SALES_ORDER_ID = 1000;
    public static final int EXISTING_XACT_ID = 7777;
    public static final int CUSTOMER_ID = 3333;
    public static final int TOTAL_SALES_ORDERS_CLOSED = 1;

    /**
     * 
     * @return
     */
    public static final List<SalesOrderDto> createMockSalesOrders() {
        List<SalesOrderDto> list = new ArrayList<SalesOrderDto>();
        SalesOrder o = AccountingMockDataFactory.createMockOrmSalesOrder(1000, 3333, 0, 300.00, "2017-01-01");
        SalesOrderDto dto = Rmt2SalesOrderDtoFactory.createSalesOrderInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmSalesOrder(1001, 3333, 0, 600.00, "2017-02-01");
        dto = Rmt2SalesOrderDtoFactory.createSalesOrderInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmSalesOrder(1002, 3333, 0, 900.00, "2017-03-01");
        dto = Rmt2SalesOrderDtoFactory.createSalesOrderInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmSalesOrder(1003, 3333, 0, 1200.00, "2017-04-01");
        dto = Rmt2SalesOrderDtoFactory.createSalesOrderInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmSalesOrder(1004, 3333, 0, 1500.00, "2017-05-01");
        dto = Rmt2SalesOrderDtoFactory.createSalesOrderInstance(o);
        list.add(dto);
        return list;
    }

    /**
     * 
     * @return
     */
    public static final List<SalesOrderDto> createMockSalesOrder() {
        List<SalesOrderDto> list = new ArrayList<SalesOrderDto>();
        SalesOrder o = AccountingMockDataFactory.createMockOrmSalesOrder(1000, 3333, 0, 100.00, "2017-01-01");
        SalesOrderDto dto = Rmt2SalesOrderDtoFactory.createSalesOrderInstance(o);
        list.add(dto);
        return list;
    }

    /**
     * 
     * @param soId
     * @return
     */
    public static final List<SalesOrderItemDto> createMockSalesOrderItems(int soId) {
        List<SalesOrderItemDto> list = new ArrayList<>();
        SalesOrderItems o = AccountingMockDataFactory.createMockOrmSalesOrderItem(88880, 33330, soId, 1, 20.00);
        SalesOrderItemDto dto = Rmt2SalesOrderDtoFactory.createSalesOrderItemInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmSalesOrderItem(88881, 33331, soId, 2, 5.00);
        dto = Rmt2SalesOrderDtoFactory.createSalesOrderItemInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmSalesOrderItem(88882, 33332, soId, 10, 2.00);
        dto = Rmt2SalesOrderDtoFactory.createSalesOrderItemInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmSalesOrderItem(88883, 33333, soId, 2, 15.00);
        dto = Rmt2SalesOrderDtoFactory.createSalesOrderItemInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmSalesOrderItem(88884, 33334, soId, 20, 1.00);
        dto = Rmt2SalesOrderDtoFactory.createSalesOrderItemInstance(o);
        list.add(dto);
        return list;
    }

    // /**
    // *
    // * @return
    // */
    // public static final List<XactDto> createMockTransactions() {
    // List<XactDto> list = new ArrayList<XactDto>();
    // VwXactList o = AccountingMockDataFactory.createMockOrmXact(111110,
    // XactConst.XACT_TYPE_CASHRECEIPT, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
    // RMT2Date.stringToDate("2017-01-13"), 100.00, 11, "1111-1111-1111-1111");
    // XactDto d = Rmt2XactDtoFactory.createXactInstance(o);
    // list.add(d);
    //
    // o = AccountingMockDataFactory.createMockOrmXact(111111,
    // XactConst.XACT_TYPE_CASHRECEIPT, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
    // RMT2Date.stringToDate("2017-01-14"), 101.00, 11, "2222-2222-2222-2222");
    // d = Rmt2XactDtoFactory.createXactInstance(o);
    // list.add(d);
    //
    // o = AccountingMockDataFactory.createMockOrmXact(111112,
    // XactConst.XACT_TYPE_CASHRECEIPT, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
    // RMT2Date.stringToDate("2017-01-15"), 102.00, 11, "3333-3333-3333-3333");
    // d = Rmt2XactDtoFactory.createXactInstance(o);
    // list.add(d);
    //
    // o = AccountingMockDataFactory.createMockOrmXact(111113,
    // XactConst.XACT_TYPE_CASHRECEIPT, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
    // RMT2Date.stringToDate("2017-01-16"), 103.00, 11, "4444-4444-4444-4444");
    // d = Rmt2XactDtoFactory.createXactInstance(o);
    // list.add(d);
    //
    // o = AccountingMockDataFactory.createMockOrmXact(111114,
    // XactConst.XACT_TYPE_CASHRECEIPT, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
    // RMT2Date.stringToDate("2017-01-17"), 104.00, 11, "5555-5555-5555-5555");
    // d = Rmt2XactDtoFactory.createXactInstance(o);
    // list.add(d);
    // return list;
    // }
    //
    // /**
    // *
    // * @return
    // */
    // public static final List<XactDto> createMockSingleTransaction() {
    // List<XactDto> list = new ArrayList<XactDto>();
    // VwXactList o = AccountingMockDataFactory.createMockOrmXact(111111,
    // XactConst.XACT_TYPE_CASHRECEIPT, XactConst.XACT_SUBTYPE_NOT_ASSIGNED,
    // RMT2Date.stringToDate("2017-01-13"), 100.00, 11, "1111-1111-1111-1111");
    // XactDto d = Rmt2XactDtoFactory.createXactInstance(o);
    // list.add(d);
    // return list;
    // }

}

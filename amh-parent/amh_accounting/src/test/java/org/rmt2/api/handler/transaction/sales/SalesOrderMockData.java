package org.rmt2.api.handler.transaction.sales;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.SalesOrder;
import org.dao.mapping.orm.rmt2.SalesOrderItems;
import org.dao.mapping.orm.rmt2.VwSalesOrderInvoice;
import org.dto.SalesInvoiceDto;
import org.dto.SalesOrderDto;
import org.dto.SalesOrderItemDto;
import org.dto.adapter.orm.transaction.sales.Rmt2SalesOrderDtoFactory;
import org.modules.transaction.sales.SalesApiConst;
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

    /**
     * 
     * @return
     */
    public static final List<SalesInvoiceDto> createMockSalesInvoices() {
        List<SalesInvoiceDto> list = new ArrayList<>();
        VwSalesOrderInvoice o = AccountingMockDataFactory
                .createMockOrmVwSalesOrderInvoice(7000, 1000, "2017-01-01",
                        300.00, SalesApiConst.STATUS_CODE_INVOICED, "80000", 1,
                        "2017-01-10", 444440, CUSTOMER_ID, 1234, "111-111");
        SalesInvoiceDto dto = Rmt2SalesOrderDtoFactory.createSalesIvoiceInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmVwSalesOrderInvoice(7001,
                1001, "2017-02-01", 600.00, SalesApiConst.STATUS_CODE_INVOICED,
                "80001", 1, "2017-02-10", 444440, CUSTOMER_ID, 1234, "111-111");
        dto = Rmt2SalesOrderDtoFactory.createSalesIvoiceInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmVwSalesOrderInvoice(7002,
                1002, "2017-03-01", 900.00, SalesApiConst.STATUS_CODE_INVOICED,
                "80002", 1, "2017-03-10", 444440, CUSTOMER_ID, 1234, "111-111");
        dto = Rmt2SalesOrderDtoFactory.createSalesIvoiceInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmVwSalesOrderInvoice(7003,
                1003, "2017-04-01", 1200.00, SalesApiConst.STATUS_CODE_INVOICED,
                "80003", 1, "2017-04-10", 444440, CUSTOMER_ID, 1234, "111-111");
        dto = Rmt2SalesOrderDtoFactory.createSalesIvoiceInstance(o);
        list.add(dto);

        o = AccountingMockDataFactory.createMockOrmVwSalesOrderInvoice(7004,
                1004, "2017-05-01", 1500.00, SalesApiConst.STATUS_CODE_INVOICED,
                "80004", 1, "2017-05-10", 444440, CUSTOMER_ID, 1234, "111-111");
        dto = Rmt2SalesOrderDtoFactory.createSalesIvoiceInstance(o);
        list.add(dto);
        return list;
    }

}

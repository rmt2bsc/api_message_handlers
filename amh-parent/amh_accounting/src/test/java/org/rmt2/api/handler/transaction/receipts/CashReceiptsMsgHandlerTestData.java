package org.rmt2.api.handler.transaction.receipts;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.Customer;
import org.dao.mapping.orm.rmt2.ItemMaster;
import org.dao.mapping.orm.rmt2.ItemMasterType;
import org.dao.mapping.orm.rmt2.SalesInvoice;
import org.dao.mapping.orm.rmt2.SalesOrder;
import org.dao.mapping.orm.rmt2.SalesOrderItems;
import org.dao.mapping.orm.rmt2.SalesOrderStatus;
import org.dao.mapping.orm.rmt2.SalesOrderStatusHist;
import org.dao.mapping.orm.rmt2.VwSalesOrderInvoice;
import org.dao.mapping.orm.rmt2.VwSalesorderItemsBySalesorder;
import org.junit.Before;
import org.modules.inventory.InventoryConst;
import org.modules.transaction.sales.SalesApiConst;
import org.rmt2.api.AccountingMockDataFactory;
import org.rmt2.api.handler.transaction.TransactionMsgHandlerTestData;

public class CashReceiptsMsgHandlerTestData extends TransactionMsgHandlerTestData {

    protected List<SalesOrder> mockSalesOrderNotFoundResponse;
    protected List<SalesOrder> mockSalesOrderAllResponse;
    protected List<SalesOrder> mockSalesOrderSingleResponse;
    protected List<SalesInvoice> mockSalesInvoiceNotFoundResponse;
    protected List<SalesInvoice> mockSalesInvoiceAllResponse;
    protected List<SalesInvoice> mockSalesInvoiceSingleResponse;
    protected List<SalesOrderItems> mockSalesOrderItemsNotFoundResponse;
    protected List<SalesOrderItems> mockSalesOrderItemsSingleResponse;
    protected List<SalesOrderItems> mockSalesOrderItemsAllResponse;
    protected List<VwSalesOrderInvoice> mockVwSalesOrderInvoiceNotFoundResponse;
    protected List<VwSalesOrderInvoice> mockVwSalesOrderInvoiceAllResponse;
    protected List<VwSalesOrderInvoice> mockVwSalesOrderInvoiceSingleResponse;
    protected List<VwSalesorderItemsBySalesorder> mockVwSalesorderItemsBySalesorderNotFoundResponse;
    protected List<VwSalesorderItemsBySalesorder> mockVwSalesorderItemsBySalesorderAllResponse;
    protected List<SalesOrderStatusHist> mockStatusHistoryNotFoundResponse;
    protected List<SalesOrderStatusHist> mockStatusHistoryAllResponse;
    protected List<SalesOrderStatus> mockStatusNotFoundResponse;
    protected List<SalesOrderStatus> mockStatusAllResponse;
    protected List<SalesOrderStatus> mockStatusSingleResponse;
    
    protected List<ItemMaster> mockSingleItemMasterFetchResponse;
    protected List<ItemMaster> mockAllItemMasterResponse;
    protected List<ItemMaster> mockNotFoundItemMasterResponse;
    
    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.mockSalesOrderNotFoundResponse = createMockSalesOrderNotFoundResponse();
        this.mockSalesOrderAllResponse = createMockSalesOrderAllsponse();
        this.mockSalesOrderSingleResponse = createMockSalesOrderSingleResponse();
        
        this.mockSalesInvoiceNotFoundResponse = createMockSalesInvoiceNotFoundResponse();
        this.mockSalesInvoiceAllResponse = createMockSalesInvoiceAllsponse();
        this.mockSalesInvoiceSingleResponse = createMockSalesInvoiceSingleResponse();

        this.mockSalesOrderItemsNotFoundResponse = createMockSalesOrderItemsNotFoundResponse();
        this.mockSalesOrderItemsSingleResponse = createMockSalesOrderItemsSingleResponse();
        this.mockSalesOrderItemsAllResponse = createMockSalesOrderItemsAllResponse();
        
        this.mockVwSalesorderItemsBySalesorderNotFoundResponse = createMockVwSalesorderItemsBySalesorderNotFoundsponse();
        this.mockVwSalesorderItemsBySalesorderAllResponse = createMockVwSalesorderItemsBySalesorderAllsponse();
        
        this.mockVwSalesOrderInvoiceNotFoundResponse = createMockVwSalesOrderInvoiceNotFoundResponse();
        this.mockVwSalesOrderInvoiceAllResponse = createMockVwSalesOrderInvoiceAllsponse();
        this.mockVwSalesOrderInvoiceSingleResponse = createMockVwSalesOrderInvoiceSingleResponse();

        this.mockStatusNotFoundResponse = createMockSalesOrderStatusNotFoundResponse();
        this.mockStatusAllResponse = createMockSalesOrderStatusAllResponse();
        this.mockStatusSingleResponse = createMockSalesOrderStatusSingleResponse();
        
        this.mockStatusHistoryAllResponse = createMockSalesOrderStatusHistoryAllResponse();
        this.mockStatusHistoryNotFoundResponse = createMockSalesOrderStatusHistoryNotFoundResponse();
        
        this.mockSingleItemMasterFetchResponse = createMockItemMasterSingleFetchResponse();
        this.mockAllItemMasterResponse = createMockItemMasterFetchAllResponse();
        this.mockNotFoundItemMasterResponse = createMockItemMasterNotFoundResponse();
        return;
    }

    public static final List<SalesOrder> createMockSalesOrderNotFoundResponse() {
        List<SalesOrder> list = null;
        return list;
    }

    public static final List<SalesOrder> createMockSalesOrderAllsponse() {
        List<SalesOrder> list = new ArrayList<SalesOrder>();
        SalesOrder o = AccountingMockDataFactory.createMockOrmSalesOrder(1000,
                2000, 0, 300.00, "2017-01-01");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrder(1001, 2000, 0,
                600.00, "2017-02-01");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrder(1002, 2000, 0,
                900.00, "2017-03-01");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrder(1003, 2000, 0,
                1200.00, "2017-04-01");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrder(1004, 2000, 0,
                1500.00, "2017-05-01");
        list.add(o);
        return list;
    }

    public static final List<SalesOrder> createMockSalesOrderSingleResponse() {
        List<SalesOrder> list = new ArrayList<SalesOrder>();
        SalesOrder o = AccountingMockDataFactory.createMockOrmSalesOrder(1000,
                2000, 0, 100.00, "2017-01-01");
        list.add(o);
        return list;
    }

    public static final List<SalesInvoice> createMockSalesInvoiceNotFoundResponse() {
        List<SalesInvoice> list = null;
        return list;
    }

    public static final List<SalesInvoice> createMockSalesInvoiceAllsponse() {
        List<SalesInvoice> list = new ArrayList<SalesInvoice>();
        SalesInvoice o = AccountingMockDataFactory
                .createMockOrmSalesInvoice(7000, 1000, 5000, "80000");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesInvoice(7001, 1001,
                5001, "80001");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesInvoice(7002, 1002,
                5002, "80002");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesInvoice(7003, 1003,
                5003, "80003");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesInvoice(7004, 1004,
                5004, "80004");
        list.add(o);
        return list;
    }

    public static final List<SalesInvoice> createMockSalesInvoiceSingleResponse() {
        List<SalesInvoice> list = new ArrayList<SalesInvoice>();
        SalesInvoice o = AccountingMockDataFactory
                .createMockOrmSalesInvoice(7000, 1000, 5000, "80000");
        list.add(o);
        return list;
    }

    public static final List<SalesOrderItems> createMockSalesOrderItemsNotFoundResponse() {
        List<SalesOrderItems> list = null;
        return list;
    }

    public static final List<SalesOrderItems> createMockSalesOrderItemsSingleResponse() {
        List<SalesOrderItems> list = new ArrayList<SalesOrderItems>();
        SalesOrderItems o = AccountingMockDataFactory
                .createMockOrmSalesOrderItem(88880, 33330, 1000, 1, 20.00);
        list.add(o);
        return list;
    }
    
    public static final List<SalesOrderItems> createMockSalesOrderItemsAllResponse() {
        List<SalesOrderItems> list = new ArrayList<SalesOrderItems>();
        SalesOrderItems o = AccountingMockDataFactory
                .createMockOrmSalesOrderItem(88880, 33330, 1000, 1, 20.00);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrderItem(88881, 33331,
                1000, 2, 5.00);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrderItem(88882, 33332,
                1000, 10, 5.00);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrderItem(88883, 33333,
                1000, 2, 15.00);
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrderItem(88884, 33334,
                1000, 20, 5.00);
        list.add(o);
        return list;
    }

    public static final List<VwSalesorderItemsBySalesorder> createMockVwSalesorderItemsBySalesorderNotFoundsponse() {
        List<VwSalesorderItemsBySalesorder> list = null;
        return list;
    }
    
    public static final List<VwSalesorderItemsBySalesorder> createMockVwSalesorderItemsBySalesorderAllsponse() {
        List<VwSalesorderItemsBySalesorder> list = new ArrayList<VwSalesorderItemsBySalesorder>();

        Customer cust = AccountingMockDataFactory.createMockOrmCustomer(2000,
                1351, 0, 333, "C1234580", "Customer 1");
        SalesOrder so = AccountingMockDataFactory.createMockOrmSalesOrder(1000,
                2000, 0, 300.00, "2017-01-01");
        ItemMasterType imt = AccountingMockDataFactory
                .createMockOrmItemMasterType(InventoryConst.ITEM_TYPE_MERCH,
                        "ItemTypeMerchandise");
        SalesOrderItems soi = AccountingMockDataFactory
                .createMockOrmSalesOrderItem(88880, 33330, 1000, 1, 20.00);
        ItemMaster im = AccountingMockDataFactory.createMockOrmItemMaster(
                33330, InventoryConst.ITEM_TYPE_MERCH, "1111-111-110",
                "11111110", 77770, "Item1", 10, 20.00, true);

        VwSalesorderItemsBySalesorder o = AccountingMockDataFactory
                .createMockOrmVwSalesorderItemsBySalesorder(soi, so, cust, im, imt);
        list.add(o);

        soi = AccountingMockDataFactory.createMockOrmSalesOrderItem(88881,
                33331, 1000, 1, 20.00);
        im = AccountingMockDataFactory.createMockOrmItemMaster(33331,
                InventoryConst.ITEM_TYPE_MERCH, "1111-111-111", "11111111",
                77770, "Item2", 10, 20.00, true);
        o = AccountingMockDataFactory
                .createMockOrmVwSalesorderItemsBySalesorder(soi, so, cust, im, imt);
        list.add(o);

        soi = AccountingMockDataFactory.createMockOrmSalesOrderItem(88882,
                33332, 1000, 1, 20.00);
        im = AccountingMockDataFactory.createMockOrmItemMaster(33332,
                InventoryConst.ITEM_TYPE_MERCH, "1111-111-112", "11111112",
                77770, "Item3", 10, 20.00, true);
        o = AccountingMockDataFactory
                .createMockOrmVwSalesorderItemsBySalesorder(soi, so, cust, im, imt);
        list.add(o);

        soi = AccountingMockDataFactory.createMockOrmSalesOrderItem(88883,
                33333, 1000, 1, 20.00);
        im = AccountingMockDataFactory.createMockOrmItemMaster(33333,
                InventoryConst.ITEM_TYPE_MERCH, "1111-111-113", "11111113",
                77770, "Item4", 10, 20.00, true);
        o = AccountingMockDataFactory
                .createMockOrmVwSalesorderItemsBySalesorder(soi, so, cust, im, imt);
        list.add(o);

        soi = AccountingMockDataFactory.createMockOrmSalesOrderItem(88884,
                33334, 1000, 1, 20.00);
        im = AccountingMockDataFactory.createMockOrmItemMaster(33334,
                InventoryConst.ITEM_TYPE_MERCH, "1111-111-114", "11111114",
                77770, "Item5", 10, 20.00, true);
        o = AccountingMockDataFactory
                .createMockOrmVwSalesorderItemsBySalesorder(soi, so, cust, im, imt);
        list.add(o);
        return list;
    }
    
    
    public static final List<VwSalesOrderInvoice> createMockVwSalesOrderInvoiceNotFoundResponse() {
        List<VwSalesOrderInvoice> list = null;
        return list;
    }

    public static final List<VwSalesOrderInvoice> createMockVwSalesOrderInvoiceAllsponse() {
        List<VwSalesOrderInvoice> list = new ArrayList<VwSalesOrderInvoice>();
        VwSalesOrderInvoice o = AccountingMockDataFactory
                .createMockOrmVwSalesOrderInvoice(7000, 1000, "2017-01-01",
                        300.00, SalesApiConst.STATUS_CODE_INVOICED, "80000", 1,
                        "2017-01-10", 444440, 2000, 1234, "111-111");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmVwSalesOrderInvoice(7001,
                1001, "2017-02-01", 600.00, SalesApiConst.STATUS_CODE_INVOICED,
                "80001", 1, "2017-02-10", 444440, 2000, 1234, "111-111");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmVwSalesOrderInvoice(7002,
                1002, "2017-03-01", 900.00, SalesApiConst.STATUS_CODE_INVOICED,
                "80002", 1, "2017-03-10", 444440, 2000, 1234, "111-111");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmVwSalesOrderInvoice(7003,
                1003, "2017-04-01", 1200.00, SalesApiConst.STATUS_CODE_INVOICED,
                "80003", 1, "2017-04-10", 444440, 2000, 1234, "111-111");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmVwSalesOrderInvoice(7004,
                1004, "2017-05-01", 1500.00, SalesApiConst.STATUS_CODE_INVOICED,
                "80004", 1, "2017-05-10", 444440, 2000, 1234, "111-111");
        list.add(o);
        return list;
    }

    public static final List<VwSalesOrderInvoice> createMockVwSalesOrderInvoiceSingleResponse() {
        List<VwSalesOrderInvoice> list = new ArrayList<VwSalesOrderInvoice>();
        VwSalesOrderInvoice o = AccountingMockDataFactory
                .createMockOrmVwSalesOrderInvoice(7000, 1000, "2017-01-01",
                        300.00, SalesApiConst.STATUS_CODE_INVOICED, "80000", 1,
                        "2017-01-10", 444440, 2000, 1234, "111-111");
        list.add(o);
        return list;
    }
    
    
    
    public static final List<SalesOrderStatus> createMockSalesOrderStatusNotFoundResponse() {
        List<SalesOrderStatus> list = null;
        return list;
    }

    public static final List<SalesOrderStatus> createMockSalesOrderStatusSingleResponse() {
        List<SalesOrderStatus> list = new ArrayList<SalesOrderStatus>();
        SalesOrderStatus o = AccountingMockDataFactory.createMockOrmSalesOrderStatus(
                SalesApiConst.STATUS_CODE_QUOTE, "Quote");
        list.add(o);
        return list;
    }
    
    /**
     * 
     * @param statusId
     * @param description
     * @return
     */
    public static final List<SalesOrderStatus> createMockSingleSalesOrderStatus(
            int statusId, String description) {
        List<SalesOrderStatus> list = new ArrayList<>();
        SalesOrderStatus o = AccountingMockDataFactory
                .createMockOrmSalesOrderStatus(statusId, description);
        list.add(o);
        return list;
    }
    
    
    public static final List<SalesOrderStatus> createMockSalesOrderStatusAllResponse() {
        List<SalesOrderStatus> list = new ArrayList<SalesOrderStatus>();
        SalesOrderStatus o = AccountingMockDataFactory
                .createMockOrmSalesOrderStatus(SalesApiConst.STATUS_CODE_NEW, "New");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrderStatus(
                SalesApiConst.STATUS_CODE_QUOTE, "Quote");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrderStatus(
                SalesApiConst.STATUS_CODE_INVOICED, "Invoiced");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrderStatus(
                SalesApiConst.STATUS_CODE_CANCELLED, "Canclelled");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrderStatus(
                SalesApiConst.STATUS_CODE_REFUNDED, "Refunded");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrderStatus(
                SalesApiConst.STATUS_CODE_CLOSED, "Closed");
        list.add(o);
        return list;
    }
    
    public static final List<SalesOrderStatusHist> createMockSalesOrderStatusHistoryNotFoundResponse() {
        List<SalesOrderStatusHist> list = null;
        return list;
    }
    
    public static final List<SalesOrderStatusHist> createMockSalesOrderStatusHistoryAllResponse() {
        List<SalesOrderStatusHist> list = new ArrayList<SalesOrderStatusHist>();
        SalesOrderStatusHist o = AccountingMockDataFactory
                .createMockOrmSalesOrderStatusHist(70, 1000,
                        SalesApiConst.STATUS_CODE_NEW, "2017-01-10",
                        "2017-01-31");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrderStatusHist(71,
                1000, SalesApiConst.STATUS_CODE_QUOTE, "2017-02-10",
                "2017-02-28");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrderStatusHist(72,
                1000, SalesApiConst.STATUS_CODE_INVOICED, "2017-03-10",
                "2017-03-31");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrderStatusHist(73,
                1000, SalesApiConst.STATUS_CODE_CANCELLED, "2017-04-10",
                "2017-04-30");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrderStatusHist(74,
                1000, SalesApiConst.STATUS_CODE_REFUNDED, "2017-05-10",
                "2017-05-31");
        list.add(o);

        o = AccountingMockDataFactory.createMockOrmSalesOrderStatusHist(75,
                1000, SalesApiConst.STATUS_CODE_CLOSED, "2017-06-10",
                "2017-06-30");
        list.add(o);
        return list;
    }
    
    
    public static final SalesOrderStatusHist createMockSalesOrderStatusHistoryInvoicedResponse() {
        SalesOrderStatusHist o = AccountingMockDataFactory.createMockOrmSalesOrderStatusHist(72,
                1000, SalesApiConst.STATUS_CODE_INVOICED, "2017-03-10",
                null);
        return o;
    }
    
    
    public static final List<ItemMaster> createMockItemMasterNotFoundResponse() {
        List<ItemMaster> list = null;
        return list;
    }
    
    public static final List<ItemMaster> createMockItemMasterSingleFetchResponse() {
        List<ItemMaster> list = new ArrayList<ItemMaster>();
        ItemMaster p = AccountingMockDataFactory.createMockOrmItemMaster(33330, 1,
                "111-111-111", "11111111", 1234, "Item1", 2, 1.23, true);
        list.add(p);
        return list;
    }
    
    public static final List<ItemMaster> createMockItemMasterFetchAllResponse() {
        List<ItemMaster> list = new ArrayList<ItemMaster>();
        ItemMaster p = AccountingMockDataFactory.createMockOrmItemMaster(33330, 1,
                "100-111-111", "11111110", 1351, "Item1", 1, 1.23, true);
        list.add(p);

        p = AccountingMockDataFactory.createMockOrmItemMaster(33331, 1,
                "101-111-111", "11111111", 1352, "Item2", 2, 1.23, true);
        list.add(p);

        p = AccountingMockDataFactory.createMockOrmItemMaster(33332, 1,
                "102-111-111", "11111112", 1353, "Item3", 3, 1.23, true);
        list.add(p);

        p = AccountingMockDataFactory.createMockOrmItemMaster(33333, 1,
                "103-111-111", "11111113", 1354, "Item4", 4, 1.23, true);
        list.add(p);

        p = AccountingMockDataFactory.createMockOrmItemMaster(33334, 1,
                "104-111-111", "11111114", 1355, "Item5", 5, 1.23, true);
        list.add(p);
        return list;
    }

}

package org.rmt2.api.handler.inventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dao.mapping.orm.rmt2.ItemMaster;
import org.dao.mapping.orm.rmt2.ItemMasterStatus;
import org.dao.mapping.orm.rmt2.ItemMasterStatusHist;
import org.dto.ItemMasterDto;
import org.dto.ItemMasterStatusDto;
import org.dto.ItemMasterStatusHistDto;
import org.dto.adapter.orm.inventory.Rmt2InventoryDtoFactory;

import com.SystemException;
import com.api.util.RMT2Date;

public class InventoryMockData {

    public InventoryMockData() {
    }
    
    /**
     * 
     * @param id
     * @param itemTypeId
     * @param serialNo
     * @param vendorItemNo
     * @param creditorId
     * @param description
     * @param qty
     * @param unitCost
     * @param reatilPrice
     * @param active
     * @return
     */
    public static final ItemMaster createMockOrmItemMaster(int id,
            int itemTypeId, String serialNo, String vendorItemNo,
            int creditorId, String description, int qty, double unitCost,
            boolean active) {
        ItemMaster i = new ItemMaster();
        i.setItemId(id);
        i.setItemTypeId(itemTypeId);
        i.setItemSerialNo(serialNo);
        i.setVendorItemNo(vendorItemNo);
        i.setCreditorId(creditorId);
        i.setDescription(description);
        i.setQtyOnHand(qty);
        i.setUnitCost(unitCost);
        i.setActive(active ? 1 : 0);
        i.setOverrideRetail(0);
        i.setMarkup(5);
        i.setRetailPrice((qty * unitCost) * i.getMarkup());
        return i;
    }
    
    public static final List<ItemMasterDto> createMockItemMasterList() {
        List<ItemMasterDto> list = new ArrayList<>();
        ItemMaster o = InventoryMockData.createMockOrmItemMaster(100, 1,
                "100-111-111", "11111110", 1351, "Item1", 1, 1.23, true);
        ItemMasterDto p = Rmt2InventoryDtoFactory.createItemMasterInstance(o);
        list.add(p);

        o = InventoryMockData.createMockOrmItemMaster(101, 1,
                "101-111-111", "11111111", 1352, "Item2", 2, 1.23, true);
        p = Rmt2InventoryDtoFactory.createItemMasterInstance(o);
        list.add(p);

        o = InventoryMockData.createMockOrmItemMaster(102, 1,
                "102-111-111", "11111112", 1353, "Item3", 3, 1.23, true);
        p = Rmt2InventoryDtoFactory.createItemMasterInstance(o);
        list.add(p);

        o = InventoryMockData.createMockOrmItemMaster(103, 1,
                "103-111-111", "11111113", 1354, "Item4", 4, 1.23, true);
        p = Rmt2InventoryDtoFactory.createItemMasterInstance(o);
        list.add(p);

        o = InventoryMockData.createMockOrmItemMaster(104, 1,
                "104-111-111", "11111114", 1355, "Item5", 5, 1.23, true);
        p = Rmt2InventoryDtoFactory.createItemMasterInstance(o);
        list.add(p);
        return list;
    }
    
    /**
     * 
     * @param id
     * @param itemId
     * @param statusId
     * @param unitCost
     * @param markup
     * @param effDate
     * @param endDate
     * @param reason
     * @return
     */
    public static final ItemMasterStatusHist createMockOrmItemMasterStatusHistory(
            int id, int itemId, int statusId, double unitCost, double markup,
            String effDate, String endDate, String reason) {
        ItemMasterStatusHist i = new ItemMasterStatusHist();
        i.setItemStatusHistId(id);
        i.setItemId(itemId);
        i.setItemStatusId(statusId);
        i.setUnitCost(unitCost);
        i.setMarkup(markup);
        try {
            i.setEffectiveDate(RMT2Date.stringToDate(effDate));
        } catch (SystemException e) {
            i.setEffectiveDate(new Date());
        }
        try {
            i.setEndDate(RMT2Date.stringToDate(endDate));
        } catch (SystemException e) {
            i.setEndDate(new Date());
        }
        i.setReason(reason);
        return i;
    }

    
    /**
     * 
     * @return
     */
    public static final List<ItemMasterStatusHistDto> createMockItemStatusHistoryList() {
        List<ItemMasterStatusHistDto> list = new ArrayList<ItemMasterStatusHistDto>();
        ItemMasterStatusHist o = InventoryMockData.createMockOrmItemMasterStatusHistory(10, 100, 1000, 12.50, 3,
                        "2017-01-01", "2017-03-01",
                        "Item Status History Description 1");
        ItemMasterStatusHistDto p = Rmt2InventoryDtoFactory.createItemStatusHistoryInstance(o);
        list.add(p);

        o = InventoryMockData.createMockOrmItemMasterStatusHistory(11,
                101, 1001, 13.50, 3, "2017-01-02", "2017-03-02",
                "Item Status History Description 2");
        p = Rmt2InventoryDtoFactory.createItemStatusHistoryInstance(o);
        list.add(p);

        o = InventoryMockData.createMockOrmItemMasterStatusHistory(12,
                102, 1002, 14.50, 3, "2017-01-03", "2017-03-03",
                "Item Status History Description 3");
        p = Rmt2InventoryDtoFactory.createItemStatusHistoryInstance(o);
        list.add(p);

        o = InventoryMockData.createMockOrmItemMasterStatusHistory(13,
                103, 1003, 15.50, 3, "2017-01-04", "2017-03-04",
                "Item Status History Description 4");
        p = Rmt2InventoryDtoFactory.createItemStatusHistoryInstance(o);
        list.add(p);

        o = InventoryMockData.createMockOrmItemMasterStatusHistory(14,
                104, 1004, 16.50, 3, "2017-01-05", "2017-03-05",
                "Item Status History Description 5");
        p = Rmt2InventoryDtoFactory.createItemStatusHistoryInstance(o);
        list.add(p);
        return list;
    }
    
    /**
     * 
     * @param id
     * @param description
     * @return
     */
    public static final ItemMasterStatus createMockOrmItemMasterStatus(int id, String description) {
        ItemMasterStatus i = new ItemMasterStatus();
        i.setItemStatusId(id);
        i.setDescription(description);
        return i;
    }

    /**
     * 
     * @return
     */
    public static final List<ItemMasterStatusDto> createMockItemStatus() {
        List<ItemMasterStatusDto> list = new ArrayList<>();
        ItemMasterStatus o = InventoryMockData.createMockOrmItemMasterStatus(100, "Item Status #1");
        ItemMasterStatusDto p = Rmt2InventoryDtoFactory.createItemStatusInstance(o);
        list.add(p);

        o = InventoryMockData.createMockOrmItemMasterStatus(101, "Item Status #2");
        p = Rmt2InventoryDtoFactory.createItemStatusInstance(o);
        list.add(p);

        o = InventoryMockData.createMockOrmItemMasterStatus(102, "Item Status #3");
        p = Rmt2InventoryDtoFactory.createItemStatusInstance(o);
        list.add(p);

        o = InventoryMockData.createMockOrmItemMasterStatus(103, "Item Status #4");
        p = Rmt2InventoryDtoFactory.createItemStatusInstance(o);
        list.add(p);

        o = InventoryMockData.createMockOrmItemMasterStatus(104, "Item Status #5");
        p = Rmt2InventoryDtoFactory.createItemStatusInstance(o);
        list.add(p);
        return list;
    }
}

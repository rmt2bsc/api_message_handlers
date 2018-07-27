package org.rmt2.api.handler.inventory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dao.mapping.orm.rmt2.ItemMasterStatusHist;
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
    
    
   
}

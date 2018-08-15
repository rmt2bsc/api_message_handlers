package org.rmt2.api.handlers.inventory;

import java.util.List;

import org.rmt2.jaxb.SimpleItemType;

public class InventoryUtility {

    public InventoryUtility() {
    }
    
    /**
     * 
     * @param items
     * @return
     */
    public static final Integer[] getItemIdList(List<SimpleItemType> items) {
        if (items == null) {
            return null;
        }
        Integer[] list = new Integer[items.size()];
        for (int ndx = 0; ndx < items.size(); ndx++) {
            list[ndx] = items.get(ndx).getItemId().intValue();
        }
        return list;
    }

}

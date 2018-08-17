package org.rmt2.api.handlers;

import java.util.List;

import org.rmt2.jaxb.SimpleItemType;

/**
 * Utility class for accounting API Message Handling 
 * 
 * @author roy.terrell
 *
 */
public class AccountingtMsgHandlerUtility {

    /**
     * Default constructor
     */
    public AccountingtMsgHandlerUtility() {
    }
    
    /**
     * Translates all inventory item id's contained in a list of SimpleItemType
     * JAXB objects to an Integer array
     * 
     * @param items
     *            an List of {@link SimpleItemType} instances
     * @return Integer[]
     */
    public static final Integer[] getInventoryItemIdArray(List<SimpleItemType> items) {
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

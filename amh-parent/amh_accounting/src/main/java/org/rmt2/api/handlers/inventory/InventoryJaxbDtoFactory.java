package org.rmt2.api.handlers.inventory;

import org.dao.mapping.orm.rmt2.ItemMaster;
import org.dto.ItemMasterDto;
import org.dto.ItemMasterStatusHistDto;
import org.dto.adapter.orm.inventory.Rmt2ItemMasterDtoFactory;
import org.rmt2.jaxb.InventoryItemType;
import org.rmt2.jaxb.InventoryStatusHistoryType;
import org.rmt2.jaxb.ItemCriteriaType;
import org.rmt2.jaxb.ItemStatusHistoryCriteriaType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.inventory.InventoryItemStatusHistTypeBuilder;
import org.rmt2.util.accounting.inventory.InventoryItemTypeBuilder;

import com.RMT2Base;

/**
 * A factory for converting inventory related JAXB objects to DTO and vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class InventoryJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>ItemMasterDto</i> using a valid
     * <i>ItemCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link ItemCriteriaType}
     * @return an instance of {@link ItemMasterDto}
     */
    public static final ItemMasterDto createItemMasterDtoCriteriaInstance(ItemCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        ItemMaster nullItemMaster = null;
        ItemMasterDto dto = Rmt2ItemMasterDtoFactory.createItemMasterInstance(nullItemMaster);
        if (jaxbCriteria.getItemId() != null) {
            dto.setItemId(jaxbCriteria.getItemId().intValue());    
        }
        if (jaxbCriteria.getItemName() != null) {
            dto.setItemName(jaxbCriteria.getItemName());    
        }
        if (jaxbCriteria.getItemSerialNo() != null) {
            dto.setItemSerialNo(jaxbCriteria.getItemSerialNo());    
        }
        if (jaxbCriteria.getItemType() != null) {
            if (jaxbCriteria.getItemType().getItemTypeId() != null) {
                dto.setItemTypeId(jaxbCriteria.getItemType().getItemTypeId().intValue());
            }
        }
        if (jaxbCriteria.getVendor() != null) {
            if (jaxbCriteria.getVendor().getCreditorId() != null) {
                dto.setVendorId(jaxbCriteria.getVendor().getCreditorId().intValue());
            }
        }
        if (jaxbCriteria.getMarkup() != null) {
            dto.setMarkup(jaxbCriteria.getMarkup().doubleValue());
        }
        if (jaxbCriteria.getUnitCost() != null) {
            dto.setUnitCost(jaxbCriteria.getUnitCost().doubleValue());
        }
        if (jaxbCriteria.getQtyOnHand() != null) {
            dto.setQtyOnHand(jaxbCriteria.getQtyOnHand().intValue());
        }
        if (jaxbCriteria.getVendorItemNo() != null) {
            dto.setVendorItemNo(jaxbCriteria.getVendorItemNo());
        }
        return dto;
    }
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final ItemMasterDto createItemMasterDtoInstance(InventoryItemType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ItemMaster nullItemMaster = null;
        ItemMasterDto dto = Rmt2ItemMasterDtoFactory.createItemMasterInstance(nullItemMaster);
        if (jaxbObj.getItemId() != null) {
            dto.setItemId(jaxbObj.getItemId().intValue());    
        }
        if (jaxbObj.getDescription() != null) {
            dto.setItemName(jaxbObj.getDescription());    
        }
        if (jaxbObj.getItemSerialNo() != null) {
            dto.setItemSerialNo(jaxbObj.getItemSerialNo());    
        }
        if (jaxbObj.getItemType() != null) {
            if (jaxbObj.getItemType().getItemTypeId() != null) {
                dto.setItemTypeId(jaxbObj.getItemType().getItemTypeId().intValue());
            }
        }
        if (jaxbObj.getCreditor() != null) {
            if (jaxbObj.getCreditor().getCreditorId() != null) {
                dto.setVendorId(jaxbObj.getCreditor().getCreditorId().intValue());
            }
        }
        if (jaxbObj.getMarkup() != null) {
            dto.setMarkup(jaxbObj.getMarkup().doubleValue());
        }
        if (jaxbObj.getUnitCost() != null) {
            dto.setUnitCost(jaxbObj.getUnitCost().doubleValue());
        }
        if (jaxbObj.getQtyOnHand() != null) {
            dto.setQtyOnHand(jaxbObj.getQtyOnHand().intValue());
        }
        if (jaxbObj.getVendorItemNo() != null) {
            dto.setVendorItemNo(jaxbObj.getVendorItemNo());
        }
        
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    public static final InventoryItemType createItemMasterJaxbInstance(ItemMasterDto dto) {
        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated()).build();
        
        InventoryItemType jaxbObj = InventoryItemTypeBuilder.Builder.create()
                .withItemId(dto.getItemId())
                .withCreditorId(dto.getVendorId())
                .withItemName(dto.getItemName())
                .withItemSerialNo(dto.getItemSerialNo())
                .withMarkup(dto.getMarkup())
                .withUnitCost(dto.getUnitCost())
                .withQtyOnHand(dto.getQtyOnHand())
                .withVendorItemNo(dto.getVendorItemNo())
                .withActive(dto.getActive() == 1 ? true : false)
                .withItemTypeId(dto.getItemTypeId())
                .withRecordTrackingType(rtt).build();
        return jaxbObj;
    }
    
    /**
     * Creates an instance of <i>ItemMasterStatusHistDto</i> using a valid
     * <i>ItemMasterStatusHistoryType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link ItemMasterStatusHistoryType}
     * @return an instance of {@link ItemMasterStatusHistDto}
     */
    public static final ItemMasterStatusHistDto createStatusHistDtoCriteriaInstance(ItemStatusHistoryCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        ItemMasterStatusHistDto dto = Rmt2ItemMasterDtoFactory.createItemStatusHistoryInstance(null);
        if (jaxbCriteria.getItemStatusHistId() != null) {
            dto.setEntityId(jaxbCriteria.getItemStatusHistId().intValue());    
        }
        if (jaxbCriteria.getItemStatusId() != null) {
            dto.setItemStatusId(jaxbCriteria.getItemStatusId().intValue());    
        }
        if (jaxbCriteria.getItemId() != null) {
            dto.setItemId(jaxbCriteria.getItemId().intValue());    
        }
        return dto;
    }
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final ItemMasterStatusHistDto createStatusHistDtoInstance(InventoryStatusHistoryType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ItemMasterStatusHistDto dto = Rmt2ItemMasterDtoFactory.createItemStatusHistoryInstance(null);
        if (jaxbObj.getStatusHistId() != null) {
            dto.setEntityId(jaxbObj.getStatusHistId().intValue());    
        }
        if (jaxbObj.getStatus() != null) {
            if (jaxbObj.getStatus().getItemStatusId() != null) {
                dto.setItemStatusId(jaxbObj.getStatus().getItemStatusId().intValue());    
            }
        }
        if (jaxbObj.getEffectiveDate() != null) {
            dto.setEffectiveDate(jaxbObj.getEffectiveDate().toGregorianCalendar().getTime());    
        }
        if (jaxbObj.getEndDate() != null) {
            dto.setEndDate(jaxbObj.getEndDate().toGregorianCalendar().getTime());    
        }
        if (jaxbObj.getItem() != null) {
            if (jaxbObj.getItem().getItemId() != null) {
                dto.setItemId(jaxbObj.getItem().getItemId().intValue());    
            }
            if (jaxbObj.getItem().getMarkup() != null) {
                dto.setMarkup(jaxbObj.getItem().getMarkup().doubleValue());
            }
            if (jaxbObj.getItem().getUnitCost() != null) {
                dto.setUnitCost(jaxbObj.getItem().getUnitCost().doubleValue());
            }
        }
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    public static final InventoryStatusHistoryType createStatusHistJaxbInstance(ItemMasterStatusHistDto dto) {
        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated()).build();
        
        InventoryStatusHistoryType jaxbObj = InventoryItemStatusHistTypeBuilder.Builder.create()
                .withStatusHistId(dto.getEntityId())
                .withItemId(dto.getItemId())
                .withItemStatusId(dto.getItemStatusId(), null)
                .withEffectiveDate(dto.getEffectiveDate())
                .withEndDate(dto.getEndDate())
                .withMarkup(dto.getMarkup())
                .withUnitCost(dto.getUnitCost())
                .withReason(dto.getReason())
                .withRecordTrackingType(rtt).build();
        return jaxbObj;
    }
}

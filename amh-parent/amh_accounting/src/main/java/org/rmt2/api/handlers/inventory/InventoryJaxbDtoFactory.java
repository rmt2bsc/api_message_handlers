package org.rmt2.api.handlers.inventory;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.dto.ItemMasterStatusHistDto;
import org.dto.adapter.orm.inventory.Rmt2ItemMasterDtoFactory;
import org.rmt2.jaxb.InventoryItemStatusType;
import org.rmt2.jaxb.InventoryItemType;
import org.rmt2.jaxb.InventoryStatusHistoryType;
import org.rmt2.jaxb.ItemStatusHistoryCriteriaType;
import org.rmt2.jaxb.ObjectFactory;

import com.RMT2Base;
import com.api.util.RMT2Date;

/**
 * A factory for converting inventory related JAXB objects to DTO and vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class InventoryJaxbDtoFactory extends RMT2Base {

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
        ObjectFactory jaxbObjFactory = new ObjectFactory();
        InventoryStatusHistoryType jaxbObj = jaxbObjFactory.createInventoryStatusHistoryType();
        jaxbObj.setStatusHistId(BigInteger.valueOf(dto.getEntityId()));
        jaxbObj.setEffectiveDate(RMT2Date.toXmlDate(dto.getEffectiveDate()));
        jaxbObj.setEndDate(RMT2Date.toXmlDate(dto.getEndDate()));
        jaxbObj.setMarkup(BigDecimal.valueOf(dto.getMarkup()));
        jaxbObj.setUnitCost(BigDecimal.valueOf(dto.getUnitCost()));
        
        InventoryItemType item = jaxbObjFactory.createInventoryItemType();
        item.setItemId(BigInteger.valueOf(dto.getItemId()));
        jaxbObj.setItem(item);
        
        InventoryItemStatusType status = jaxbObjFactory.createInventoryItemStatusType();
        status.setItemStatusId(BigInteger.valueOf(dto.getItemStatusId()));
        jaxbObj.setStatus(status);
        return jaxbObj;
    }
}

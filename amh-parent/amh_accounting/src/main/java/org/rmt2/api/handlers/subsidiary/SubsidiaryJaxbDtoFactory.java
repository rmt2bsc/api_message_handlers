package org.rmt2.api.handlers.subsidiary;

import org.dto.CustomerDto;
import org.dto.ItemMasterStatusDto;
import org.dto.ItemMasterStatusHistDto;
import org.dto.ItemMasterTypeDto;
import org.dto.VendorItemDto;
import org.dto.adapter.orm.account.subsidiary.Rmt2SubsidiaryDtoFactory;
import org.dto.adapter.orm.inventory.Rmt2InventoryDtoFactory;
import org.dto.adapter.orm.inventory.Rmt2ItemMasterDtoFactory;
import org.rmt2.jaxb.CustomerCriteriaType;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.InventoryItemStatusType;
import org.rmt2.jaxb.InventoryItemType;
import org.rmt2.jaxb.InventoryItemtypeType;
import org.rmt2.jaxb.InventoryStatusHistoryType;
import org.rmt2.jaxb.ItemStatusCriteriaType;
import org.rmt2.jaxb.ItemStatusHistoryCriteriaType;
import org.rmt2.jaxb.ItemtypeCriteriaType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.VendorItemCriteriaType;
import org.rmt2.jaxb.VendorItemType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.inventory.InventoryItemStatusHistTypeBuilder;
import org.rmt2.util.accounting.inventory.InventoryItemStatusTypeBuilder;
import org.rmt2.util.accounting.inventory.InventoryItemTypeBuilder;
import org.rmt2.util.accounting.inventory.InventoryItemtypeTypeBuilder;
import org.rmt2.util.accounting.inventory.VendorItemTypeBuilder;

import com.RMT2Base;

/**
 * A factory for converting subsidiary related JAXB objects such as customers
 * and creditors to DTO and vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class SubsidiaryJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>CustomerDto</i> using a valid
     * <i>CustomerCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link CustomerCriteriaType}
     * @return an instance of {@link CustomerDto}
     */
    public static final CustomerDto createCustomerDtoCriteriaInstance(CustomerCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        CustomerDto dto = Rmt2SubsidiaryDtoFactory.createCustomerInstance(null, null);
        if (jaxbCriteria.getCustomer() != null) {
            if (!jaxbCriteria.getCustomer().getAccountNo().isEmpty()) {
                dto.setAccountNo(jaxbCriteria.getCustomer().getAccountNo());    
            }
            if (!jaxbCriteria.getCustomer().getAcctDescription().isEmpty()) {
                dto.setDescription(jaxbCriteria.getCustomer().getAcctDescription());    
            }
            if (jaxbCriteria.getCustomer().getAcctId() != null) {
                dto.setAcctId(jaxbCriteria.getCustomer().getAcctId().intValue());    
            }
            if (jaxbCriteria.getCustomer().getActive() != null) {
                dto.setActive(jaxbCriteria.getCustomer().getActive().intValue());    
            }
            if (jaxbCriteria.getCustomer().getCustomerId() != null) {
                dto.setCustomerId(jaxbCriteria.getCustomer().getCustomerId().intValue());    
            }
            if (jaxbCriteria.getCustomer().getBusinessContactDetails() != null) {
                if (jaxbCriteria.getCustomer().getBusinessContactDetails().getBusinessId() != null) {
                    dto.setContactId(jaxbCriteria.getCustomer().getBusinessContactDetails().getBusinessId().intValue());    
                }
                if (!jaxbCriteria.getCustomer().getBusinessContactDetails().getLongName().isEmpty()) {
                    dto.setContactName(jaxbCriteria.getCustomer().getBusinessContactDetails().getLongName());    
                }
            }
            
            // TODO: In the future, we can make provisions to handle person related data.
        }
        
        return dto;
    }
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final CustomerDto createCustomerDtoInstance(CustomerType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        CustomerDto dto = Rmt2SubsidiaryDtoFactory.createCustomerInstance(null, null);
        if (!jaxbObj.getAccountNo().isEmpty()) {
            dto.setAccountNo(jaxbObj.getAccountNo());    
        }
        if (!jaxbObj.getAcctDescription().isEmpty()) {
            dto.setDescription(jaxbObj.getAcctDescription());    
        }
        if (jaxbObj.getAcctId() != null) {
            dto.setAcctId(jaxbObj.getAcctId().intValue());    
        }
        if (jaxbObj.getActive() != null) {
            dto.setActive(jaxbObj.getActive().intValue());    
        }
        if (jaxbObj.getCustomerId() != null) {
            dto.setCustomerId(jaxbObj.getCustomerId().intValue());    
        }
        if (jaxbObj.getBusinessContactDetails() != null) {
            if (jaxbObj.getBusinessContactDetails().getBusinessId() != null) {
                dto.setContactId(jaxbObj.getBusinessContactDetails().getBusinessId().intValue());    
            }
            if (!jaxbObj.getBusinessContactDetails().getLongName().isEmpty()) {
                dto.setContactName(jaxbObj.getBusinessContactDetails().getLongName());    
            }
            if (jaxbObj.getBusinessContactDetails().getCategory() != null) {
                if (jaxbObj.getBusinessContactDetails().getCategory().getGroupId() != null) {
                    dto.setCategoryId(jaxbObj.getBusinessContactDetails().getCategory().getGroupId().intValue());    
                }    
            }
            if (!jaxbObj.getBusinessContactDetails().getContactEmail().isEmpty()) {
                dto.setContactEmail(jaxbObj.getBusinessContactDetails().getContactEmail());    
            }
            if (!jaxbObj.getBusinessContactDetails().getContactFirstname().isEmpty()) {
                dto.setContactFirstname(jaxbObj.getBusinessContactDetails().getContactFirstname());    
            }
            if (!jaxbObj.getBusinessContactDetails().getContactLastname().isEmpty()) {
                dto.setContactLastname(jaxbObj.getBusinessContactDetails().getContactLastname());    
            }
            if (!jaxbObj.getBusinessContactDetails().getContactPhone().isEmpty()) {
                dto.setContactPhone(jaxbObj.getBusinessContactDetails().getContactPhone());    
            }
            if (!jaxbObj.getBusinessContactDetails().getContactExt().isEmpty()) {
                dto.setContactExt(jaxbObj.getBusinessContactDetails().getContactExt());    
            }
            if (jaxbObj.getBusinessContactDetails().getEntityType() != null) {
                if (jaxbObj.getBusinessContactDetails().getEntityType().getCodeId() != null) {
                    dto.setEntityTypeId(jaxbObj.getBusinessContactDetails().getEntityType().getCodeId().intValue());    
                }
            }
            if (jaxbObj.getBusinessContactDetails().getServiceType() != null) {
                if (jaxbObj.getBusinessContactDetails().getServiceType().getCodeId() != null) {
                    dto.setServTypeId(jaxbObj.getBusinessContactDetails().getServiceType().getCodeId().intValue());    
                }
            }
            if (!jaxbObj.getBusinessContactDetails().getShortName().isEmpty()) {
                dto.setShortName(jaxbObj.getBusinessContactDetails().getShortName());    
            }
            if (!jaxbObj.getBusinessContactDetails().getTaxId().isEmpty()) {
                dto.setTaxId(jaxbObj.getBusinessContactDetails().getTaxId());    
            }
            if (!jaxbObj.getBusinessContactDetails().getWebsite().isEmpty()) {
                dto.setWebsite(jaxbObj.getBusinessContactDetails().getWebsite());    
            }
            if (jaxbObj.getBusinessContactDetails().getAddress() != null) {
                if (jaxbObj.getBusinessContactDetails().getAddress().getAddrId() != null) {
                    dto.setAddrId(jaxbObj.getBusinessContactDetails().getAddress().getAddrId().intValue());    
                }
                if (!jaxbObj.getBusinessContactDetails().getAddress().getAddr1().isEmpty()) {
                    dto.setAddr1(jaxbObj.getBusinessContactDetails().getAddress().getAddr1());    
                }
                if (!jaxbObj.getBusinessContactDetails().getAddress().getAddr2().isEmpty()) {
                    dto.setAddr2(jaxbObj.getBusinessContactDetails().getAddress().getAddr2());    
                }
                if (!jaxbObj.getBusinessContactDetails().getAddress().getAddr3().isEmpty()) {
                    dto.setAddr3(jaxbObj.getBusinessContactDetails().getAddress().getAddr3());    
                }
                if (!jaxbObj.getBusinessContactDetails().getAddress().getAddr4().isEmpty()) {
                    dto.setAddr4(jaxbObj.getBusinessContactDetails().getAddress().getAddr4());    
                }
                if (!jaxbObj.getBusinessContactDetails().getAddress().getPhoneMain().isEmpty()) {
                    dto.setPhoneCompany(jaxbObj.getBusinessContactDetails().getAddress().getPhoneMain());    
                }
                if (!jaxbObj.getBusinessContactDetails().getAddress().getPhonePager().isEmpty()) {
                    dto.setPhonePager(jaxbObj.getBusinessContactDetails().getAddress().getPhonePager());    
                }
                if (!jaxbObj.getBusinessContactDetails().getAddress().getPhoneFax().isEmpty()) {
                    dto.setPhoneFax(jaxbObj.getBusinessContactDetails().getAddress().getPhoneFax());    
                }
                if (!jaxbObj.getBusinessContactDetails().getAddress().getPhoneFax().isEmpty()) {
                    dto.setPhoneFax(jaxbObj.getBusinessContactDetails().getAddress().getPhoneFax());    
                }
                if (jaxbObj.getBusinessContactDetails().getAddress().getZip() != null) {
                    if (!jaxbObj.getBusinessContactDetails().getAddress().getZip().getCity().isEmpty()) {
                        dto.setCity(jaxbObj.getBusinessContactDetails().getAddress().getZip().getCity());    
                    }
                    if (!jaxbObj.getBusinessContactDetails().getAddress().getZip().getState().isEmpty()) {
                        dto.setState(jaxbObj.getBusinessContactDetails().getAddress().getZip().getState());    
                    }
                    if (jaxbObj.getBusinessContactDetails().getAddress().getZip().getZipcode() != null) {
                        dto.setZip(jaxbObj.getBusinessContactDetails().getAddress().getZip().getZipcode().intValue());    
                    }
                }
            }
        }
        
        
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    public static final CustomerType createCustomerJaxbInstance(CustomerDto dto) {
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
                .withOverrideRetail(dto.getOverrideRetail())
                .withRetailPrice(dto.getRetailPrice())
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
    
    
    /**
     * Creates an instance of <i>ItemMasterStatusDto</i> using a valid
     * <i>ItemStatusCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link ItemStatusCriteriaType}
     * @return an instance of {@link ItemMasterStatusDto}
     */
    public static final ItemMasterStatusDto createStatusDtoCriteriaInstance(ItemStatusCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        ItemMasterStatusDto dto = Rmt2ItemMasterDtoFactory.createItemStatusInstance(null);
        if (jaxbCriteria.getItemStatusId() != null) {
            dto.setEntityId(jaxbCriteria.getItemStatusId().intValue());    
        }
        if (jaxbCriteria.getItemStatusDescription() != null) {
            dto.setEntityName(jaxbCriteria.getItemStatusDescription());    
        }
        return dto;
    }
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final ItemMasterStatusDto createStatusDtoInstance(ItemStatusCriteriaType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ItemMasterStatusDto dto = Rmt2ItemMasterDtoFactory.createItemStatusInstance(null);
        if (jaxbObj.getItemStatusId() != null) {
            dto.setEntityId(jaxbObj.getItemStatusId().intValue());    
        }
        if (jaxbObj.getItemStatusDescription() != null) {
            dto.setEntityName(jaxbObj.getItemStatusDescription());    
        }
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    public static final InventoryItemStatusType createStatusJaxbInstance(ItemMasterStatusDto dto) {
        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated()).build();
        
        InventoryItemStatusType jaxbObj = InventoryItemStatusTypeBuilder.Builder.create()
                .withStatusId(dto.getEntityId())
                .withDescription(dto.getEntityName())
                .withRecordTrackingType(rtt).build();
        return jaxbObj;
    }
    
    
    /**
     * Creates an instance of <i>ItemMasterTypeDto</i> using a valid
     * <i>ItemtypeCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link ItemtypeCriteriaType}
     * @return an instance of {@link ItemMasterTypeDto}
     */
    public static final ItemMasterTypeDto createItemTypeDtoCriteriaInstance(ItemtypeCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        ItemMasterTypeDto dto = Rmt2ItemMasterDtoFactory.createItemTypeInstance(null);
        if (jaxbCriteria.getItemTypeId() != null) {
            dto.setItemTypeId(jaxbCriteria.getItemTypeId().intValue());    
        }
        if (jaxbCriteria.getItemTypeDescription() != null) {
            dto.setItemTypeDescription(jaxbCriteria.getItemTypeDescription());    
        }
        return dto;
    }
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final ItemMasterTypeDto createItemTypeDtoInstance(InventoryItemtypeType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        ItemMasterTypeDto dto = Rmt2ItemMasterDtoFactory.createItemTypeInstance(null);
        if (jaxbObj.getItemTypeId() != null) {
            dto.setItemTypeId(jaxbObj.getItemTypeId().intValue());    
        }
        if (jaxbObj.getDescription() != null) {
            dto.setItemTypeDescription(jaxbObj.getDescription());    
        }
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    public static final InventoryItemtypeType createItemTypeJaxbInstance(ItemMasterTypeDto dto) {
        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated()).build();
        
        InventoryItemtypeType jaxbObj = InventoryItemtypeTypeBuilder.Builder.create()
                .withItemTypeId(dto.getItemTypeId())
                .withDescription(dto.getItemTypeDescription())
                .withRecordTrackingType(rtt).build();
        return jaxbObj;
    }
    
    /**
     * Creates an instance of <i>VendorItemDto</i> using a valid
     * <i>VendorItemCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link VendorItemCriteriaType}
     * @return an instance of {@link VendorItemDto}
     */
    public static final VendorItemDto createVendorItemDtoCriteriaInstance(VendorItemCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        VendorItemDto dto = Rmt2InventoryDtoFactory.createVendorItemInstance(null);
        if (jaxbCriteria.getItemId() != null) {
            dto.setItemId(jaxbCriteria.getItemId().intValue());    
        }
        if (jaxbCriteria.getCreditorId() != null) {
            dto.setVendorId(jaxbCriteria.getCreditorId().intValue());
        }
        if (jaxbCriteria.getItemSerialNo() != null) {
            dto.setItemSerialNo(jaxbCriteria.getItemSerialNo());
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
    public static final VendorItemDto createVendorItemDtoInstance(VendorItemType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        VendorItemDto dto = Rmt2InventoryDtoFactory.createVendorItemInstance(null);
        if (jaxbObj.getItemId() != null) {
            dto.setItemId(jaxbObj.getItemId().intValue());    
        }
        if (jaxbObj.getCreditor() != null && jaxbObj.getCreditor().getCreditorId() != null) {
            dto.setVendorId(jaxbObj.getCreditor().getCreditorId().intValue());
        }
        if (jaxbObj.getUnitCost() != null) {
            dto.setUnitCost(jaxbObj.getUnitCost().doubleValue());
        }
        dto.setItemTypeDescription(jaxbObj.getDescription());
        dto.setItemSerialNo(jaxbObj.getItemSerialNo());
        dto.setVendorItemNo(jaxbObj.getVendorItemNo());
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    public static final VendorItemType createVendorItemTypeJaxbInstance(VendorItemDto dto) {
        VendorItemType jaxbObj = VendorItemTypeBuilder.Builder.create()
                .withItemId(dto.getItemId())
                .withDescription(dto.getItemName())
                .withCreditorId(dto.getVendorId())
                .withItemSerialNo(dto.getItemSerialNo())
                .withVendorItemNo(dto.getVendorItemNo())
                .withUnitCost(dto.getUnitCost()).build();

        return jaxbObj;
    }
}

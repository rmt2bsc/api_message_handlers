/**
 * 
 */
package org.rmt2.api.handlers.transaction.sales;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.SalesOrderItems;
import org.dao.mapping.orm.rmt2.VwSalesOrderInvoice;
import org.dto.SalesInvoiceDto;
import org.dto.SalesOrderDto;
import org.dto.SalesOrderItemDto;
import org.dto.adapter.orm.transaction.sales.Rmt2SalesOrderDtoFactory;
import org.modules.transaction.sales.SalesApiConst;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.jaxb.InventoryItemType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.SalesInvoiceType;
import org.rmt2.jaxb.SalesOrderCriteria;
import org.rmt2.jaxb.SalesOrderItemType;
import org.rmt2.jaxb.SalesOrderType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.inventory.InventoryItemTypeBuilder;
import org.rmt2.util.accounting.transaction.sales.SalesInvoiceTypeBuilder;
import org.rmt2.util.accounting.transaction.sales.SalesOrderItemTypeBuilder;
import org.rmt2.util.accounting.transaction.sales.SalesOrderTypeBuilder;

/**
 * A factory for converting the data componenets of sales order related JAXB
 * object to a DTO and vice versa.
 * 
 * @author rterrell
 *
 */
public class SalesOrderJaxbDtoFactory extends TransactionJaxbDtoFactory {

    /**
     * Creates an instance of SalesOrderDto from a SalesOrderCriteria object.
     * 
     * @param criteriaData
     *            an instance of {@link SalesOrderCriteria}
     * @return an instance of {@link SalesOrderDto}
     */
    public static final SalesOrderDto createSalesOrderCriteriaDtoInstance(SalesOrderCriteria criteriaData) {
        if (criteriaData == null) {
            return null;
        }

        SalesOrderDto dto = Rmt2SalesOrderDtoFactory.createSalesOrderInstance();
        if (criteriaData.getSalesOrderId() != null) {
            dto.setSalesOrderId(criteriaData.getSalesOrderId().intValue());
        }
        if (criteriaData.getCustomerId() != null) {
            dto.setCustomerId(criteriaData.getCustomerId().intValue());
        }
        if (criteriaData.getAccountNo() != null) {
            dto.setAccountNo(criteriaData.getAccountNo());
        }
        if (criteriaData.getBusinessId() != null) {
            dto.setBusinessId(criteriaData.getBusinessId().intValue());
        }
        if (criteriaData.getBusinessName() != null) {
            dto.setCustomerName(criteriaData.getBusinessName());
        }

        if (criteriaData.getSalesOrderDate() != null) {
            dto.setSaleOrderDate(criteriaData.getSalesOrderDate().toGregorianCalendar().getTime());
        }
        if (criteriaData.getSalesOrderTotal() != null) {
            dto.setOrderTotal(criteriaData.getSalesOrderTotal().doubleValue());
        }

        return dto;
    }


    /**
     * Creates an instance of <i>SalesOrderDto</i> using a valid
     * <i>SalesOrderType</i> JAXB object.
     * 
     * @param profileData
     *            an instance of {@link SalesOrderType}
     * @return an instance of {@link SalesOrderDto}
     * @throws {@link com.SystemException} Transaction date could not converted
     *         from a String.
     */
    public static final SalesOrderDto createSalesOrderHeaderDtoInstance(SalesOrderType profileData) {
        if (profileData == null) {
            return null;
        }

        SalesOrderDto dto = Rmt2SalesOrderDtoFactory.createSalesOrderInstance();
        if (profileData.getSalesOrderId() != null) {
            dto.setSalesOrderId(profileData.getSalesOrderId().intValue());
        }
        else {
            // In the event client did not supply the sales order id, initialize
            // it as "new".
            dto.setSalesOrderId(0);
        }
        if (profileData.getCustomerId() != null) {
            dto.setCustomerId(profileData.getCustomerId().intValue());
        }
        if (profileData.getOrderTotal() != null) {
            dto.setOrderTotal(profileData.getOrderTotal().doubleValue());
        }
        if (profileData.getCustomerAccountNo() != null) {
            dto.setAccountNo(profileData.getCustomerAccountNo());
        }
        if (profileData.getStatus() != null) {
            if (profileData.getStatus().getStatusId() != null) {
                dto.setSoStatusId(profileData.getStatus().getStatusId().intValue());
            }
            if (profileData.getStatus().getDescription() != null) {
                dto.setSoStatusDescription(profileData.getStatus().getDescription());
            }
        }

        // if (criteria.getCustomerName() != null) {
        // dto.setCustomerName(criteria.getCustomerName());
        // }
        if (profileData.getEffectiveDate() != null) {
            dto.setEffectiveDate(profileData.getEffectiveDate().toGregorianCalendar().getTime());
        }
        dto.setInvoiced(profileData.isInvoiced());

        return dto;
    }

    /**
     * Creates a List instance of <i>SalesOrderItemDto</i> using a valid
     * List<i>SalesOrderItemType</i> JAXB object.
     * 
     * @param profileData
     *            an instance of List {@link SalesOrderItemType}
     * @return an instance of List {@link SalesOrderItemDto}
     * @throws {@link com.SystemException} Transaction date could not converted
     *         from a String.
     */
    public static final List<SalesOrderItemDto> createSalesOrderItemsDtoInstance(List<SalesOrderItemType> profileData) {
        if (profileData == null) {
            return null;
        }

        List<SalesOrderItemDto> list = new ArrayList<>();
        for (SalesOrderItemType item : profileData) {
            SalesOrderItemDto dto = createSalesOrderItemDtoInstance(item);
            list.add(dto);
        }
        return list;
    }

    /**
     * Creates an instance of <i>SalesOrderItemDto</i> using a valid
     * <i>SalesOrderItemType</i> JAXB object.
     * 
     * @param profileItem
     *            an instance of {@link SalesOrderItemType}
     * @return an instance of {@link SalesOrderItemDto}
     * @throws {@link com.SystemException} Transaction date could not converted
     *         from a String.
     */
    public static final SalesOrderItemDto createSalesOrderItemDtoInstance(SalesOrderItemType profileItem) {
        if (profileItem == null) {
            return null;
        }
        SalesOrderItemDto dto = Rmt2SalesOrderDtoFactory.createSalesOrderItemInstance((SalesOrderItems) null);
        if (profileItem.getSalesOrderItemId() != null) {
            dto.setSoItemId(profileItem.getSalesOrderItemId().intValue());
        }
        else {
            dto.setSoItemId(0);
        }
        if (profileItem.getSalesOrderId() != null) {
            dto.setSalesOrderId(profileItem.getSalesOrderId().intValue());
        }
        else {
            dto.setSalesOrderId(0);
        }
        if (profileItem.getItem() != null && profileItem.getItem().getItemId() != null) {
            dto.setItemId(profileItem.getItem().getItemId().intValue());
        }
        if (profileItem.getItemNameOverride() != null) {
            dto.setItemNameOverride(profileItem.getItemNameOverride());
        }
        if (profileItem.getOrderQty() != null) {
            dto.setOrderQty(profileItem.getOrderQty().doubleValue());
        }
        if (profileItem.getBackOrderQty() != null) {
            dto.setBackOrderQty(profileItem.getBackOrderQty().doubleValue());
        }
        if (profileItem.getUnitCost() != null) {
            dto.setInitUnitCost(profileItem.getUnitCost().doubleValue());
        }
        if (profileItem.getMarkup() != null) {
            dto.setInitMarkup(profileItem.getMarkup().doubleValue());
        }
        return dto;
    }
    
    /**
     * 
     * @param criteriaData
     * @return
     */
    public static final SalesInvoiceDto createSalesInvoiceCriteriaDtoInstance(SalesOrderCriteria criteriaData) {
        if (criteriaData == null) {
            return null;
        }

        SalesInvoiceDto dto = Rmt2SalesOrderDtoFactory.createSalesIvoiceInstance(new VwSalesOrderInvoice());
        if (criteriaData.getSalesOrderId() != null) {
            dto.setSalesOrderId(criteriaData.getSalesOrderId().intValue());
        }
        if (criteriaData.getCustomerId() != null) {
            dto.setCustomerId(criteriaData.getCustomerId().intValue());
        }
        if (criteriaData.getAccountNo() != null) {
            dto.setAccountNo(criteriaData.getAccountNo());
        }
        if (criteriaData.getBusinessId() != null) {
            dto.setBusinessId(criteriaData.getBusinessId().intValue());
        }
        if (criteriaData.getBusinessName() != null) {
            dto.setCustomerName(criteriaData.getBusinessName());
        }

        if (criteriaData.getSalesOrderDate() != null) {
            dto.setSaleOrderDate(criteriaData.getSalesOrderDate().toGregorianCalendar().getTime());
        }
        if (criteriaData.getSalesOrderTotal() != null) {
            dto.setOrderTotal(criteriaData.getSalesOrderTotal().doubleValue());
        }

        return dto;
    }

    /**
     * Creates an instance of <i>SalesOrderType</i> using a valid
     * <i>SalesOrderDto</i> DTO object.
     * 
     * @param dto
     *            instance of {@link SalesOrderDto}
     * @return instance of {@link SalesOrderType}
     */
    public static final SalesOrderType createSalesOrderHeaderJaxbInstance(SalesOrderDto dto) {
        if (dto == null) {
            return null;
        }

        RecordTrackingType tracking = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .build();

        SalesInvoiceType si = null;
        if (dto instanceof SalesInvoiceDto) {
            SalesInvoiceDto siDto = (SalesInvoiceDto) dto;
            si = SalesInvoiceTypeBuilder.Builder.create()
                    .withInvoiceId(siDto.getInvoiceId())
                    .withInvoiceNo(siDto.getInvoiceNo())
                    .withInvoiceDate(siDto.getInvoiceDate())
                    .build();
        }

        SalesOrderType jaxb = SalesOrderTypeBuilder.Builder.create()
                .withCustomerAcctNo(dto.getAccountNo())
                .withInvoiced(dto.getSoStatusId() == SalesApiConst.STATUS_CODE_INVOICED)
                .withSalesOrderId(dto.getSalesOrderId())
                .withCustomerId(dto.getCustomerId())
                .withCustomerName(dto.getCustomerName())
                .withOrderTotal(dto.getOrderTotal())
                .withEffectiveDate(dto.getEffectiveDate())
                .withStatusId(dto.getSoStatusId())
                .withStatusDescription(dto.getSoStatusDescription())
                .withSalesInvoiceType(si)
                .withRecordTracking(tracking)
                .build();
        
        return jaxb;
    }


    /**
     * Creates an instance of <i>SalesOrderItemType</i> using a valid
     * <i>SalesOrderItemDto</i> DTO object.
     * 
     * @param dto
     *            instance of {@link SalesOrderItemDto}
     * 
     * @return instance of {@link SalesOrderItemType}
     */
    public static final SalesOrderItemType createSalesOrderItemJaxbInstance(SalesOrderItemDto dto) {
        if (dto == null) {
            return null;
        }

        RecordTrackingType tracking = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .build();

        InventoryItemType inv = InventoryItemTypeBuilder.Builder.create()
                .withItemId(dto.getItemId())
                .withItemName(dto.getImName())
                .build();

        SalesOrderItemType jaxb = SalesOrderItemTypeBuilder.Builder.create()
                .withSalesOrderItemId(dto.getSoItemId())
                .withInventoryItem(inv)
                .withSalesOrderId(dto.getSalesOrderId())
                .withBackOrderQty(dto.getBackOrderQty())
                .withMarkup(dto.getInitMarkup())
                .withUnitCost(dto.getInitUnitCost())
                .withOrderQty(Double.valueOf(dto.getOrderQty()).intValue())
                .withRecordTracking(tracking)
                .build();
        
        return jaxb;
    }

    /**
     * Creates an instance of List <i>SalesOrderItemType</i> using a valid List
     * <i>SalesOrderItemDto</i> DTO object.
     * 
     * @param dtoList
     *            a List of {@link SalesOrderItemDto} instances
     * @return A List of {@link SalesOrderItemType} objects
     */
    public static final List<SalesOrderItemType> createSalesOrderItemJaxbInstance(List<SalesOrderItemDto> dtoList) {
        if (dtoList == null) {
            return null;
        }
        List<SalesOrderItemType> jaxbList = new ArrayList<>();
        for (SalesOrderItemDto dto : dtoList) {
            SalesOrderItemType jaxb = SalesOrderJaxbDtoFactory.createSalesOrderItemJaxbInstance(dto);
            jaxbList.add(jaxb);
        }
        return jaxbList;
    }
}

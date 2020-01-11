/**
 * 
 */
package org.rmt2.api.handlers.transaction.sales;

import java.util.ArrayList;
import java.util.List;

import org.dao.mapping.orm.rmt2.SalesOrderItems;
import org.dto.SalesOrderDto;
import org.dto.SalesOrderItemDto;
import org.dto.adapter.orm.transaction.sales.Rmt2SalesOrderDtoFactory;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.jaxb.SalesOrderItemType;
import org.rmt2.jaxb.SalesOrderType;

/**
 * A factory for converting the data componenets of sales order related JAXB
 * object to a DTO and vice versa.
 * 
 * @author rterrell
 *
 */
public class SalesOrderJaxbDtoFactory extends TransactionJaxbDtoFactory {

    /**
     * Creates an instance of <i>SalesOrderDto</i> using a valid
     * <i>SalesOrderType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link SalesOrderType}
     * @return an instance of {@link SalesOrderDto}
     * @throws {@link com.SystemException} Transaction date could not converted
     *         from a String.
     */
    public static final SalesOrderDto createSalesOrderHeaderDtoInstance(SalesOrderType criteria) {
        if (criteria == null) {
            return null;
        }

        SalesOrderDto dto = Rmt2SalesOrderDtoFactory.createSalesOrderInstance();
        if (criteria.getSalesOrderId() != null) {
            dto.setSalesOrderId(criteria.getSalesOrderId().intValue());
        }
        if (criteria.getCustomerId() != null) {
            dto.setCustomerId(criteria.getCustomerId().intValue());
        }
        if (criteria.getOrderTotal() != null) {
            dto.setOrderTotal(criteria.getOrderTotal().doubleValue());
        }
        if (criteria.getCustomerAccountNo() != null) {
            dto.setAccountNo(criteria.getCustomerAccountNo());
        }
        if (criteria.getCustomerName() != null) {
            dto.setCustomerName(criteria.getCustomerName());
        }
        if (criteria.getEffectiveDate() != null) {
            dto.setEffectiveDate(criteria.getEffectiveDate().toGregorianCalendar().getTime());
        }
        dto.setInvoiced(criteria.isInvoiced());

        return dto;
    }

    /**
     * Creates a List instance of <i>SalesOrderItemDto</i> using a valid
     * List<i>SalesOrderItemType</i> JAXB object.
     * 
     * @param jaxbObjs
     *            an instance of List {@link SalesOrderItemType}
     * @return an instance of List {@link SalesOrderItemDto}
     * @throws {@link com.SystemException} Transaction date could not converted
     *         from a String.
     */
    public static final List<SalesOrderItemDto> createSalesOrderItemsDtoInstance(List<SalesOrderItemType> jaxbObjs) {
        if (jaxbObjs == null) {
            return null;
        }

        List<SalesOrderItemDto> list = new ArrayList<>();
        for (SalesOrderItemType item : jaxbObjs) {
            SalesOrderItemDto dto = createSalesOrderItemDtoInstance(item);
            list.add(dto);
        }
        return list;
    }

    /**
     * Creates an instance of <i>SalesOrderItemDto</i> using a valid
     * <i>SalesOrderItemType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link SalesOrderItemType}
     * @return an instance of {@link SalesOrderItemDto}
     * @throws {@link com.SystemException} Transaction date could not converted
     *         from a String.
     */
    public static final SalesOrderItemDto createSalesOrderItemDtoInstance(SalesOrderItemType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        SalesOrderItemDto dto = Rmt2SalesOrderDtoFactory.createSalesOrderItemInstance((SalesOrderItems) null);
        if (jaxbObj.getSalesOrderItemId() != null) {
            dto.setSoItemId(jaxbObj.getSalesOrderItemId().intValue());
        }
        if (jaxbObj.getSalesOrderId() != null) {
            dto.setSalesOrderId(jaxbObj.getSalesOrderId().intValue());
        }
        if (jaxbObj.getItem() != null && jaxbObj.getItem().getItemId() != null) {
            dto.setItemId(jaxbObj.getItem().getItemId().intValue());
        }
        if (jaxbObj.getItemNameOverride() != null) {
            dto.setItemNameOverride(jaxbObj.getItemNameOverride());
        }
        if (jaxbObj.getOrderQty() != null) {
            dto.setOrderQty(jaxbObj.getOrderQty().doubleValue());
        }
        if (jaxbObj.getBackOrderQty() != null) {
            dto.setBackOrderQty(jaxbObj.getBackOrderQty().doubleValue());
        }
        if (jaxbObj.getUnitCost() != null) {
            dto.setInitUnitCost(jaxbObj.getUnitCost().doubleValue());
        }
        if (jaxbObj.getMarkup() != null) {
            dto.setInitMarkup(jaxbObj.getMarkup().doubleValue());
        }
        return dto;
    }
}

/**
 * 
 */
package org.rmt2.api.handlers.transaction.purchases;

import org.dto.XactCreditChargeDto;
import org.dto.adapter.orm.transaction.purchases.creditor.Rmt2CreditChargeDtoFactory;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.jaxb.XactBasicCriteriaType;

import com.api.util.RMT2Date;
import com.api.util.RMT2String2;

/**
 * A factory for converting a creditor purchases transaction related JAXB object
 * to a DTO and vice versa.
 * 
 * @author rterrell
 *
 */
public class CreditorPurchasesJaxbDtoFactory extends TransactionJaxbDtoFactory {
    
    /**
     * Creates an instance of <i>XactCreditChargeDto</i> using a valid
     * <i>XactBasicCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link XactBasicCriteriaType}
     * @return an instance of {@link XactCreditChargeDto}
     * @throws {@link com.SystemException} Transaction date could not converted from a String.
     */
    public static final XactCreditChargeDto createCreditorPurchasesDtoCriteriaInstance(XactBasicCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        
        XactCreditChargeDto dto = Rmt2CreditChargeDtoFactory.createCreditChargeInstance();
        if (!RMT2String2.isEmpty(jaxbCriteria.getConfirmNo())) {
            dto.setXactConfirmNo(jaxbCriteria.getConfirmNo());    
        }
        if (jaxbCriteria.getXactId() != null) {
            dto.setXactId(jaxbCriteria.getXactId().intValue());    
        }
        if (jaxbCriteria.getContactId() != null) {
            dto.setBusinessId(jaxbCriteria.getContactId().intValue());    
        }
        if (jaxbCriteria.getSubsidiaryId() != null) {
            dto.setCreditorId(jaxbCriteria.getSubsidiaryId().intValue());    
        }
        if (jaxbCriteria.getXactDate() != null) {
            dto.setXactDate(RMT2Date.stringToDate(jaxbCriteria.getXactDate()));    
        }  
        if (jaxbCriteria.getXactTypeId() != null) {
            dto.setXactTypeId(jaxbCriteria.getXactTypeId().intValue());    
        }
        if (jaxbCriteria.getXactCatgId() != null) {
            dto.setXactCatgId(jaxbCriteria.getXactCatgId().intValue());    
        }
        if (jaxbCriteria.getTenderId() != null) {
            dto.setXactTenderId(jaxbCriteria.getTenderId().intValue());    
        }
        
        return dto;
    }
}

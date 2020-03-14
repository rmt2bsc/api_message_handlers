/**
 * 
 */
package org.rmt2.api.handlers.transaction.purchases;

import org.dto.XactCreditChargeDto;
import org.dto.adapter.orm.transaction.purchases.creditor.Rmt2CreditChargeDtoFactory;
import org.rmt2.api.handlers.transaction.TransactionJaxbDtoFactory;
import org.rmt2.jaxb.XactBasicCriteriaType;
import org.rmt2.jaxb.XactType;

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
    
    /**
     * Creates an instance of <i>XactCreditChargeDto</i> using a valid
     * <i>XactType</i> JAXB object.
     * 
     * @param jaxbObj
     *            an instance of {@link XactType}
     * @return an instance of {@link XactCreditChargeDto}
     * @throws {@link com.SystemException} Transaction date could not converted from a String.
     */
    public static XactCreditChargeDto createCreditorPurchasesDtoInstance(XactType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        XactCreditChargeDto dto = Rmt2CreditChargeDtoFactory.createCreditChargeInstance();
        
        // Get creditor purchase transaction data
        if (jaxbObj.getXactId() != null) {
            dto.setXactId(jaxbObj.getXactId().intValue());    
        }
        if (jaxbObj.getXactType() != null && jaxbObj.getXactType().getXactTypeId() != null) {
            dto.setXactTypeId(jaxbObj.getXactType().getXactTypeId().intValue());    
        }
        if (jaxbObj.getXactSubtype() != null && jaxbObj.getXactSubtype().getXactTypeId() != null) {
            dto.setXactSubtypeId(jaxbObj.getXactSubtype().getXactTypeId().intValue());    
        }
        if (jaxbObj.getXactSubtype() != null && jaxbObj.getXactSubtype().getXactTypeId() != null) {
            dto.setXactSubtypeId(jaxbObj.getXactSubtype().getXactTypeId().intValue());    
        }
        if (jaxbObj.getXactCodeGroup() != null) {
            if (jaxbObj.getXactCodeGroup().getXactCodeGrpId() != null) {
                dto.setXactCodeGrpId(jaxbObj.getXactCodeGroup().getXactCodeGrpId().intValue());
            }
            if (!RMT2String2.isEmpty(jaxbObj.getXactCodeGroup().getDescription())) {
                dto.setXactCodeGrpDescription(jaxbObj.getXactCodeGroup().getDescription());
            }
        }
        if (jaxbObj.getXactCode() != null) {
            if (jaxbObj.getXactCode().getXactCodeId() != null) {
                dto.setXactCodeId(jaxbObj.getXactCode().getXactCodeId().intValue());
            }
            if (!RMT2String2.isEmpty(jaxbObj.getXactCode().getDescription())) {
                dto.setXactCodeDescription(jaxbObj.getXactCode().getDescription());
            }
        }
        if (jaxbObj.getXactAmount() != null) {
            dto.setXactAmount(jaxbObj.getXactAmount().doubleValue());    
        }   
        if (jaxbObj.getXactDate() != null) {
            dto.setXactDate(jaxbObj.getXactDate().toGregorianCalendar().getTime());    
        } 
        if (jaxbObj.getPostedDate() != null) {
            dto.setXactPostedDate(jaxbObj.getPostedDate().toGregorianCalendar().getTime());    
        }
        if (jaxbObj.getTenderId() != null) {
            dto.setXactTenderId(jaxbObj.getTenderId().intValue());    
        }   
        if (jaxbObj.getDocumentId() != null) {
            dto.setDocumentId(jaxbObj.getDocumentId().intValue());    
        }   
        dto.setXactReason(jaxbObj.getXactReason());
        dto.setXactEntityRefNo(jaxbObj.getEntityRefNo());
        dto.setXactNegInstrNo(jaxbObj.getNegInstrNo());
        dto.setXactConfirmNo(jaxbObj.getConfirmNo());
        dto.setXactBankTransInd(jaxbObj.getBankTransInd());
        
        // Get creditor data
        if (jaxbObj.getCreditor() != null) {
            if (jaxbObj.getCreditor().getCreditorId() != null) {
                dto.setCreditorId(jaxbObj.getCreditor().getCreditorId().intValue());
            }
            dto.setAccountNumber(jaxbObj.getCreditor().getAccountNo());
        }
        return dto;
    }
}

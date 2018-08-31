package org.rmt2.api.handlers.transaction;

import java.util.ArrayList;
import java.util.List;

import org.dto.CustomerXactHistoryDto;
import org.dto.XactCustomCriteriaDto;
import org.dto.XactDto;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.modules.transaction.XactApiFactory;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.CustomerActivityType;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.XactBasicCriteriaType;
import org.rmt2.jaxb.XactCustomRelationalCriteriaType;
import org.rmt2.jaxb.XactLineitemType;
import org.rmt2.jaxb.XactType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CustomerActivityTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CustomerTypeBuilder;
import org.rmt2.util.addressbook.BusinessTypeBuilder;

import com.RMT2Base;
import com.api.util.RMT2Date;
import com.api.util.RMT2String2;

/**
 * A factory for converting general transaction related JAXB objects to DTO and
 * vice versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class TransactionJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>XactDto</i> using a valid
     * <i>XactBasicCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link XactBasicCriteriaType}
     * @return an instance of {@link XactDto}
     * @throws {@link com.SystemException} Transaction date could not converted from a String.
     */
    public static final XactDto createBaseXactDtoCriteriaInstance(XactBasicCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        
        XactDto dto = Rmt2XactDtoFactory.createXactBaseInstance(null);
        if (!RMT2String2.isEmpty(jaxbCriteria.getConfirmNo())) {
            dto.setXactConfirmNo(jaxbCriteria.getConfirmNo());    
        }
        if (jaxbCriteria.getXactId() != null) {
            dto.setXactId(jaxbCriteria.getXactId().intValue());    
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
     * 
     * @param jaxbCriteria
     * @return
     */
    public static final XactCustomCriteriaDto createCustomXactDtoCriteriaInstance(XactCustomRelationalCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        XactCustomCriteriaDto dto = XactApiFactory.createCustomCriteriaInstance();
        if (jaxbCriteria.getTargetLevel() != null && !RMT2String2.isEmpty(jaxbCriteria.getTargetLevel().name())) {
            dto.setTargetLevel(jaxbCriteria.getTargetLevel().name());    
        }
        if (jaxbCriteria.getXactReasonOptions() != null && !RMT2String2.isEmpty(jaxbCriteria.getXactReasonOptions().name())) {
            dto.setXactReasonFilterOption(jaxbCriteria.getXactReasonOptions().name());    
        }
        if (jaxbCriteria.getFromXactAmount() != null) {
            dto.setFromXactAmount(jaxbCriteria.getFromXactAmount().doubleValue());    
        }
        if (jaxbCriteria.getToXactAmount() != null) {
            dto.setToXactAmount(jaxbCriteria.getToXactAmount().doubleValue());    
        }
        if (jaxbCriteria.getFromItemAmount() != null) {
            dto.setFromItemAmount(jaxbCriteria.getFromItemAmount().doubleValue());    
        }
        if (jaxbCriteria.getToItemAmount() != null) {
            dto.setToItemAmount(jaxbCriteria.getToItemAmount().doubleValue());    
        }
        if (jaxbCriteria.getFromXactDate() != null) {
            dto.setFromXactDate(RMT2Date.stringToDate(jaxbCriteria.getFromXactDate()));    
        }
        if (jaxbCriteria.getToXactDate() != null) {
            dto.setToXactDate(RMT2Date.stringToDate(jaxbCriteria.getToXactDate()));    
        }
        
        if (!RMT2String2.isEmpty(jaxbCriteria.getFromRelOpXactAmount())) {
            dto.setFromXactAmountRelOp(jaxbCriteria.getFromRelOpXactAmount());    
        }
        if (!RMT2String2.isEmpty(jaxbCriteria.getToRelOpXactAmount())) {
            dto.setToXactAmountRelOp(jaxbCriteria.getToRelOpXactAmount());    
        }
        if (!RMT2String2.isEmpty(jaxbCriteria.getFromRelOpItemAmount())) {
            dto.setFromItemAmountRelOp(jaxbCriteria.getFromRelOpItemAmount());    
        }
        if (!RMT2String2.isEmpty(jaxbCriteria.getToRelOpItemAmount())) {
            dto.setToItemAmountRelOp(jaxbCriteria.getToRelOpItemAmount());    
        }
        if (!RMT2String2.isEmpty(jaxbCriteria.getFromRelOpXactDate())) {
            dto.setFromXactDateRelOp(jaxbCriteria.getFromRelOpXactDate());    
        }
        if (!RMT2String2.isEmpty(jaxbCriteria.getToRelOpXactDate())) {
            dto.setToXactDateRelOp(jaxbCriteria.getToRelOpXactDate());    
        }
        return dto;
    }
    
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final XactDto createTransactionDtoInstance(XactType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        XactDto dto = Rmt2XactDtoFactory.createXactBaseInstance(null);
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
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @param balance
     * @param transactions
     * @return
     */
    public static final XactType createXactJaxbInstance(XactDto dto, double balance, 
            List<XactLineitemType> transactions) {
        
        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated()).build();
        
        BusinessType businessContactDetails = BusinessTypeBuilder.Builder.create()
                .withBusinessId(dto.getContactId())
                .withLongname(dto.getContactName()).build();
        
        List<CustomerActivityType> catList = null;
        if (transactions != null) {
            catList = new ArrayList<>();
            for (CustomerXactHistoryDto trans : transactions) {
                CustomerActivityType cat = CustomerActivityTypeBuilder.Builder.create()
                        .withAmount(trans.getActivityAmount())
                        .withCustomerActivityId(trans.getActivityId())
                        .withCustomerId(trans.getCustomerId())
                        .withXactDetails(null)
                        .withXactId(trans.getXactId()).build();
                
                catList.add(cat);
            }
        }
        
        CustomerType jaxbObj = CustomerTypeBuilder.Builder.create()
                .withCustomerId(dto.getCustomerId())
                .withAcctId(dto.getAcctId())
                .withBusinessType(businessContactDetails)
                .withPersonType(null)
                .withAccountNo(dto.getAccountNo())
                .withCreditLimit(dto.getCreditLimit())
                .withAcctDescription(dto.getDescription())
                .withBalance(balance)
                .withActive(dto.getActive())
                .withTransactions(catList)
                .withRecordTracking(rtt).build();
        return jaxbObj;
    }

}


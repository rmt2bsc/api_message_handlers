package org.rmt2.api.handlers.transaction;

import java.util.ArrayList;
import java.util.List;

import org.dto.CustomerDto;
import org.dto.CustomerXactHistoryDto;
import org.dto.XactCustomCriteriaDto;
import org.dto.XactDto;
import org.dto.adapter.orm.account.subsidiary.Rmt2SubsidiaryDtoFactory;
import org.dto.adapter.orm.transaction.Rmt2XactDtoFactory;
import org.modules.transaction.XactApiFactory;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.CustomerActivityType;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.jaxb.XactBasicCriteriaType;
import org.rmt2.jaxb.XactCustomRelationalCriteriaType;
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
    public static final CustomerDto createCustomerDtoInstance(CustomerType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        CustomerDto dto = Rmt2SubsidiaryDtoFactory.createCustomerInstance(null, null);
        if (RMT2String2.isNotEmpty(jaxbObj.getAccountNo())) {
            dto.setAccountNo(jaxbObj.getAccountNo());    
        }
        if (RMT2String2.isNotEmpty(jaxbObj.getAcctDescription())) {
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
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getLongName())) {
                dto.setContactName(jaxbObj.getBusinessContactDetails().getLongName());    
            }
            if (jaxbObj.getBusinessContactDetails().getCategory() != null) {
                if (jaxbObj.getBusinessContactDetails().getCategory().getGroupId() != null) {
                    dto.setCategoryId(jaxbObj.getBusinessContactDetails().getCategory().getGroupId().intValue());    
                }    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getContactEmail())) {
                dto.setContactEmail(jaxbObj.getBusinessContactDetails().getContactEmail());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getContactFirstname())) {
                dto.setContactFirstname(jaxbObj.getBusinessContactDetails().getContactFirstname());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getContactLastname())) {
                dto.setContactLastname(jaxbObj.getBusinessContactDetails().getContactLastname());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getContactPhone())) {
                dto.setContactPhone(jaxbObj.getBusinessContactDetails().getContactPhone());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getContactExt())) {
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
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getShortName())) {
                dto.setShortName(jaxbObj.getBusinessContactDetails().getShortName());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getTaxId())) {
                dto.setTaxId(jaxbObj.getBusinessContactDetails().getTaxId());    
            }
            if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getWebsite())) {
                dto.setWebsite(jaxbObj.getBusinessContactDetails().getWebsite());    
            }
            if (jaxbObj.getBusinessContactDetails().getAddress() != null) {
                if (jaxbObj.getBusinessContactDetails().getAddress().getAddrId() != null) {
                    dto.setAddrId(jaxbObj.getBusinessContactDetails().getAddress().getAddrId().intValue());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getAddr1())) {
                    dto.setAddr1(jaxbObj.getBusinessContactDetails().getAddress().getAddr1());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getAddr2())) {
                    dto.setAddr2(jaxbObj.getBusinessContactDetails().getAddress().getAddr2());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getAddr3())) {
                    dto.setAddr3(jaxbObj.getBusinessContactDetails().getAddress().getAddr3());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getAddr4())) {
                    dto.setAddr4(jaxbObj.getBusinessContactDetails().getAddress().getAddr4());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getPhoneMain())) {
                    dto.setPhoneCompany(jaxbObj.getBusinessContactDetails().getAddress().getPhoneMain());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getPhonePager())) {
                    dto.setPhonePager(jaxbObj.getBusinessContactDetails().getAddress().getPhonePager());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getPhoneFax())) {
                    dto.setPhoneFax(jaxbObj.getBusinessContactDetails().getAddress().getPhoneFax());    
                }
                if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getPhoneFax())) {
                    dto.setPhoneFax(jaxbObj.getBusinessContactDetails().getAddress().getPhoneFax());    
                }
                if (jaxbObj.getBusinessContactDetails().getAddress().getZip() != null) {
                    if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getZip().getCity())) {
                        dto.setCity(jaxbObj.getBusinessContactDetails().getAddress().getZip().getCity());    
                    }
                    if (RMT2String2.isNotEmpty(jaxbObj.getBusinessContactDetails().getAddress().getZip().getState())) {
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
    public static final CustomerType createCustomerJaxbInstance(CustomerDto dto,
            double balance, List<CustomerXactHistoryDto> transactions) {
        
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


package org.rmt2.api.handlers.subsidiary;

import java.util.ArrayList;
import java.util.List;

import org.dto.CustomerDto;
import org.dto.CustomerXactHistoryDto;
import org.dto.adapter.orm.account.subsidiary.Rmt2SubsidiaryDtoFactory;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.CustomerActivityType;
import org.rmt2.jaxb.CustomerCriteriaType;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CustomerActivityTypeBuilder;
import org.rmt2.util.accounting.subsidiary.CustomerTypeBuilder;
import org.rmt2.util.addressbook.BusinessTypeBuilder;

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
            if (jaxbCriteria.getCustomer().getAccountNo() != null
                    && !jaxbCriteria.getCustomer().getAccountNo().isEmpty()) {
                dto.setAccountNo(jaxbCriteria.getCustomer().getAccountNo());    
            }
            if (jaxbCriteria.getCustomer().getAcctDescription() != null
                    && !jaxbCriteria.getCustomer().getAcctDescription().isEmpty()) {
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
                if (jaxbCriteria.getCustomer().getBusinessContactDetails().getLongName() != null
                        && !jaxbCriteria.getCustomer().getBusinessContactDetails().getLongName().isEmpty()) {
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

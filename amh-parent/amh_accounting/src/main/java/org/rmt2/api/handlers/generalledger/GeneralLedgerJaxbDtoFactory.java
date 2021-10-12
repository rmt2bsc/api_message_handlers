package org.rmt2.api.handlers.generalledger;

import org.dto.AccountCategoryDto;
import org.dto.AccountDto;
import org.dto.AccountTypeDto;
import org.dto.adapter.orm.account.generalledger.Rmt2AccountDtoFactory;
import org.rmt2.jaxb.GlAccountType;
import org.rmt2.jaxb.GlAccountcatgType;
import org.rmt2.jaxb.GlAccounttypeType;
import org.rmt2.jaxb.GlBalancetypeType;
import org.rmt2.jaxb.GlCriteriaType;
import org.rmt2.jaxb.RecordTrackingType;
import org.rmt2.util.RecordTrackingTypeBuilder;
import org.rmt2.util.accounting.generalledger.GlAccountBalanceTypeBuilder;
import org.rmt2.util.accounting.generalledger.GlAccountCategoryTypeBuilder;
import org.rmt2.util.accounting.generalledger.GlAccountTypeBuilder;
import org.rmt2.util.accounting.generalledger.GlAccounttypeTypeBuilder;

import com.RMT2Base;

/**
 * A factory for converting general ledger related JAXB objects to DTO and vice
 * versa.
 * 
 * @author Roy Terrell.
 * 
 */
public class GeneralLedgerJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>AccountDto</i> using a valid
     * <i>GlCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link GlCriteriaType}
     * @return an instance of {@link AccountDto}
     */
    public static final AccountDto createGlAccountDtoCriteriaInstance(GlCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        AccountDto dto = Rmt2AccountDtoFactory.createAccountInstance(null);
        if (jaxbCriteria.getAcctId() != null) {
            dto.setAcctId(jaxbCriteria.getAcctId().intValue());    
        }
        if (jaxbCriteria.getAcctType() != null && jaxbCriteria.getAcctType().getAcctTypeId() != null) {
            dto.setAcctTypeId(jaxbCriteria.getAcctType().getAcctTypeId().intValue());    
        }
        if (jaxbCriteria.getAcctCatg() != null && jaxbCriteria.getAcctCatg().getAcctCatgId() != null) {
            dto.setAcctCatgId(jaxbCriteria.getAcctCatg().getAcctCatgId().intValue());    
        }
        if (jaxbCriteria.getBalanceType() != null && jaxbCriteria.getBalanceType().getAccountBaltypeId() != null) {
            dto.setBalanceTypeId(jaxbCriteria.getBalanceType().getAccountBaltypeId().intValue());    
        }
        dto.setAcctNo(jaxbCriteria.getAccountNo());
        dto.setAcctCode(jaxbCriteria.getAccountCode());
        dto.setAcctName(jaxbCriteria.getAccountName());
        dto.setAcctDescription(jaxbCriteria.getAccountDescription());
        return dto;
    }
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final AccountDto createGlAccountDtoInstance(GlAccountType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        AccountDto dto = Rmt2AccountDtoFactory.createAccountInstance(null);
        if (jaxbObj.getAcctId() != null) {
            dto.setAcctId(jaxbObj.getAcctId().intValue());    
        }
        if (jaxbObj.getAcctType() != null && jaxbObj.getAcctType().getAcctTypeId() != null) {
            dto.setAcctTypeId(jaxbObj.getAcctType().getAcctTypeId().intValue());    
        }
        if (jaxbObj.getAcctCatg() != null && jaxbObj.getAcctCatg().getAcctCatgId() != null) {
            dto.setAcctCatgId(jaxbObj.getAcctCatg().getAcctCatgId().intValue());    
        }
        if (jaxbObj.getBalanceType() != null && jaxbObj.getBalanceType().getAccountBaltypeId() != null) {
            dto.setBalanceTypeId(jaxbObj.getBalanceType().getAccountBaltypeId().intValue());    
        }
        if (jaxbObj.getAcctSeq() != null) {
            dto.setAcctSeq(jaxbObj.getAcctSeq().intValue());
        }
        dto.setAcctNo(jaxbObj.getAccountNo());
        dto.setAcctCode(jaxbObj.getAccountCode());
        dto.setAcctName(jaxbObj.getAccountName());
        dto.setAcctDescription(jaxbObj.getAccountDescription());
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    public static final GlAccountType createGlAccountJaxbInstance(AccountDto dto) {
        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated()).build();
        
        GlAccounttypeType gatt = GlAccounttypeTypeBuilder.Builder.create()
                .withAcctTypeId(dto.getAcctTypeId()).build();
        
        GlAccountcatgType gact = GlAccountCategoryTypeBuilder.Builder.create()
                .withAcctCatgId(dto.getAcctCatgId()).build();
        
        GlBalancetypeType gabt = GlAccountBalanceTypeBuilder.Builder.create()
                .withAcctBalanceTypeId(dto.getBalanceTypeId()).build();
        
        GlAccountType jaxbObj = GlAccountTypeBuilder.Builder.create()
                .withAcctId(dto.getAcctId())
                .withAcctSeq(dto.getAcctSeq())
                .withAccountCode(dto.getAcctCode())
                .withAccountDescription(dto.getAcctDescription())
                .withAccountName(dto.getAcctName())
                .withAccountNumber(dto.getAcctNo())
                .withAccountType(gatt)
                .withAccountCategory(gact)
                .withBalanceType(gabt)
                .withRecordTrackingType(rtt).build();
        return jaxbObj;
    }
    
    /**
     * Creates an instance of <i>AccountTypeDto</i> using a valid
     * <i>GlCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link GlCriteriaType}
     * @return an instance of {@link AccountTypeDto}
     */
    public static final AccountTypeDto createGlAccountTypeDtoCriteriaInstance(GlCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        AccountTypeDto dto = Rmt2AccountDtoFactory.createAccountTypeInstance(null);
        if (jaxbCriteria.getAcctType() != null) {
            if (jaxbCriteria.getAcctType().getAcctTypeId() != null) {
                dto.setAcctTypeId(jaxbCriteria.getAcctType().getAcctTypeId().intValue());    
            }
            if (jaxbCriteria.getAcctType().getDescription() != null) {
                dto.setAcctTypeDescription(jaxbCriteria.getAcctType().getDescription());    
            }
            if (jaxbCriteria.getAcctType().getBalanceType() != null
                    && jaxbCriteria.getAcctType().getBalanceType().getAccountBaltypeId() != null) {
                dto.setBalanceTypeId(jaxbCriteria.getAcctType().getBalanceType().getAccountBaltypeId().intValue());
            }
        }

        return dto;
    }
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final AccountTypeDto createGlAccountTypeDtoInstance(GlAccounttypeType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        AccountTypeDto dto = Rmt2AccountDtoFactory.createAccountTypeInstance(null);
        if (jaxbObj.getAcctTypeId() != null) {
            dto.setAcctTypeId(jaxbObj.getAcctTypeId().intValue());
        }
        if (jaxbObj.getDescription() != null) {
            dto.setAcctTypeDescription(jaxbObj.getDescription());
        }
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    public static final GlAccounttypeType createGlAccountTypeJaxbInstance(AccountTypeDto dto) {
        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated()).build();
        
        GlBalancetypeType gabt = GlAccountBalanceTypeBuilder.Builder.create()
                .withAcctBalanceTypeId(dto.getBalanceTypeId()).build();
        
        GlAccounttypeType jaxbObj = GlAccounttypeTypeBuilder.Builder.create()
                .withAcctTypeId(dto.getAcctTypeId())
                .withDescription(dto.getAcctTypeDescription())
                .withBalanceType(gabt)
                .withRecordTrackingType(rtt).build();
        return jaxbObj;
    }
    
    
    /**
     * Creates an instance of <i>AccountCategoryDto</i> using a valid
     * <i>GlCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link GlCriteriaType}
     * @return an instance of {@link AccountCategoryDto}
     */
    public static final AccountCategoryDto createGlAccountCatgDtoCriteriaInstance(GlCriteriaType jaxbCriteria) {
        if (jaxbCriteria == null) {
            return null;
        }
        AccountCategoryDto dto = Rmt2AccountDtoFactory.createAccountCategoryInstance(null);
        if (jaxbCriteria.getAcctCatg() != null) {
            if (jaxbCriteria.getAcctCatg().getAcctCatgId() != null) {
                dto.setAcctCatgId(jaxbCriteria.getAcctCatg().getAcctCatgId().intValue());
            }
            if (jaxbCriteria.getAcctCatg().getDescription() != null) {
                dto.setAcctCatgDescription(jaxbCriteria.getAcctCatg().getDescription());
            }
            if (jaxbCriteria.getAcctCatg().getAcctType() != null
                    && jaxbCriteria.getAcctCatg().getAcctType().getAcctTypeId() != null) {
                dto.setAcctTypeId(jaxbCriteria.getAcctCatg().getAcctType().getAcctTypeId().intValue());
            }
        }
        
        return dto;
    }
    
    /**
     * 
     * @param jaxbObj
     * @return
     */
    public static final AccountCategoryDto createGlAccountCatgDtoInstance(GlAccountcatgType jaxbObj) {
        if (jaxbObj == null) {
            return null;
        }
        AccountCategoryDto dto = Rmt2AccountDtoFactory.createAccountCategoryInstance(null);
        if (jaxbObj.getAcctType() != null && jaxbObj.getAcctType().getAcctTypeId() != null) {
            dto.setAcctTypeId(jaxbObj.getAcctType().getAcctTypeId().intValue());
        }
        if (jaxbObj.getAcctCatgId() != null) {
            dto.setAcctCatgId(jaxbObj.getAcctCatgId().intValue());
        }
        if (jaxbObj.getDescription() != null) {
            dto.setAcctCatgDescription(jaxbObj.getDescription());
        }
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    public static final GlAccountcatgType createGlAccountCatgJaxbInstance(AccountCategoryDto dto) {
        RecordTrackingType rtt = RecordTrackingTypeBuilder.Builder.create()
                .withDateCreated(dto.getDateCreated())
                .withDateUpdate(dto.getDateUpdated())
                .withUserId(dto.getUpdateUserId())
                .withIpCreated(dto.getIpCreated())
                .withIpUpdate(dto.getIpUpdated()).build();
        
        GlAccounttypeType gatt = GlAccounttypeTypeBuilder.Builder.create()
                .withAcctTypeId(dto.getAcctTypeId()).build();
        
        GlAccountcatgType jaxbObj = GlAccountCategoryTypeBuilder.Builder.create()
                .withAcctCatgId(dto.getAcctCatgId())
                .withDescription(dto.getAcctCatgDescription())
                .withAccountType(gatt)
                .withRecordTrackingType(rtt).build();
        
        return jaxbObj;
    }
}

package org.rmt2.api.handlers.generalledger;

import java.math.BigInteger;

import org.dto.AccountCategoryDto;
import org.dto.AccountDto;
import org.dto.AccountTypeDto;
import org.dto.adapter.orm.account.generalledger.Rmt2AccountDtoFactory;
import org.rmt2.jaxb.GlAccountType;
import org.rmt2.jaxb.GlAccountcatgType;
import org.rmt2.jaxb.GlAccounttypeType;
import org.rmt2.jaxb.GlBalancetypeType;
import org.rmt2.jaxb.GlCriteriaType;
import org.rmt2.jaxb.ObjectFactory;

import com.RMT2Base;

/**
 * A factory containing several adapter methods for transforming JAXB objects to
 * DTO's.
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
        dto.setAcctCode(jaxbCriteria.getAccountNo());
        dto.setAcctName(jaxbCriteria.getAccountName());
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
        dto.setAcctNo(jaxbObj.getAccountNo());
        dto.setAcctCode(jaxbObj.getAccountNo());
        dto.setAcctName(jaxbObj.getAccountName());
        return dto;
    }
    
    /**
     * 
     * @param dto
     * @return
     */
    public static final GlAccountType createGlAccountJaxbInstance(AccountDto dto) {
        ObjectFactory jaxbObjFactory = new ObjectFactory();
        GlAccountType jaxbObj = jaxbObjFactory.createGlAccountType();
        jaxbObj.setAcctId(BigInteger.valueOf(dto.getAcctId()));
        GlAccounttypeType gatt = jaxbObjFactory.createGlAccounttypeType();
        gatt.setAcctTypeId(BigInteger.valueOf(dto.getAcctTypeId()));
        jaxbObj.setAcctType(gatt);
        GlAccountcatgType gact = jaxbObjFactory.createGlAccountcatgType();
        gact.setAcctCatgId(BigInteger.valueOf(dto.getAcctCatgId()));
        jaxbObj.setAcctCatg(gact);
        GlBalancetypeType gbtt = jaxbObjFactory.createGlBalancetypeType();
        gbtt.setAccountBaltypeId(BigInteger.valueOf(dto.getBalanceTypeId()));
        jaxbObj.setBalanceType(gbtt);
        jaxbObj.setAcctSeq(BigInteger.valueOf(dto.getAcctSeq()));
        jaxbObj.setAccountCode(dto.getAcctCode());
        jaxbObj.setAccountDescription(dto.getAcctDescription());
        jaxbObj.setAccountName(dto.getAcctName());
        jaxbObj.setAccountNo(dto.getAcctNo());
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
        ObjectFactory jaxbObjFactory = new ObjectFactory();
        GlAccounttypeType jaxbObj = jaxbObjFactory.createGlAccounttypeType();
        jaxbObj.setAcctTypeId(BigInteger.valueOf(dto.getAcctTypeId()));
        GlBalancetypeType gbtt = jaxbObjFactory.createGlBalancetypeType();
        gbtt.setAccountBaltypeId(BigInteger.valueOf(dto.getBalanceTypeId()));
        jaxbObj.setBalanceType(gbtt);
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
        if (jaxbCriteria.getAcctType() != null) {
            if (jaxbCriteria.getAcctType().getAcctTypeId() != null) {
                dto.setAcctTypeId(jaxbCriteria.getAcctType().getAcctTypeId().intValue());    
            }
        }
        
        if (jaxbCriteria.getAcctCatg() != null) {
            if (jaxbCriteria.getAcctCatg().getAcctCatgId() != null) {
                dto.setAcctCatgId(jaxbCriteria.getAcctCatg().getAcctCatgId().intValue());
            }
            if (jaxbCriteria.getAcctCatg().getDescription() != null) {
                dto.setAcctCatgDescription(jaxbCriteria.getAcctCatg().getDescription());
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
        ObjectFactory jaxbObjFactory = new ObjectFactory();
        GlAccountcatgType jaxbObj = jaxbObjFactory.createGlAccountcatgType();
        jaxbObj.setAcctCatgId(BigInteger.valueOf(dto.getAcctCatgId()));
        jaxbObj.setDescription(dto.getAcctCatgDescription());
        GlAccounttypeType gatt = jaxbObjFactory.createGlAccounttypeType();
        gatt.setAcctTypeId(BigInteger.valueOf(dto.getAcctTypeId()));
        jaxbObj.setAcctType(gatt);
        jaxbObj.setAcctType(gatt);
        return jaxbObj;
    }
}

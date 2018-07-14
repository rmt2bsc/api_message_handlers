package org.rmt2.api.adapters.jaxb;

import java.math.BigInteger;

import org.dto.AccountDto;
import org.dto.adapter.orm.account.generalledger.Rmt2AccountDtoFactory;
import org.rmt2.jaxb.GlAccountType;
import org.rmt2.jaxb.GlAccountcatgType;
import org.rmt2.jaxb.GlAccounttypeType;
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
public class AccountingJaxbDtoFactory extends RMT2Base {

    /**
     * Creates an instance of <i>AccountDto</i> using a valid
     * <i>GlCriteriaType</i> JAXB object.
     * 
     * @param criteria
     *            an instance of {@link GlCriteriaType}
     * @return an instance of {@link AccountDto}
     */
    public static final AccountDto createGlAccountJaxbCriteriaInstance(GlCriteriaType jaxbCriteria) {
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
    public static final AccountDto createGlAccountJaxbInstance(GlAccountType jaxbObj) {
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
        gatt.setAcctTypeId(BigInteger.valueOf(dto.getAcctId()));
        jaxbObj.setAcctType(gatt);
        GlAccountcatgType gact = jaxbObjFactory.createGlAccountcatgType();
        gact.setAcctCatgId(BigInteger.valueOf(dto.getAcctId()));
        jaxbObj.setAcctCatg(gact);
        jaxbObj.setAccountCode(dto.getAcctCode());
        jaxbObj.setAccountDescription(dto.getAcctDescription());
        jaxbObj.setAccountName(dto.getAcctName());
        jaxbObj.setAccountNo(dto.getAcctNo());
        return jaxbObj;
    }
    
   
}

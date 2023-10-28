package org.rmt2.api.adapters.jaxb;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.dto.BusinessContactDto;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.CodeDetailType;
import org.rmt2.jaxb.ObjectFactory;

/**
 * Adapts a JAXB <i>BusinessContactCriteria</i> object to an
 * <i>BusinessContactDto</i>.
 * 
 * @author Roy Terrell
 * 
 */
class BusinessContactJaxbAdapter extends AddressJaxbAdapter implements BusinessContactDto {

    private BusinessType bus;
    private List<Integer> businessIdList;
    private ObjectFactory f;

    /**
     * Create a BusinessContactCriteriaJaxbAdapter using an instance of
     * <i>VwBusinessAddress</i>, which contains both business and address
     * contact information.
     * 
     * @param bus
     *            an instance of {@link BusinessContactCriteria} or null for the
     *            purpose of creating a new Business object
     */
    protected BusinessContactJaxbAdapter(BusinessType bus) {
        super();
        if (bus == null) {
            f = new ObjectFactory();
            bus = f.createBusinessType();
        }
        this.bus = bus;

        // Setup address DTO
        super.init(bus.getAddress());
        this.businessIdList = new ArrayList<Integer>();
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setBusinessId(int)
     */
    @Override
    public void setContactId(int value) {
        this.bus.setBusinessId(BigInteger.valueOf(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getBusinessId()
     */
    @Override
    public int getContactId() {
        return (this.bus.getBusinessId() != null ? this.bus.getBusinessId().intValue() : 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setEntityTypeId(int)
     */
    @Override
    public void setEntityTypeId(int value) {
        CodeDetailType cdt = null;
        if (this.bus.getEntityType() == null) {
            cdt = this.f.createCodeDetailType();
            this.bus.setEntityType(cdt);
        }
        else {
            cdt = this.bus.getEntityType();
        }
        cdt.setCodeId(BigInteger.valueOf(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getEntityTypeId()
     */
    @Override
    public int getEntityTypeId() {
        if (this.bus.getEntityType() != null) {
            return this.bus.getEntityType().getCodeId().intValue();
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setServTypeId(int)
     */
    @Override
    public void setServTypeId(int value) {
        CodeDetailType cdt = null;
        if (this.bus.getServiceType() == null) {
            cdt = this.f.createCodeDetailType();
            this.bus.setServiceType(cdt);
        }
        else {
            cdt = this.bus.getServiceType();
        }
        cdt.setCodeId(BigInteger.valueOf(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getServTypeId()
     */
    @Override
    public int getServTypeId() {
        if (this.bus.getServiceType() != null) {
            return this.bus.getServiceType().getCodeId().intValue();
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setCompanyName(java.lang.String)
     */
    @Override
    public void setContactName(String value) {
        this.bus.setLongName(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getCompanyName()
     */
    @Override
    public String getContactName() {
        return this.bus.getLongName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setContactFirstname(java.lang.String)
     */
    @Override
    public void setContactFirstname(String value) {
        this.bus.setContactFirstname(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getContactFirstname()
     */
    @Override
    public String getContactFirstname() {
        return this.bus.getContactFirstname();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setContactLastname(java.lang.String)
     */
    @Override
    public void setContactLastname(String value) {
        this.bus.setContactLastname(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getContactLastname()
     */
    @Override
    public String getContactLastname() {
        return this.bus.getContactLastname();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setContactPhone(java.lang.String)
     */
    @Override
    public void setContactPhone(String value) {
        this.bus.setContactPhone(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getContactPhone()
     */
    @Override
    public String getContactPhone() {
        return this.bus.getContactPhone();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setContactExt(java.lang.String)
     */
    @Override
    public void setContactExt(String value) {
        this.bus.setContactExt(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getContactExt()
     */
    @Override
    public String getContactExt() {
        return this.bus.getContactExt();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setContactEmail(java.lang.String)
     */
    @Override
    public void setContactEmail(String value) {
        this.bus.setContactEmail(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getContactEmail()
     */
    @Override
    public String getContactEmail() {
        return this.bus.getContactEmail();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setTaxId(java.lang.String)
     */
    @Override
    public void setTaxId(String value) {
        this.bus.setTaxId(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getTaxId()
     */
    @Override
    public String getTaxId() {
        return this.bus.getTaxId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setWebsite(java.lang.String)
     */
    @Override
    public void setWebsite(String value) {
        this.bus.setWebsite(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getWebsite()
     */
    @Override
    public String getWebsite() {
        return this.bus.getWebsite();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setCategoryId(int)
     */
    @Override
    public void setCategoryId(int value) {
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getCategoryId()
     */
    @Override
    public int getCategoryId() {
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setShortName(java.lang.String)
     */
    @Override
    public void setShortName(String value) {
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getShortName()
     */
    @Override
    public String getShortName() {
        return null;
    }

    @Override
    public void setContactIdList(List<Integer> value) {
        this.businessIdList = value;
    }

    @Override
    public List<Integer> getContactIdList() {
        return this.businessIdList;
    }

    /* (non-Javadoc)
     * @see org.dto.BusinessContactDto#setEntityTypeGrpId(int)
     */
    @Override
    public void setEntityTypeGrpId(int value) {
        CodeDetailType cdt = this.getEntityTypeJaxbOject();
        cdt.setGroupId(BigInteger.valueOf(value));
    }

    /* (non-Javadoc)
     * @see org.dto.BusinessContactDto#getEntityTypeGrpId()
     */
    @Override
    public int getEntityTypeGrpId() {
        CodeDetailType cdt = this.getEntityTypeJaxbOject();
        return cdt.getGroupId().intValue();
    }

    /* (non-Javadoc)
     * @see org.dto.BusinessContactDto#setEntityTypeShortdesc(java.lang.String)
     */
    @Override
    public void setEntityTypeShortdesc(String value) {
        CodeDetailType cdt = this.getEntityTypeJaxbOject();
        cdt.setShortdesc(value);
    }

    /* (non-Javadoc)
     * @see org.dto.BusinessContactDto#getEntityTypeShortdesc()
     */
    @Override
    public String getEntityTypeShortdesc() {
        CodeDetailType cdt = this.getEntityTypeJaxbOject();
        return cdt.getShortdesc();
    }

    /* (non-Javadoc)
     * @see org.dto.BusinessContactDto#setEntityTypeLongdesc(java.lang.String)
     */
    @Override
    public void setEntityTypeLongdesc(String value) {
        CodeDetailType cdt = this.getEntityTypeJaxbOject();
        cdt.setLongdesc(value);
    }

    /* (non-Javadoc)
     * @see org.dto.BusinessContactDto#getEntityTypeLongtdesc()
     */
    @Override
    public String getEntityTypeLongtdesc() {
        CodeDetailType cdt = this.getEntityTypeJaxbOject();
        return cdt.getLongdesc();
    }

    /* (non-Javadoc)
     * @see org.dto.BusinessContactDto#setServTypeGrpId(int)
     */
    @Override
    public void setServTypeGrpId(int value) {
        CodeDetailType cdt = this.getServTypeJaxbOject();
        cdt.setGroupId(BigInteger.valueOf(value));
        
    }

    /* (non-Javadoc)
     * @see org.dto.BusinessContactDto#getServTypeGrpId()
     */
    @Override
    public int getServTypeGrpId() {
        CodeDetailType cdt = this.getServTypeJaxbOject();
        return cdt.getGroupId().intValue();
    }

    /* (non-Javadoc)
     * @see org.dto.BusinessContactDto#setServTypeShortdesc(java.lang.String)
     */
    @Override
    public void setServTypeShortdesc(String value) {
        CodeDetailType cdt = this.getServTypeJaxbOject();
        cdt.setShortdesc(value);
        
    }

    /* (non-Javadoc)
     * @see org.dto.BusinessContactDto#getServTypeShortdesc()
     */
    @Override
    public String getServTypeShortdesc() {
        CodeDetailType cdt = this.getServTypeJaxbOject();
        return cdt.getShortdesc();
    }

    /* (non-Javadoc)
     * @see org.dto.BusinessContactDto#setServTypeLongdesc(java.lang.String)
     */
    @Override
    public void setServTypeLongdesc(String value) {
        CodeDetailType cdt = this.getServTypeJaxbOject();
        cdt.setLongdesc(value);
    }

    /* (non-Javadoc)
     * @see org.dto.BusinessContactDto#getServTypeLongtdesc()
     */
    @Override
    public String getServTypeLongtdesc() {
        CodeDetailType cdt = this.getServTypeJaxbOject();
        return cdt.getLongdesc();
    }

    private CodeDetailType getEntityTypeJaxbOject() {
        CodeDetailType cdt = null;
        if (this.bus.getEntityType() == null) {
            cdt = this.f.createCodeDetailType();
            this.bus.setEntityType(cdt);
        }
        else {
            cdt = this.bus.getEntityType();
        }
        return cdt;
    }

    private CodeDetailType getServTypeJaxbOject() {
        CodeDetailType cdt = null;
        if (this.bus.getServiceType() == null) {
            cdt = this.f.createCodeDetailType();
            this.bus.setServiceType(cdt);
        }
        else {
            cdt = this.bus.getServiceType();
        }
        return cdt;
    }
}

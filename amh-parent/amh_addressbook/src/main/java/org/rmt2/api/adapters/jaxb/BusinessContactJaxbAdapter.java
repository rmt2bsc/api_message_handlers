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
        CodeDetailType cdt = this.f.createCodeDetailType();
        cdt.setCodeId(BigInteger.valueOf(value));
        this.bus.setEntityType(cdt);
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
        CodeDetailType cdt = this.f.createCodeDetailType();
        cdt.setCodeId(BigInteger.valueOf(value));
        this.bus.setServiceType(cdt);
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
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getContactExt()
     */
    @Override
    public String getContactExt() {
        return null;
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
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getWebsite()
     */
    @Override
    public String getWebsite() {
        return null;
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
}

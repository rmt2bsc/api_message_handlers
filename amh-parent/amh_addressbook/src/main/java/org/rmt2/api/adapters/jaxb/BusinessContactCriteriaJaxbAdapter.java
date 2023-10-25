package org.rmt2.api.adapters.jaxb;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.dto.BusinessContactDto;
import org.dto.DefaultAddressAdapter;
import org.rmt2.jaxb.BusinessContactCriteria;
import org.rmt2.jaxb.ObjectFactory;

import com.api.util.RMT2Money;

/**
 * Adapts a JAXB <i>BusinessContactCriteria</i> object to an
 * <i>BusinessContactDto</i>.
 * 
 * @author Roy Terrell
 * 
 */
class BusinessContactCriteriaJaxbAdapter extends DefaultAddressAdapter implements BusinessContactDto {

    private BusinessContactCriteria criteria;

    /**
     * Create a BusinessContactCriteriaJaxbAdapter using an instance of
     * <i>BusinessContactCriteria</i>, which contains business selection
     * criteria contact information.
     * 
     * @param criteria
     *            an instance of {@link BusinessContactCriteria} or null for the
     *            purpose of creating a new BusinessContactDto object
     */
    protected BusinessContactCriteriaJaxbAdapter(BusinessContactCriteria bus) {
        super();
        if (bus == null) {
            ObjectFactory f = new ObjectFactory();
            bus = f.createBusinessContactCriteria();
        }
        this.criteria = bus;
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setBusinessId(int)
     */
    @Override
    public void setContactId(int value) {
        if (this.criteria.getBusinessId() != null) {
            this.criteria.getBusinessId().add(BigInteger.valueOf(value));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getBusinessId()
     */
    @Override
    public int getContactId() {
        return (this.criteria.getContactId() != null ? this.criteria.getContactId().intValue() : 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setEntityTypeId(int)
     */
    @Override
    public void setEntityTypeId(int value) {
        this.criteria.setEntityType(BigInteger.valueOf(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getEntityTypeId()
     */
    @Override
    public int getEntityTypeId() {
        if (this.criteria.getEntityType() != null) {
            return this.criteria.getEntityType().intValue();
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
        this.criteria.setServiceType(BigInteger.valueOf(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getServTypeId()
     */
    @Override
    public int getServTypeId() {
        if (this.criteria.getServiceType() != null) {
            return this.criteria.getServiceType().intValue();
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
        this.criteria.setBusinessName(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getCompanyName()
     */
    @Override
    public String getContactName() {
        return this.criteria.getBusinessName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setContactFirstname(java.lang.String)
     */
    @Override
    public void setContactFirstname(String value) {
        this.criteria.setContactFname(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getContactFirstname()
     */
    @Override
    public String getContactFirstname() {
        return this.criteria.getContactFname();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setContactLastname(java.lang.String)
     */
    @Override
    public void setContactLastname(String value) {
        this.criteria.setContactLname(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getContactLastname()
     */
    @Override
    public String getContactLastname() {
        return this.criteria.getContactLname();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setContactPhone(java.lang.String)
     */
    @Override
    public void setContactPhone(String value) {
        this.criteria.setMainPhone(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getContactPhone()
     */
    @Override
    public String getContactPhone() {
        return this.criteria.getMainPhone();
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
        this.criteria.setContactEmail(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getContactEmail()
     */
    @Override
    public String getContactEmail() {
        return this.criteria.getContactEmail();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setTaxId(java.lang.String)
     */
    @Override
    public void setTaxId(String value) {
        this.criteria.setTaxId(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getTaxId()
     */
    @Override
    public String getTaxId() {
        return this.criteria.getTaxId();
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
        if (value == null) {
            return;
        }
        for (Integer item : value) {
            this.criteria.getBusinessId().add(BigInteger.valueOf(item));    
        }
    }

    @Override
    public List<Integer> getContactIdList() {
        List<Integer> intList = new ArrayList<Integer>();
        List<BigInteger> list = this.criteria.getBusinessId(); 
        for (BigInteger item : list) {
            intList.add(item.intValue());    
        }
        return intList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.DefaultAddressAdapter#setZip(int)
     */
    @Override
    public void setZip(int value) {
        this.criteria.setZipcode(String.valueOf(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.DefaultAddressAdapter#getZip()
     */
    @Override
    public int getZip() {
        if (this.criteria.getZipcode() != null && RMT2Money.isNumeric(this.criteria.getZipcode())) {
            return Integer.valueOf(this.criteria.getZipcode());
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.DefaultAddressAdapter#setCity(java.lang.String)
     */
    @Override
    public void setCity(String value) {
        this.criteria.setCity(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.DefaultAddressAdapter#getCity()
     */
    @Override
    public String getCity() {
        return this.criteria.getCity();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.DefaultAddressAdapter#setState(java.lang.String)
     */
    @Override
    public void setState(String value) {
        this.criteria.setState(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.DefaultAddressAdapter#getState()
     */
    @Override
    public String getState() {
        return this.criteria.getState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setEntityTypeGrpId(int)
     */
    @Override
    public void setEntityTypeGrpId(int value) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getEntityTypeGrpId()
     */
    @Override
    public int getEntityTypeGrpId() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setEntityTypeShortdesc(java.lang.String)
     */
    @Override
    public void setEntityTypeShortdesc(String value) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getEntityTypeShortdesc()
     */
    @Override
    public String getEntityTypeShortdesc() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setEntityTypeLongdesc(java.lang.String)
     */
    @Override
    public void setEntityTypeLongdesc(String value) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getEntityTypeLongtdesc()
     */
    @Override
    public String getEntityTypeLongtdesc() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setServTypeGrpId(int)
     */
    @Override
    public void setServTypeGrpId(int value) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getServTypeGrpId()
     */
    @Override
    public int getServTypeGrpId() {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setServTypeShortdesc(java.lang.String)
     */
    @Override
    public void setServTypeShortdesc(String value) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getServTypeShortdesc()
     */
    @Override
    public String getServTypeShortdesc() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#setServTypeLongdesc(java.lang.String)
     */
    @Override
    public void setServTypeLongdesc(String value) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.BusinessContactDto#getServTypeLongtdesc()
     */
    @Override
    public String getServTypeLongtdesc() {
        // TODO Auto-generated method stub
        return null;
    }

}

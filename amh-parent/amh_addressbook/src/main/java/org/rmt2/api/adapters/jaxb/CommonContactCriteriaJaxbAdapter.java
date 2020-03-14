package org.rmt2.api.adapters.jaxb;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.dto.ContactDto;
import org.dto.DefaultAddressAdapter;
import org.rmt2.jaxb.CommonContactCriteria;
import org.rmt2.jaxb.ObjectFactory;

/**
 * Adapts a JAXB <i>CommonContactCriteria</i> object to an
 * <i>ContactDto</i>.
 * 
 * @author Roy Terrell
 * 
 */
class CommonContactCriteriaJaxbAdapter extends DefaultAddressAdapter implements ContactDto {

    private CommonContactCriteria criteria;

    /**
     * Create a CommonContactCriteriaJaxbAdapter using an instance of
     * <i>CommonContactCriteria</i>, which contains both person selection
     * criteria contact information.
     * 
     * @param criteria
     *            an instance of {@link CommonContactCriteria} or null for the
     *            purpose of creating a new ContactDto object
     */
    protected CommonContactCriteriaJaxbAdapter(CommonContactCriteria obj) {
        super();
        if (obj == null) {
            ObjectFactory f = new ObjectFactory();
            obj = f.createCommonContactCriteria();
        }
        this.criteria = obj;
        return;
    }

    @Override
    public void setContactId(int value) {
        if (this.criteria.getContactId() != null) {
            this.criteria.getContactId().add(BigInteger.valueOf(value));
        }
    }

    @Override
    public int getContactId() {
        return (this.criteria.getContactId() != null
                && this.criteria.getContactId().size() == 1
                        ? this.criteria.getContactId().get(0).intValue() : 0);
    }

    @Override
    public void setContactName(String value) {
        return;
    }

    @Override
    public String getContactName() {
        return this.criteria.getContactName();
    }

    @Override
    public void setContactEmail(String value) {
        return;        
    }

    @Override
    public String getContactEmail() {
        return null;
    }
    
    @Override
    public void setContactIdList(List<Integer> value) {
        if (value == null) {
            return;
        }
        for (Integer item : value) {
            this.criteria.getContactId().add(BigInteger.valueOf(item));
        }
    }

    @Override
    public List<Integer> getContactIdList() {
        List<Integer> intList = new ArrayList<Integer>();
        List<BigInteger> list = this.criteria.getContactId(); 
        for (BigInteger item : list) {
            intList.add(item.intValue());    
        }
        return intList;
    }

    @Override
    public void setZip(int value) {
        this.criteria.setZipcode(String.valueOf(value));
    }

    @Override
    public int getZip() {
        if (this.criteria.getZipcode() != null) {
            return Integer.valueOf(this.criteria.getZipcode());
        }
        return 0;
    }

    @Override
    public void setCity(String value) {
        this.criteria.setCity(value);
    }

    @Override
    public String getCity() {
        return this.criteria.getCity();
    }

    @Override
    public void setState(String value) {
        this.criteria.setState(value);
    }

    @Override
    public String getState() {
        return this.criteria.getState();
    }

    @Override
    public void setPhoneCompany(String value) {
        this.criteria.setMainPhone(value);
    }

    @Override
    public String getPhoneCompany() {
        return this.criteria.getMainPhone();
    }

}

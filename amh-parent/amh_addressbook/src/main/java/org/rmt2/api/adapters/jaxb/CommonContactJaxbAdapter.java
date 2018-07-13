package org.rmt2.api.adapters.jaxb;

import java.math.BigInteger;
import java.util.List;

import org.dto.ContactDto;
import org.dto.DefaultAddressAdapter;
import org.rmt2.jaxb.CommonContactType;
import org.rmt2.jaxb.ContacttypeType;
import org.rmt2.jaxb.ObjectFactory;

/**
 * Adapts a JAXB <i>CommonContactType</i> object to an
 * <i>ContactDto</i>.
 * 
 * @author Roy Terrell
 * 
 */
class CommonContactJaxbAdapter extends DefaultAddressAdapter implements ContactDto {

    private CommonContactType cct;
    private List<Integer> contactIdList;

    /**
     * Create a CommonContactJaxbAdapter using an instance of
     * <i>CommonContactType</i>, which contains both person selection
     * criteria contact information.
     * 
     * @param criteria
     *            an instance of {@link CommonContactType} or null for the
     *            purpose of creating a new ContactDto object
     */
    protected CommonContactJaxbAdapter(CommonContactType obj) {
        super();
        if (obj == null) {
            ObjectFactory f = new ObjectFactory();
            obj = f.createCommonContactType();
        }
        this.cct = obj;
        return;
    }

    @Override
    public void setContactId(int value) {
        if (this.cct.getContactId() != null) {
            this.cct.setContactId(BigInteger.valueOf(value));
        }
    }

    @Override
    public int getContactId() {
        if (this.cct.getContactId() != null) {
            return this.cct.getContactId().intValue();
        }
        return 0;
    }

    @Override
    public void setContactName(String value) {
        this.cct.setContactName(value);
    }

    @Override
    public String getContactName() {
        return this.cct.getContactName();
    }

    @Override
    public void setContactEmail(String value) {
        this.cct.setContactEmail(value);        
    }

    @Override
    public String getContactEmail() {
        return this.cct.getContactEmail();
    }
    
    @Override
    public void setContactIdList(List<Integer> value) {
        this.contactIdList = value;
    }

    @Override
    public List<Integer> getContactIdList() {
        return contactIdList;
    }

    @Override
    public void setZip(int value) {
        if (this.cct.getAddress() != null
                && this.cct.getAddress().getZip() != null
                && this.cct.getAddress().getZip().getZipcode() != null) {
            this.cct.getAddress().getZip().setZipcode(BigInteger.valueOf(value));
        }
    }

    @Override
    public int getZip() {
        if (this.cct.getAddress() != null
                && this.cct.getAddress().getZip() != null
                && this.cct.getAddress().getZip().getZipcode() != null) {
            return this.cct.getAddress().getZip().getZipcode().intValue();
        }
        return 0;
    }

    @Override
    public void setCity(String value) {
        if (this.cct.getAddress() != null && this.cct.getAddress().getZip() != null) {
            this.cct.getAddress().getZip().setCity(value);
        }
    }

    @Override
    public String getCity() {
        if (this.cct.getAddress() != null && this.cct.getAddress().getZip() != null) {
            return this.cct.getAddress().getZip().getCity();
        }
        return null;
    }

    @Override
    public void setState(String value) {
        if (this.cct.getAddress() != null && this.cct.getAddress().getZip() != null) {
            this.cct.getAddress().getZip().setState(value);
        }
    }

    @Override
    public String getState() {
        if (this.cct.getAddress() != null && this.cct.getAddress().getZip() != null) {
            return this.cct.getAddress().getZip().getState();
        }
        return null;
    }

    @Override
    public void setPhoneCompany(String value) {
        if (this.cct.getAddress() != null) {
            this.cct.getAddress().setPhoneMain(value);
        }
    }

    @Override
    public String getPhoneCompany() {
        if (this.cct.getAddress() != null) {
            return this.cct.getAddress().getPhoneMain();
        }
        return null;
    }

    @Override
    public void setContactType(String value) {
        this.cct.setContactType(ContacttypeType.valueOf(value));
    }

    @Override
    public String getContactType() {
        return this.cct.getContactType().value();
    }

}

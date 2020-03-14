package org.rmt2.api.adapters.jaxb;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dto.DefaultAddressAdapter;
import org.dto.PersonalContactDto;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.PersonContactCriteria;

import com.api.util.RMT2Date;

/**
 * Adapts a JAXB <i>PersonContactCriteria</i> object to an
 * <i>PersonalContactDto</i>.
 * 
 * @author Roy Terrell
 * 
 */
class PersonContactCriteriaJaxbAdapter extends DefaultAddressAdapter implements PersonalContactDto {

    private PersonContactCriteria criteria;

    /**
     * Create a PersonContactCriteriaJaxbAdapter using an instance of
     * <i>PersonContactCriteria</i>, which contains both person selection
     * criteria contact information.
     * 
     * @param criteria
     *            an instance of {@link PersonContactCriteria} or null for the
     *            purpose of creating a new PersonalContactDto object
     */
    protected PersonContactCriteriaJaxbAdapter(PersonContactCriteria obj) {
        super();
        if (obj == null) {
            ObjectFactory f = new ObjectFactory();
            obj = f.createPersonContactCriteria();
        }
        this.criteria = obj;
        return;
    }

    @Override
    public void setContactId(int value) {
        if (this.criteria.getPersonId() != null) {
            this.criteria.getPersonId().add(BigInteger.valueOf(value));
        }
    }

    @Override
    public int getContactId() {
        return (this.criteria.getContactId() != null ? this.criteria.getContactId().intValue() : 0);
    }

    @Override
    public void setContactName(String value) {
        return;
    }

    @Override
    public String getContactName() {
        return this.criteria.getFirstName() + " " + this.criteria.getLastName();
    }

    @Override
    public void setFirstname(String value) {
        this.criteria.setFirstName(value);
    }

    @Override
    public String getFirstname() {
        return this.criteria.getFirstName();
    }

    @Override
    public void setLastname(String value) {
        this.criteria.setLastName(value);
    }

    @Override
    public String getLastname() {
        return this.criteria.getLastName();
    }

    @Override
    public void setContactEmail(String value) {
        this.criteria.setEmail(value);
        
    }

    @Override
    public String getContactEmail() {
        return this.criteria.getEmail();
    }

    @Override
    public void setMidname(String value) {
        return;
    }

    @Override
    public String getMidname() {
        return null;
    }

    @Override
    public void setMaidenname(String value) {
        return;        
    }

    @Override
    public String getMaidenname() {
        return null;
    }

    @Override
    public void setGeneration(String value) {
        return;
    }

    @Override
    public String getGeneration() {
        return null;
    }

    @Override
    public void setTitle(int value) {
        return;        
    }

    @Override
    public int getTitle() {
        return 0;
    }

    @Override
    public void setGenderId(int value) {
        this.criteria.setGender(BigInteger.valueOf(value));
        
    }

    @Override
    public int getGenderId() {
        if (this.criteria.getGender() != null) {
            return this.criteria.getGender().intValue();
        }
        return 0;
    }

    @Override
    public void setMaritalStatusId(int value) {
        this.criteria.setMaritalStatus(BigInteger.valueOf(value));
        
    }

    @Override
    public int getMaritalStatusId() {
        if (this.criteria.getMaritalStatus() != null) {
            return this.criteria.getMaritalStatus().intValue();
        }
        return 0;
    }

    @Override
    public void setBirthDate(Date value) {
        this.criteria.setBirthDate(RMT2Date.formatDate(value, "yyyy-MM-dd"));
        
    }

    @Override
    public Date getBirthDate() {
        if (this.criteria.getBirthDate() != null) {
            return RMT2Date.stringToDate(this.criteria.getBirthDate());
        }
        return null;
    }

    @Override
    public void setRaceId(int value) {
        this.criteria.setRace(BigInteger.valueOf(value));
    }

    @Override
    public int getRaceId() {
        if (this.criteria.getRace() != null) {
            return this.criteria.getRace().intValue();
        }
        return 0;
    }

    @Override
    public void setSsn(String value) {
        this.criteria.setSsn(value);
        
    }

    @Override
    public String getSsn() {
        return this.criteria.getSsn();
    }

    @Override
    public void setCategoryId(int value) {
        return;
    }

    @Override
    public int getCategoryId() {
        return 0;
    }

  
    @Override
    public void setContactIdList(List<Integer> value) {
        if (value == null) {
            return;
        }
        for (Integer item : value) {
            this.criteria.getPersonId().add(BigInteger.valueOf(item));    
        }
    }

    @Override
    public List<Integer> getContactIdList() {
        List<Integer> intList = new ArrayList<Integer>();
        List<BigInteger> list = this.criteria.getPersonId(); 
        for (BigInteger item : list) {
            intList.add(item.intValue());    
        }
        return intList;
    }
   

}

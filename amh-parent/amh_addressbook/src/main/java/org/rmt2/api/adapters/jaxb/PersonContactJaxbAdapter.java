package org.rmt2.api.adapters.jaxb;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.dto.PersonalContactDto;
import org.rmt2.jaxb.CodeDetailType;
import org.rmt2.jaxb.GenerationType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.PersonType;

import com.api.util.RMT2Date;

/**
 * Adapts a JAXB <i>PersonType</i> object to an
 * <i>PersonalContactDto</i>.
 * 
 * @author Roy Terrell
 * 
 */
class PersonContactJaxbAdapter extends AddressJaxbAdapter implements PersonalContactDto {

    private PersonType person;
    private List<Integer> personIdList;
    private ObjectFactory f;

    /**
     * Create a PersonContactJaxbAdapter using an instance of
     * <i>PersonType</i>, which contains both person selection
     * criteria contact information.
     * 
     * @param criteria
     *            an instance of {@link PersonContactCriteria} or null for the
     *            purpose of creating a new PersonType object
     */
    protected PersonContactJaxbAdapter(PersonType obj) {
        super(obj.getAddress());
        this.person = obj;
        return;
    }

    @Override
    public void setContactId(int value) {
        if (this.person.getPersonId() != null) {
            this.person.getPersonId().add(BigInteger.valueOf(value));
        }
    }

    @Override
    public int getContactId() {
        return (this.person.getPersonId() != null ? this.person.getPersonId().intValue() : 0);
    }

    @Override
    public void setContactName(String value) {
        return;
    }

    @Override
    public String getContactName() {
        return this.person.getFirstName() + " " + this.person.getLastName();
    }

    @Override
    public void setFirstname(String value) {
        this.person.setFirstName(value);
    }

    @Override
    public String getFirstname() {
        return this.person.getFirstName();
    }

    @Override
    public void setLastname(String value) {
        this.person.setLastName(value);
    }

    @Override
    public String getLastname() {
        return this.person.getLastName();
    }

    @Override
    public void setContactEmail(String value) {
        this.person.setEmail(value);
        
    }

    @Override
    public String getContactEmail() {
        return this.person.getEmail();
    }

    @Override
    public void setMidname(String value) {
        this.person.setMidName(value);
    }

    @Override
    public String getMidname() {
        return this.person.getMidName();
    }

    @Override
    public void setMaidenname(String value) {
        this.person.setMaidenName(value);;        
    }

    @Override
    public String getMaidenname() {
        return this.person.getMaidenName();
    }

    @Override
    public void setGeneration(String value) {
        this.person.setGeneration(GenerationType.valueOf(value));
    }

    @Override
    public String getGeneration() {
        if (this.person.getGeneration() != null) {
            return this.person.getGeneration().value();
        }
        return null;
    }

    @Override
    public void setTitle(int value) {
        CodeDetailType cdt = this.f.createCodeDetailType();
        cdt.setCodeId(BigInteger.valueOf(value));
        this.person.setTitle(cdt);        
    }

    @Override
    public int getTitle() {
        if (this.person.getTitle() != null) {
            return this.person.getTitle().getCodeId().intValue();
        }
        return 0;
    }

    @Override
    public void setGenderId(int value) {
        CodeDetailType cdt = this.f.createCodeDetailType();
        cdt.setCodeId(BigInteger.valueOf(value));
        this.person.setGender(cdt);
        
    }

    @Override
    public int getGenderId() {
        if (this.person.getGender() != null) {
            return this.person.getGender().getCodeId().intValue();
        }
        return 0;
    }

    @Override
    public void setMaritalStatusId(int value) {
        CodeDetailType cdt = this.f.createCodeDetailType();
        cdt.setCodeId(BigInteger.valueOf(value));
        this.person.setMaritalStatus(cdt);
        
    }

    @Override
    public int getMaritalStatusId() {
        if (this.person.getMaritalStatus() != null) {
            return this.person.getMaritalStatus().getCodeId().intValue();
        }
        return 0;
    }

    @Override
    public void setBirthDate(Date value) {
        this.person.setBirthDate(RMT2Date.formatDate(value, "yyyy-MM-dd"));
        
    }

    @Override
    public Date getBirthDate() {
        if (this.person.getBirthDate() != null) {
            return RMT2Date.stringToDate(this.person.getBirthDate());
        }
        return null;
    }

    @Override
    public void setRaceId(int value) {
        CodeDetailType cdt = this.f.createCodeDetailType();
        cdt.setCodeId(BigInteger.valueOf(value));
        this.person.setRace(cdt);
    }

    @Override
    public int getRaceId() {
        if (this.person.getRace() != null) {
            return this.person.getRace().getCodeId().intValue();
        }
        return 0;
    }

    @Override
    public void setSsn(String value) {
        this.person.setSsn(value);
        
    }

    @Override
    public String getSsn() {
        return this.person.getSsn();
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
        personIdList = value;
    }

    @Override
    public List<Integer> getContactIdList() {
        return personIdList;
    }
   

}

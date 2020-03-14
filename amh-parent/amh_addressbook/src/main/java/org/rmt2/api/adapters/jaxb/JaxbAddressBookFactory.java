package org.rmt2.api.adapters.jaxb;

import org.dto.AddressDto;
import org.dto.BusinessContactDto;
import org.dto.ContactDto;
import org.dto.PersonalContactDto;
import org.rmt2.jaxb.AddressType;
import org.rmt2.jaxb.BusinessContactCriteria;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.CommonContactCriteria;
import org.rmt2.jaxb.CommonContactType;
import org.rmt2.jaxb.PersonContactCriteria;
import org.rmt2.jaxb.PersonType;

import com.RMT2Base;

/**
 * A factory containing several adapter methods for transforming JAXB objects to
 * DTO's.
 * 
 * @author Roy Terrell.
 * 
 */
public class JaxbAddressBookFactory extends RMT2Base {

    /**
     * Creates an instance of <i>ContactDto</i> using a valid
     * <i>CommonContactCriteria</i> object.
     * 
     * @param criteria
     *            an instance of {@link CommonContactCriteria}
     * @return an instance of {@link ContactDto}
     */
    public static final ContactDto createContactDtoInstance(CommonContactCriteria criteria) {
        ContactDto dto = new CommonContactCriteriaJaxbAdapter(criteria);
        return dto;
    }
    
    /**
     * Creates an instance of <i>ContactDto</i> using a valid
     * <i>CommonContactType</i> object.
     * 
     * @param criteria
     *            an instance of {@link CommonContactType}
     * @return an instance of {@link ContactDto}
     */
    public static final ContactDto createContactDtoInstance(CommonContactType criteria) {
        ContactDto dto = new CommonContactJaxbAdapter(criteria);
        return dto;
    }
    
    /**
     * Creates an instance of <i>PersonalContactDto</i> using a valid
     * <i>PersonContactCriteria</i> object.
     * 
     * @param criteria
     *            an instance of {@link PersonContactCriteria}
     * @return an instance of {@link PersonalContactDto}
     */
    public static final PersonalContactDto createPersonContactDtoInstance(PersonContactCriteria criteria) {
        PersonalContactDto dto = new PersonContactCriteriaJaxbAdapter(criteria);
        return dto;
    }
    
    /**
     * Creates an instance of <i>PersonalContactDto</i> using a valid
     * <i>PersonType</i> object.
     * 
     * @param person
     *            an instance of {@link PersonType}
     * @return an instance of {@link PersonalContactDto}
     */
    public static final PersonalContactDto createPersonContactDtoInstance(PersonType person) {
        PersonalContactDto dto = new PersonContactJaxbAdapter(person);
        return dto;
    }
    
    /**
     * Creates an instance of <i>BusinessContactDto</i> using a valid
     * <i>BusinessContactCriteria</i> object.
     * 
     * @param criteria
     *            an instance of {@link BusinessContactCriteria}
     * @return an instance of {@link BusinessContactDto}
     */
    public static final BusinessContactDto createBusinessContactDtoInstance(BusinessContactCriteria criteria) {
        BusinessContactDto dto = new BusinessContactCriteriaJaxbAdapter(criteria);
        return dto;
    }

    /**
     * Creates an instance of <i>BusinessContactDto</i> using a valid
     * <i>BusinessType</i> object.
     * 
     * @param bus
     *            an instance of {@link BusinessType}
     * @return an instance of {@link BusinessContactDto}
     */
    public static final BusinessContactDto createBusinessContactDtoInstance(BusinessType bus) {
        BusinessContactDto dto = new BusinessContactJaxbAdapter(bus);
        return dto;
    }

    /**
     * Creates an instance of <i>AddressDto</i> using a valid <i>AddressType</i>
     * object.
     * 
     * @param addr
     *            an instance of {@link AddressType}
     * @return an instance of {@link AddressDto}
     */
    public static final AddressDto createAddressDtoInstance(AddressType addr) {
        AddressDto dto = new AddressJaxbAdapter(addr);
        return dto;
    }
}

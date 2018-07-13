package org.rmt2.api.adapters.jaxb;

import java.math.BigInteger;

import org.dto.AddressDto;
import org.rmt2.jaxb.AddressType;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.ZipcodeType;

import com.api.foundation.TransactionDtoImpl;

/**
 * Adapts an JAXB <i>Address</i> object to an <i>AddressDto</i>.
 * 
 * @author rterrell
 * 
 */
public class AddressJaxbAdapter extends TransactionDtoImpl implements AddressDto {

    protected ObjectFactory f;

    protected AddressType src;

    private String contactType;

    private String city;

    private String state;

    /**
     * Default Constructor
     */
    protected AddressJaxbAdapter() {
        return;
    }

    /**
     * Create a AddressJaxbAdapter using an instance of <i>AddressType</i>.
     * 
     * @param contact
     *            an instance of {@link AddressType} containing the address data
     *            of the contact or null for the purpose of creating a new
     *            AddressType object
     */
    protected AddressJaxbAdapter(AddressType contact) {
        super();
        this.init(contact);
        return;
    }

    protected void init(AddressType contact) {
        if (contact == null) {
            f = new ObjectFactory();
            contact = f.createAddressType();
        }
        this.src = contact;
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setAddrId(int)
     */
    @Override
    public void setAddrId(int value) {
        this.src.setAddrId(BigInteger.valueOf(value));

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getAddrId()
     */
    @Override
    public int getAddrId() {
        if (this.src.getAddrId() != null) {
            return this.src.getAddrId().intValue();
        }
        return 0;
    }

    /**
     * Sets the contact's type.
     * 
     * @param value
     *            equals either <i>per</i> or <i>bus</i> for Personal or
     *            Business, respectively.
     */
    public void setContactType(String value) {
        this.contactType = value;
    }

    /**
     * Returns the contact's type.
     * 
     * @return equals either <i>per</i> or <i>bus</i> for Personal or Business,
     *         respectively.
     */
    public String getContactType() {
        return this.contactType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setAddr1(java.lang.String)
     */
    @Override
    public void setAddr1(String value) {
        this.src.setAddr1(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getAddr1()
     */
    @Override
    public String getAddr1() {
        return this.src.getAddr1();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setAddr2(java.lang.String)
     */
    @Override
    public void setAddr2(String value) {
        this.src.setAddr2(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getAddr2()
     */
    @Override
    public String getAddr2() {
        return this.src.getAddr2();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setAddr3(java.lang.String)
     */
    @Override
    public void setAddr3(String value) {
        this.src.setAddr3(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getAddr3()
     */
    @Override
    public String getAddr3() {
        return this.src.getAddr3();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setAddr4(java.lang.String)
     */
    @Override
    public void setAddr4(String value) {
        this.src.setAddr4(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getAddr4()
     */
    @Override
    public String getAddr4() {
        return this.src.getAddr4();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setZip(int)
     */
    @Override
    public void setZip(int value) {
        ZipcodeType z = this.f.createZipcodeType();
        z.setZipcode(BigInteger.valueOf(value));
        this.src.setZip(z);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getZip()
     */
    @Override
    public int getZip() {
        if (this.src.getZip() != null && this.src.getZip().getZipcode() != null) {
            return this.src.getZip().getZipcode().intValue();
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setZipext(int)
     */
    @Override
    public void setZipext(int value) {
        this.src.setZipExt(BigInteger.valueOf(value));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getZipext()
     */
    @Override
    public int getZipext() {
        if (this.src.getZipExt() != null) {
            return this.src.getZipExt().intValue();
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setPhoneHome(java.lang.String)
     */
    @Override
    public void setPhoneHome(String value) {
        this.src.setPhoneHome(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getPhoneHome()
     */
    @Override
    public String getPhoneHome() {
        return this.src.getPhoneHome();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setPhoneWork(java.lang.String)
     */
    @Override
    public void setPhoneWork(String value) {
        this.src.setPhoneWork(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getPhoneWork()
     */
    @Override
    public String getPhoneWork() {
        return this.src.getPhoneWork();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setPhoneExt(java.lang.String)
     */
    @Override
    public void setPhoneExt(String value) {
        this.src.setPhoneWorkExt(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getPhoneExt()
     */
    @Override
    public String getPhoneExt() {
        return this.src.getPhoneWorkExt();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setPhoneCompany(java.lang.String)
     */
    @Override
    public void setPhoneCompany(String value) {
        this.src.setPhoneMain(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getPhoneCompany()
     */
    @Override
    public String getPhoneCompany() {
        return this.src.getPhoneMain();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setPhoneCell(java.lang.String)
     */
    @Override
    public void setPhoneCell(String value) {
        this.src.setPhoneCell(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getPhoneCell()
     */
    @Override
    public String getPhoneCell() {
        return this.src.getPhoneCell();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setPhoneFax(java.lang.String)
     */
    @Override
    public void setPhoneFax(String value) {
        this.src.setPhoneFax(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getPhoneFax()
     */
    @Override
    public String getPhoneFax() {
        return this.src.getPhoneFax();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setPhonePager(java.lang.String)
     */
    @Override
    public void setPhonePager(String value) {
        this.src.setPhonePager(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getPhonePager()
     */
    @Override
    public String getPhonePager() {
        return this.src.getPhonePager();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setCity(java.lang.String)
     */
    @Override
    public void setCity(String value) {
        this.city = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getCity()
     */
    @Override
    public String getCity() {
        return this.city;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#setState(java.lang.String)
     */
    @Override
    public void setState(String value) {
        this.state = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.dto.AddressDto#getState()
     */
    @Override
    public String getState() {
        return this.state;
    }

}

package org.rmt2.api.handler.contacts;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.math.BigInteger;
import java.util.List;

import org.dao.contacts.ContactDaoException;
import org.dao.mapping.orm.rmt2.Address;
import org.dao.mapping.orm.rmt2.Business;
import org.dto.ContactDto;
import org.dto.ZipcodeDto;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiException;
import org.modules.contacts.ContactsApiFactory;
import org.modules.postal.PostalApi;
import org.modules.postal.PostalApiFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.rmt2.api.handler.AddressBookMockData;
import org.rmt2.api.handler.BaseAddressBookMessageHandlerTest;
import org.rmt2.api.handlers.contacts.ContactProfileApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AddressBookResponse;

import com.api.config.SystemConfigurator;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.persistence.AbstractDaoClientImpl;
import com.api.persistence.db.orm.Rmt2OrmClientFactory;
import com.api.util.RMT2File;

/**
 * 
 * @author roy.terrell
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AbstractDaoClientImpl.class, Rmt2OrmClientFactory.class,
        ContactProfileApiHandler.class, PostalApiFactory.class,
        SystemConfigurator.class })
public class ContactProfileMessageHandlerTest extends BaseAddressBookMessageHandlerTest {

    private ContactsApiFactory mockApiFactory;
    private ContactsApi mockApi;


    /**
     * 
     */
    public ContactProfileMessageHandlerTest() {
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see testcases.messaging.MessageToListenerToHandlerTest#setUp()
     */
    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        
        PowerMockito.mockStatic(PostalApiFactory.class);
        PostalApi mockPostalApi = Mockito.mock(PostalApi.class);
        ZipcodeDto mockZipcodeDto = AddressBookMockData.createZipcodeOrm(75232, 75232, "Tx", "Dallas", "214", "Dallas", 6);
        when(PostalApiFactory.createApi()).thenReturn(mockPostalApi);
        when(mockPostalApi.getZipCode(isA(Integer.class))).thenReturn(mockZipcodeDto);
        
        return;
    }

    private void setupMockContactApiCall() {
      this.mockApiFactory = Mockito.mock(ContactsApiFactory.class);
      this.mockApi = Mockito.mock(ContactsApi.class);
      try {
          PowerMockito.whenNew(ContactsApiFactory.class).withNoArguments().thenReturn(this.mockApiFactory);
      } catch (Exception e) {
          e.printStackTrace();
      }
      when(this.mockApiFactory.createApi()).thenReturn(this.mockApi);
      doNothing().when(this.mockApi).close();
    }
    
    private void setupMockForContactInsert() {
        try {
            when(this.mockPersistenceClient.insertRow(isA(Business.class), any(Boolean.class)))
                    .thenReturn(1351);
        } catch (ContactDaoException e) {
            e.printStackTrace();
            Assert.fail("Business contact insert test case failed setting up update call");
        }
        
        try {
            when(this.mockPersistenceClient.insertRow(isA(Address.class), any(Boolean.class))).thenReturn(2222);
        } catch (ContactDaoException e) {
            e.printStackTrace();
            Assert.fail("Address insert test case failed setting up update call");
        }
    }
    
    private void setupMockForContactUpdate() {
        try {
            when(this.mockPersistenceClient.retrieveObject(any(Object.class))).thenReturn(this.createBusinessMockObject(),
                    this.createAddressMockObject());
        } catch (ContactDaoException e) {
            e.printStackTrace();
            Assert.fail("Business contact and Address fetch test case failed setting up business and address object calls");
        }
        
        try {
            when(this.mockPersistenceClient.updateRow(isA(Business.class))).thenReturn(1);
        } catch (ContactDaoException e) {
            e.printStackTrace();
            Assert.fail("Business contact update test case failed setting up update call");
        }
        
        try {
            when(this.mockPersistenceClient.updateRow(isA(Address.class))).thenReturn(1);
        } catch (ContactDaoException e) {
            e.printStackTrace();
            Assert.fail("Address update test case failed setting up update call");
        }
    }
    
    private Business createBusinessMockObject() {
        Business b = new Business();
        b.setBusinessId(1351);
        b.setLongname("Ticket Master");
        b.setContactFirstname("roy");
        b.setContactLastname("terrell");
        b.setContactPhone("9728882222");
        b.setContactEmail("royterrell@gte.net");
        b.setServTypeId(130);
        b.setEntityTypeId(100);
        b.setTaxId("75-9847382");
        b.setWebsite("ticketmaster.com");
        return b;
    }
    
    private Address createAddressMockObject() {
        Address a = new Address();
        a.setAddrId(2222);
        a.setBusinessId(1351);
        a.setAddr1("94393 Hall Ave.");
        a.setAddr2("Suite 948");
        a.setAddr3("P.O. Box 84763");
        a.setZip(75028);
        a.setZipext(1234);
        return a;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see testcases.messaging.MessageToListenerToHandlerTest#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        return;
    }

    @Test
    public void testSuccess_FetchSingleBusinessContact() {
        String request = RMT2File.getFileContentsAsString("xml/contacts/BusinessContactSimpleSearchRequest.xml");
        List<ContactDto> mockSingleContactDtoResponse = AddressBookMockData.createMockSingleContactDtoResponseData();

        this.setupMockContactApiCall();
        
        try {
            when(this.mockApi.getContact(isA(ContactDto.class))).thenReturn(mockSingleContactDtoResponse);
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a business contact");
        }
        
        MessageHandlerResults results = null;
        ContactProfileApiHandler handler = new ContactProfileApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.CONTACTS_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AddressBookResponse actualRepsonse = 
                (AddressBookResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getBusinessContacts().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Contact record(s) found",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("firstname_1",
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getContactFirstname());
        Assert.assertEquals("lastname_1",
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getContactLastname());
        Assert.assertEquals("firstname_1.lastname_1@gte.net",
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getContactEmail());
        Assert.assertEquals("BusinessName_1",
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getLongName());
        Assert.assertEquals("750000001",
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getTaxId());
        Assert.assertEquals(BigInteger.valueOf(1351),
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getBusinessId());
        Assert.assertEquals("9999999991",
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getContactPhone());
        Assert.assertEquals("www.BusinessName_1.com",
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getWebsite());
        Assert.assertEquals("shortname",
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getShortName());
        Assert.assertEquals("address_line1_1",
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getAddress().getAddr1());
        Assert.assertEquals("address_line2_1",
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getAddress().getAddr2());
        Assert.assertEquals("address_line3_1",
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getAddress().getAddr3());
        Assert.assertEquals("address_line4_1",
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getAddress().getAddr4());
        Assert.assertEquals(BigInteger.valueOf(2001),
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getAddress().getAddrId());
        Assert.assertEquals("Dallas",
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getAddress().getZip().getCity());
        Assert.assertEquals("Tx",
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getAddress().getZip().getState());
        Assert.assertEquals(BigInteger.valueOf(75232),
                actualRepsonse.getProfile().getBusinessContacts().get(0)
                        .getAddress().getZip().getZipcode());
    }
    
    
    @Test
    public void testSuccess_FetchBusinessContactList() {
        String request = RMT2File.getFileContentsAsString("xml/contacts/BusinessContactComplexSearchRequest.xml");
        List<ContactDto> mockContactDtoListResponse = AddressBookMockData.createMockContactDtoResponseListData();

        this.setupMockContactApiCall();
        try {
            when(this.mockApi.getContact(isA(ContactDto.class))).thenReturn(mockContactDtoListResponse);
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a list of contacts");
        }
        
        MessageHandlerResults results = null;
        ContactProfileApiHandler handler = new ContactProfileApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.CONTACTS_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        AddressBookResponse actualRepsonse = 
                (AddressBookResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(3, actualRepsonse.getProfile().getBusinessContacts().size());
        Assert.assertEquals(3, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Contact record(s) found",
                actualRepsonse.getReplyStatus().getMessage());
        
        for (int ndx = 0; ndx < actualRepsonse.getProfile().getBusinessContacts().size(); ndx++) {
            Assert.assertEquals("firstname_" + (ndx + 1), actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getContactFirstname());
            Assert.assertEquals("lastname_" + (ndx + 1), actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getContactLastname());
            Assert.assertEquals("firstname_" + (ndx + 1) + ".lastname_" + (ndx + 1) + "@gte.net",
                    actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getContactEmail());
            Assert.assertEquals("BusinessName_" + (ndx + 1), actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getLongName());
            Assert.assertEquals("75000000" + (ndx + 1), actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getTaxId());
            Assert.assertEquals(BigInteger.valueOf(1351 + ndx),
                    actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getBusinessId());
            Assert.assertEquals("999999999" + (ndx + 1), actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getContactPhone());
            Assert.assertEquals("www.BusinessName_" + (ndx + 1) + ".com",
                    actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getWebsite());
            Assert.assertEquals("shortname", actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getShortName());
            Assert.assertEquals("address_line1_" + (ndx + 1), actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getAddress().getAddr1());
            Assert.assertEquals("address_line2_" + (ndx + 1), actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getAddress().getAddr2());
            Assert.assertEquals("address_line3_" + (ndx + 1), actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getAddress().getAddr3());
            Assert.assertEquals("address_line4_" + (ndx + 1), actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getAddress().getAddr4());
            Assert.assertEquals(BigInteger.valueOf(2001 + ndx),
                    actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getAddress().getAddrId());
            Assert.assertEquals("Dallas", actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getAddress().getZip().getCity());
            Assert.assertEquals("Tx", actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getAddress().getZip().getState());
            Assert.assertEquals(BigInteger.valueOf(75232),
                    actualRepsonse.getProfile().getBusinessContacts().get(ndx)
                            .getAddress().getZip().getZipcode());
        }
    }
    
    @Test
    public void testSuccess_FetchBusinessContact_NoDataFound() {
        String request = RMT2File.getFileContentsAsString("xml/contacts/BusinessContactSimpleSearchRequest.xml");

        this.setupMockContactApiCall();
        try {
            when(this.mockApi.getContact(isA(ContactDto.class))).thenReturn(null);
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a business contact");
        }
        
        MessageHandlerResults results = null;
        ContactProfileApiHandler handler = new ContactProfileApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.CONTACTS_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        AddressBookResponse actualRepsonse = 
                (AddressBookResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(0, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Contact data not found!", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_FetchBusinessContact_API_Error() {
        String request = RMT2File.getFileContentsAsString("xml/contacts/BusinessContactNoCriteriaSearchRequest.xml");

        this.setupMockContactApiCall();
        try {
            when(this.mockApi.getContact(null))
                    .thenThrow(new ContactsApiException("Test validation error: selection criteria is required"));
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a business contact");
        }
        
        MessageHandlerResults results = null;
        ContactProfileApiHandler handler = new ContactProfileApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.CONTACTS_GET, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        AddressBookResponse actualRepsonse = 
                (AddressBookResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals("Failure to retrieve contact(s)", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals(
                "AddressBook ContactCriteriaGroup is required to have one and only one criteria object that is of type either personal, business, or common",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
    @Test
    public void testSuccess_UpdateBusinessContact() {
        String request = RMT2File.getFileContentsAsString("xml/contacts/BusinessContactUpdateRequest.xml");
        
        this.setupMockContactApiCall();
        try {
            when(this.mockApi.updateContact(isA(ContactDto.class))).thenReturn(1);
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for updating a business contact");
        }
        
        MessageHandlerResults results = null;
        ContactProfileApiHandler handler = new ContactProfileApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.CONTACTS_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        AddressBookResponse actualRepsonse = 
                (AddressBookResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getBusinessContacts().size());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Contact was modified successfully",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("Total number of rows modified: " + actualRepsonse.getReplyStatus().getReturnCode().intValue(),
                actualRepsonse.getReplyStatus().getExtMessage());
        Assert.assertTrue(actualRepsonse.getProfile().getBusinessContacts().get(0).getBusinessId().intValue() > 0);
        Assert.assertTrue(actualRepsonse.getProfile().getBusinessContacts().get(0).getAddress().getAddrId().intValue() > 0);
    }
    
    @Test
    public void testError_Update_Contact_NotFound() {
        String request = RMT2File.getFileContentsAsString("xml/contacts/BusinessContactUpdateRequest.xml");
        
        this.setupMockForContactUpdate();
        try {
            when(this.mockPersistenceClient.retrieveObject(any(Object.class))).thenReturn(null);
        } catch (ContactDaoException e) {
            e.printStackTrace();
            Assert.fail("Business contact and Address fetch test case failed setting up business and address object calls");
        }
        
        MessageHandlerResults results = null;
        ContactProfileApiHandler handler = new ContactProfileApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.CONTACTS_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        AddressBookResponse actualRepsonse = 
                (AddressBookResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getBusinessContacts().size());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Failure to update existing contact",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertTrue(actualRepsonse.getReplyStatus().getExtMessage()
                .contains("Business contact profile not found in database: "));
    }
    
    @Test
    public void testSuccess_InsertBusinessContact() {
        String request = RMT2File.getFileContentsAsString("xml/contacts/BusinessContactInsertRequest.xml");
        this.setupMockForContactInsert();
        
        MessageHandlerResults results = null;
        ContactProfileApiHandler handler = new ContactProfileApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.CONTACTS_UPDATE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        AddressBookResponse actualRepsonse = 
                (AddressBookResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertEquals(1, actualRepsonse.getProfile().getBusinessContacts().size());
        Assert.assertEquals(1351, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Contact was created successfully",
                actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("The new contact id is " + actualRepsonse.getReplyStatus().getReturnCode().intValue(),
                actualRepsonse.getReplyStatus().getExtMessage());
        Assert.assertEquals(1351, actualRepsonse.getProfile().getBusinessContacts().get(0).getBusinessId().intValue());
        Assert.assertEquals(2222, actualRepsonse.getProfile().getBusinessContacts().get(0).getAddress().getAddrId().intValue());
    }

    @Test
    public void testError_Incorrect_Trans_Code() {
        String request = RMT2File.getFileContentsAsString("xml/contacts/BusinessContactIncorrectTransCodeRequest.xml");

        this.setupMockContactApiCall();
        try {
            when(this.mockApi.getContact(null)).thenThrow(
                    new ContactsApiException("Test validation error: selection criteria is required"));
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for fetching a business contact");
        }

        MessageHandlerResults results = null;
        ContactProfileApiHandler handler = new ContactProfileApiHandler();
        try {
            results = handler.processMessage("GET_INCORRECT_TRANS", request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());

        AddressBookResponse actualRepsonse = (AddressBookResponse) jaxb.unMarshalMessage(results.getPayload()
                .toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_BAD_REQUEST, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(ContactProfileApiHandler.ERROR_MSG_TRANS_NOT_FOUND + "GET_INCORRECT_TRANS", actualRepsonse
                .getReplyStatus().getMessage());
    }
    
    
    @Test
    public void testSuccess_DeleteBusinessContact() {
        String request = RMT2File.getFileContentsAsString("xml/contacts/BusinessContactDeleteRequest.xml");
        
        this.setupMockContactApiCall();
        try {
            when(this.mockApi.deleteContact(isA(ContactDto.class))).thenReturn(1);
        } catch (ContactsApiException e) {
            Assert.fail("Unable to setup mock stub for deleting a business contact");
        }
        
        MessageHandlerResults results = null;
        ContactProfileApiHandler handler = new ContactProfileApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.CONTACTS_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        AddressBookResponse actualRepsonse = 
                (AddressBookResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Contact was deleted successfully", actualRepsonse.getReplyStatus().getMessage());
    }
    
    @Test
    public void testError_Delete_Invalid_ContactId() {
        String request = RMT2File.getFileContentsAsString("xml/contacts/BusinessContactDeleteInvalidRequest.xml");
        
        this.setupMockForContactUpdate();
        
        MessageHandlerResults results = null;
        ContactProfileApiHandler handler = new ContactProfileApiHandler();
        try {
            results = handler.processMessage(ApiTransactionCodes.CONTACTS_DELETE, request);
        } catch (MessageHandlerCommandException e) {
            e.printStackTrace();
            Assert.fail("An unexpected exception was thrown");
        }
        Assert.assertNotNull(results);
        Assert.assertNotNull(results.getPayload());
        
        AddressBookResponse actualRepsonse = 
                (AddressBookResponse) jaxb.unMarshalMessage(results.getPayload().toString());
        Assert.assertNull(actualRepsonse.getProfile());
        Assert.assertEquals(-1, actualRepsonse.getReplyStatus().getReturnCode().intValue());
        Assert.assertEquals(MessagingConstants.RETURN_STATUS_SUCCESS, actualRepsonse.getReplyStatus().getReturnStatus());
        Assert.assertEquals("Failure to delelte contact", actualRepsonse.getReplyStatus().getMessage());
        Assert.assertEquals("A valid Contact Id is required when deleting a contact from the database",
                actualRepsonse.getReplyStatus().getExtMessage());
    }
    
}

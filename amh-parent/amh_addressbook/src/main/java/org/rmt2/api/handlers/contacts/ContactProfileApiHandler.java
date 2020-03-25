package org.rmt2.api.handlers.contacts;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dao.contacts.ContactsConst;
import org.dto.BusinessContactDto;
import org.dto.ContactDto;
import org.dto.PersonalContactDto;
import org.dto.converter.jaxb.ContactsJaxbFactory;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiFactory;
import org.rmt2.api.adapters.jaxb.JaxbAddressBookFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AddressBookRequest;
import org.rmt2.jaxb.AddressBookResponse;
import org.rmt2.jaxb.BusinessContactCriteria;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.CommonContactCriteria;
import org.rmt2.jaxb.CommonContactType;
import org.rmt2.jaxb.ContactCriteriaGroup;
import org.rmt2.jaxb.ContactDetailGroup;
import org.rmt2.jaxb.ObjectFactory;
import org.rmt2.jaxb.PersonContactCriteria;
import org.rmt2.jaxb.PersonType;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.AbstractJaxbMessageHandler;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Business, Personal, and Common contact related messages to
 * the AddressBook API.
 * 
 * @author roy.terrell
 *
 */
public class ContactProfileApiHandler extends 
                  AbstractJaxbMessageHandler<AddressBookRequest, AddressBookResponse, ContactDetailGroup> {
    
    private static final Logger logger = Logger.getLogger(ContactProfileApiHandler.class);
    private ObjectFactory jaxbObjFactory;
    protected ContactsApi api;

    /**
     * @param payload
     */
    public ContactProfileApiHandler() {
        super();
        this.jaxbObjFactory = new ObjectFactory();
        this.responseObj = jaxbObjFactory.createAddressBookResponse();
        this.api = ContactsApiFactory.createApi();
        logger.info(ContactProfileApiHandler.class.getName() + " was instantiated successfully");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.api.messaging.jms.handler.AbstractMessageHandler#processRequest(java
     * .lang.String, java.io.Serializable)
     */
    @Override
    public MessageHandlerResults processMessage(String command, Serializable payload) throws MessageHandlerCommandException {
        MessageHandlerResults r = super.processMessage(command, payload);

        if (r != null) {
            // This means an error occurred.
            return r;
        }
        switch (command) {
            case ApiTransactionCodes.CONTACTS_UPDATE:
                 r = this.update(this.requestObj);
                break;
            case ApiTransactionCodes.CONTACTS_DELETE:
                 r = this.delete(this.requestObj);
                break;
            case ApiTransactionCodes.CONTACTS_GET:
                r = this.fetch(this.requestObj);
                break;
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to fetch one or more contacts.
     * <p>
     * This method is capable of processing personal, business, or generic
     * contact types.
     * 
     * @param req
     *            The request used to build the ContactDto selection criteria
     * @return an instance of {@link MessageHandlerResults}           
     */
    protected MessageHandlerResults fetch(AddressBookRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        ContactDetailGroup cdg = null;

        // ContactsApiFactory cf = new ContactsApiFactory();
        ContactsApi api = ContactsApiFactory.createApi();
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            this.validateRequest(req);
            ContactDto criteriaDto = this.extractSelectionCriteria(req.getCriteria());
            
            List<ContactDto> dtoList = api.getContact(criteriaDto);
            if (dtoList == null) {
                rs.setMessage("Contact data not found!");
                rs.setReturnCode(0);
            }
            else {
                cdg = this.buildContactDetailGroup(dtoList);
                rs.setMessage("Contact record(s) found");
                rs.setReturnCode(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());

        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to retrieve contact(s)");
            rs.setExtMessage(e.getMessage());
        } finally {
            api.close();
        }
        String xml = this.buildResponse(cdg, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to update the specified
     * contact.
     * <p>
     * This method is capable of processing personal, business, or generic
     * contact types.
     * 
     * @param req
     *            The request used to build the ContactDto selection criteria
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults update(AddressBookRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        ContactDetailGroup cdg = null;
        
        boolean newContact = false;
        ContactsApi api = ContactsApiFactory.createApi();
        int rc = 0;
        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            this.validateRequest(req); 
            ContactDto contactDto = this.extractContactObject(req.getProfile());
            newContact = (contactDto.getContactId() == 0);
            
            // call api
            api.beginTrans();
            rc = api.updateContact(contactDto);
            
            // // prepare response with updated contact data
            cdg = req.getProfile();

            // Return code is either the total number of rows updated or the business id of the contact created
            rs.setReturnCode(rc);
            if (newContact) {
                rs.setMessage("Contact was created successfully");
                rs.setExtMessage("The new contact id is " + rc);
            }
            else {
                rs.setMessage("Contact was modified successfully");
                rs.setExtMessage("Total number of rows modified: " + rc);
            }
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to update " + (newContact ? "new" : "existing")  + " contact");
            rs.setExtMessage(e.getMessage());
            cdg = req.getProfile();
            api.rollbackTrans();
        } finally {
            api.close();
        }

        String xml = this.buildResponse(cdg, rs);
        results.setPayload(xml);
        return results;
    }
    
    /**
     * Handler for invoking the appropriate API in order to delete the specified
     * contact.
     * <p>
     * This method is capable of processing personal, business, or generic
     * contact types.
     * 
     * @param req
     *            The request used to build the ContactDto selection criteria
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults delete(AddressBookRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        
        ContactsApiFactory cf = new ContactsApiFactory();
        ContactsApi api = cf.createApi();
        int rc = 0;
        try {
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            this.validateRequest(req); 
            ContactDto criteriaDto = this.extractSelectionCriteria(req.getCriteria());
            
            // call api
            api.beginTrans();
            rc = api.deleteContact(criteriaDto);
            
            // Return code is either the total number of rows deleted
            rs.setReturnCode(rc);
            rs.setMessage("Contact was deleted successfully");
            rs.setExtMessage("Contact Id deleted was " + criteriaDto.getContactId());
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage("Failure to delelte contact");
            rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            api.close();
        }
        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
    }
    
    private ContactDetailGroup buildContactDetailGroup(List<ContactDto> results) {
        ContactDetailGroup cdg = jaxbObjFactory.createContactDetailGroup();
        ContactsJaxbFactory cjf = new ContactsJaxbFactory();
        
        for (ContactDto contact : results) {
            if (contact instanceof BusinessContactDto) {
                BusinessType jaxbObj = cjf.createBusinessTypeInstance(contact);
                cdg.getBusinessContacts().add(jaxbObj);
            } else if (contact instanceof PersonalContactDto) {
                PersonType jaxbObj = cjf.createPersonalTypeInstance(contact);
                cdg.getPersonContacts().add(jaxbObj);
            } else {
                CommonContactType jaxbObj = cjf.createCommonContactTypeInstance(contact);
                cdg.getCommonContacts().add(jaxbObj);
            }
        }
        return cdg;
    }
   /**
    * 
    * @param criteriaObj
    * @return
    */
   private ContactDto extractSelectionCriteria(ContactCriteriaGroup criteriaGroup) {
       Object criteriaObj = this.validateSelectionCriteria(criteriaGroup);
       ContactDto dto = null;
       if (criteriaObj instanceof BusinessContactCriteria) {
           BusinessContactCriteria bcc = (BusinessContactCriteria) criteriaObj;
           dto = JaxbAddressBookFactory.createBusinessContactDtoInstance(bcc);
       }
       if (criteriaObj instanceof PersonContactCriteria) {
           PersonContactCriteria pcc = (PersonContactCriteria) criteriaObj;
           dto = JaxbAddressBookFactory.createPersonContactDtoInstance(pcc);
       }
       if (criteriaObj instanceof CommonContactCriteria) {
           CommonContactCriteria ccc = (CommonContactCriteria) criteriaObj;
           dto = JaxbAddressBookFactory.createContactDtoInstance(ccc);
       }
       
       return dto;
   }
   
   private ContactDto extractContactObject(ContactDetailGroup cdg) {
       Object contactObj = this.validateContactDetailGroup(cdg);
       ContactDto dto = null;
       if (contactObj instanceof BusinessType) {
           BusinessType bt = (BusinessType) contactObj;
           dto = JaxbAddressBookFactory.createBusinessContactDtoInstance(bt);
           dto.setContactType(ContactsConst.CONTACT_TYPE_BUSINESS);
       }
       if (contactObj instanceof PersonType) {
           PersonType pt = (PersonType) contactObj;
           dto = JaxbAddressBookFactory.createPersonContactDtoInstance(pt);
           dto.setContactType(ContactsConst.CONTACT_TYPE_PERSONAL);
       }
       if (contactObj instanceof CommonContactType) {
           CommonContactType cct = (CommonContactType) contactObj;
           dto = JaxbAddressBookFactory.createContactDtoInstance(cct);
       }
       
       return dto;
   }
   
   private Object validateSelectionCriteria(ContactCriteriaGroup criteriaGroup) {
        try {
            Verifier.verifyNotNull(criteriaGroup);
        } catch (VerifyException e) {
            throw new InvalidRequestContactCriteriaException(
                    "AddressBook contact query request is rquired to have a criteria group element");
        }

        // Use a hashtable to assist in ContactCriteriaGroup validations since
        // hashtable only allows non-null keys and values.
        Map<Object, Object> criteriaHash = new Hashtable<>();
        this.addToValidationHashTable(criteriaHash, criteriaGroup.getBusinessCriteria());
        this.addToValidationHashTable(criteriaHash, criteriaGroup.getCommonCriteria());
        this.addToValidationHashTable(criteriaHash, criteriaGroup.getPersonCriteria());
        
        // ContactCriteriaGroup is required to have one and only one criteria
        // object available.
        try {
            Verifier.verifyTrue(!criteriaHash.isEmpty() && criteriaHash.size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestContactCriteriaException(
                    "AddressBook ContactCriteriaGroup is required to have one and only one criteria object that is of type either personal, business, or common");
        }
        
        // If we arrived here, that means there must be one and only one criteria object available
        return criteriaHash.keySet().iterator().next();
    }
    
    /**
     * Validates the request's list of contacts.
     */
    private Object validateContactDetailGroup(ContactDetailGroup cdg) {
        try {
            Verifier.verifyNotNull(cdg);
        }
        catch (VerifyException e) {
            throw new InvalidRequestContactProfileException("AddressBook request ContactDetailGroup element is required");
        }
        
        // Use a hashtable to assist in ContactDetailGroup validations since
        // hashtable only allows non-null keys and values.
        Map<List, List> detailGroupHash = new Hashtable<>();
        this.addToValidationHashTable(detailGroupHash, cdg.getBusinessContacts().isEmpty() ? null : cdg.getBusinessContacts());
        this.addToValidationHashTable(detailGroupHash, cdg.getCommonContacts().isEmpty() ? null : cdg.getCommonContacts());
        this.addToValidationHashTable(detailGroupHash, cdg.getPersonContacts().isEmpty() ? null : cdg.getPersonContacts());
        
        // ContactCriteriaGroup is required to have one and only one criteria
        // object available.
        try {
            Verifier.verifyTrue(!detailGroupHash.isEmpty() && detailGroupHash.size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestContactProfileException(
                    "AddressBook ContactDetailGroup is required to have one and only one detail group object that is of type either personal, business, or common");
        }
        
        // If we arrived here, that means there must be one and only one contact detail grouop object available
        List contacts = detailGroupHash.keySet().iterator().next();
        
        try {
            Verifier.verifyNotEmpty(contacts);
        }
        catch (VerifyException e) {
            throw new NoContactProfilesAvailableException(
                    "AddressBook message request's list of contacts cannot be empty for an update operation");
        }

        try {
            Verifier.verifyFalse(contacts.size() > 1);
        }
        catch (VerifyException e) {
            throw new TooManyContactProfilesException("Too many contacts were available for update operation");
        }
        return contacts.get(0);
    }
    
    private void addToValidationHashTable(Map hash, Object criteriaObj) {
        try {
            hash.put(criteriaObj, criteriaObj);    
        }
        catch (NullPointerException e) {
            //Do nothing
        }
    }
    
    @Override
    protected void validateRequest(AddressBookRequest req) throws InvalidDataException {
        try {
            Verifier.verifyNotNull(req);
        }
        catch (VerifyException e) {
            throw new InvalidRequestException("AddressBook message request element is invalid");
        }
    }

    @Override
    protected String buildResponse(ContactDetailGroup payload,  MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);      
        }
        
        if (payload != null) {
            this.responseObj.setProfile((ContactDetailGroup) payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

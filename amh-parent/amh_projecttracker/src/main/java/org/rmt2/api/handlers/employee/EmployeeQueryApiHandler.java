package org.rmt2.api.handlers.employee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ContactDto;
import org.dto.EmployeeDto;
import org.dto.PersonalContactDto;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiException;
import org.modules.contacts.ContactsApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AddressType;
import org.rmt2.jaxb.EmployeeType;
import org.rmt2.jaxb.ProjectDetailGroup;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ReplyStatusType;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.rmt2.jaxb.ZipcodeType;
import org.rmt2.util.addressbook.AddressTypeBuilder;
import org.rmt2.util.addressbook.ZipcodeTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;

/**
 * Handles and routes employee query related messages to the ProjectTracker API.
 * 
 * @author roy.terrell
 *
 */
public class EmployeeQueryApiHandler extends EmployeeApiHandler {
    
    private static final Logger logger = Logger.getLogger(EmployeeQueryApiHandler.class);
    /**
     * @param payload
     */
    public EmployeeQueryApiHandler() {
        super();
        logger.info(EmployeeQueryApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_EMPLOYEE_GET:
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
     * Handler for invoking the appropriate API in order to fetch one or more
     * project tracker employee objects.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetch(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<EmployeeType> queryDtoResults = null;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            EmployeeDto criteriaDto = EmployeeJaxbDtoFactory
                    .createEmployeeDtoCriteriaInstance(req.getCriteria().getEmployeeCriteria());
            
            List<EmployeeDto> dtoList = this.api.getEmployeeExt(criteriaDto);
            if (dtoList == null) {
                rs.setMessage(EmployeeMessageHandlerConst.MESSAGE_NOT_FOUND);
                rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            }
            else {
                queryDtoResults = this.buildJaxbResults(dtoList);
                rs.setMessage(EmployeeMessageHandlerConst.MESSAGE_FOUND);
                rs.setRecordCount(dtoList.size());
                rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage(EmployeeMessageHandlerConst.MESSAGE_FETCH_ERROR);
            rs.setExtMessage(e.getMessage());
        } finally {
            this.api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<EmployeeType> buildJaxbResults(List<EmployeeDto> results) {
        List<EmployeeType> list = new ArrayList<>();
        ContactsApi contactApi = ContactsApiFactory.createApi();
        for (EmployeeDto item : results) {
            EmployeeType jaxbObj = EmployeeJaxbDtoFactory.createEmployeeDtoInstance(item);
            
            // Get and include address info
            if (jaxbObj.getContactDetails() != null && jaxbObj.getContactDetails().getPersonId() != null
                    && jaxbObj.getContactDetails().getPersonId().intValue() > 0) {
                PersonalContactDto perCriteriaDto = Rmt2AddressBookDtoFactory.getPersonInstance(null, null);
                perCriteriaDto.setContactId(jaxbObj.getContactDetails().getPersonId().intValue());
                try {
                    List<ContactDto> contactList = contactApi.getContact(perCriteriaDto);
                    if (contactList != null && contactList.size() > 0) {
                        PersonalContactDto contact = (PersonalContactDto) contactList.get(0);
                        ZipcodeType zip = ZipcodeTypeBuilder.Builder.create()
                                .withCity(contact.getCity())
                                .withState(contact.getState())
                                .withZipcode(contact.getZip())
                                .build();

                        AddressType addr = AddressTypeBuilder.Builder.create()
                                .withAddrId(contact.getAddrId())
                                .withAddressLine1(contact.getAddr1())
                                .withAddressLine2(contact.getAddr2())
                                .withAddressLine3(contact.getAddr3())
                                .withAddressLine4(contact.getAddr4())
                                .withPhoneHome(contact.getPhoneHome())
                                .withPhoneMobile(contact.getPhoneCell())
                                .withPhoneWork(contact.getPhoneWork())
                                .withZipcode(zip)
                                .build();

                        jaxbObj.getContactDetails().setAddress(addr);
                    }
                    else {
                        StringBuilder buf = new StringBuilder();
                        buf.append("Unable to fetch personal contact information for employee, ");
                        buf.append(jaxbObj.getContactDetails().getFirstName());
                        buf.append(" ");
                        buf.append(jaxbObj.getContactDetails().getLastName());
                        buf.append(", using person id, ");
                        buf.append(jaxbObj.getContactDetails().getPersonId().intValue());
                        logger.error(buf.toString());
                    }
                } catch (ContactsApiException e) {
                    e.printStackTrace();
                }
            }
            list.add(jaxbObj);
        }
        return list;
    }
    
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);
    }

    @Override
    protected String buildResponse(List<EmployeeType> payload, MessageHandlerCommonReplyStatus replyStatus) {
        if (replyStatus != null) {
            ReplyStatusType rs = MessageHandlerUtility.createReplyStatus(replyStatus);
            this.responseObj.setReplyStatus(rs);    
        }
        
        if (payload != null) {
            ProjectDetailGroup profile = this.jaxbObjFactory.createProjectDetailGroup();
            this.responseObj.setProfile(profile);
            this.responseObj.getProfile().getEmployee().addAll(payload);
        }
        
        String xml = this.jaxb.marshalMessage(this.responseObj);
        return xml;
    }
}

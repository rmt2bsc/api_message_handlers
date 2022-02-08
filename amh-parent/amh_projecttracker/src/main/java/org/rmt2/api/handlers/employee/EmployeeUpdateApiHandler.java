package org.rmt2.api.handlers.employee;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.EmployeeDto;
import org.dto.PersonalContactDto;
import org.modules.ProjectTrackerApiConst;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiFactory;
import org.modules.employee.EmployeeApi;
import org.modules.employee.EmployeeApiFactory;
import org.rmt2.api.handler.util.MessageHandlerUtility;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.EmployeeType;
import org.rmt2.jaxb.PersonType;
import org.rmt2.jaxb.ProjectDetailGroup;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ReplyStatusType;
import org.rmt2.util.addressbook.PersonTypeBuilder;
import org.rmt2.util.projecttracker.employee.EmployeeTypeBuilder;

import com.InvalidDataException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;

/**
 * Handles and routes employee update related messages to the ProjectTracker
 * API.
 * 
 * @author roy.terrell
 *
 */
public class EmployeeUpdateApiHandler extends EmployeeApiHandler {
    
    private static final Logger logger = Logger.getLogger(EmployeeUpdateApiHandler.class);
    /**
     * @param payload
     */
    public EmployeeUpdateApiHandler() {
        super();
        logger.info(EmployeeUpdateApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_EMPLOYEE_UPDATE:
                r = this.doOperation(this.requestObj);
                break;
            default:
                r = this.createErrorReply(MessagingConstants.RETURN_CODE_FAILURE,
                        MessagingConstants.RETURN_STATUS_BAD_REQUEST,
                        ERROR_MSG_TRANS_NOT_FOUND + command);
        }
        return r;
    }

    /**
     * Handler for invoking the appropriate API in order to create/update
     * project tracker employee objects.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        EmployeeDto employeeDto = null;
        
        boolean newEmployee = false;
        boolean newContact = false;

        // IS-71: Use local scoped API instance for the purpose of preventing memory leaks
        // caused by dangling API instances. 
        EmployeeApi api = EmployeeApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);
        ContactsApi contactApi = ContactsApiFactory.createApi();
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            
            employeeDto = EmployeeJaxbDtoFactory
                    .createEmploiyeeDtoInstance(req.getProfile().getEmployee().get(0));
            PersonalContactDto contactDto = EmployeeJaxbDtoFactory
                    .createContactDtoInstance(req.getProfile().getEmployee().get(0).getContactDetails());
            newEmployee = employeeDto.getEmployeeId() == 0;

            // Perform contact update first.
            contactApi.beginTrans();
            int contactRc = 0;
            if (contactDto != null) {
                newContact = contactDto.getContactId() == 0;
                contactRc = contactApi.updateContact(contactDto);
                if (contactRc < 1) {
                    rs.setExtMessage(EmployeeMessageHandlerConst.MESSAGE_CONTACT_UPDATE_ERROR);
                }
            }
            if (newContact) {
                employeeDto.setPersonId(contactRc);
            }

            // Perform employee updates
            api.beginTrans();
            int empRc = api.update(employeeDto);
            
            if (newEmployee) {
                rs.setMessage(EmployeeMessageHandlerConst.MESSAGE_UPDATE_NEW_SUCCESS);
                rs.setRecordCount(1);
                employeeDto.setEmployeeId(empRc);
            }
            else {
                rs.setMessage(EmployeeMessageHandlerConst.MESSAGE_UPDATE_EXISTING_SUCCESS);
                rs.setRecordCount(empRc);
            }
            this.responseObj.setHeader(req.getHeader());
            api.commitTrans();
            contactApi.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            if (newEmployee) {
                rs.setMessage(EmployeeMessageHandlerConst.MESSAGE_UPDATE_NEW_ERROR);
            }
            else {
                rs.setMessage(EmployeeMessageHandlerConst.MESSAGE_UPDATE_EXISTING_ERROR);
            }
            rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
            contactApi.rollbackTrans();
        } finally {
            api.close();
            contactApi.close();
        }

        List<EmployeeType> updateDtoResults = this.buildJaxbResults(employeeDto);
        String xml = this.buildResponse(updateDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<EmployeeType> buildJaxbResults(EmployeeDto results) {
        List<EmployeeType> list = new ArrayList<>();

        PersonType pt = PersonTypeBuilder.Builder.create()
                .withPersonId(results.getPersonId())
                .withFirstName(results.getEmployeeFirstname())
                .withLastName(results.getEmployeeLastname())
                .build();

        EmployeeType jaxbObj = EmployeeTypeBuilder.Builder.create()
                .withEmployeeId(results.getEmployeeId())
                .withContactDetails(pt)
                .build();

        list.add(jaxbObj);
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

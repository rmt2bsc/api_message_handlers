package org.rmt2.api.handlers.admin.client;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.BusinessContactDto;
import org.dto.ClientDto;
import org.dto.ContactDto;
import org.modules.ProjectTrackerApiConst;
import org.modules.admin.ProjectAdminApi;
import org.modules.admin.ProjectAdminApiFactory;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiFactory;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.adapters.jaxb.JaxbAddressBookFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.ProjectProfileRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes client update related messages to the ProjectTracker API.
 * 
 * @author roy.terrell
 *
 */
public class ClientUpdateApiHandler extends ClientApiHandler {
    
    private static final Logger logger = Logger.getLogger(ClientUpdateApiHandler.class);
    /**
     * @param payload
     */
    public ClientUpdateApiHandler() {
        super();
        logger.info(ClientUpdateApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_CLIENT_UPDATE:
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
     * Handler for invoking the appropriate API in order to update a
     * project tracker client object.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();

        ProjectAdminApi api = ProjectAdminApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);
        ContactsApi contactApi = ContactsApiFactory.createApi();
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            
            
            // Update locally
            ClientDto clientChangesDto = ClientJaxbDtoFactory.createClientDtoInstance(req.getProfile().getClient().get(0));
            int rc = api.updateClientWithoutNotification(clientChangesDto);
            
            // Target AddressBook to update remotely.  Get original record and apply deltas
            BusinessContactDto criteriaDto = JaxbAddressBookFactory.createBusinessContactDtoInstance((BusinessType) null);
            criteriaDto.setContactId(clientChangesDto.getBusinessId());
            List<ContactDto> contactList = contactApi.getContact(criteriaDto);
            int updateCount = 0;
            if (rc > 0 && contactList != null && contactList.size() == 1) {
                BusinessContactDto busContactDto = (BusinessContactDto) contactList.get(0);
                busContactDto.setContactName(clientChangesDto.getClientName());
                busContactDto.setContactFirstname(clientChangesDto.getClientContactFirstname());
                busContactDto.setContactLastname(clientChangesDto.getClientContactLastname());
                busContactDto.setContactPhone(clientChangesDto.getClientContactPhone());
                busContactDto.setContactExt(clientChangesDto.getClientContactPhone());
                busContactDto.setContactEmail(clientChangesDto.getClientContactEmail());
                updateCount = contactApi.updateContact(busContactDto);
            }
            rs.setRecordCount(rc);
            if (rc > 0) {
                rs.setMessage(ClientMessageHandlerConst.MESSAGE_UPDATE_SUCCESSFUL);
            }
            else {
                rs.setMessage(ClientMessageHandlerConst.MESSAGE_UPDATE_RECORDS_NOT_FOUND);
            }
            if (updateCount > 0) {
                rs.setExtMessage(ClientMessageHandlerConst.MESSAGE_REMOTEUPDATE_SUCCESSFUL);
            }
            else {
                rs.setExtMessage(ClientMessageHandlerConst.MESSAGE_REMOTEUPDATE_RECORDS_NOT_FOUND); 
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage(ClientMessageHandlerConst.MESSAGE_UPDATE_ERROR);
            rs.setExtMessage(e.getMessage());
        } finally {
            api.close();
            contactApi.close();
        }

        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
    }
    
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);
        
        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidRequestException(ApiMessageHandlerConst.MSG_MISSING_PROFILE_DATA);
        }
        try {
            Verifier.verifyTrue(req.getProfile().getClient().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(ClientMessageHandlerConst.MESSAGE_UPDATE_TOO_MANY_CLIENTS);
        }
    }
  
}

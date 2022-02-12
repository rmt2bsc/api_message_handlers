package org.rmt2.api.handlers.admin.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.BusinessContactDto;
import org.dto.ClientDto;
import org.dto.ContactDto;
import org.dto.CustomerDto;
import org.dto.adapter.orm.ProjectObjectFactory;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.modules.CommonAccountingConst;
import org.modules.ProjectTrackerApiConst;
import org.modules.admin.ProjectAdminApi;
import org.modules.admin.ProjectAdminApiException;
import org.modules.admin.ProjectAdminApiFactory;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiFactory;
import org.modules.subsidiary.CustomerApi;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ClientType;
import org.rmt2.jaxb.ProjectProfileRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes client query related messages to the ProjectTracker API.
 * 
 * @author roy.terrell
 *
 */
public class ClientImportApiHandler extends ClientApiHandler {
    
    private static final Logger logger = Logger.getLogger(ClientImportApiHandler.class);
    private StringBuilder extMsg;
    
    /**
     * @param payload
     */
    public ClientImportApiHandler() {
        super();
        logger.info(ClientImportApiHandler.class.getName() + " was instantiated successfully");
        this.extMsg = new StringBuilder();
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
            case ApiTransactionCodes.PROJTRACK_CLIENT_IMPORT:
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
     * Handler for invoking the appropriate API in order to import customer
     * information into the project tracker system as client profiles.
     * 
     * @param req
     *            an instance of {@link ProjectProfileRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<ClientType> queryDtoResults = null;
        List<ClientDto> dtoList = new ArrayList<>();

        // IS-71: Changed the scope to local to prevent memory leaks as a result
        // of sharing the API instance that was once contained in ancestor
        // class, SalesORderApiHandler.
        ProjectAdminApi api = ProjectAdminApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);
        CustomerApi custApi = SubsidiaryApiFactory.createCustomerApi(CommonAccountingConst.APP_NAME);
        ContactsApi contactApi = ContactsApiFactory.createApi();
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            List<Integer> custIdList = ClientJaxbDtoFactory.getCustomerImportIdentifiers(req);
            
            // Get the details of each customer ID and import the data to the
            // local project tracker external datasouce.
            api.beginTrans();
            for (int custId : custIdList) {
                String prefixMsg = "Unable to import customer profile identified by customer ID, " + custId + ", was skipped due to ";
                
                // Verify that client profile has not already been imported
                Object obj = this.getImportedClient(api, custId);
                if (obj != null) {
                    // Skip iteration due to customer has already been imported.
                    this.addExtMessageContent(prefixMsg + "the client profile already exists");
                    continue;
                }
                
                // Get customer data
                CustomerDto custObj = custApi.get(custId);
                if (custObj == null) {
                    // Skip iteration due to erroneous customer and append to Ext Message.
                    this.addExtMessageContent(prefixMsg + "the customer profile could not be found in the Accounting system.");
                    continue;
                }
                
                // Get business contact data
                BusinessContactDto criteria = Rmt2AddressBookDtoFactory.getNewBusinessInstance();
                criteria.setContactId(custObj.getContactId());
                List<ContactDto> contactList = contactApi.getContact(criteria);
                if (contactList == null) {
                    // Skip iteration due to erroneous business contact and append to Ext Message
                    this.addExtMessageContent(prefixMsg + "the associated business contact profile [" +  custObj.getContactId() + "] could not be found in the Address Book system.");
                    continue;
                }
                BusinessContactDto bcDto = (BusinessContactDto) contactList.get(0);
                
                // Package customer/business contact data to be imported as the
                // client profile in the project tracker system
                ClientDto client = this.buildClientDataForUpdate(custObj, bcDto);
                int rc = api.updateClientWithoutNotification(client);
                ClientDto verifiedClient = this.getImportedClient(api, client.getClientId());
                if (verifiedClient != null) {
                    dtoList.add(verifiedClient);    
                }
            }

            // Prepare response message
            if (dtoList.size() == 0) {
                rs.setMessage(ClientMessageHandlerConst.MESSAGE_CUSTOMER_NOT_IMPORTED);
            }
            else {
                queryDtoResults = this.buildJaxbResults(dtoList);
                rs.setMessage(ClientMessageHandlerConst.MESSAGE_CUSTOMER_IMPORTED);
                rs.setRecordCount(dtoList.size());
            }
            if (this.extMsg.length() > 0) {
                rs.setExtMessage(this.extMsg.toString());
            }
            
            this.responseObj.setHeader(req.getHeader());
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage(ClientMessageHandlerConst.MESSAGE_CUSTOMER_IMPORT_ERROR);
            rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            api.close();
            custApi.close();
            contactApi.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    private ClientDto buildClientDataForUpdate(CustomerDto custDto, BusinessContactDto busDto) {
        ClientDto clientDto = ProjectObjectFactory.createClientDtoInstance(null);
        
        // Customer ID and Client ID are the same value
        clientDto.setClientId(custDto.getCustomerId());
        clientDto.setAccountNo(custDto.getAccountNo());
        clientDto.setBusinessId(busDto.getContactId());
        clientDto.setClientName(busDto.getContactName());
        clientDto.setClientContactFirstname(busDto.getContactFirstname());
        clientDto.setClientContactLastname(busDto.getContactLastname());
        clientDto.setClientContactPhone(busDto.getContactPhone());
        clientDto.setClientContactExt(busDto.getContactExt());
        clientDto.setClientContactEmail(busDto.getContactEmail());
        return clientDto;
    }
    
    private ClientDto getImportedClient(ProjectAdminApi api, int clientId) throws ProjectAdminApiException {
        ClientDto criteria = ProjectObjectFactory.createClientDtoInstance(null);
        criteria.setClientId(clientId);
        List<ClientDto> list = api.getClient(criteria);
        if (list == null) {
            return null;
        }
        return list.get(0);
    }
    
    
    private List<ClientType> buildJaxbResults(List<ClientDto> results) {
        List<ClientType> list = new ArrayList<>();
        for (ClientDto item : results) {
            ClientType jaxbObj = ClientJaxbDtoFactory.createClientJaxbInstance(item);
            list.add(jaxbObj);
        }
        return list;
    }
    
    private void addExtMessageContent(String msg) {
        if (this.extMsg.length() > 0) {
            this.extMsg.append("\n");
        }
        this.extMsg.append(msg);
    }
    
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);
        
        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidRequestException(ApiMessageHandlerConst.MSG_MISSING_PROFILE_DATA);
        }
    }

}

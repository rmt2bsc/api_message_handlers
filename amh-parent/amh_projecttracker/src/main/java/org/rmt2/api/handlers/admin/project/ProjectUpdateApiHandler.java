package org.rmt2.api.handlers.admin.project;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.BusinessContactDto;
import org.dto.ClientDto;
import org.dto.ContactDto;
import org.dto.CustomerDto;
import org.dto.Project2Dto;
import org.dto.adapter.orm.ProjectObjectFactory;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.dto.adapter.orm.account.subsidiary.Rmt2SubsidiaryDtoFactory;
import org.modules.CommonAccountingConst;
import org.modules.ProjectTrackerApiConst;
import org.modules.admin.ProjectAdminApi;
import org.modules.admin.ProjectAdminApiFactory;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiException;
import org.modules.contacts.ContactsApiFactory;
import org.modules.subsidiary.CustomerApi;
import org.modules.subsidiary.CustomerApiException;
import org.modules.subsidiary.SubsidiaryApiFactory;
import org.rmt2.api.handlers.admin.client.ClientJaxbDtoFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.BusinessType;
import org.rmt2.jaxb.ClientType;
import org.rmt2.jaxb.CustomerType;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.ProjectType;
import org.rmt2.util.accounting.subsidiary.CustomerTypeBuilder;
import org.rmt2.util.addressbook.BusinessTypeBuilder;
import org.rmt2.util.projecttracker.admin.ClientTypeBuilder;

import com.InvalidDataException;
import com.RMT2Exception;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes project create/modify related messages to the
 * ProjectTracker API.
 * 
 * @author roy.terrell
 *
 */
public class ProjectUpdateApiHandler extends ProjectApiHandler {
    
    private static final Logger logger = Logger.getLogger(ProjectUpdateApiHandler.class);
    private BusinessContactDto verifiedContactDto;
    private CustomerDto verifiedCustomerDto;
    private boolean createClient;
    private ProjectAdminApi api;

    /**
     * @param payload
     */
    public ProjectUpdateApiHandler() {
        super();
        this.verifiedContactDto = null;
        this.verifiedCustomerDto = null;
        this.createClient = false;
        // IS-71: Changed the scope to local to prevent memory leaks as a result
        // of sharing the API instance that was once contained in ancestor
        // class, ProjectApiHandler.
        this.api = ProjectAdminApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);
        logger.info(ProjectUpdateApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_PROJECT_UPDATE:
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
     * Handler for invoking the appropriate API in order to update a project
     * object for the Project Tracker Admin moudule.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        Project2Dto project2Dto = null;
        boolean newProject = false;

        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);

            project2Dto = ProjectJaxbDtoFactory.createProjetDtoInstance(req.getProfile().getProject().get(0));
            newProject = project2Dto.getProjId() == 0;
            
            api.beginTrans();

            // If new project, determine if a project client needs to be
            // created.
            if (newProject) {
                if (this.createClient) {
                    // A valid Contact object should have been fetched during
                    // the validation phase
                    BusinessType bt = BusinessTypeBuilder.Builder.create()
                            .withLongname(verifiedContactDto.getContactName())
                            .withBusinessId(verifiedContactDto.getContactId())
                            .withContactFirstname(verifiedContactDto.getContactFirstname())
                            .withContactLastname(verifiedContactDto.getContactLastname())
                            .withContactEmail(verifiedContactDto.getContactEmail())
                            .withContactPhone(verifiedContactDto.getContactPhone())
                            .build();

                    CustomerType ct = CustomerTypeBuilder.Builder.create()
                            .withBusinessType(bt)
                            .withAccountNo(verifiedCustomerDto.getAccountNo())
                            .build();

                    ClientType clientType = ClientTypeBuilder.Builder.create()
                            // Primary key is not autogenerated
                            .withClientId(verifiedCustomerDto.getCustomerId())
                            .withClientName(this.verifiedContactDto.getContactName())
                            .withCustomerData(ct)
                            .build();

                    ClientDto projClientDto = ClientJaxbDtoFactory.createClientDtoInstance(clientType);
                    int rc = api.updateClientWithoutNotification(projClientDto);
                    if (rc == 1) {
                        project2Dto.setClientId(verifiedCustomerDto.getCustomerId());
                    }
                    else {
                        throw new RMT2Exception(ProjectMessageHandlerConst.MESSAGE_ERROR_CREATING_PROJECT_CLIENT);
                    }
                }
            }

            // Now create or update the project entity
            int rc = api.updateProject(project2Dto);
            if (newProject) {
                rs.setMessage(ProjectMessageHandlerConst.MESSAGE_NEW_PROJECT_UPDATE_SUCCESS);
                rs.setRecordCount(1);
                // Make sure project id is populated in the DTO
                project2Dto.setProjId(rc);
            }
            else {
                rs.setMessage(ProjectMessageHandlerConst.MESSAGE_EXISTING_PROJECT_UPDATE_SUCCESS);
                rs.setRecordCount(rc);
            }
            this.responseObj.setHeader(req.getHeader());
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            if (newProject) {
                rs.setMessage(ProjectMessageHandlerConst.MESSAGE_NEW_PROJECT_UPDATE_FAILED);
            }
            else {
                rs.setMessage(ProjectMessageHandlerConst.MESSAGE_EXISTING_PROJECT_UPDATE_FAILED);
            }
            rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            api.close();
        }

        // Add Project Type type data to the project profile element
        List<ProjectType> updateDtoResults = this.buildJaxbUpdateResults(project2Dto);

        String xml = this.buildResponse(updateDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);
        super.validateUpdateRequest(req);

        // One or more projects must exists
        try {
            Verifier.verifyNotEmpty(req.getProfile().getProject());
            Verifier.verifyTrue(req.getProfile().getProject().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidDataException(ProjectMessageHandlerConst.VALIDATION_PROJECT_MISSING, e);
        }
        
        try {
            Verifier.verifyNotNull(req.getProfile().getProject().get(0).getClient());
        } catch (VerifyException e) {
            throw new InvalidDataException(ProjectMessageHandlerConst.VALIDATION_PROJECT_CLIENT_MISSING, e);
        }

        // If this is a new project and user did not provide the client id, then
        // enforce user to include customer's business id so that we can derive
        // the client id to be sent to the API
        if ((req.getProfile().getProject().get(0).getProjectId() == null ||
                req.getProfile().getProject().get(0).getProjectId().intValue() == 0)
                && (req.getProfile().getProject().get(0).getClient().getClientId() == null ||
                req.getProfile().getProject().get(0).getClient().getClientId().intValue() == 0)) {
            try {
                Verifier.verifyNotNull(req.getProfile().getProject().get(0).getClient().getCustomer());
                Verifier.verifyNotNull(req.getProfile().getProject().get(0).getClient().getCustomer().getBusinessContactDetails());
                Verifier.verifyNotNull(req.getProfile().getProject().get(0).getClient().getCustomer().getBusinessContactDetails()
                        .getBusinessId());
                Verifier.verifyTrue(req.getProfile().getProject().get(0).getClient().getCustomer().getBusinessContactDetails()
                        .getBusinessId().intValue() > 0);
            } catch (VerifyException e) {
                throw new InvalidDataException(ProjectMessageHandlerConst.VALIDATION_BUSID_MISSING_FOR_NEW_PROJECT, e);
            }

            // For new projects, verify that the client exists using the
            // customer's buisness id since the customer and client entities are
            // identified by the same business contact id.
            int busId = req.getProfile().getProject().get(0).getClient().getCustomer().getBusinessContactDetails()
                    .getBusinessId().intValue();
            ClientDto clientCriteria = ProjectObjectFactory.createClientDtoInstance(null);
            clientCriteria.setBusinessId(busId);
            List<ClientDto> clientList = this.api.getClient(clientCriteria);
            if (clientList != null && clientList.size() == 1) {
                // Client was found using business id
                int verifiedClientId = clientList.get(0).getClientId();
                // Set client id in the request
                req.getProfile().getProject().get(0).getClient().setClientId(BigInteger.valueOf(verifiedClientId));
            }
            else {
                // In the event client does not exist, verify that the business
                // contact exists in the addressbook project and hold in memory
                // so that we can use it later to create the client.
                // If business contact does not exist, abort process with error
                // message.
                ContactsApi addressApi = ContactsApiFactory.createApi();
                Rmt2AddressBookDtoFactory.getContactInstance(null);
                BusinessContactDto criteria = Rmt2AddressBookDtoFactory.getBusinessInstance(null);
                criteria.setContactId(busId);
                try {
                    List<ContactDto> contact = addressApi.getContact(criteria);
                    if (contact != null) {
                        this.verifiedContactDto = (BusinessContactDto) contact.get(0);
                    }
                    else {
                        throw new InvalidDataException(ProjectMessageHandlerConst.VALIDATION_BUSID_NOT_BUSINESS_CONTACT);
                    }
                } catch (ContactsApiException e) {
                    String errorMsg = "A address book API error occurred validating business id as a business contact";
                    throw new InvalidDataException(errorMsg, e);
                }
                finally {
                    addressApi.close();
                }

                // Verify that the business contact exists as a customer in the
                // accounting project. If so, store customer object in memory
                // for later use.
                CustomerApi custApi = SubsidiaryApiFactory.createCustomerApi(CommonAccountingConst.APP_NAME);
                CustomerDto criteriaDto = Rmt2SubsidiaryDtoFactory.createCustomerInstance(null, null);
                criteriaDto.setContactId(busId);
                try {
                    List<CustomerDto> customerList = custApi.get(criteriaDto);
                    if (customerList != null) {
                        this.verifiedCustomerDto = customerList.get(0);
                    }
                    else {
                        throw new InvalidDataException(ProjectMessageHandlerConst.VALIDATION_BUSID_NOT_CUSTOMER);
                    }
                } catch (CustomerApiException e) {
                    throw new InvalidDataException("A accounting API error occurred validating business id as a customer", e);
                }
                finally {
                    custApi.close();
                }
                this.createClient = true;
            }
        }
    }

}

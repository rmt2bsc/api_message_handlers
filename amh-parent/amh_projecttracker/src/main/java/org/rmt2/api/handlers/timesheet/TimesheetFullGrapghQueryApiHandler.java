package org.rmt2.api.handlers.timesheet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dto.BusinessContactDto;
import org.dto.ClientDto;
import org.dto.ContactDto;
import org.dto.EventDto;
import org.dto.ProjectTaskDto;
import org.dto.TimesheetDto;
import org.dto.adapter.orm.ProjectObjectFactory;
import org.dto.adapter.orm.Rmt2AddressBookDtoFactory;
import org.modules.ProjectTrackerApiConst;
import org.modules.admin.ProjectAdminApi;
import org.modules.admin.ProjectAdminApiFactory;
import org.modules.contacts.ContactsApi;
import org.modules.contacts.ContactsApiFactory;
import org.modules.timesheet.TimesheetApi;
import org.modules.timesheet.TimesheetApiFactory;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.ProjTrackMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ProjectProfileRequest;
import org.rmt2.jaxb.TimesheetType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes full objec graph timesheet query related messages to the ProjectTracker
 * API.
 * 
 * @author roy.terrell
 *
 */
public class TimesheetFullGrapghQueryApiHandler extends TimesheetApiHandler {
    
    private static final Logger logger = Logger.getLogger(TimesheetFullGrapghQueryApiHandler.class);
    /**
     * @param payload
     */
    public TimesheetFullGrapghQueryApiHandler() {
        super();
        logger.info(TimesheetFullGrapghQueryApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_TIMESHEET_GET_FULLGRAPH:
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
     * Handler for invoking the appropriate API in order to fetch one or more
     * project tracker timesheet objects.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<TimesheetType> queryDtoResults = null;

        // IS-71: Use local scoped API instance for the purpose of preventing memory leaks
        // caused by dangling API instances. 
        TimesheetApi api = TimesheetApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            
            TimesheetDto criteriaDto = TimesheetJaxbDtoFactory
                    .createTimesheetDtoCriteriaInstance(req.getCriteria().getTimesheetCriteria());
            
            // Fetch full timesheet object graph
            Map<ProjectTaskDto, List<EventDto>> timeEntries = api.load(criteriaDto.getTimesheetId());
            if (timeEntries == null) {
                rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_NOT_FOUND);
            }
            else {
                // Fetch business contact data
                BusinessContactDto busContactData = this.getBusinessContact(api.getTimesheet().getClientId());
                // Convert timesheet graph object(s) to JAXB Timesheet object
                TimesheetType graph = TimesheetJaxbDtoFactory.createTimesheetJaxbInstance(api.getTimesheet(), timeEntries, busContactData);
                queryDtoResults = new ArrayList<>();
                queryDtoResults.add(graph);
                rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_FOUND);
                rs.setRecordCount(queryDtoResults.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setMessage(TimesheetMessageHandlerConst.MESSAGE_FETCH_ERROR);
            rs.setExtMessage(e.getMessage());
        } finally {
            api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    private BusinessContactDto getBusinessContact(int clientId) throws Exception {
        int busId = 0;
        ProjectAdminApi api = ProjectAdminApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);
        try {
            ClientDto criteria = ProjectObjectFactory.createClientDtoInstance(null);
            criteria.setClientId(clientId);
            List<ClientDto> results = api.getClient(criteria);
            if (results == null) {
                return null;
            }
            busId = results.get(0).getBusinessId();
        }
        finally {
            api.close();
        }
        
        // Get business contact data
        ContactsApi contactApi = ContactsApiFactory.createApi();
        try {
            BusinessContactDto criteria = Rmt2AddressBookDtoFactory.getNewBusinessInstance();
            criteria.setContactId(busId);
            List<ContactDto> results = contactApi.getContact(criteria);
            if (results == null) {
                return null;
            }
            return (BusinessContactDto) results.get(0);
        }
        finally {
            contactApi.close();
        }
      
    }
    
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);
        
        try {
            Verifier.verifyNotNull(req.getCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(ApiMessageHandlerConst.MSG_MISSING_QUERY_CRITERIA_DATA);
        }

        try {
            Verifier.verifyNotNull(req.getCriteria().getTimesheetCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(ProjTrackMessageHandlerConst.MSG_MISSING_TIMESHEET_CRITERIA_SECTION);
        }
        
        try {
            Verifier.verifyNotNull(req.getCriteria().getTimesheetCriteria().getTimesheetId());
        } catch (VerifyException e) {
            throw new InvalidRequestException(ProjTrackMessageHandlerConst.MSG_TIMESHEETID_REQUIRED_QUERY_PARAM);
        }
    }
}

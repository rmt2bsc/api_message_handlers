package org.rmt2.api.handlers.admin.client;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.dto.ClientDto;
import org.modules.ProjectTrackerApiConst;
import org.modules.admin.ProjectAdminApi;
import org.modules.admin.ProjectAdminApiFactory;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ClientCriteriaType;
import org.rmt2.jaxb.ProjectProfileRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes client delete related messages to the ProjectTracker API.
 * 
 * @author roy.terrell
 *
 */
public class ClientDeleteApiHandler extends ClientApiHandler {
    
    private static final Logger logger = Logger.getLogger(ClientDeleteApiHandler.class);
    /**
     * @param payload
     */
    public ClientDeleteApiHandler() {
        super();
        logger.info(ClientDeleteApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_CLIENT_DELETE:
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
     * Handler for invoking the appropriate API in order to delete one or more
     * project tracker client objects.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults doOperation(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();

        ProjectAdminApi api = ProjectAdminApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            ClientDto criteriaDto = ClientJaxbDtoFactory.createClientDtoCriteriaInstance(req.getCriteria());
            
            int rc = api.deleteClient(criteriaDto);
            rs.setRecordCount(rc);
            if (rc > 0) {
                rs.setMessage(ClientMessageHandlerConst.MESSAGE_DELETE_SUCCESSFUL);
            }
            else {
                rs.setMessage(ClientMessageHandlerConst.MESSAGE_DELETE_RECORDS_NOT_FOUND);
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage(ClientMessageHandlerConst.MESSAGE_DELETE_ERROR);
            rs.setExtMessage(e.getMessage());
        } finally {
            api.close();
        }

        String xml = this.buildResponse(null, rs);
        results.setPayload(xml);
        return results;
    }
    
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);
        
        try {
            Verifier.verifyNotNull(req.getCriteria());
            Verifier.verifyNotNull(req.getCriteria().getClientCriteria());
            Verifier.verifyTrue(this.isCriteriaAvailable(req.getCriteria().getClientCriteria()));
        } catch (VerifyException e) {
            throw new InvalidRequestException(ApiMessageHandlerConst.MSG_MISSING_CRITERIA_DATA);
        }
    }

    private boolean isCriteriaAvailable(ClientCriteriaType criteria) {
        boolean available = false;
        if (criteria.getClientId() != null) {
            available = true;
        }
        if (criteria.getBusinessId() != null) {
            available = true;
        }
        if (criteria.getClientName() != null) {
            available = true;
        }
        if (criteria.getAccountNo() != null) {
            available = true;
        }
        return available;
    }
}

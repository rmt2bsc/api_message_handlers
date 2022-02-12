package org.rmt2.api.handlers.admin.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ClientDto;
import org.modules.ProjectTrackerApiConst;
import org.modules.admin.ProjectAdminApi;
import org.modules.admin.ProjectAdminApiFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.ClientType;
import org.rmt2.jaxb.ProjectProfileRequest;

import com.InvalidDataException;
import com.api.messaging.handler.MessageHandlerCommandException;
import com.api.messaging.handler.MessageHandlerCommonReplyStatus;
import com.api.messaging.handler.MessageHandlerResults;

/**
 * Handles and routes client query related messages to the ProjectTracker API.
 * 
 * @author roy.terrell
 *
 */
public class ClientQueryApiHandler extends ClientApiHandler {
    
    private static final Logger logger = Logger.getLogger(ClientQueryApiHandler.class);
    /**
     * @param payload
     */
    public ClientQueryApiHandler() {
        super();
        logger.info(ClientQueryApiHandler.class.getName() + " was instantiated successfully");
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
            case ApiTransactionCodes.PROJTRACK_CLIENT_GET:
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
     * project tracker client objects.
     * 
     * @param req
     *            an instance of {@link AccountingGeneralLedgerRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    protected MessageHandlerResults fetch(ProjectProfileRequest req) {
        MessageHandlerResults results = new MessageHandlerResults();
        MessageHandlerCommonReplyStatus rs = new MessageHandlerCommonReplyStatus();
        List<ClientType> queryDtoResults = null;

        // IS-71: Changed the scope to local to prevent memory leaks as a result
        // of sharing the API instance that was once contained in ancestor
        // class, SalesORderApiHandler.
        ProjectAdminApi api = ProjectAdminApiFactory.createApi(ProjectTrackerApiConst.APP_NAME);
        try {
            // Set reply status
            rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            rs.setReturnCode(MessagingConstants.RETURN_CODE_SUCCESS);
            rs.setRecordCount(0);
            ClientDto criteriaDto = ClientJaxbDtoFactory.createClientDtoCriteriaInstance(req.getCriteria());
            
            List<ClientDto> dtoList = api.getClient(criteriaDto);
            if (dtoList == null) {
                rs.setMessage(ClientMessageHandlerConst.MESSAGE_NOT_FOUND);
            }
            else {
                queryDtoResults = this.buildJaxbResults(dtoList);
                rs.setMessage(ClientMessageHandlerConst.MESSAGE_FOUND);
                rs.setRecordCount(dtoList.size());
            }
            this.responseObj.setHeader(req.getHeader());
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e );
            rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            rs.setRecordCount(0);
            rs.setMessage(ClientMessageHandlerConst.MESSAGE_FETCH_ERROR);
            rs.setExtMessage(e.getMessage());
        } finally {
            api.close();
        }

        String xml = this.buildResponse(queryDtoResults, rs);
        results.setPayload(xml);
        return results;
    }
    
    private List<ClientType> buildJaxbResults(List<ClientDto> results) {
        List<ClientType> list = new ArrayList<>();
        for (ClientDto item : results) {
            ClientType jaxbObj = ClientJaxbDtoFactory.createClientJaxbInstance(item);
            list.add(jaxbObj);
        }
        return list;
    }
    
    
    @Override
    protected void validateRequest(ProjectProfileRequest req) throws InvalidDataException {
        super.validateRequest(req);
    }

}

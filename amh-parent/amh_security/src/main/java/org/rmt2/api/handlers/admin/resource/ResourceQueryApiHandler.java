package org.rmt2.api.handlers.admin.resource;

import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ResourceDto;
import org.modules.resource.ResourceRegistryApi;
import org.modules.resource.ResourceRegistryApiFactory;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.AuthenticationAdminJaxbDtoFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes resource query related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class ResourceQueryApiHandler extends ResourcesInfoApiHandler {
    
    private static final Logger logger = Logger.getLogger(ResourceQueryApiHandler.class);

    /**
     * @param payload
     */
    public ResourceQueryApiHandler() {
        super();
        logger.info(ResourceQueryApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to fetch resource
     * objects.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        ResourceDto dto = AuthenticationAdminJaxbDtoFactory.createResourceCriteriaDtoInstance(this.requestObj.getCriteria()
                .getResourceCriteria());
        ResourceRegistryApi api = ResourceRegistryApiFactory.createWebServiceRegistryApiInstance();
        List<ResourceDto> list = null;
        try {
            // call api
            list = api.getResource(dto);
            if (list == null) {
                this.rs.setMessage(ResourceMessageHandlerConst.MESSAGE_NOT_FOUND);
                this.rs.setRecordCount(0);
                this.jaxbObj = null;
            }
            else {
                this.rs.setMessage(ResourceMessageHandlerConst.MESSAGE_FOUND);
                this.rs.setRecordCount(list.size());
                this.jaxbObj = ResourceJaxbDtoFactory.createJaxbResourcesInfoInstance(list);
            }
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(ResourceMessageHandlerConst.MESSAGE_FETCH_ERROR);
            this.rs.setExtMessage(e.getMessage());
        } finally {
            api.close();
        }
        return;
    }


    @Override
    protected void validateRequest(AuthenticationRequest req) throws InvalidDataException {
        super.validateRequest(req);

        try {
            Verifier.verifyTrue(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.AUTH_RESOURCE_GET));
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE);
        }

        try {
            Verifier.verifyNotNull(req.getCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_MISSING_CRITERIA_SECTION);
        }

        try {
            Verifier.verifyNotNull(req.getCriteria().getResourceCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(ResourceMessageHandlerConst.MESSAGE_MISSING_RESOURCE_CRITERIA_SECTION);
        }
    }

}

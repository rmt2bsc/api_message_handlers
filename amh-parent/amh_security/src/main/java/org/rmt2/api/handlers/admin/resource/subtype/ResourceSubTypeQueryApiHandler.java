package org.rmt2.api.handlers.admin.resource.subtype;

import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ResourceDto;
import org.modules.resource.ResourceRegistryApi;
import org.modules.resource.ResourceRegistryApiFactory;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.AuthenticationAdminJaxbDtoFactory;
import org.rmt2.api.handlers.admin.resource.ResourceJaxbDtoFactory;
import org.rmt2.api.handlers.admin.resource.ResourcesInfoApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes resource sub type query related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class ResourceSubTypeQueryApiHandler extends ResourcesInfoApiHandler {
    
    private static final Logger logger = Logger.getLogger(ResourceSubTypeQueryApiHandler.class);

    /**
     * @param payload
     */
    public ResourceSubTypeQueryApiHandler() {
        super();
        logger.info(ResourceSubTypeQueryApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to fetch resource sub
     * type objects.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        ResourceDto dto = AuthenticationAdminJaxbDtoFactory.createResourceSubTypeCriteriaDtoInstance(this.requestObj
                .getCriteria().getResourceCriteria());
        ResourceRegistryApi api = ResourceRegistryApiFactory.createWebServiceRegistryApiInstance();
        List<ResourceDto> list = null;
        try {
            // call api
            list = api.getResourceSubTypeExt(dto);
            if (list == null) {
                this.rs.setMessage(ResourceSubTypeMessageHandlerConst.MESSAGE_NOT_FOUND);
                this.rs.setRecordCount(0);
                this.jaxbObj = null;
            }
            else {
                this.rs.setMessage(ResourceSubTypeMessageHandlerConst.MESSAGE_FOUND);
                this.rs.setRecordCount(list.size());
                this.jaxbObj = ResourceJaxbDtoFactory.createJaxbResourcesInfoInstance(list);
            }
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(ResourceSubTypeMessageHandlerConst.MESSAGE_FETCH_ERROR);
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
            Verifier.verifyTrue(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_GET));
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
            throw new InvalidRequestException(
                    ResourceSubTypeMessageHandlerConst.MESSAGE_MISSING_RESOURCE_SUBTYPE_CRITERIA_SECTION);
        }
    }

}

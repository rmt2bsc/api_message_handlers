package org.rmt2.api.handlers.admin.resource;

import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ResourceDto;
import org.dto.WebServiceDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.modules.resource.ResourceRegistryApi;
import org.modules.resource.ResourceRegistryApiException;
import org.modules.resource.ResourceRegistryApiFactory;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes resource update related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class ResourceUpdateApiHandler extends ResourcesInfoApiHandler {
    
    private static final Logger logger = Logger.getLogger(ResourceUpdateApiHandler.class);

    /**
     * @param payload
     */
    public ResourceUpdateApiHandler() {
        super();
        logger.info(ResourceUpdateApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to add a new or modify
     * an existing resource object.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        WebServiceDto dto = ResourceJaxbDtoFactory.createDtoInstance(this.requestObj.getProfile().getResourcesInfo()
                .getResource().get(0));
        boolean newRec = (dto.getUid() == 0);
        ResourceRegistryApi api = ResourceRegistryApiFactory.createWebServiceRegistryApiInstance();
        int rc = 0;
        try {
            // call api
            api.beginTrans();
            rc = api.updateResource(dto);

            if (rc > 0) {
                if (newRec) {
                    this.rs.setMessage(ResourceMessageHandlerConst.MESSAGE_CREATE_SUCCESS);
                    this.rs.setExtMessage("The resource id is " + rc);
                    this.rs.setRecordCount(1);
                    // Update DTO with new resource type id. This is probably
                    // done at the DAO level.
                    dto.setUid(rc);
                }
                else {
                    this.rs.setMessage(ResourceMessageHandlerConst.MESSAGE_UPDATE_SUCCESS);
                    this.rs.setExtMessage("Total number of resource objects modified: " + rc);
                    this.rs.setRecordCount(rc);
                }

                List<ResourceDto> list = null;
                try {
                    // call api
                    WebServiceDto criteria = Rmt2OrmDtoFactory.getNewResourceInstance();
                    criteria.setUid(dto.getUid());
                    criteria.setSecured(dto.getSecured());
                    list = api.getResource(criteria);

                    // Include profile data in response
                    this.jaxbObj = ResourceJaxbDtoFactory.createJaxbResourcesInfoInstance(list);
                } catch (ResourceRegistryApiException e) {
                    // Do not include profile data in response
                    this.jaxbObj = null;
                }
            }
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(ResourceMessageHandlerConst.MESSAGE_UPDATE_ERROR);
            this.rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            api.close();
        }
        return;
    }


    @Override
    protected void validateRequest(AuthenticationRequest req) throws InvalidDataException {
        super.validateRequest(req);

        try {
            Verifier.verifyTrue(req.getHeader().getTransaction()
                    .equalsIgnoreCase(ApiTransactionCodes.AUTH_RESOURCE_UPDATE));
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE);
        }

        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_MISSING_PROFILE_SECTION);
        }

        try {
            Verifier.verifyNotNull(req.getProfile().getResourcesInfo());
            Verifier.verifyTrue(req.getProfile().getResourcesInfo().getResource().size() > 0);
        } catch (VerifyException e) {
            throw new InvalidRequestException(ResourceMessageHandlerConst.MESSAGE_MISSING_RESOURCE_SECTION);
        }

        try {
            Verifier.verifyTrue(req.getProfile().getResourcesInfo().getResource().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_TOO_MANY_RECORDS);
        }
    }

}

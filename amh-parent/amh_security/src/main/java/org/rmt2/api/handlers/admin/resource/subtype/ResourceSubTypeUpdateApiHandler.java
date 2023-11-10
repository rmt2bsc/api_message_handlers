package org.rmt2.api.handlers.admin.resource.subtype;

import java.util.List;

import org.apache.log4j.Logger;
import org.dto.ResourceDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.modules.resource.ResourceRegistryApi;
import org.modules.resource.ResourceRegistryApiException;
import org.modules.resource.ResourceRegistryApiFactory;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.resource.ResourceJaxbDtoFactory;
import org.rmt2.api.handlers.admin.resource.ResourcesInfoApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes resource sub type update related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class ResourceSubTypeUpdateApiHandler extends ResourcesInfoApiHandler {
    
    private static final Logger logger = Logger.getLogger(ResourceSubTypeUpdateApiHandler.class);

    /**
     * @param payload
     */
    public ResourceSubTypeUpdateApiHandler() {
        super();
        logger.info(ResourceSubTypeUpdateApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to add a new or modify
     * an existing resource sub type object.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        ResourceDto dto = ResourceJaxbDtoFactory.createDtoInstance(this.requestObj.getProfile().getResourcesInfo()
                .getResourcesubtype().get(0));
        boolean newRec = (dto.getSubTypeId() == 0);
        ResourceRegistryApi api = ResourceRegistryApiFactory.createWebServiceRegistryApiInstance();

        // UI-37: Added for capturing the update user id
        api.setApiUser(this.userId);

        int rc = 0;
        try {
            // call api
            api.beginTrans();
            rc = api.updateResourceSubType(dto);

            if (rc > 0) {
                if (newRec) {
                    this.rs.setMessage(ResourceSubTypeMessageHandlerConst.MESSAGE_CREATE_SUCCESS);
                    this.rs.setExtMessage("The resource sub type id is " + rc);
                    this.rs.setRecordCount(1);
                    // Update DTO with new resource type id. This is probably
                    // done at the DAO level.
                    dto.setSubTypeId(rc);

                    // Include profile data in response
                    this.jaxbObj = ResourceJaxbDtoFactory.createJaxbResourcesInfoInstance(dto);
                }
                else {
                    this.rs.setMessage(ResourceSubTypeMessageHandlerConst.MESSAGE_UPDATE_SUCCESS);
                    this.rs.setExtMessage("Total number of resource sub type objects modified: " + rc);
                    this.rs.setRecordCount(rc);

                    // Do not include profile data in response
                    this.jaxbObj = null;
                }

                // Verify changes and include in response message
                try {
                    ResourceDto criteria = Rmt2OrmDtoFactory.getNewResourceSubTypeInstance();
                    criteria.setSubTypeId(dto.getSubTypeId());
                    List<ResourceDto> list = api.getResourceSubType(criteria);
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
            this.rs.setMessage(ResourceSubTypeMessageHandlerConst.MESSAGE_UPDATE_ERROR);
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
                    .equalsIgnoreCase(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_UPDATE));
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
            Verifier.verifyTrue(req.getProfile().getResourcesInfo().getResourcesubtype().size() > 0);
        } catch (VerifyException e) {
            throw new InvalidRequestException(ResourceSubTypeMessageHandlerConst.MESSAGE_MISSING_RESOURCE_SUBTYPE_SECTION);
        }

        try {
            Verifier.verifyTrue(req.getProfile().getResourcesInfo().getResourcesubtype().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_TOO_MANY_RECORDS);
        }
    }

}

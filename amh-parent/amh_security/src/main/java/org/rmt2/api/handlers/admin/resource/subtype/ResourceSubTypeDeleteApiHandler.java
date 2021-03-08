package org.rmt2.api.handlers.admin.resource.subtype;

import org.apache.log4j.Logger;
import org.dto.ResourceDto;
import org.modules.resource.ResourceRegistryApi;
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
 * Handles and routes resource sub type delete related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class ResourceSubTypeDeleteApiHandler extends ResourcesInfoApiHandler {
    
    private static final Logger logger = Logger.getLogger(ResourceSubTypeDeleteApiHandler.class);

    /**
     * @param payload
     */
    public ResourceSubTypeDeleteApiHandler() {
        super();
        logger.info(ResourceSubTypeDeleteApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to delete resource sub
     * type object.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        ResourceDto dto = ResourceJaxbDtoFactory.createDtoInstance(this.requestObj.getProfile().getResourcesInfo()
                .getResourcesubtype()
                .get(0));
        ResourceRegistryApi api = ResourceRegistryApiFactory.createWebServiceRegistryApiInstance();
        int rc = 0;
        try {
            // call api
            api.beginTrans();
            rc = api.deleteResourceSubType(dto);

            if (rc > 0) {
                this.rs.setMessage(ResourceSubTypeMessageHandlerConst.MESSAGE_DELETE_SUCCESS);
            }
            else {
                this.rs.setMessage(ResourceSubTypeMessageHandlerConst.MESSAGE_NOT_FOUND);
            }
            // Do not include profile data in response
            this.jaxbObj = null;
            this.rs.setExtMessage("Total number of resource sub type objects deleted: " + rc);
            this.rs.setRecordCount(rc);
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(ResourceSubTypeMessageHandlerConst.MESSAGE_DELETE_ERROR);
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
                    .equalsIgnoreCase(ApiTransactionCodes.AUTH_RESOURCE_SUB_TYPE_DELETE));
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

package org.rmt2.api.handlers.admin.role;

import org.apache.log4j.Logger;
import org.dto.CategoryDto;
import org.modules.roles.RoleApi;
import org.modules.roles.RoleSecurityApiFactory;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes role delete related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class RoleDeleteApiHandler extends RoleApiHandler {
    
    private static final Logger logger = Logger.getLogger(RoleDeleteApiHandler.class);

    /**
     * @param payload
     */
    public RoleDeleteApiHandler() {
        super();
        logger.info(RoleDeleteApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to delete an existing
     * role object.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        CategoryDto dto = RoleJaxbDtoFactory.createDtoInstance(this.requestObj.getProfile().getRoleInfo().get(0));
        RoleApi api = RoleSecurityApiFactory.createRoleApi();

        // UI-37: Added for capturing the update user id
        api.setApiUser(this.userId);

        int rc = 0;
        try {
            // call api
            api.beginTrans();
            rc = api.delete(dto.getRoleId());

            if (rc > 0) {
                this.rs.setMessage(RoleMessageHandlerConst.MESSAGE_DELETE_SUCCESS);
            }
            else {
                this.rs.setMessage(RoleMessageHandlerConst.MESSAGE_NOT_FOUND);
            }
            this.rs.setExtMessage("Total number of role objects deleted: " + rc);
            this.rs.setRecordCount(rc);
            this.jaxbObj = null;
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(RoleMessageHandlerConst.MESSAGE_DELETE_ERROR);
            this.rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            api.close();
            api = null;
        }
        return;
    }


    @Override
    protected void validateRequest(AuthenticationRequest req) throws InvalidDataException {
        super.validateRequest(req);

        try {
            Verifier.verifyTrue(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.AUTH_ROLE_DELETE));
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE);
        }

        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_MISSING_PROFILE_SECTION);
        }

        try {
            Verifier.verifyNotNull(req.getProfile().getRoleInfo());
            Verifier.verifyTrue(req.getProfile().getRoleInfo().size() > 0);
        } catch (VerifyException e) {
            throw new InvalidRequestException(RoleMessageHandlerConst.MESSAGE_MISSING_ROLE_SECTION);
        }

        try {
            Verifier.verifyTrue(req.getProfile().getRoleInfo().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_TOO_MANY_RECORDS);
        }
    }

}

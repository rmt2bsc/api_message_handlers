package org.rmt2.api.handlers.admin.user.permissions;

import org.apache.log4j.Logger;
import org.dto.CategoryDto;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.user.UserJaxbDtoFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes user application-role delete related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class UserAppRoleDeleteApiHandler extends UserAppRoleApiHandler {
    
    private static final Logger logger = Logger.getLogger(UserAppRoleDeleteApiHandler.class);

    /**
     * @param payload
     */
    public UserAppRoleDeleteApiHandler() {
        super();
        logger.info(UserAppRoleDeleteApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to add a new or modify
     * an existing user object.
     * <p>
     * The idea is for the client to include every application role that was
     * selected to assigned to the user in the request. Subsequently the web
     * service will refresh the user's profile with the new assignments in which
     * deleting those that are not included in the request.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        CategoryDto userAppRoleDto = UserJaxbDtoFactory.createDtoInstance(this.requestObj.getProfile().getUserAppRoleInfo()
                .get(0));

        int rc = 0;
        try {
            // call api
            api.beginTrans();
            rc = this.api.delete(userAppRoleDto.getUserAppRoleId());
            if (rc > 0) {
                this.rs.setMessage(UserAppRoleMessageHandlerConst.MESSAGE_DELTED_SUCCESS);
            }
            else {
                this.rs.setMessage(UserAppRoleMessageHandlerConst.MESSAGE_NOT_FOUND);
            }
            // Do not include profile data in response
            this.jaxbObj = null;
            this.rs.setRecordCount(rc);
            this.rs.setExtMessage("User application role id target: " + userAppRoleDto.getUserAppRoleId());
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(UserAppRoleMessageHandlerConst.MESSAGE_DELETE_ERROR);
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
                    .equalsIgnoreCase(ApiTransactionCodes.AUTH_USER_APPROLE_DELETE));
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE);
        }

        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_MISSING_PROFILE_SECTION);
        }

        try {
            Verifier.verifyNotNull(req.getProfile().getUserAppRoleInfo());
            Verifier.verifyTrue(req.getProfile().getUserAppRoleInfo().size() > 0);
        } catch (VerifyException e) {
            throw new InvalidRequestException(UserAppRoleMessageHandlerConst.MESSAGE_MISSING_USER_SECTION);
        }

        try {
            Verifier.verifyTrue(req.getProfile().getUserAppRoleInfo().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_TOO_MANY_RECORDS);
        }
    }

}

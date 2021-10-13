package org.rmt2.api.handlers.admin.user;

import org.apache.log4j.Logger;
import org.dto.UserDto;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes user delete related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class UserDeleteApiHandler extends UserApiHandler {
    
    private static final Logger logger = Logger.getLogger(UserDeleteApiHandler.class);

    /**
     * @param payload
     */
    public UserDeleteApiHandler() {
        super();
        logger.info(UserDeleteApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to delete an existing
     * user object.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        UserDto dto = UserJaxbDtoFactory.createDtoInstance(this.requestObj.getProfile().getUserInfo().get(0));
        int rc = 0;
        try {
            // call api
            api.beginTrans();
            rc = this.api.deleteUser(dto.getLoginUid());

            // Do not include profile data in response
            this.jaxbObj = null;
            if (rc > 0) {
                this.rs.setMessage(UserMessageHandlerConst.MESSAGE_DELETE_SUCCESS);
                this.rs.setExtMessage("The user id is " + dto.getLoginUid());
            }
            else {
                this.rs.setMessage(UserMessageHandlerConst.MESSAGE_NOT_FOUND);
                this.rs.setExtMessage("No records were deleted due user was not found: " + dto.getLoginUid());
            }
            this.rs.setRecordCount(rc);
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(UserMessageHandlerConst.MESSAGE_DELETE_ERROR);
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
                    .equalsIgnoreCase(ApiTransactionCodes.AUTH_USER_DELETE));
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE);
        }

        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_MISSING_PROFILE_SECTION);
        }

        try {
            Verifier.verifyTrue(req.getProfile().getUserInfo().size() > 0);
        } catch (VerifyException e) {
            throw new InvalidRequestException(UserMessageHandlerConst.MESSAGE_MISSING_USER_SECTION);
        }

        try {
            Verifier.verifyTrue(req.getProfile().getUserInfo().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_TOO_MANY_RECORDS);
        }
    }

}

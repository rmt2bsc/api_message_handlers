package org.rmt2.api.handlers.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.UserDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.user.UserJaxbDtoFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.security.authentication.web.LogoutException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes messages related to logging a user out of one or more applications using the 
 * Authentication/Security API.
 *
 * @author roy.terrell
 *
 */
public class UserLogoutApiHandler extends UserAuthenticationApiHandler {
    private static final Logger logger = Logger.getLogger(UserLogoutApiHandler.class);

    /**
     * @param payload
     */
    public UserLogoutApiHandler() {
        super();
        logger.info(UserLogoutApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order logout user from the system.
     * 
     */
    @Override
    protected void processTransactionCode() {
        UserDto userCredentialsDto = UserAuthenticationJaxbDtoFactory
                .createDtoInstance(this.requestObj.getProfile().getApplicationAccessInfo().get(0));
        int rc = 0;

        // Setup authenticator API
        List<UserDto> userList = new ArrayList<>();
        UserDto user = Rmt2OrmDtoFactory.getNewUserInstance();
        userList.add(user);
        user.setUsername(userCredentialsDto.getUsername());
        try {
            rc = this.api.logout(userCredentialsDto.getUsername());
            this.rs.setMessage(UserAuthenticationMessageHandlerConst.MESSAGE_LOGOUT_SUCCESS);
            this.rs.setRecordCount(rc);
            return;
        } catch (LogoutException e) {
            // User name could not be found or password is incorrect
            this.rs.setMessage(e.getMessage());
            this.rs.setMessage(UserAuthenticationMessageHandlerConst.MESSAGE_LOGOUT_FAILED);
            this.rs.setRecordCount(0);
            this.rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            logger.error(e.getMessage());
        } catch (InvalidDataException e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(UserAuthenticationMessageHandlerConst.MESSAGE_AUTH_API_VALIDATION_ERROR);
            this.rs.setExtMessage(e.getMessage());
            this.rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(UserAuthenticationMessageHandlerConst.MESSAGE_FETCH_ERROR);
            this.rs.setExtMessage(e.getMessage());
            this.rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
        } finally {
            // Build the user JAXB object which includes user's application/role and resource permissions
            this.jaxbObj = UserJaxbDtoFactory.createJaxbInstance(userList, null);
            this.api.close();
        }
    }

    @Override
    protected void validateRequest(AuthenticationRequest req) throws InvalidDataException {
        super.validateRequest(req);
        try {
            Verifier.verifyTrue(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.AUTH_USER_LOGOUT));
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE);
        }

        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_MISSING_PROFILE_SECTION);
        }

        try {
            Verifier.verifyTrue(req.getProfile().getApplicationAccessInfo().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(UserAuthenticationMessageHandlerConst.MESSAGE_INVALID_APPLICATION_ACCESS_INFO);
        }

        try {
            Verifier.verifyNotNull(req.getProfile().getApplicationAccessInfo().get(0).getUserInfo());
        } catch (VerifyException e) {
            throw new InvalidRequestException(UserAuthenticationMessageHandlerConst.MESSAGE_INVALID_USERINFO);
        }

    }

}

package org.rmt2.api.handlers.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.UserDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.modules.authentication.AuthenticationException;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.user.UserJaxbDtoFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;
import com.api.web.security.RMT2SecurityToken;

/**
 * Handles and routes messages related to authenticating and logging in a user
 * via means of Single Sign On (SSO) using the Authentication/Security API.
 *
 * @author roy.terrell
 *
 */
public class SsoUserLoginApiHandler extends UserAuthenticationApiHandler {
    private static final Logger logger = Logger.getLogger(SsoUserLoginApiHandler.class);

    /**
     * @param payload
     */
    public SsoUserLoginApiHandler() {
        super();
        logger.info(SsoUserLoginApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order authenticate user.
     * 
     */
    @Override
    protected void processTransactionCode() {
        UserDto userCredentialsDto = UserAuthenticationJaxbDtoFactory
                .createDtoInstance(this.requestObj.getProfile().getApplicationAccessInfo().get(0));
        RMT2SecurityToken token = null;

        // Setup authenticator API
        List<UserDto> userList = new ArrayList<>();
        UserDto user = Rmt2OrmDtoFactory.getNewUserInstance();
        userList.add(user);
        user.setUsername(userCredentialsDto.getUsername());
        try {
            token = this.api.authenticate(userCredentialsDto.getUsername());
            // Build user response from the security token
            user.setLoginUid(token.getUid());
            user.setFirstname(token.getFirstname());
            user.setLastname(token.getLastname());
            user.setTotalLogons(token.getTotalLogons());
            user.setActive(token.getActive());
            user.setLoggedIn(1);

            String successMsg = RMT2String.replace(UserAuthenticationMessageHandlerConst.MESSAGE_AUTH_SSO_SUCCESS,
                    userCredentialsDto.getUsername(), ApiMessageHandlerConst.MSG_PLACEHOLDER1);
            successMsg = RMT2String.replace(successMsg, String.valueOf(token.getAppCount()),
                    ApiMessageHandlerConst.MSG_PLACEHOLDER2);
            this.rs.setMessage(successMsg);
            this.rs.setRecordCount(token.getAppCount());
            return;
        } catch (AuthenticationException e) {
            // User did not have a security token...not signed in to any system
            this.rs.setExtMessage(e.getMessage());
            this.rs.setMessage(UserAuthenticationMessageHandlerConst.MESSAGE_AUTH_SSO_FAILED);
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
            Verifier.verifyTrue(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.AUTH_SSO_USER_LOGIN));
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

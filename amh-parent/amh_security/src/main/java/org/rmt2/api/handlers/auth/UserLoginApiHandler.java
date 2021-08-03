package org.rmt2.api.handlers.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dto.CategoryDto;
import org.dto.UserDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.modules.authentication.AuthenticationException;
import org.modules.authentication.Authenticator;
import org.modules.authentication.AuthenticatorFactory;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.user.UserJaxbDtoFactory;
import org.rmt2.api.handlers.admin.user.UserMessageHandlerConst;
import org.rmt2.api.handlers.admin.user.permissions.UserAppRoleApiHandler;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.RMT2Base64Decoder;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;
import com.api.web.security.RMT2SecurityToken;

/**
 * Handles and routes messages related to authenticating and logging in a user using the 
 * Authentication/Security API.
 *
 * @author roy.terrell
 *
 */
public class UserLoginApiHandler extends UserAppRoleApiHandler {
    
    private static final Logger logger = Logger.getLogger(UserLoginApiHandler.class);

    /**
     * @param payload
     */
    public UserLoginApiHandler() {
        super();
        logger.info(UserLoginApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order authenticate user.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        UserDto userCredentialsDto = UserAuthenticationJaxbDtoFactory.createDtoInstance(this.requestObj.getProfile().getApplicationAccessInfo().get(0));
        RMT2SecurityToken token = null;
        
        // Decode Base64 password.
        String pw = RMT2Base64Decoder.decode(userCredentialsDto.getPassword());

        // Setup authenticator API
        Authenticator authApi = AuthenticatorFactory.createApi();
        Map<Integer, List<CategoryDto>> userAppRolesMap = new HashMap<>();
        List<UserDto> userList = new ArrayList<>();
        UserDto user = Rmt2OrmDtoFactory.getNewUserInstance();
        userList.add(user);
        user.setUsername(userCredentialsDto.getUsername());
        try {
            token = authApi.authenticate(userCredentialsDto.getUsername(), pw);
            if (token != null) {
                // Build user response from the security token
                user.setLoginUid(token.getUid());
                user.setFirstname(token.getFirstname());
                user.setLastname(token.getLastname());
                user.setTotalLogons(token.getTotalLogons());
                user.setActive(token.getActive());
                
                // Build user application role codes
                List<CategoryDto> userAppRoles = new ArrayList<>();
                for (String appRole : token.getRoles()) {
                    CategoryDto role = Rmt2OrmDtoFactory.getAppRoleDtoInstance(null);
                    role.setAppRoleCode(appRole);
                    userAppRoles.add(role);
                }
                userAppRolesMap.put(user.getLoginUid(), userAppRoles);
                this.rs.setMessage(UserMessageHandlerConst.MESSAGE_FOUND);
                this.rs.setRecordCount(userList.size());
            }
            return;
        } catch (AuthenticationException e) {
            // User name could not be found or password is incorrect
            this.rs.setMessage(e.getMessage());
            this.rs.setMessage(UserMessageHandlerConst.MESSAGE_AUTH_FAILED);
            this.rs.setRecordCount(0);
            logger.error(e.getMessage());
        }
        catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(UserMessageHandlerConst.MESSAGE_FETCH_ERROR);
            this.rs.setExtMessage(e.getMessage());
        } 
        finally {
            // Build the user JAXB object which includes user's application/role and resource permissions
            this.jaxbObj = UserJaxbDtoFactory.createJaxbInstance(userList, userAppRolesMap);
            authApi.close();
            api.close();
        }
    }


    @Override
    protected void validateRequest(AuthenticationRequest req) throws InvalidDataException {
        super.validateRequest(req);

        try {
            Verifier.verifyTrue(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.AUTH_USER_LOGIN));
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE);
        }

        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_MISSING_PROFILE_SECTION);
        }

        try {
            Verifier.verifyNotNull(req.getProfile().getApplicationAccessInfo());
            Verifier.verifyTrue(req.getProfile().getApplicationAccessInfo().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_INVALID_APPLICATION_ACCESS_INFO);
        }
    }

}

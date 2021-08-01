package org.rmt2.api.handlers.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dto.CategoryDto;
import org.dto.UserDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.modules.authentication.AuthenticationException;
import org.modules.roles.RoleSecurityApiFactory;
import org.modules.roles.UserAppRoleApi;
import org.modules.users.UserApi;
import org.modules.users.UserApiFactory;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.user.UserJaxbDtoFactory;
import org.rmt2.api.handlers.admin.user.UserMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
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
public class UserLoginApiHandler extends UserAuthenticationApiHandler {
    
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
        UserDto dto = UserAuthenticationJaxbDtoFactory.createDtoInstance(this.requestObj.getProfile().getApplicationAccessInfo().get(0));
        RMT2SecurityToken token = null;
        
        // TODO:  Add logic to authenticate user.  May need to consider client not sending pass word in plain text.
        try {
            token = this.api.authenticate(dto.getUsername(), dto.getPassword());
        } catch (AuthenticationException e) {
            // User name could not be found or password is incorrect
            this.rs.setMessage(e.getMessage());
        }
        try {
            if (token == null) {
                // User was not successfully authentiated
                return;
            }
            
            // Build response data by fetching authenticated user and its persmissions
            List<UserDto> userList = null;
            UserApi userApi = UserApiFactory.createApiInstance();
            userList = userApi.getUser(dto);
            if (userList == null) {
                this.rs.setMessage(UserMessageHandlerConst.MESSAGE_NOT_FOUND);
                this.rs.setRecordCount(0);
                this.jaxbObj = null;
            }
            else {
                // Fetch each user's application/role and resource
                // permissions
                UserAppRoleApi userAppRoleApi = RoleSecurityApiFactory.createUserAppRoleApi();
                Map<Integer, List<CategoryDto>> userAppRolesMap = new HashMap<>();
                for (UserDto user : userList) {
                    CategoryDto userAppRoleCriteria = Rmt2OrmDtoFactory.getUserAppRoleDtoInstance(null, null);
                    userAppRoleCriteria.setUsername(user.getUsername());
                    List<CategoryDto> userAppRoles = userAppRoleApi.getAssignedRoles(userAppRoleCriteria);
                    userAppRolesMap.put(user.getLoginUid(), userAppRoles);
                }

                this.rs.setMessage(UserMessageHandlerConst.MESSAGE_FOUND);
                this.rs.setRecordCount(userList.size());
                
                // Build the user JAXB object and attach all the user's
                // application/role and resource permissions
                this.jaxbObj = UserJaxbDtoFactory.createJaxbInstance(userList, userAppRolesMap);
            }
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(UserMessageHandlerConst.MESSAGE_FETCH_ERROR);
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

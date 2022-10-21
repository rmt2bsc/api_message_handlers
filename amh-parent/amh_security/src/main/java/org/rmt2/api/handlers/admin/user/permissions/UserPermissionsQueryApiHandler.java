package org.rmt2.api.handlers.admin.user.permissions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dto.CategoryDto;
import org.dto.UserDto;
import org.dto.adapter.orm.Rmt2OrmDtoFactory;
import org.modules.users.UserApi;
import org.modules.users.UserApiFactory;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.user.UserJaxbDtoFactory;
import org.rmt2.api.handlers.admin.user.UserMessageHandlerConst;
import org.rmt2.api.handlers.auth.UserAuthenticationMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes messages related to the user and its permissions from the
 * Authentication/Security API.
 * <p>
 * The permissions consist of the application roles and resources that are
 * associated with the user.
 * 
 * @author roy.terrell
 *
 */
public class UserPermissionsQueryApiHandler extends UserAppRoleApiHandler {
    
    private static final Logger logger = Logger.getLogger(UserPermissionsQueryApiHandler.class);

    /**
     * @param payload
     */
    public UserPermissionsQueryApiHandler() {
        super();
        logger.info(UserPermissionsQueryApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to fetch user objects
     * containing the application roles and resources the user has been granted.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        CategoryDto userAppRolesCriteriaDto = UserJaxbDtoFactory.createDtoInstance(this.requestObj.getCriteria().getUserAppRolesCriteria());
        List<UserDto> userList = null;
        UserApi userApi = UserApiFactory.createApiInstance();
        try {
            // call api
            userList = userApi.getUser(userAppRolesCriteriaDto);
            if (userList == null) {
                this.rs.setMessage(UserMessageHandlerConst.MESSAGE_NOT_FOUND);
                this.rs.setRecordCount(0);
                this.jaxbObj = null;
            }
            else {
                // Fetch each user's application/role and resource
                // permissions
                Map<Integer, List<CategoryDto>> grantedAppRolesMap = new HashMap<>();
                Map<Integer, List<CategoryDto>> revokedAppRolesMap = new HashMap<>();
                for (UserDto user : userList) {

                    // Get user's granted application roles
                    CategoryDto grantedAppRoleCriteria = Rmt2OrmDtoFactory.getUserAppRoleDtoInstance(null, null);
                    grantedAppRoleCriteria.setUsername(user.getUsername());
                    grantedAppRoleCriteria.setApplicationId(userAppRolesCriteriaDto.getApplicationId());
                    List<CategoryDto> userAppRoles = this.api.getAssignedRoles(grantedAppRoleCriteria);
                    grantedAppRolesMap.put(user.getLoginUid(), userAppRoles);

                    // Get application roles that have been revoked or
                    // unassigned relative to the user
                    CategoryDto revokedAppRoleCriteria = Rmt2OrmDtoFactory.getUserAppRoleDtoInstance(null, null);
                    revokedAppRoleCriteria.setLoginUid(user.getLoginUid());
                    revokedAppRoleCriteria.setApplicationId(userAppRolesCriteriaDto.getApplicationId());
                    List<CategoryDto> revokedAppRoles = this.api.getRevokedRoles(revokedAppRoleCriteria);
                    revokedAppRolesMap.put(user.getLoginUid(), revokedAppRoles);
                }

                this.rs.setMessage(UserMessageHandlerConst.MESSAGE_FOUND);
                this.rs.setRecordCount(userList.size());
                
                // Build the user JAXB object and attach all the user's
                // application/role and resource permissions
                this.jaxbObj = UserJaxbDtoFactory.createJaxbInstance(userList, grantedAppRolesMap, revokedAppRolesMap);
            }
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(UserMessageHandlerConst.MESSAGE_FETCH_ERROR);
            this.rs.setExtMessage(e.getMessage());
        } finally {
            // IS-70: Added logic to close api in the event an error occurred
            // which will prevent memory leaks
            if (userApi != null) {
                userApi.close();
            }
            api.close();
        }
        return;
    }


    @Override
    protected void validateRequest(AuthenticationRequest req) throws InvalidDataException {
        super.validateRequest(req);

        try {
            Verifier.verifyTrue(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.AUTH_USER_PERMISSIONS_GET));
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE);
        }

        try {
            Verifier.verifyNotNull(req.getCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_MISSING_CRITERIA_SECTION);
        }

        try {
            Verifier.verifyNotNull(req.getCriteria().getUserAppRolesCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(UserAuthenticationMessageHandlerConst.MESSAGE_MISSING_USER_APP_ROLE_SECTION);
        }
    }

}

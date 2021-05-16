package org.rmt2.api.handlers.admin.user.approle;

import java.util.List;

import org.apache.log4j.Logger;
import org.dto.UserDto;
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

/**
 * Handles and routes user application role query related messages to the Authentication/Security
 * API.
 * 
 * @author roy.terrell
 *
 */
public class UserAppRoleQueryApiHandler extends UserAppRoleApiHandler {
    
    private static final Logger logger = Logger.getLogger(UserAppRoleQueryApiHandler.class);

    /**
     * @param payload
     */
    public UserAppRoleQueryApiHandler() {
        super();
        logger.info(UserAppRoleQueryApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to fetch user objects
     * containing the application roles the user has been granted.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        UserDto dto = UserJaxbDtoFactory.createDtoInstance(this.requestObj.getCriteria().getUserCriteria());
        List<UserDto> list = null;
        UserApi userApi = UserApiFactory.createApiInstance();
        try {
            // call api
            list = userApi.getUser(dto);
            if (list == null) {
                this.rs.setMessage(UserMessageHandlerConst.MESSAGE_NOT_FOUND);
                this.rs.setRecordCount(0);
                this.jaxbObj = null;
            }
            else {
                this.rs.setMessage(UserMessageHandlerConst.MESSAGE_FOUND);
                this.rs.setRecordCount(list.size());
                
                // TODO:  Use user DTO results to fetch the granted application roles for each user
                this.jaxbObj = UserJaxbDtoFactory.createJaxbInstance(list);
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
            Verifier.verifyTrue(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.AUTH_USER_GET));
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE);
        }

        try {
            Verifier.verifyNotNull(req.getCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_MISSING_CRITERIA_SECTION);
        }

        try {
            Verifier.verifyNotNull(req.getCriteria().getUserCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(UserMessageHandlerConst.MESSAGE_MISSING_USER_CRITERIA_SECTION);
        }
    }

}

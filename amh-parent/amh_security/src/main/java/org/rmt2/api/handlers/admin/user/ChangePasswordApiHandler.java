package org.rmt2.api.handlers.admin.user;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.UserDto;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.constants.MessagingConstants;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes user password change related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class ChangePasswordApiHandler extends UserApiHandler {
    
    private static final Logger logger = Logger.getLogger(ChangePasswordApiHandler.class);

    /**
     * @param payload
     */
    public ChangePasswordApiHandler() {
        super();
        logger.info(ChangePasswordApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to change the user's
     * password.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        UserDto dto = UserJaxbDtoFactory.createDtoInstance(this.requestObj.getProfile().getUserInfo().get(0));
        try {
            // call api
            api.beginTrans();
            this.api.changePassword(dto.getUsername(), dto.getPassword());
            this.rs.setMessage(UserMessageHandlerConst.MESSAGE_CHANGE_PASSWORD_SUCCESS);
            this.rs.setRecordCount(1);
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setReturnCode(MessagingConstants.RETURN_CODE_FAILURE);
            this.rs.setMessage(UserMessageHandlerConst.MESSAGE_CHANGE_PASSWORD_ERROR);
            this.rs.setReturnStatus(MessagingConstants.RETURN_STATUS_SUCCESS);
            this.rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            // Include profile data in response
            List<UserDto> list = new ArrayList<>();
            list.add(dto);
            this.jaxbObj = UserJaxbDtoFactory.createJaxbInstance(list);

            // Redact the password since it comes back as the raw value the user
            // entered.
            if (this.jaxbObj.size() == 1) {
                this.jaxbObj.get(0).setPassword(UserMessageHandlerConst.PASSWORD_REDACTED);
            }
            api.close();
        }
        return;
    }


    @Override
    protected void validateRequest(AuthenticationRequest req) throws InvalidDataException {
        super.validateRequest(req);

        try {
            Verifier.verifyTrue(req.getHeader().getTransaction()
                    .equalsIgnoreCase(ApiTransactionCodes.AUTH_CHANGE_PASSWORD));
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

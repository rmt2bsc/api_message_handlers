package org.rmt2.api.handlers.admin.user;

import java.util.ArrayList;
import java.util.List;

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
 * Handles and routes user update related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class UserUpdateApiHandler extends UserApiHandler {
    
    private static final Logger logger = Logger.getLogger(UserUpdateApiHandler.class);

    /**
     * @param payload
     */
    public UserUpdateApiHandler() {
        super();
        logger.info(UserUpdateApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to add a new or modify
     * an existing user object.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        UserDto dto = UserJaxbDtoFactory.createDtoInstance(this.requestObj.getProfile().getUserInfo().get(0));
        boolean newRec = (dto.getLoginUid() == 0);

        int rc = 0;
        try {
            // call api
            api.beginTrans();
            rc = this.api.updateUser(dto);

            if (rc > 0) {
                if (newRec) {
                    this.rs.setMessage(UserMessageHandlerConst.MESSAGE_CREATE_SUCCESS);
                    this.rs.setExtMessage("The user id is " + rc);
                    this.rs.setRecordCount(1);
                    // Update DTO with new group id. This is probably
                    // done at the DAO level.
                    dto.setGroupId(rc);

                    // Include profile data in response
                    List<UserDto> list = new ArrayList<>();
                    list.add(dto);
                    this.jaxbObj = UserJaxbDtoFactory.createJaxbInstance(list);
                }
                else {
                    this.rs.setMessage(UserMessageHandlerConst.MESSAGE_UPDATE_SUCCESS);
                    this.rs.setExtMessage("Total number of user objects modified: " + rc);
                    this.rs.setRecordCount(rc);

                    // Do not include profile data in response
                    this.jaxbObj = null;
                }
            }
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(UserMessageHandlerConst.MESSAGE_UPDATE_ERROR);
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
                    .equalsIgnoreCase(ApiTransactionCodes.AUTH_USER_UPDATE));
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

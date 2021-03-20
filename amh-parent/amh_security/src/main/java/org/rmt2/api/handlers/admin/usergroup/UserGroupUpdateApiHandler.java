package org.rmt2.api.handlers.admin.usergroup;

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
 * Handles and routes user group update related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class UserGroupUpdateApiHandler extends UserGroupApiHandler {
    
    private static final Logger logger = Logger.getLogger(UserGroupUpdateApiHandler.class);

    /**
     * @param payload
     */
    public UserGroupUpdateApiHandler() {
        super();
        logger.info(UserGroupUpdateApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to add a new or modify
     * an existing resource object.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        UserDto dto = UserGroupJaxbDtoFactory.createDtoInstance(this.requestObj.getCriteria().getUserCriteria());
        boolean newRec = (dto.getGroupId() == 0);

        int rc = 0;
        try {
            // call api
            api.beginTrans();
            rc = this.api.updateGroup(dto);

            if (rc > 0) {
                if (newRec) {
                    this.rs.setMessage(UserGroupMessageHandlerConst.MESSAGE_CREATE_SUCCESS);
                    this.rs.setExtMessage("The user group id is " + rc);
                    this.rs.setRecordCount(1);
                    // Update DTO with new group id. This is probably
                    // done at the DAO level.
                    dto.setGroupId(rc);

                    // Include profile data in response
                    this.jaxbObj = UserGroupJaxbDtoFactory.createJaxbInstance(dto);
                }
                else {
                    this.rs.setMessage(UserGroupMessageHandlerConst.MESSAGE_UPDATE_SUCCESS);
                    this.rs.setExtMessage("Total number of user group objects modified: " + rc);
                    this.rs.setRecordCount(rc);

                    // Do not include profile data in response
                    this.jaxbObj = null;
                }
            }
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(UserGroupMessageHandlerConst.MESSAGE_UPDATE_ERROR);
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
                    .equalsIgnoreCase(ApiTransactionCodes.AUTH_USER_GROUP_UPDATE));
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE);
        }

        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_MISSING_PROFILE_SECTION);
        }

        try {
            Verifier.verifyTrue(req.getProfile().getUserGroupInfo().size() > 0);
        } catch (VerifyException e) {
            throw new InvalidRequestException(UserGroupMessageHandlerConst.MESSAGE_MISSING_USERGROUP_SECTION);
        }

        try {
            Verifier.verifyTrue(req.getProfile().getUserGroupInfo().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_TOO_MANY_RECORDS);
        }
    }

}

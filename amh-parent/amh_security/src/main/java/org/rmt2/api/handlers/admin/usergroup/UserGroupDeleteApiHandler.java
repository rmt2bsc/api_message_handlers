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
 * Handles and routes user group delete related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class UserGroupDeleteApiHandler extends UserGroupApiHandler {
    
    private static final Logger logger = Logger.getLogger(UserGroupDeleteApiHandler.class);

    /**
     * @param payload
     */
    public UserGroupDeleteApiHandler() {
        super();
        logger.info(UserGroupDeleteApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to delete
     * an existing resource object.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        UserDto dto = UserGroupJaxbDtoFactory.createDtoInstance(this.requestObj.getProfile().getUserGroupInfo().get(0));      
        int rc = 0;
        try {
            // call api
            api.beginTrans();
            rc = this.api.deleteGroup(dto.getGroupId());

            if (rc > 0) {
                this.rs.setMessage(UserGroupMessageHandlerConst.MESSAGE_DELETE_SUCCESS);
                this.rs.setExtMessage("The user group id: " + dto.getGroupId());
            }
            else {
                this.rs.setMessage(UserGroupMessageHandlerConst.MESSAGE_NOT_FOUND);
                this.rs.setExtMessage("The user group id: " + dto.getGroupId());
            }
            this.rs.setRecordCount(rc);
            this.jaxbObj = null;
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(UserGroupMessageHandlerConst.MESSAGE_DELETE_ERROR);
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
                    .equalsIgnoreCase(ApiTransactionCodes.AUTH_USER_GROUP_DELETE));
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

package org.rmt2.api.handlers.admin.usergroup;

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
 * Handles and routes user group query related messages to the Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class UserGroupQueryApiHandler extends UserGroupApiHandler {
    
    private static final Logger logger = Logger.getLogger(UserGroupQueryApiHandler.class);

    /**
     * @param payload
     */
    public UserGroupQueryApiHandler() {
        super();
        logger.info(UserGroupQueryApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to fetch user group
     * objects.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        UserDto dto = UserGroupJaxbDtoFactory.createDtoInstance(this.requestObj.getCriteria().getUserCriteria());
        List<UserDto> list = null;
        try {
            // call api
            list = api.getGroup(dto);
            if (list == null) {
                this.rs.setMessage(UserGroupMessageHandlerConst.MESSAGE_NOT_FOUND);
                this.rs.setRecordCount(0);
                this.jaxbObj = null;
            }
            else {
                this.rs.setMessage(UserGroupMessageHandlerConst.MESSAGE_FOUND);
                this.rs.setRecordCount(list.size());
                this.jaxbObj = UserGroupJaxbDtoFactory.createJaxbResourcesInfoInstance(list);
            }
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(UserGroupMessageHandlerConst.MESSAGE_FETCH_ERROR);
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
            Verifier.verifyTrue(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.AUTH_USER_GROUP_GET));
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
            throw new InvalidRequestException(UserGroupMessageHandlerConst.MESSAGE_MISSING_USERGROUP_CRITERIA_SECTION);
        }
    }

}

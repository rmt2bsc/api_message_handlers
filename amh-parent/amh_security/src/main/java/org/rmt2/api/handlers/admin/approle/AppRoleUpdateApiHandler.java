package org.rmt2.api.handlers.admin.approle;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.CategoryDto;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes Application Role update related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class AppRoleUpdateApiHandler extends AppRoleApiHandler {
    
    private static final Logger logger = Logger.getLogger(AppRoleUpdateApiHandler.class);

    /**
     * @param payload
     */
    public AppRoleUpdateApiHandler() {
        super();
        logger.info(AppRoleUpdateApiHandler.class.getName() + " was instantiated successfully");
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
        CategoryDto dto = AppRoleJaxbDtoFactory.createDtoInstance(this.requestObj.getProfile().getAppRoleInfo().get(0));
        boolean newRec = (dto.getAppRoleId() == 0);

        int rc = 0;
        try {
            // call api
            api.beginTrans();
            rc = this.api.update(dto);

            if (rc > 0) {
                if (newRec) {
                    this.rs.setMessage(AppRoleMessageHandlerConst.MESSAGE_CREATE_SUCCESS);
                    this.rs.setExtMessage("The new application role id is " + rc);
                    this.rs.setRecordCount(1);
                    // Update DTO with new group id. This is probably
                    // done at the DAO level.
                    dto.setAppRoleId(rc);

                    // Include profile data in response
                    List<CategoryDto> list = new ArrayList<>();
                    list.add(dto);
                    this.jaxbObj = AppRoleJaxbDtoFactory.createJaxbInstance(list);
                }
                else {
                    this.rs.setMessage(AppRoleMessageHandlerConst.MESSAGE_UPDATE_SUCCESS);
                    this.rs.setExtMessage("Total number of user group objects modified: " + rc);
                    this.rs.setRecordCount(rc);

                    // Do not include profile data in response
                    this.jaxbObj = null;
                }
            }
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(AppRoleMessageHandlerConst.MESSAGE_UPDATE_ERROR);
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
                    .equalsIgnoreCase(ApiTransactionCodes.AUTH_APP_ROLE_UPDATE));
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE);
        }

        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_MISSING_PROFILE_SECTION);
        }

        try {
            Verifier.verifyTrue(req.getProfile().getAppRoleInfo().size() > 0);
        } catch (VerifyException e) {
            throw new InvalidRequestException(AppRoleMessageHandlerConst.MESSAGE_MISSING_APPROLE_SECTION);
        }

        try {
            Verifier.verifyTrue(req.getProfile().getAppRoleInfo().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_TOO_MANY_RECORDS);
        }
    }

}

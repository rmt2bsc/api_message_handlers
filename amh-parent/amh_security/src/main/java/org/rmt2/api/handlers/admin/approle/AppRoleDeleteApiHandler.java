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
 * Handles and routes Application Role delete related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class AppRoleDeleteApiHandler extends AppRoleApiHandler {
    
    private static final Logger logger = Logger.getLogger(AppRoleDeleteApiHandler.class);

    /**
     * @param payload
     */
    public AppRoleDeleteApiHandler() {
        super();
        logger.info(AppRoleDeleteApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to delete an existing
     * resource object.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        CategoryDto dto = AppRoleJaxbDtoFactory.createDtoInstance(this.requestObj.getProfile().getAppRoleInfo().get(0));
        int rc = 0;
        try {
            // call api
            api.beginTrans();
            rc = this.api.delete(dto.getAppRoleId());

            this.rs.setRecordCount(rc);
            if (rc > 0) {
                this.rs.setMessage(AppRoleMessageHandlerConst.MESSAGE_DELETE_SUCCESS);
                this.rs.setExtMessage("The deleted application role id is " + dto.getAppRoleId());

                // Include profile data in response
                List<CategoryDto> list = new ArrayList<>();
                list.add(dto);
                this.jaxbObj = AppRoleJaxbDtoFactory.createJaxbInstance(list);
            }
            else {
                // Do not include profile data in response
                this.jaxbObj = null;
                this.rs.setMessage(AppRoleMessageHandlerConst.MESSAGE_NOT_FOUND);
                this.rs.setExtMessage("Total number of application role objects modified: " + rc);
                this.rs.setRecordCount(rc);
            }
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(AppRoleMessageHandlerConst.MESSAGE_DELETE_ERROR);
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
                    .equalsIgnoreCase(ApiTransactionCodes.AUTH_APP_ROLE_DELETE));
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

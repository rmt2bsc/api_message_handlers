package org.rmt2.api.handlers.admin.application;

import org.apache.log4j.Logger;
import org.dto.ApplicationDto;
import org.modules.application.AppApi;
import org.modules.application.AppApiFactory;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes application delete related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class ApplicationDeleteApiHandler extends ApplicationApiHandler {
    
    private static final Logger logger = Logger.getLogger(ApplicationDeleteApiHandler.class);

    /**
     * @param payload
     */
    public ApplicationDeleteApiHandler() {
        super();
        logger.info(ApplicationDeleteApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to delete an existing
     * application object.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        this.jaxbObj = null;
        ApplicationDto appDto = ApplicationJaxbDtoFactory
                .createDtoInstance(this.requestObj.getProfile().getApplicationInfo().get(0));
        int appId = appDto.getApplicationId();
        AppApi api = AppApiFactory.createApi();
        int rc = 0;
        try {
            // call api
            api.beginTrans();
            rc = api.delete(appId);

            if (rc > 0) {
                this.rs.setMessage(ApplicationMessageHandlerConst.MESSAGE_DELETE_SUCCESS);
                this.rs.setExtMessage("Application id of deleted record is: " + appId);
            }
            else {
                this.rs.setMessage(ApplicationMessageHandlerConst.MESSAGE_NOT_FOUND);
                this.rs.setExtMessage("Application id: " + appId);
            }
            this.rs.setRecordCount(rc);
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(ApplicationMessageHandlerConst.MESSAGE_DELETE_ERROR);
            this.rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        }
        return;
    }


    @Override
    protected void validateRequest(AuthenticationRequest req) throws InvalidDataException {
        super.validateRequest(req);

        try {
            Verifier.verifyTrue(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.AUTH_APPLICATION_DELETE));
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE);
        }

        try {
            Verifier.verifyNotNull(req.getProfile());
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_MISSING_PROFILE_SECTION);
        }

        try {
            Verifier.verifyNotNull(req.getProfile().getApplicationInfo());
            Verifier.verifyTrue(req.getProfile().getApplicationInfo().size() > 0);
        } catch (VerifyException e) {
            throw new InvalidRequestException(ApplicationMessageHandlerConst.MESSAGE_MISSING_APPLICATION_SECTION);
        }

        try {
            Verifier.verifyTrue(req.getProfile().getApplicationInfo().size() == 1);
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_TOO_MANY_RECORDS);
        }
    }

}

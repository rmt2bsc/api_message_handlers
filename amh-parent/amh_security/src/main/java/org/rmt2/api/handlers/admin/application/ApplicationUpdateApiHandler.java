package org.rmt2.api.handlers.admin.application;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.dto.ApplicationDto;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes application update related messages to the
 * Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class ApplicationUpdateApiHandler extends ApplicationApiHandler {
    
    private static final Logger logger = Logger.getLogger(ApplicationUpdateApiHandler.class);

    /**
     * @param payload
     */
    public ApplicationUpdateApiHandler() {
        super();
        logger.info(ApplicationUpdateApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to add a new or modify
     * an existing application object.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        ApplicationDto appDto = ApplicationJaxbDtoFactory
                .createDtoInstance(this.requestObj.getProfile().getApplicationInfo().get(0));
        boolean newRec = (appDto.getApplicationId() == 0);
        int rc = 0;
        try {
            // call api
            // IS-70: Used member variable representing the API instance
            api.beginTrans();
            rc = this.api.update(appDto);

            if (rc > 0) {
                if (newRec) {
                    this.rs.setMessage(ApplicationMessageHandlerConst.MESSAGE_CREATE_SUCCESS);
                    this.rs.setExtMessage("The application id is " + rc);
                    this.rs.setRecordCount(1);
                    // Update DTO with new application id. This is probably done
                    // at the DAO level.
                    appDto.setApplicationId(rc);
                }
                else {
                    this.rs.setMessage(ApplicationMessageHandlerConst.MESSAGE_UPDATE_SUCCESS);
                    this.rs.setExtMessage("Total number of application objects modified: " + rc);
                    this.rs.setRecordCount(rc);
                }
                this.jaxbObj = new ArrayList<>();
                this.jaxbObj.add(ApplicationJaxbDtoFactory.createJaxbInstance(appDto));
            }
            api.commitTrans();
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(ApplicationMessageHandlerConst.MESSAGE_UPDATE_ERROR);
            this.rs.setExtMessage(e.getMessage());
            api.rollbackTrans();
        } finally {
            // IS-70: Added logic to close database connections associated with
            // the API instance to prevent memory leaks.
            this.api.close();
            this.api = null;
        }
        return;
    }


    @Override
    protected void validateRequest(AuthenticationRequest req) throws InvalidDataException {
        super.validateRequest(req);

        try {
            Verifier.verifyTrue(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.AUTH_APPLICATION_UPDATE));
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

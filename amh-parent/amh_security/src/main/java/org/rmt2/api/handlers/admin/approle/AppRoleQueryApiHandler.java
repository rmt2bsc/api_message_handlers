package org.rmt2.api.handlers.admin.approle;

import java.util.List;

import org.apache.log4j.Logger;
import org.dto.CategoryDto;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.AuthenticationAdminJaxbDtoFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes application role query related messages to the Authentication/Security API.
 * 
 * @author roy.terrell
 *
 */
public class AppRoleQueryApiHandler extends AppRoleApiHandler {
    
    private static final Logger logger = Logger.getLogger(AppRoleQueryApiHandler.class);

    /**
     * @param payload
     */
    public AppRoleQueryApiHandler() {
        super();
        logger.info(AppRoleQueryApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to fetch application role
     * objects.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        CategoryDto dto = AuthenticationAdminJaxbDtoFactory.createAppRoleCriteriaDtoInstance(this.requestObj.getCriteria().getUserAppRolesCriteria());
        List<CategoryDto> list = null;
        try {
            // call api
            list = api.get(dto);
            if (list == null) {
                this.rs.setMessage(AppRoleMessageHandlerConst.MESSAGE_NOT_FOUND);
                this.rs.setRecordCount(0);
                this.jaxbObj = null;
            }
            else {
                this.rs.setMessage(AppRoleMessageHandlerConst.MESSAGE_FOUND);
                this.rs.setRecordCount(list.size());
                this.jaxbObj = AppRoleJaxbDtoFactory.createJaxbInstance(list);
            }
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(AppRoleMessageHandlerConst.MESSAGE_FETCH_ERROR);
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
            Verifier.verifyTrue(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.AUTH_APP_ROLE_GET));
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_INVALID_TRANSACTION_CODE);
        }

        try {
            Verifier.verifyNotNull(req.getCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(AuthenticationMessageHandlerConst.MSG_MISSING_CRITERIA_SECTION);
        }

        try {
            Verifier.verifyNotNull(req.getCriteria().getUserAppRolesCriteria());
        } catch (VerifyException e) {
            throw new InvalidRequestException(AppRoleMessageHandlerConst.MESSAGE_MISSING_APPROLE_CRITERIA_SECTION);
        }
    }

}

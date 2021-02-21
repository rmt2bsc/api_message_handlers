package org.rmt2.api.handlers.admin.role;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.dto.CategoryDto;
import org.modules.roles.RoleApi;
import org.modules.roles.RoleSecurityApiFactory;
import org.rmt2.api.ApiMessageHandlerConst;
import org.rmt2.api.handlers.AuthenticationMessageHandlerConst;
import org.rmt2.api.handlers.admin.AuthenticationAdminJaxbDtoFactory;
import org.rmt2.constants.ApiTransactionCodes;
import org.rmt2.jaxb.AuthenticationRequest;
import org.rmt2.jaxb.UserAppRolesCriteriaType;

import com.InvalidDataException;
import com.api.messaging.InvalidRequestException;
import com.api.util.RMT2String;
import com.api.util.assistants.Verifier;
import com.api.util.assistants.VerifyException;

/**
 * Handles and routes role query related messages to the Authentication/Security
 * API.
 * 
 * @author roy.terrell
 *
 */
public class RoleQueryApiHandler extends RoleApiHandler {
    
    private static final Logger logger = Logger.getLogger(RoleQueryApiHandler.class);

    /**
     * @param payload
     */
    public RoleQueryApiHandler() {
        super();
        logger.info(RoleQueryApiHandler.class.getName() + " was instantiated successfully");
    }

    /**
     * Handler for invoking the appropriate API in order to fetch role objects.
     * 
     * @param req
     *            an instance of {@link AuthenticationRequest}
     * @return an instance of {@link MessageHandlerResults}
     */
    @Override
    protected void processTransactionCode() {
        UserAppRolesCriteriaType jaxbCriteria = this.requestObj.getCriteria().getUserAppRolesCriteria();
        CategoryDto criteriaDto = AuthenticationAdminJaxbDtoFactory.createRoleCriteriaDtoInstance(jaxbCriteria);
        RoleApi api = RoleSecurityApiFactory.createRoleApi();
        List<CategoryDto> list = null;
        try {
            // call api
            list = api.get(criteriaDto);
            if (list == null) {
                this.rs.setMessage(RoleMessageHandlerConst.MESSAGE_NOT_FOUND);
                this.rs.setRecordCount(0);
                this.jaxbObj = null;
            }
            else {
                this.rs.setMessage(RoleMessageHandlerConst.MESSAGE_FOUND);
                this.rs.setRecordCount(list.size());
                this.jaxbObj = new ArrayList<>();
                this.jaxbObj.addAll(RoleJaxbDtoFactory.createJaxbInstance(list));
            }
        } catch (Exception e) {
            logger.error("Error occurred during API Message Handler operation, " + this.command, e);
            this.rs.setMessage(RoleMessageHandlerConst.MESSAGE_FETCH_ERROR);
            this.rs.setExtMessage(e.getMessage());
        }
        return;
    }


    @Override
    protected void validateRequest(AuthenticationRequest req) throws InvalidDataException {
        super.validateRequest(req);

        try {
            Verifier.verifyTrue(req.getHeader().getTransaction().equalsIgnoreCase(ApiTransactionCodes.AUTH_ROLE_GET));
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
            String errMsg = RMT2String.replace(AuthenticationMessageHandlerConst.MSG_MISSING_USER_APP_ROLE_CRITERIA_SECTION, req
                    .getHeader().getApplication(), ApiMessageHandlerConst.MSG_PLACEHOLDER);
            throw new InvalidRequestException(errMsg);
        }
    }

}

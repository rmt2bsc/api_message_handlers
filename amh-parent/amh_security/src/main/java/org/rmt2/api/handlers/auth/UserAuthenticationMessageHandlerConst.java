package org.rmt2.api.handlers.auth;

/**
 * User authentication constancts
 * 
 * @author roy.terrell
 *
 */
public class UserAuthenticationMessageHandlerConst {
    
    public static final String MESSAGE_AUTH_SUCCESS = "User authenticated successfully!";
    public static final String MESSAGE_AUTH_FAILED = "User name or password is incorrect";
    public static final String MESSAGE_AUTH_API_VALIDATION_ERROR = "Authentication API validation error occurred";
    public static final String MESSAGE_MISSING_USER_APP_ROLE_CRITERIA_SECTION = "User Application Role criteria section is required for query operations";

    /**
     * 
     */
    public UserAuthenticationMessageHandlerConst() {
    }

}

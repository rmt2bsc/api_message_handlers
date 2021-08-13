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
    public static final String MESSAGE_FETCH_ERROR = "User authentication fetch operation failed";
    public static final String MESSAGE_LOGOUT_SUCCESS = "User logged out successfully!";
    public static final String MESSAGE_LOGOUT_FAILED = "User logout failed";
    public static final String MESSAGE_AUTH_API_VALIDATION_ERROR = "Authentication API validation error occurred";
    public static final String MESSAGE_MISSING_USER_APP_ROLE_SECTION = "User Application Role criteria section is required for query operations";
    public static final String MESSAGE_INVALID_APPLICATION_ACCESS_INFO = "Application Access Info section is invalid or missing";
    public static final String MESSAGE_INVALID_USERINFO = "User Info section is invalid or missing";

    /**
     * 
     */
    public UserAuthenticationMessageHandlerConst() {
    }

}

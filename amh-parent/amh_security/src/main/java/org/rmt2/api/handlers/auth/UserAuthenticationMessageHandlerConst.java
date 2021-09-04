package org.rmt2.api.handlers.auth;

/**
 * User authentication constancts
 * 
 * @author roy.terrell
 *
 */
public class UserAuthenticationMessageHandlerConst {
    
    public static final String MESSAGE_AUTH_SUCCESS = "User, %1, authenticated successfully!  Total number of applications available: %2";
    public static final String MESSAGE_AUTH_FAILED = "User name or password is incorrect";
    public static final String MESSAGE_AUTH_SSO_SUCCESS = "User, %1, authenticated successfully via SSO!  Total number of applications available: %2";
    public static final String MESSAGE_AUTH_SSO_FAILED = "User SSO attempt failed";
    public static final String MESSAGE_FETCH_ERROR = "User authentication fetch operation failed";
    public static final String MESSAGE_LOGOUT_SUCCESS = "User, %1, logged out successfully!  Remaining applications available are %2";
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

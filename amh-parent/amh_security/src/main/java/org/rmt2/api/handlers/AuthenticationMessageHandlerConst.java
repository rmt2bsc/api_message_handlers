package org.rmt2.api.handlers;

/**
 * Authentication common message handler API constants
 * 
 * @author appdev
 *
 */
public class AuthenticationMessageHandlerConst {
    public static final String MSG_MISSING_CRITERIA_SECTION = "Authentication criteria section is missing";
    public static final String MSG_MISSING_PROFILE_SECTION = "Authentication profile section is missing";
    public static final String MSG_MISSING_USER_APP_ROLE_CRITERIA_SECTION = "Application criteria section is required for %s query operations";
    public static final String MSG_TOO_MANY_RECORDS = "Too many records exist in the request";
    public static final String MSG_INVALID_TRANSACTION_CODE = "Transaction code is invalid for target operation";

    /**
     * 
     */
    public AuthenticationMessageHandlerConst() {
    }

}

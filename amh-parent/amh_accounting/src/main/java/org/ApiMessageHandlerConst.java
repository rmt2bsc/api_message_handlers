package org;

/**
 * Common message handler API constants
 * 
 * @author appdev
 *
 */
public class ApiMessageHandlerConst {

    public static final String TARGET_LEVEL_HEADER = "HEADER";
    public static final String TARGET_LEVEL_DETAILS = "DETAILS";
    public static final String TARGET_LEVEL_FULL = "FULL";
    public static final String MSG_DETAILS_NOT_SUPPORTED = "Transaction level \"DETAILS\" is not supported at this time";
    public static final String MSG_MISSING_TARGET_LEVEL = "Fetch request must contain a target level value";
    public static final String MSG_INCORRECT_TARGET_LEVEL = "Fetch request contains an invalid target level: %s";
    public static final String MSG_MISSING_PROFILE_DATA = "Profile data is required for create operation";
    public static final String MSG_MISSING_TRANSACTION_SECTION = "Transaction section is missing from the profile data section";

    /**
     * 
     */
    public ApiMessageHandlerConst() {
    }

}

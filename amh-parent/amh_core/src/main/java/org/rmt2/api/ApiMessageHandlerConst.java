package org.rmt2.api;

/**
 * Common message handler API constants
 * 
 * @author appdev
 *
 */
public class ApiMessageHandlerConst {
    public static final String MSG_PLACEHOLDER = "%s";
    public static final String TARGET_LEVEL_HEADER = "HEADER";
    public static final String TARGET_LEVEL_DETAILS = "DETAILS";
    public static final String TARGET_LEVEL_FULL = "FULL";
    public static final String MSG_DETAILS_NOT_SUPPORTED = "Fetch request does not support level \"DETAILS\" at this time";
    public static final String MSG_MISSING_TARGET_LEVEL = "Fetch request must contain a target level value";
    public static final String MSG_INCORRECT_TARGET_LEVEL = "Fetch request contains an invalid target level: " + MSG_PLACEHOLDER;
    public static final String MSG_MISSING_PROFILE_DATA = "Profile data is required for update type operations";

    /**
     * 
     */
    public ApiMessageHandlerConst() {
    }

}

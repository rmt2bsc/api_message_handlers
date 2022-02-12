package org.rmt2.api;

/**
 * Common message handler API constants
 * 
 * @author appdev
 *
 */
public class ApiMessageHandlerConst {
    public static final String MSG_PLACEHOLDER = "%s";
    public static final String MSG_PLACEHOLDER1 = "%1";
    public static final String MSG_PLACEHOLDER2 = "%2";
    public static final String TARGET_LEVEL_HEADER = "HEADER";
    public static final String TARGET_LEVEL_DETAILS = "DETAILS";
    public static final String TARGET_LEVEL_FULL = "FULL";
    public static final String MSG_DETAILS_NOT_SUPPORTED = "Fetch request does not support level \"DETAILS\" at this time";
    public static final String MSG_MISSING_TARGET_LEVEL = "Fetch request must contain a target level value";
    public static final String MSG_INCORRECT_TARGET_LEVEL = "Fetch request contains an invalid target level: " + MSG_PLACEHOLDER;
    public static final String MSG_MISSING_PROFILE_DATA = "Profile data is required for update type operations";
    public static final String MSG_MISSING_CRITERIA_DATA = "Criteria data is required for update type operations";
    public static final String MEDIA_LINK_PROCESSING_SKIPPED = "%s project is not applicable for this attachment";
    public static final String MEDIA_LINK_VALID_APPNAME_ACCOUNTING = "Accounting";
    public static final String MEDIA_LINK_VALID_MODULENAME_ACCOUNTING = "transaction";
    public static final String MEDIA_LINK_VALID_APPNAME_PROJECTTRACKER = "ProjectTracker";
    public static final String MEDIA_LINK_VALID_MODULENAME_PROJECTTRACKER = "timesheet";

    /**
     * 
     */
    public ApiMessageHandlerConst() {
    }

}

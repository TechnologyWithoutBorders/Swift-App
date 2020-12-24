package ngo.teog.swift.helpers;

/**
 * This class contains all standardized resource keys used within the application and for server communication.
 * @author nitelow
 */
public final class ResourceKeys {
    /** Identifier for device IDs */
    public static final String DEVICE_ID = "device_id";

    /** Identifier for device objects */
    public static final String DEVICE = "device";

    /** Identifier for device object lists */
    public static final String DEVICES = "devices";

    /** Identifier for user IDs */
    public static final String USER_ID = "user_id";

    public static final String USER_MAIL = "user_mail";
    public static final String USER_NAME = "user_name";

    /** Identifier for user objects */
    public static final String USER = "user";

    /** Identifier for user object lists */
    public static final String USERS = "users";

    /** Identifier for report IDs */
    public static final String REPORT_ID = "report_id";

    /** Identifier for report objects */
    public static final String REPORT = "report";

    /** Identifier for report object lists */
    public static final String REPORTS = "reports";

    /** Identifier for all kinds of images */
    public static final String IMAGE = "image";

    public static final String IMAGE_HASH = "image_hash";//TODO der müsste eher wo anders hin
    public static final String LAST_SYNC = "lastSync";//TODO der auch

    /** Identifier for all kinds of paths */
    public static final String PATH = "path";

    public static final String HOSPITAL_ID = "hospital_id";

    /** Identifier for hospital objects */
    public static final String HOSPITAL = "hospital";

    /** Identifier for previous states used in reports */
    public static final String REPORT_OLD_STATE = "old_state";

    /** Identifier for current states used in reports */
    public static final String REPORT_NEW_STATE = "new_state";

    /** Identifier for all kinds of data */
    public static final String DATA = "data";
}

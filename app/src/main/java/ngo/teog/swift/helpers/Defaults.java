package ngo.teog.swift.helpers;

/**
 * Contains some global system constants.
 * @author nitelow
 */

public final class Defaults {

    //Preferences

    /** Key that identifies shared preferences file */
    public static final String PREF_FILE_KEY = "ngo.teog.swift.PREFERENCE_FILE_KEY";

    /** Key that identifies user ID in shared preferences */
    public static final String ID_PREFERENCE = "ID_PREFERENCE";

    /** Key that identifies user password in shared preferences */
    public static final String PW_PREFERENCE = "PW_PREFERENCE";

    /** Key that identifies user country in shared preferences */
    public static final String COUNTRY_PREFERENCE = "COUNTRY_PREFERENCE";

    /** Key that identifies timestamp of last synchronization with server in shared preferences */
    public static final String LAST_SYNC_PREFERENCE = "LAST_SYNC_PREFERENCE";

    //Server URLs

    /**
     * Base URL for server communication. The number in the last segment determines
     * the version of the interface.
     */
    public static final String BASE_URL = "https://teog.virlep.de/interface/4/";

    /** Additional URL segment for server communication regarding devices (e.g. device image upload) */
    public static final String DEVICES_URL = "devices.php";

    /** Additional URL segment for server communication regarding users (e.g. login requests) */
    public static final String USERS_URL = "users.php";

    /** Additional URL segment for server communication regarding hospitals (general synchronization) */
    public static final String HOSPITALS_URL = "hospitals.php";

    public static final String DOCUMENTS_URL = "documents.php";

    //Local paths

    /** Folder for device images in local file system */
    public static final String DEVICE_IMAGE_PATH = "device_images";

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DATETIME_PRECISE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String ACTION_KEY = "action";
    public static final String COUNTRY_KEY = "country";

    /** Prefix for telephone URIs */
    public static final String URI_TEL_PREFIX = "tel:";

    /** Prefix for e-mail URIs */
    public static final String URI_MAILTO_PREFIX = "mailto:";

    /** Key that identifies user ID used for server authentication */
    public static final String AUTH_ID_KEY = "authId";

    /** Key that identifies user password used for server authentication */
    public static final String AUTH_PW_KEY = "authPw";
}

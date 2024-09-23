package ngo.teog.swift.helpers;

import java.time.format.DateTimeFormatter;

/**
 * Contains some global system constants/default values.
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

    public static final String LAST_RESET_PREFERENCE = "LAST_RESET_PREFERENCE";

    //Server URLs

    public static final String HOST = "https://teog.virlep.de/";
    public static final String INTERFACE_PATH = "interface/";
    public static final int INTERFACE_ID = 5;

    public static final String BASE_URL = HOST+INTERFACE_PATH+INTERFACE_ID+"/";

    public static final String DOCUMENTS_PATH = "device_documents/";

    /** Additional URL segment for server communication regarding devices (e.g. device image upload) */
    public static final String DEVICES_URL = "devices.php";

    /** Additional URL segment for server communication regarding users (e.g. login requests) */
    public static final String USERS_URL = "users.php";

    /** Additional URL segment for server communication regarding hospitals (general synchronization) */
    public static final String HOSPITALS_URL = "hospitals.php";

    public static final String INFO_URL = "info.php";

    public static final String DOCUMENTS_URL = "documents.php";

    //Local paths

    /** Folder for device images in local file system */
    public static final String DEVICE_IMAGE_PATH = "device_images";

    /** File name for dump of hospital data */
    public static final String EXPORT_FILE_NAME = "swift_export.zip";
    public static final String ACTIVITY_EXPORT_FILE_NAME = "swift_activity.csv";

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DATETIME_PRECISE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String VERSION_CODE_KEY = "version_code";
    public static final String ACTION_KEY = "action";
    public static final String COUNTRY_KEY = "country";

    public static final String HOSPITAL_KEY = "hospital";

    /** Prefix for telephone URIs */
    public static final String URI_TEL_PREFIX = "tel:";

    /** Prefix for e-mail URIs */
    public static final String URI_MAILTO_PREFIX = "mailto:";

    /** Key that identifies user ID used for server authentication */
    public static final String AUTH_ID_KEY = "authId";

    /** Key that identifies user password used for server authentication */
    public static final String AUTH_PW_KEY = "authPw";

    public static final String TIMEZONE_UTC = "UTC";

    //Observables

    /** Observable that indicates when a server synchronization took place recently */
    public static final int SYNC_OBSERVABLE = 1;

    //Date/Time formatters
    public static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
}

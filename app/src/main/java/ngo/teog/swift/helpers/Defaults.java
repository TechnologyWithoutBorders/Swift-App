package ngo.teog.swift.helpers;

/**
 * Klasse mit globalen Konstanten für bessere Übersicht.
 * Strings sollte man aber eher in der strings.xml unter resources ablegen.
 * Aber es wird sicher auch andere Konstanten geben.
 * Created by Julian on 05.11.2017.
 */

public final class Defaults {

    //Preferences
    public static final String PREF_FILE_KEY = "ngo.teog.swift.PREFERENCE_FILE_KEY";

    public static final String ID_PREFERENCE = "ID_PREFERENCE";
    public static final String PW_PREFERENCE = "PW_PREFERENCE";
    public static final String COUNTRY_PREFERENCE = "COUNTRY_PREFERENCE";
    public static final String LAST_SYNC_PREFERENCE = "LAST_SYNC_PREFERENCE";
    public static final String NOTIFICATION_COUNTER = "NOTIFICATION_COUNTER";

    //Paths
    public static final String BASE_URL = "https://teog.virlep.de/interface/2/";
    public static final String DEVICES_URL = "devices.php";
    public static final String USERS_URL = "users.php";
    public static final String NEWS_URL = "info.php";
    public static final String REPORTS_URL = "reports.php";
    public static final String HOSPITALS_URL = "hospitals.php";

    public static final String DEVICE_IMAGE_PATH = "device_images";

    //default methods
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm";
    public static final String DATETIME_PRECISE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static final String SCOPE = "scope";
    public static final String SCOPE_LOCAL = "local";
    public static final String SCOPE_GLOBAL = "global";

    public static final String SEARCH_OBJECT = "search_object";

    public static final String ACTION_KEY = "action";
    public static final String COUNTRY_KEY = "country";

    public static final String URI_TEL_PREFIX = "tel:";
    public static final String URI_MAILTO_PREFIX = "mailto:";

    public static final String AUTH_ID_KEY = "authId";
    public static final String AUTH_PW_KEY = "authPw";
}

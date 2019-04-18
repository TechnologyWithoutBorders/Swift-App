package ngo.teog.swift.helpers;

import java.text.SimpleDateFormat;

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
    public static final String NOTIFICATION_COUNTER = "NOTIFICATION_COUNTER";

    //Paths
    public static final String BASE_URL = "https://teog.virlep.de/interface/2/";
    public static final String DEVICES_URL = "devices.php";
    public static final String USERS_URL = "users.php";
    public static final String NEWS_URL = "info.php";
    public static final String REPORTS_URL = "reports.php";
    public static final String HOSPITALS_URL = "hospitals.php";

    //default methodes
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}

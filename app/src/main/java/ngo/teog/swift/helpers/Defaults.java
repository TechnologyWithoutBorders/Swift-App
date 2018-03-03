package ngo.teog.swift.helpers;

import java.text.SimpleDateFormat;

/**
 * Klasse mit globalen Konstanten für bessere Übersicht.
 * Strings sollte man aber eher in der strings.xml unter resources ablegen.
 * Aber es wird sicher auch andere Konstanten geben.
 * Created by Julian on 05.11.2017.
 */

public final class Defaults {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    //Preferences
    public static final String PREF_FILE_KEY = "ngo.teog.hstest.PREFERENCE_FILE_KEY";

    public static final String ID_PREFERENCE = "ID_PREFERENCE";
    public static final String PW_PREFERENCE = "PW_PREFERENCE";
    public static final String LAST_NEWS_PREF = "LAST_NEWS_ID";

    //Paths
    public static final String BASE_URL = "https://teog.virlep.de/";
    public static final String DEVICES_URL = "devices.php";
    public static final String USERS_URL = "users.php";
    public static final String NEWS_URL = "info.php";
}

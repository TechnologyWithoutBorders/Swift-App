package ngo.teog.swift.helpers;

import java.text.SimpleDateFormat;

/**
 * Klasse mit globalen Konstanten für bessere Übersicht.
 * Strings sollte man aber eher in der strings.xml unter resources ablegen.
 * Aber es wird sicher auch andere Konstanten geben.
 * Created by Julian on 05.11.2017.
 */

public class Defaults {
    @Deprecated
    public static final String PREF_FILE_KEY = "ngo.teog.hstest.PREFERENCE_FILE_KEY";

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
}

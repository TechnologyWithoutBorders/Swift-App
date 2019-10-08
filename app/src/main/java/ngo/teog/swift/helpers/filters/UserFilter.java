package ngo.teog.swift.helpers.filters;

/**
 * Filterklasse für die Auswahl von Benutzern.
 * Definiert alle verfügbaren Filter (die Konstanten ganz oben) und
 * verknüpft die Filter mit den zugehörigen Werten.
 * Created by Julian on 18.11.2017.
 */

public final class UserFilter {
    public static final String ID = "id";
    public static final String FULL_NAME = "fullName";
    public static final String MAIL = "mail";
    public static final String PASSWORD = "password";
    public static final String PHONE = "phone";
    public static final String HOSPITAL = "hospital";
    public static final String POSITION = "position";
    public static final String LAST_UPDATE = "lastUpdate";

    public static final String ACTION_LOGIN_USER = "login_user";
    public static final String ACTION_FETCH_USER = "fetch_user";
    public static final String ACTION_SEARCH_USER = "search_user";
    public static final String ACTION_RESET_PASSWORD = "reset_password";
}

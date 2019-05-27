package ngo.teog.swift.helpers.filters;

/**
 * Filterklasse für die Auswahl von Benutzern.
 * Definiert alle verfügbaren Filter (die Konstanten ganz oben) und
 * verknüpft die Filter mit den zugehörigen Werten.
 * Created by Julian on 18.11.2017.
 */

public final class UserFilter {
    public static final String ID = "u_ID";
    public static final String FULL_NAME = "u_full_name";
    public static final String MAIL = "u_mail";
    public static final String PASSWORD = "u_password";
    public static final String PHONE = "u_phone";

    public static final String ACTION_LOGIN_USER = "login_user";
    public static final String ACTION_FETCH_USER = "fetch_user";
    public static final String ACTION_SEARCH_USER = "search_user";
}

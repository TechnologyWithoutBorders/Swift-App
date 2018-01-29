package ngo.teog.swift.helpers;

/**
 * Filterklasse für die Auswahl von Benutzern.
 * Definiert alle verfügbaren Filter (die Konstanten ganz oben) und
 * verknüpft die Filter mit den zugehörigen Werten.
 * Created by Julian on 18.11.2017.
 */

public class UserFilter {
    public static final String ID = "u_ID";
    public static final String FULL_NAME = "u_full_name";
    public static final String MAIL = "u_mail";
    public static final String PASSWORD = "u_password";

    private String type;
    private String value;

    public UserFilter(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}

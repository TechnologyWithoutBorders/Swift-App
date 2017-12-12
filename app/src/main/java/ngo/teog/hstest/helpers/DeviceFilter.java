package ngo.teog.hstest.helpers;

/**
 * Filterklasse für die Auswahl von Geräten.
 * Definiert alle verfügbaren Filter (die Konstanten ganz oben) und
 * verknüpft die Filter mit den zugehörigen Werten.
 * Created by Julian on 18.11.2017.
 */

public class DeviceFilter {
    public static final String ID = "id";
    public static final String NAME = "name";

    private String type;
    private String value;

    public DeviceFilter(String type, String value) {
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

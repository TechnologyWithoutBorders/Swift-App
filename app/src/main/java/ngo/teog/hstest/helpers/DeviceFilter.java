package ngo.teog.hstest.helpers;

/**
 * Filterklasse für die Auswahl von Geräten.
 * Definiert alle verfügbaren Filter (die Konstanten ganz oben) und
 * verknüpft die Filter mit den zugehörigen Werten.
 * Created by Julian on 18.11.2017.
 */

public class DeviceFilter {
    public static final String ID = "ID";
    public static final String ASSET_NUMBER = "asset_no";
    public static final String TYPE = "type";
    public static final String SERVICE_NUMBER = "service_no";
    public static final String MANUFACTURER = "manufacturer";
    public static final String MODEL = "model";
    public static final String IMAGE_PATH = "image_path";

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

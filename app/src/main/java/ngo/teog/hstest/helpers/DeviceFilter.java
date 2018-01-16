package ngo.teog.hstest.helpers;

/**
 * Filterklasse für die Auswahl von Geräten.
 * Definiert alle verfügbaren Filter (die Konstanten ganz oben) und
 * verknüpft die Filter mit den zugehörigen Werten.
 * Created by Julian on 18.11.2017.
 */

public class DeviceFilter {
    public static final String ID = "D.ID";
    public static final String ASSET_NUMBER = "D.asset_no";
    public static final String TYPE = "D.type";
    public static final String SERIAL_NUMBER = "D.serial_no";
    public static final String MANUFACTURER = "D.manufacturer";
    public static final String MODEL = "D.model";
    public static final String IMAGE_PATH = "D.image_path";

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

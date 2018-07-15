package ngo.teog.swift.helpers.filters;

/**
 * Filterklasse für die Auswahl von Geräten.
 * Definiert alle verfügbaren Filter (die Konstanten ganz oben) und
 * verknüpft die Filter mit den zugehörigen Werten.
 * Created by Julian on 18.11.2017.
 */

public class DeviceFilter {
    public static final String ID = "d_ID";
    public static final String ASSET_NUMBER = "d_asset_no";
    public static final String TYPE = "d_type";
    public static final String SERIAL_NUMBER = "d_serial_no";
    public static final String MANUFACTURER = "d_manufacturer";
    public static final String MODEL = "d_model";
    public static final String IMAGE_PATH = "d_image_path";
    public static final String WORKING = "d_working";
    public static final String NEXT_MAINTENANCE = "d_next_maintenance";

    public static final String ACTION_FETCH_DEVICE = "fetch_device";
    public static final String ACTION_FETCH_TODO_LIST = "fetch_todo_list";
    public static final String ACTION_SEARCH_DEVICE = "search_device";
    public static final String ACTION_CREATE_DEVICE = "create_device";
    public static final String ACTION_FETCH_DEVICE_IMAGE = "fetch_device_image";

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

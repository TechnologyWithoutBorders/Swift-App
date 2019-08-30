package ngo.teog.swift.helpers.filters;

/**
 * Filterklasse für die Auswahl von Geräten.
 * Definiert alle verfügbaren Filter (die Konstanten ganz oben) und
 * verknüpft die Filter mit den zugehörigen Werten.
 * Created by Julian on 18.11.2017.
 */

public final class DeviceFilter {
    public static final String ID = "id";
    public static final String ASSET_NUMBER = "asset_no";
    public static final String TYPE = "type";
    public static final String SERIAL_NUMBER = "serial_no";
    public static final String MANUFACTURER = "manufacturer";
    public static final String MODEL = "model";
    public static final String WARD = "ward";
    public static final String HOSPITAL = "hospital";
    public static final String MAINTENANCE_INTERVAL = "maintenance_interval";
    public static final String LAST_UPDATE = "last_update";

    public static final String ACTION_FETCH_DEVICE = "fetch_device";
    public static final String ACTION_SEARCH_DEVICE = "search_device";
    public static final String ACTION_FETCH_DEVICE_IMAGE = "fetch_device_image";
}

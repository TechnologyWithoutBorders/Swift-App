package ngo.teog.hstest.helpers;

import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Hilfsklasse, die JSON-Devicelisten aus der HTPPS-Schnittstelle
 * parsen kann. Die Struktur wird sich noch deutlich ändern. Einige Methoden sind veraltet.
 * Created by Julian on 18.11.2017.
 */

public class DeviceParser {

    private JsonReader reader;

    public DeviceParser(InputStream in) {
        if(in != null) {
            reader = new JsonReader(new InputStreamReader(in));
        }
    }

    public ArrayList<HospitalDevice> parseDeviceList(JSONArray raw) throws JSONException {
        ArrayList<HospitalDevice> result = new ArrayList<>();

        for(int i = 0; i < raw.length(); i++) {
            JSONObject deviceObject = raw.getJSONObject(i);

            int id = deviceObject.getInt(DeviceFilter.ID);
            String assetNumber = deviceObject.getString(DeviceFilter.ASSET_NUMBER);
            String type = deviceObject.getString(DeviceFilter.TYPE);
            String serviceNumber = deviceObject.getString(DeviceFilter.SERVICE_NUMBER);
            String manufacturer = deviceObject.getString(DeviceFilter.MANUFACTURER);
            String imagePath = deviceObject.getString(DeviceFilter.IMAGE_PATH);

            HospitalDevice device = new HospitalDevice(id, assetNumber, type, serviceNumber, manufacturer, imagePath);
            result.add(device);
        }

        return result;
    }
}

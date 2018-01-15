package ngo.teog.hstest.helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Hilfsklasse, die JSON-Responses aus der HTPPS-Schnittstelle
 * parsen kann. Könnte man zum Singleton ausbauen.
 * Created by Julian on 18.11.2017.
 */

public class ResponseParser {

    public void parseDefaultResponse(JSONObject raw) throws Exception {
        int responseCode = raw.getInt("response_code");
        switch(responseCode) {
            case ResponseCode.OK:
                break;
            case ResponseCode.FAILED_VISIBLE:
                throw new ResponseException(raw.getString("data"));
            case ResponseCode.FAILED_HIDDEN:
            default:
                throw new Exception(raw.getString("data"));
        }
    }

    public ArrayList<HospitalDevice> parseDeviceList(JSONObject raw) throws Exception {
        int responseCode = raw.getInt("response_code");
        switch(responseCode) {
            case ResponseCode.OK:
                JSONArray deviceList = raw.getJSONArray("data");

                ArrayList<HospitalDevice> result = new ArrayList<>();

                for(int i = 0; i < deviceList.length(); i++) {
                    JSONObject deviceObject = deviceList.getJSONObject(i);

                    int id = deviceObject.getInt(DeviceFilter.ID);
                    String assetNumber = deviceObject.getString(DeviceFilter.ASSET_NUMBER);
                    String type = deviceObject.getString(DeviceFilter.TYPE);
                    String serviceNumber = deviceObject.getString(DeviceFilter.SERVICE_NUMBER);
                    String manufacturer = deviceObject.getString(DeviceFilter.MANUFACTURER);
                    String model = deviceObject.getString(DeviceFilter.MODEL);
                    String imagePath = deviceObject.getString(DeviceFilter.IMAGE_PATH);

                    HospitalDevice device = new HospitalDevice(id, assetNumber, type, serviceNumber, manufacturer, model, imagePath);
                    result.add(device);
                }

                return result;
            case ResponseCode.FAILED_VISIBLE:
                throw new ResponseException(raw.getString("data"));
            case ResponseCode.FAILED_HIDDEN:
            default:
                throw new Exception(raw.getString("data"));
        }
    }
}

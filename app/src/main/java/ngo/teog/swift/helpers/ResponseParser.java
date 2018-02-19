package ngo.teog.swift.helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import ngo.teog.swift.TodoFragment;

/**
 * Hilfsklasse, die JSON-Responses aus der HTPPS-Schnittstelle
 * parsen kann. Könnte man zum Singleton ausbauen.
 * Created by Julian on 18.11.2017.
 */

public class ResponseParser {

    public int parseLoginResponse(JSONObject raw) throws Exception {
        int responseCode = raw.getInt("response_code");
        switch(responseCode) {
            case ResponseCode.OK:
                return raw.getInt("data");
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
                    String serialNumber = deviceObject.getString(DeviceFilter.SERIAL_NUMBER);
                    String manufacturer = deviceObject.getString(DeviceFilter.MANUFACTURER);
                    String model = deviceObject.getString(DeviceFilter.MODEL);
                    String imagePath = deviceObject.getString(DeviceFilter.IMAGE_PATH);
                    String workingString = deviceObject.getString(DeviceFilter.WORKING);
                    boolean isWorking = false;
                    if(workingString.equals("1")) {
                        isWorking = true;
                    }
                    String dateString = deviceObject.getString(DeviceFilter.NEXT_MAINTENANCE);

                    Date nextMaintenance = TodoFragment.DATE_FORMAT.parse(dateString);

                    HospitalDevice device = new HospitalDevice(id, assetNumber, type, serialNumber, manufacturer, model, imagePath, isWorking, nextMaintenance);
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

    public User parseProfile(JSONObject raw) throws Exception {
        int responseCode = raw.getInt("response_code");
        switch(responseCode) {
            case ResponseCode.OK:
                JSONObject userObject = raw.getJSONObject("data");

                int id = userObject.getInt(UserFilter.ID);
                String phone = userObject.getString(UserFilter.PHONE);
                String mail = userObject.getString(UserFilter.MAIL);
                String fullName = userObject.getString(UserFilter.FULL_NAME);
                String qualifications = userObject.getString(UserFilter.QUALIFICATIONS);

                User user = new User(id, phone, mail, fullName, qualifications);

                return user;
            case ResponseCode.FAILED_VISIBLE:
                throw new ResponseException(raw.getString("data"));
            case ResponseCode.FAILED_HIDDEN:
            default:
                throw new Exception(raw.getString("data"));
        }
    }
}

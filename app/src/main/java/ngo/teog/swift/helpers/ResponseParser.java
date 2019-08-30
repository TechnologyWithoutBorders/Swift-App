package ngo.teog.swift.helpers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.Hospital;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.filters.DeviceFilter;
import ngo.teog.swift.helpers.filters.HospitalFilter;
import ngo.teog.swift.helpers.filters.ReportFilter;
import ngo.teog.swift.helpers.filters.UserFilter;

/**
 * Hilfsklasse, die JSON-Responses aus der HTPPS-Schnittstelle
 * parsen kann. Könnte man zum Singleton ausbauen.
 * @author Julian Deyerler, Technology without Borders
 */

public class ResponseParser {

    public int parseLoginResponse(JSONObject raw) throws Exception {
        int responseCode = raw.getInt(SwiftResponse.CODE_FIELD);
        switch(responseCode) {
            case SwiftResponse.CODE_OK:
                return raw.getInt(SwiftResponse.DATA_FIELD);
            case SwiftResponse.CODE_FAILED_VISIBLE:
                throw new ResponseException(raw.getString(SwiftResponse.DATA_FIELD));
            case SwiftResponse.CODE_FAILED_HIDDEN:
            default:
                throw new Exception(raw.getString(SwiftResponse.DATA_FIELD));
        }
    }

    public ArrayList<HospitalDevice> parseDeviceList(JSONObject raw) throws Exception {
        int responseCode = raw.getInt(SwiftResponse.CODE_FIELD);
        switch(responseCode) {
            case SwiftResponse.CODE_OK:
                JSONArray deviceList = raw.getJSONArray(SwiftResponse.DATA_FIELD);

                ArrayList<HospitalDevice> result = new ArrayList<>();

                DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN);

                for(int i = 0; i < deviceList.length(); i++) {
                    JSONObject deviceObject = deviceList.getJSONObject(i);

                    int id = deviceObject.getInt(DeviceFilter.ID);
                    String assetNumber = deviceObject.getString(DeviceFilter.ASSET_NUMBER);
                    String type = deviceObject.getString(DeviceFilter.TYPE);
                    String serialNumber = deviceObject.getString(DeviceFilter.SERIAL_NUMBER);
                    String manufacturer = deviceObject.getString(DeviceFilter.MANUFACTURER);
                    String model = deviceObject.getString(DeviceFilter.MODEL);
                    String ward = deviceObject.getString(DeviceFilter.WARD);

                    int hospital = deviceObject.getInt(DeviceFilter.HOSPITAL);
                    int maintenanceInterval = deviceObject.getInt(DeviceFilter.MAINTENANCE_INTERVAL);
                    Date lastUpdate = dateFormat.parse(deviceObject.getString(DeviceFilter.LAST_UPDATE));

                    HospitalDevice device = new HospitalDevice(id, assetNumber, type, serialNumber, manufacturer, model, ward, hospital, maintenanceInterval, lastUpdate);
                    result.add(device);
                }

                return result;
            case SwiftResponse.CODE_FAILED_VISIBLE:
                throw new ResponseException(raw.getString(SwiftResponse.DATA_FIELD));
            case SwiftResponse.CODE_FAILED_HIDDEN:
            default:
                throw new Exception(raw.getString(SwiftResponse.DATA_FIELD));
        }
    }

    public ArrayList<HospitalDevice> parseDeviceList(JSONArray deviceList) throws Exception {
        ArrayList<HospitalDevice> result = new ArrayList<>();

        DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN);

        for(int i = 0; i < deviceList.length(); i++) {
            JSONObject deviceObject = deviceList.getJSONObject(i);

            int id = deviceObject.getInt(DeviceFilter.ID);
            String assetNumber = deviceObject.getString(DeviceFilter.ASSET_NUMBER);
            String type = deviceObject.getString(DeviceFilter.TYPE);
            String serialNumber = deviceObject.getString(DeviceFilter.SERIAL_NUMBER);
            String manufacturer = deviceObject.getString(DeviceFilter.MANUFACTURER);
            String model = deviceObject.getString(DeviceFilter.MODEL);
            String ward = deviceObject.getString(DeviceFilter.WARD);

            int hospital = deviceObject.getInt(DeviceFilter.HOSPITAL);
            int maintenanceInterval = deviceObject.getInt(DeviceFilter.MAINTENANCE_INTERVAL);
            Date lastUpdate = dateFormat.parse(deviceObject.getString(DeviceFilter.LAST_UPDATE));

            HospitalDevice device = new HospitalDevice(id, assetNumber, type, serialNumber, manufacturer, model, ward, hospital, maintenanceInterval, lastUpdate);
            result.add(device);
        }

        return result;
    }

    public HospitalInfo parseHospital(JSONObject raw) throws Exception {
        int responseCode = raw.getInt(SwiftResponse.CODE_FIELD);
        switch(responseCode) {
            case SwiftResponse.CODE_OK:
                JSONObject hospitalObject = raw.getJSONObject(SwiftResponse.DATA_FIELD);

                DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN);

                int hospitalId = hospitalObject.getInt(HospitalFilter.ID);
                String name = hospitalObject.getString(HospitalFilter.NAME);
                String location = hospitalObject.getString(HospitalFilter.LOCATION);
                float longitude = Float.parseFloat(hospitalObject.getString(HospitalFilter.LONGITUDE));
                float latitude = Float.parseFloat(hospitalObject.getString(HospitalFilter.LATITUDE));
                Date hospitalLastUpdate = dateFormat.parse(hospitalObject.getString(HospitalFilter.LAST_UPDATE));
                JSONArray users = hospitalObject.getJSONArray("users");
                JSONArray devices = hospitalObject.getJSONArray("devices");

                List<User> userList = new ArrayList<>(users.length());

                for(int i = 0; i < users.length(); i++) {
                    JSONObject userObject = users.getJSONObject(i);

                    int id = userObject.getInt(UserFilter.ID);
                    String phone = userObject.getString(UserFilter.PHONE);
                    String mail = userObject.getString(UserFilter.MAIL);
                    String fullName = userObject.getString(UserFilter.FULL_NAME);
                    int hospital = userObject.getInt(UserFilter.HOSPITAL);
                    String position = userObject.getString(UserFilter.POSITION);
                    Date lastUpdate = dateFormat.parse(userObject.getString(UserFilter.LAST_UPDATE));

                    User user = new User(id, phone, mail, fullName, hospital, position, lastUpdate);
                    userList.add(user);
                }

                List<DeviceInfo> deviceList = new ArrayList<>(devices.length());

                for(int i = 0; i < devices.length(); i++) {
                    JSONObject deviceObject = devices.getJSONObject(i);

                    int id = deviceObject.getInt(DeviceFilter.ID);
                    String assetNumber = deviceObject.getString(DeviceFilter.ASSET_NUMBER);
                    String type = deviceObject.getString(DeviceFilter.TYPE);
                    String serialNumber = deviceObject.getString(DeviceFilter.SERIAL_NUMBER);
                    String manufacturer = deviceObject.getString(DeviceFilter.MANUFACTURER);
                    String model = deviceObject.getString(DeviceFilter.MODEL);
                    String ward = deviceObject.getString(DeviceFilter.WARD);
                    int hospital = deviceObject.getInt(DeviceFilter.HOSPITAL);
                    int maintenanceInterval = deviceObject.getInt(DeviceFilter.MAINTENANCE_INTERVAL);
                    Date lastUpdate = dateFormat.parse(deviceObject.getString(DeviceFilter.LAST_UPDATE));

                    JSONArray reports = deviceObject.getJSONArray("reports");
                    List<ReportInfo> reportList = new ArrayList<>();

                    for(int j = 0; j < reports.length(); j++) {
                        JSONObject reportObject = reports.getJSONObject(j);

                        int reportId = reportObject.getInt(ReportFilter.ID);
                        int author = reportObject.getInt(ReportFilter.AUTHOR);
                        int affectedDevice = reportObject.getInt(ReportFilter.DEVICE);
                        int previousState = reportObject.getInt(ReportFilter.PREVIOUS_STATE);
                        int currentState = reportObject.getInt(ReportFilter.CURRENT_STATE);
                        String description = reportObject.getString(ReportFilter.DESCRIPTION);
                        Date datetime = dateFormat.parse(reportObject.getString(ReportFilter.DATETIME));

                        Report report = new Report(reportId, author, affectedDevice, previousState, currentState, description, datetime);

                        ReportInfo reportInfo = new ReportInfo(report);

                        reportList.add(reportInfo);
                    }

                    HospitalDevice device = new HospitalDevice(id, assetNumber, type, serialNumber, manufacturer, model, ward, hospital, maintenanceInterval, lastUpdate);

                    DeviceInfo deviceInfo = new DeviceInfo(device);
                    deviceInfo.setReports(reportList);

                    deviceList.add(deviceInfo);
                }

                return new HospitalInfo(hospitalId, name, location, longitude, latitude, hospitalLastUpdate, userList, deviceList);
            case SwiftResponse.CODE_FAILED_VISIBLE:
                throw new ResponseException(raw.getString(SwiftResponse.DATA_FIELD));
            case SwiftResponse.CODE_FAILED_HIDDEN:
            default:
                throw new Exception(raw.getString(SwiftResponse.DATA_FIELD));
        }
    }

    public ArrayList<User> parseUserList(JSONObject raw) throws Exception {
        int responseCode = raw.getInt(SwiftResponse.CODE_FIELD);
        switch(responseCode) {
            case SwiftResponse.CODE_OK:
                JSONArray userList = raw.getJSONArray(SwiftResponse.DATA_FIELD);

                DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN);

                ArrayList<User> result = new ArrayList<>();

                for(int i = 0; i < userList.length(); i++) {
                    JSONObject userObject = userList.getJSONObject(i);

                    int id = userObject.getInt(UserFilter.ID);
                    String phone = userObject.getString(UserFilter.PHONE);
                    String mail = userObject.getString(UserFilter.MAIL);
                    String fullName = userObject.getString(UserFilter.FULL_NAME);
                    int hospitalId = userObject.getInt(UserFilter.HOSPITAL);
                    String position = userObject.getString(UserFilter.POSITION);
                    Date lastUpdate = dateFormat.parse(userObject.getString(UserFilter.LAST_UPDATE));

                    User user = new User(id, phone, mail, fullName, hospitalId, position, lastUpdate);
                    result.add(user);
                }

                return result;
            case SwiftResponse.CODE_FAILED_VISIBLE:
                throw new ResponseException(raw.getString(SwiftResponse.DATA_FIELD));
            case SwiftResponse.CODE_FAILED_HIDDEN:
            default:
                throw new Exception(raw.getString(SwiftResponse.DATA_FIELD));
        }
    }

    public ArrayList<User> parseUserList(JSONArray userList) throws Exception {
        ArrayList<User> result = new ArrayList<>();

        DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN);

        for(int i = 0; i < userList.length(); i++) {
            JSONObject userObject = userList.getJSONObject(i);

            int id = userObject.getInt(UserFilter.ID);
            String phone = userObject.getString(UserFilter.PHONE);
            String mail = userObject.getString(UserFilter.MAIL);
            String fullName = userObject.getString(UserFilter.FULL_NAME);
            int hospitalId = userObject.getInt(HospitalFilter.ID);
            String position = userObject.getString(UserFilter.POSITION);
            Date lastUpdate = dateFormat.parse(userObject.getString(UserFilter.LAST_UPDATE));

            User user = new User(id, phone, mail, fullName, hospitalId, position, lastUpdate);
            result.add(user);
        }

        return result;
    }

    public ArrayList<Report> parseReportList(JSONObject raw) throws Exception {
        int responseCode = raw.getInt(SwiftResponse.CODE_FIELD);
        switch(responseCode) {
            case SwiftResponse.CODE_OK:
                JSONArray reportList = raw.getJSONArray(SwiftResponse.DATA_FIELD);

                DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN);

                ArrayList<Report> result = new ArrayList<>();

                for(int i = 0; i < reportList.length(); i++) {
                    JSONObject reportObject = reportList.getJSONObject(i);

                    int id = reportObject.getInt(ReportFilter.ID);
                    int author = reportObject.getInt(ReportFilter.AUTHOR);
                    int device = reportObject.getInt(ReportFilter.DEVICE);
                    int previousState = reportObject.getInt(ReportFilter.PREVIOUS_STATE);
                    int currentState = reportObject.getInt(ReportFilter.CURRENT_STATE);
                    String description = reportObject.getString(ReportFilter.DESCRIPTION);
                    Date created = dateFormat.parse(reportObject.getString(ReportFilter.DATETIME));
                    String authorName = reportObject.getString(UserFilter.FULL_NAME);

                    Report report = new Report(id, author, device, previousState, currentState, description , created);
                    result.add(report);
                }

                return result;
            case SwiftResponse.CODE_FAILED_VISIBLE:
                throw new ResponseException(raw.getString(SwiftResponse.DATA_FIELD));
            case SwiftResponse.CODE_FAILED_HIDDEN:
            default:
                throw new Exception(raw.getString(SwiftResponse.DATA_FIELD));
        }
    }
}

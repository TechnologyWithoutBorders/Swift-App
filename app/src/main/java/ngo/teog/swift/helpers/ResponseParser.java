package ngo.teog.swift.helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ngo.teog.swift.helpers.filters.DeviceFilter;
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

                for(int i = 0; i < deviceList.length(); i++) {
                    JSONObject deviceObject = deviceList.getJSONObject(i);

                    int id = deviceObject.getInt(DeviceFilter.ID);
                    String assetNumber = deviceObject.getString(DeviceFilter.ASSET_NUMBER);
                    String type = deviceObject.getString(DeviceFilter.TYPE);
                    String serialNumber = deviceObject.getString(DeviceFilter.SERIAL_NUMBER);
                    String manufacturer = deviceObject.getString(DeviceFilter.MANUFACTURER);
                    String model = deviceObject.getString(DeviceFilter.MODEL);
                    String ward = deviceObject.getString("d_ward");
                    int currentState = deviceObject.getInt(ReportFilter.CURRENT_STATE);

                    final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                    String hospital = deviceObject.getString("h_name");
                    int maintenanceInterval = deviceObject.getInt("d_maintenance_interval");
                    String reportDateString = deviceObject.getString("r_datetime");

                    Date lastReportDate = DATE_FORMAT.parse(reportDateString);

                    HospitalDevice device = new HospitalDevice(id, assetNumber, type, serialNumber, manufacturer, model, ward, currentState, hospital, maintenanceInterval, lastReportDate);
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

        for(int i = 0; i < deviceList.length(); i++) {
            JSONObject deviceObject = deviceList.getJSONObject(i);

            int id = deviceObject.getInt(DeviceFilter.ID);
            String assetNumber = deviceObject.getString(DeviceFilter.ASSET_NUMBER);
            String type = deviceObject.getString(DeviceFilter.TYPE);
            String serialNumber = deviceObject.getString(DeviceFilter.SERIAL_NUMBER);
            String manufacturer = deviceObject.getString(DeviceFilter.MANUFACTURER);
            String model = deviceObject.getString(DeviceFilter.MODEL);
            String ward = deviceObject.getString("d_ward");
            int currentState = deviceObject.getInt(ReportFilter.CURRENT_STATE);

            final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            String hospital = deviceObject.getString("h_name");
            int maintenanceInterval = deviceObject.getInt("d_maintenance_interval");
            String reportDateString = deviceObject.getString("r_datetime");

            Date lastReportDate = DATE_FORMAT.parse(reportDateString);

            HospitalDevice device = new HospitalDevice(id, assetNumber, type, serialNumber, manufacturer, model, ward, currentState, hospital, maintenanceInterval, lastReportDate);
            result.add(device);
        }

        return result;
    }

    public ArrayList<User> parseUserList(JSONObject raw) throws Exception {
        int responseCode = raw.getInt(SwiftResponse.CODE_FIELD);
        switch(responseCode) {
            case SwiftResponse.CODE_OK:
                JSONArray userList = raw.getJSONArray(SwiftResponse.DATA_FIELD);

                ArrayList<User> result = new ArrayList<>();

                for(int i = 0; i < userList.length(); i++) {
                    JSONObject userObject = userList.getJSONObject(i);

                    int id = userObject.getInt(UserFilter.ID);
                    String phone = userObject.getString(UserFilter.PHONE);
                    String mail = userObject.getString(UserFilter.MAIL);
                    String fullName = userObject.getString(UserFilter.FULL_NAME);

                    int hospitalId = userObject.getInt("h_ID");
                    String hospitalName = userObject.getString("h_name");
                    String position = userObject.getString("u_position");

                    Hospital hospital = new Hospital(hospitalId, hospitalName);

                    User user = new User(id, phone, mail, fullName, hospital, position);
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

        for(int i = 0; i < userList.length(); i++) {
            JSONObject userObject = userList.getJSONObject(i);

            int id = userObject.getInt(UserFilter.ID);
            String phone = userObject.getString(UserFilter.PHONE);
            String mail = userObject.getString(UserFilter.MAIL);
            String fullName = userObject.getString(UserFilter.FULL_NAME);

            int hospitalId = userObject.getInt("h_ID");
            String hospitalName = userObject.getString("h_name");
            String position = userObject.getString("u_position");

            Hospital hospital = new Hospital(hospitalId, hospitalName);

            User user = new User(id, phone, mail, fullName, hospital, position);
            result.add(user);
        }

        return result;
    }

    public ArrayList<Report> parseReportList(JSONObject raw) throws Exception {
        int responseCode = raw.getInt(SwiftResponse.CODE_FIELD);
        switch(responseCode) {
            case SwiftResponse.CODE_OK:
                JSONArray reportList = raw.getJSONArray(SwiftResponse.DATA_FIELD);

                ArrayList<Report> result = new ArrayList<>();

                for(int i = 0; i < reportList.length(); i++) {
                    JSONObject reportObject = reportList.getJSONObject(i);

                    int id = reportObject.getInt(ReportFilter.ID);
                    int author = reportObject.getInt(ReportFilter.AUTHOR);
                    int device = reportObject.getInt(ReportFilter.DEVICE);
                    int previousState = reportObject.getInt(ReportFilter.PREVIOUS_STATE);
                    int currentState = reportObject.getInt(ReportFilter.CURRENT_STATE);
                    String description = reportObject.getString(ReportFilter.DESCRIPTION);
                    String dateString = reportObject.getString(ReportFilter.DATETIME);
                    String authorName = reportObject.getString(UserFilter.FULL_NAME);

                    Date date = Report.reportFormat.parse(dateString);

                    Report report = new Report(id, author, authorName, device, previousState, currentState, description , date);
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

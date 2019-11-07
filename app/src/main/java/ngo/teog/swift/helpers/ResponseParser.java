package ngo.teog.swift.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.filters.DeviceFilter;
import ngo.teog.swift.helpers.filters.HospitalFilter;
import ngo.teog.swift.helpers.filters.ReportFilter;
import ngo.teog.swift.helpers.filters.UserFilter;

/**
 * Parses JSON-formatted responses from the server.
 * @author nitelow
 */
public class ResponseParser {

    /**
     * Throws an appropriate Exception if the server response contains an error code.
     * @param raw JSON-formatted server response
     * @throws ServerException if server indicates an error that should <b>not</b> be visible to the user
     * @throws TransparentServerException if server indicates an error that should be visible to the user
     */
    private void probeResponseCode(JSONObject raw) throws ServerException, TransparentServerException {
        try {
            int responseCode = raw.getInt(SwiftResponse.CODE_FIELD);

            switch (responseCode) {
                case SwiftResponse.CODE_OK:
                    return;
                case SwiftResponse.CODE_FAILED_VISIBLE:
                    throw new TransparentServerException(raw.getString(SwiftResponse.DATA_FIELD));
                case SwiftResponse.CODE_FAILED_HIDDEN:
                default:
                    throw new ServerException(raw.getString(SwiftResponse.DATA_FIELD));
            }
        } catch(JSONException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Parses a response corresponding to a login request.<br>
     * Returns the user's ID if authentication was successful, otherwise throws exception.
     * @param raw JSON-formatted server response
     * @return ID of user
     * @throws ServerException if some internal server error has occurred
     * @throws TransparentServerException if user authentication has failed
     */
    public int parseLoginResponse(JSONObject raw) throws ServerException, TransparentServerException {
        probeResponseCode(raw);

        try {
            return raw.getInt(SwiftResponse.DATA_FIELD);
        } catch(JSONException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Extracts a device list from a suiting server response.
     * @param raw JSON-formatted server response
     * @return Device list
     * @throws ServerException if some internal server error has occurred
     * @throws TransparentServerException if some transparent error has happened
     */
    public ArrayList<HospitalDevice> parseDeviceList(JSONObject raw) throws ServerException, TransparentServerException {
        probeResponseCode(raw);

        try {
            JSONArray deviceList = raw.getJSONArray(SwiftResponse.DATA_FIELD);

            ArrayList<HospitalDevice> result = new ArrayList<>();

            DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            for (int i = 0; i < deviceList.length(); i++) {
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
        } catch(JSONException | ParseException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Extracts information about a hospital from a suiting server response.<br>
     * The result includes the users, devices and reports assigned to the hospital.
     * @param raw JSON-formatted server response
     * @return Hospital information
     * @throws ServerException if some internal server error has occurred
     * @throws TransparentServerException if some transparent error has happened
     */
    public HospitalInfo parseHospital(JSONObject raw) throws ServerException, TransparentServerException {
        probeResponseCode(raw);

        try {
            JSONObject hospitalObject = raw.getJSONObject(SwiftResponse.DATA_FIELD);

            DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            int hospitalId = hospitalObject.getInt(HospitalFilter.ID);
            String name = hospitalObject.getString(HospitalFilter.NAME);
            String location = hospitalObject.getString(HospitalFilter.LOCATION);
            float longitude = Float.parseFloat(hospitalObject.getString(HospitalFilter.LONGITUDE));
            float latitude = Float.parseFloat(hospitalObject.getString(HospitalFilter.LATITUDE));
            Date hospitalLastUpdate = dateFormat.parse(hospitalObject.getString(HospitalFilter.LAST_UPDATE));
            JSONArray users = hospitalObject.getJSONArray(ResourceKeys.USERS);
            JSONArray devices = hospitalObject.getJSONArray(ResourceKeys.DEVICES);

            List<User> userList = new ArrayList<>(users.length());

            for (int i = 0; i < users.length(); i++) {
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

            for (int i = 0; i < devices.length(); i++) {
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

                JSONArray reports = deviceObject.getJSONArray(ResourceKeys.REPORTS);
                List<ReportInfo> reportList = new ArrayList<>();

                for (int j = 0; j < reports.length(); j++) {
                    JSONObject reportObject = reports.getJSONObject(j);

                    int reportId = reportObject.getInt(ReportFilter.ID);
                    int author = reportObject.getInt(ReportFilter.AUTHOR);
                    int affectedDevice = reportObject.getInt(ReportFilter.DEVICE);
                    int previousState = reportObject.getInt(ReportFilter.PREVIOUS_STATE);
                    int currentState = reportObject.getInt(ReportFilter.CURRENT_STATE);
                    String description = reportObject.getString(ReportFilter.DESCRIPTION);
                    Date datetime = dateFormat.parse(reportObject.getString(ReportFilter.CREATED));

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
        } catch(JSONException | ParseException e) {
            throw new ServerException(e);
        }
    }

    /**
     * Extracts a user list from a suiting server response.
     * @param raw JSON-formatted server response
     * @return User list
     * @throws ServerException if some internal server error has occurred
     * @throws TransparentServerException if some transparent error has happened
     */
    public ArrayList<User> parseUserList(JSONObject raw) throws ServerException, TransparentServerException {
        probeResponseCode(raw);

        try {
            JSONArray userList = raw.getJSONArray(SwiftResponse.DATA_FIELD);

            DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            ArrayList<User> result = new ArrayList<>();

            for (int i = 0; i < userList.length(); i++) {
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
        } catch(JSONException | ParseException e) {
            throw new ServerException(e);
        }
    }
}

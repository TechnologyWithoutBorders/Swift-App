package ngo.teog.swift.communication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import ngo.teog.swift.helpers.Defaults;
import ngo.teog.swift.helpers.HospitalInfo;
import ngo.teog.swift.helpers.ResourceKeys;
import ngo.teog.swift.helpers.data.DeviceInfo;
import ngo.teog.swift.helpers.data.HospitalDevice;
import ngo.teog.swift.helpers.data.Report;
import ngo.teog.swift.helpers.data.ReportInfo;
import ngo.teog.swift.helpers.data.User;
import ngo.teog.swift.helpers.filters.DeviceAttribute;
import ngo.teog.swift.helpers.filters.HospitalAttribute;
import ngo.teog.swift.helpers.filters.ReportAttribute;
import ngo.teog.swift.helpers.filters.UserAttribute;

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
    public static void probeResponseCode(JSONObject raw) throws ServerException, TransparentServerException {
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
    public static int parseLoginResponse(JSONObject raw) throws ServerException, TransparentServerException {
        probeResponseCode(raw);

        try {
            return raw.getInt(SwiftResponse.DATA_FIELD);
        } catch(JSONException e) {
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
    public static HospitalInfo parseHospital(JSONObject raw) throws ServerException, TransparentServerException {
        probeResponseCode(raw);

        try {
            JSONObject hospitalObject = raw.getJSONObject(SwiftResponse.DATA_FIELD);

            DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN, Locale.getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone(Defaults.TIMEZONE_UTC));

            int hospitalId = hospitalObject.getInt(HospitalAttribute.ID);
            String name = hospitalObject.getString(HospitalAttribute.NAME);
            String hospitalLocation = hospitalObject.getString(HospitalAttribute.LOCATION);
            float longitude = Float.parseFloat(hospitalObject.getString(HospitalAttribute.LONGITUDE));
            float latitude = Float.parseFloat(hospitalObject.getString(HospitalAttribute.LATITUDE));
            Date hospitalLastUpdate = dateFormat.parse(hospitalObject.getString(HospitalAttribute.LAST_UPDATE));
            JSONArray users = hospitalObject.getJSONArray(ResourceKeys.USERS);
            JSONArray devices = hospitalObject.getJSONArray(ResourceKeys.DEVICES);

            List<User> userList = new ArrayList<>(users.length());

            for (int i = 0; i < users.length(); i++) {
                JSONObject userObject = users.getJSONObject(i);

                int id = userObject.getInt(UserAttribute.ID);
                String phone = userObject.getString(UserAttribute.PHONE);
                String mail = userObject.getString(UserAttribute.MAIL);
                String fullName = userObject.getString(UserAttribute.FULL_NAME);
                int hospital = userObject.getInt(UserAttribute.HOSPITAL);
                String position = userObject.getString(UserAttribute.POSITION);
                Date lastUpdate = dateFormat.parse(userObject.getString(UserAttribute.LAST_UPDATE));

                User user = new User(id, phone, mail, fullName, hospital, position, lastUpdate);
                userList.add(user);
            }

            List<DeviceInfo> deviceList = new ArrayList<>(devices.length());

            for (int i = 0; i < devices.length(); i++) {
                JSONObject deviceObject = devices.getJSONObject(i);

                int id = deviceObject.getInt(DeviceAttribute.ID);
                String assetNumber = deviceObject.getString(DeviceAttribute.ASSET_NUMBER);
                String type = deviceObject.getString(DeviceAttribute.TYPE);
                String serialNumber = deviceObject.getString(DeviceAttribute.SERIAL_NUMBER);
                String manufacturer = deviceObject.getString(DeviceAttribute.MANUFACTURER);
                String model = deviceObject.getString(DeviceAttribute.MODEL);
                String location = deviceObject.getString(DeviceAttribute.LOCATION);
                int hospital = deviceObject.getInt(DeviceAttribute.HOSPITAL);
                int maintenanceInterval = deviceObject.getInt(DeviceAttribute.MAINTENANCE_INTERVAL);
                Date lastUpdate = dateFormat.parse(deviceObject.getString(DeviceAttribute.LAST_UPDATE));

                JSONArray reports = deviceObject.getJSONArray(ResourceKeys.REPORTS);
                List<ReportInfo> reportList = new ArrayList<>();

                for (int j = 0; j < reports.length(); j++) {
                    JSONObject reportObject = reports.getJSONObject(j);

                    int reportId = reportObject.getInt(ReportAttribute.ID);
                    int author = reportObject.getInt(ReportAttribute.AUTHOR);
                    String title = reportObject.getString(ReportAttribute.TITLE);
                    int affectedDevice = reportObject.getInt(ReportAttribute.DEVICE);
                    int reportHospital = reportObject.getInt(ReportAttribute.HOSPITAL);
                    int currentState = reportObject.getInt(ReportAttribute.CURRENT_STATE);
                    String description = reportObject.getString(ReportAttribute.DESCRIPTION);
                    Date datetime = dateFormat.parse(reportObject.getString(ReportAttribute.CREATED));

                    Report report = new Report(reportId, author, title, affectedDevice, reportHospital, currentState, description, datetime);

                    ReportInfo reportInfo = new ReportInfo(report);

                    reportList.add(reportInfo);
                }

                HospitalDevice device = new HospitalDevice(id, assetNumber, type, serialNumber, manufacturer, model, location, hospital, maintenanceInterval, lastUpdate);

                DeviceInfo deviceInfo = new DeviceInfo(device);
                deviceInfo.setReports(reportList);

                deviceList.add(deviceInfo);
            }

            return new HospitalInfo(hospitalId, name, hospitalLocation, longitude, latitude, hospitalLastUpdate, userList, deviceList);
        } catch(JSONException | ParseException e) {
            throw new ServerException(e);
        }
    }
}

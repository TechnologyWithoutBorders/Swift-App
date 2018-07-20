package ngo.teog.swift.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import ngo.teog.swift.helpers.filters.DeviceFilter;
import ngo.teog.swift.helpers.filters.NewsFilter;
import ngo.teog.swift.helpers.filters.ReportFilter;
import ngo.teog.swift.gui.main.TodoFragment;
import ngo.teog.swift.helpers.filters.UserFilter;

/**
 * Hilfsklasse, die JSON-Responses aus der HTPPS-Schnittstelle
 * parsen kann. Könnte man zum Singleton ausbauen.
 * @author Julian Deyerler, Technology without Borders
 */

public class ResponseParser {

    public int parseLoginResponse(JSONObject raw) throws Exception {
        int responseCode = raw.getInt(Response.CODE_FIELD);
        switch(responseCode) {
            case Response.CODE_OK:
                return raw.getInt(Response.DATA_FIELD);
            case Response.CODE_FAILED_VISIBLE:
                throw new ResponseException(raw.getString(Response.DATA_FIELD));
            case Response.CODE_FAILED_HIDDEN:
            default:
                throw new Exception(raw.getString(Response.DATA_FIELD));
        }
    }

    public ArrayList<HospitalDevice> parseDeviceList(JSONObject raw) throws Exception {
        int responseCode = raw.getInt(Response.CODE_FIELD);
        switch(responseCode) {
            case Response.CODE_OK:
                JSONArray deviceList = raw.getJSONArray(Response.DATA_FIELD);

                ArrayList<HospitalDevice> result = new ArrayList<>();

                for(int i = 0; i < deviceList.length(); i++) {
                    JSONObject deviceObject = deviceList.getJSONObject(i);

                    int id = deviceObject.getInt(DeviceFilter.ID);
                    String assetNumber = deviceObject.getString(DeviceFilter.ASSET_NUMBER);
                    String type = deviceObject.getString(DeviceFilter.TYPE);
                    String serialNumber = deviceObject.getString(DeviceFilter.SERIAL_NUMBER);
                    String manufacturer = deviceObject.getString(DeviceFilter.MANUFACTURER);
                    String model = deviceObject.getString(DeviceFilter.MODEL);
                    int currentState = deviceObject.getInt(ReportFilter.CURRENT_STATE);

                    Date nextMaintenance = new Date();
                    String hospital = deviceObject.getString("h_name");
                    String ward = deviceObject.getString("d_ward");

                    boolean unsubscribed = false;

                    if(!deviceObject.isNull("s_device")) {
                        unsubscribed = true;
                    }

                    HospitalDevice device = new HospitalDevice(id, assetNumber, type, serialNumber, manufacturer, model, currentState, nextMaintenance, hospital, ward, unsubscribed);
                    result.add(device);
                }

                return result;
            case Response.CODE_FAILED_VISIBLE:
                throw new ResponseException(raw.getString(Response.DATA_FIELD));
            case Response.CODE_FAILED_HIDDEN:
            default:
                throw new Exception(raw.getString(Response.DATA_FIELD));
        }
    }

    public ArrayList<User> parseUserList(JSONObject raw) throws Exception {
        int responseCode = raw.getInt(Response.CODE_FIELD);
        switch(responseCode) {
            case Response.CODE_OK:
                JSONArray userList = raw.getJSONArray(Response.DATA_FIELD);

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
            case Response.CODE_FAILED_VISIBLE:
                throw new ResponseException(raw.getString(Response.DATA_FIELD));
            case Response.CODE_FAILED_HIDDEN:
            default:
                throw new Exception(raw.getString(Response.DATA_FIELD));
        }
    }

    public ArrayList<Report> parseReportList(JSONObject raw) throws Exception {
        int responseCode = raw.getInt(Response.CODE_FIELD);
        switch(responseCode) {
            case Response.CODE_OK:
                JSONArray reportList = raw.getJSONArray(Response.DATA_FIELD);

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

                    Date date = Report.reportFormat.parse(dateString);

                    Report report = new Report(id, author, device, previousState, currentState, description , date);
                    result.add(report);
                }

                return result;
            case Response.CODE_FAILED_VISIBLE:
                throw new ResponseException(raw.getString(Response.DATA_FIELD));
            case Response.CODE_FAILED_HIDDEN:
            default:
                throw new Exception(raw.getString(Response.DATA_FIELD));
        }
    }

    public ArrayList<NewsItem> parseNewsList(JSONObject raw) throws Exception {
        int responseCode = raw.getInt(Response.CODE_FIELD);
        switch(responseCode) {
            case Response.CODE_OK:
                JSONArray newsList = raw.getJSONArray(Response.DATA_FIELD);

                ArrayList<NewsItem> result = new ArrayList<>();

                for(int i = 0; i < newsList.length(); i++) {
                    JSONObject newsObject = newsList.getJSONObject(i);

                    int id = newsObject.getInt(NewsFilter.ID);
                    Date date = Defaults.DATE_FORMAT.parse(newsObject.getString(NewsFilter.DATE));
                    String value = newsObject.getString(NewsFilter.VALUE);

                    result.add(new NewsItem(id, date, value));
                }

                return result;
            case Response.CODE_FAILED_VISIBLE:
                throw new ResponseException(raw.getString(Response.DATA_FIELD));
            case Response.CODE_FAILED_HIDDEN:
            default:
                throw new Exception(raw.getString(Response.DATA_FIELD));
        }
    }
}

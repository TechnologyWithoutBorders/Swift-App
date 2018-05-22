package ngo.teog.swift.helpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import ngo.teog.swift.helpers.filters.DeviceFilter;
import ngo.teog.swift.helpers.filters.NewsFilter;
import ngo.teog.swift.helpers.filters.ReportFilter;
import ngo.teog.swift.gui.main.TodoFragment;

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
                    String workingString = deviceObject.getString(DeviceFilter.WORKING);
                    boolean isWorking = false;
                    if(workingString.equals("1")) {
                        isWorking = true;
                    }
                    String dateString = deviceObject.getString(DeviceFilter.NEXT_MAINTENANCE);

                    Date nextMaintenance = TodoFragment.DATE_FORMAT.parse(dateString);

                    HospitalDevice device = new HospitalDevice(id, assetNumber, type, serialNumber, manufacturer, model, isWorking, nextMaintenance);
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
                    String title = reportObject.getString(ReportFilter.TITLE);

                    Report report = new Report(id, author, device, title);
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

package ngo.teog.hstest.helpers;

import android.util.JsonReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    @Deprecated
    public ArrayList<HospitalDevice> parse() throws IOException {
        ArrayList<HospitalDevice> result = new ArrayList<>();

        reader.beginArray();

        while(reader.hasNext()) {
            reader.beginObject();

            result.add(parseDevice());

            reader.endObject();
        }

        reader.endArray();

        reader.close();

        return result;
    }

    public ArrayList<HospitalDevice> parseDeviceList(JSONArray raw) throws JSONException {
        ArrayList<HospitalDevice> result = new ArrayList<>();

        for(int i = 0; i < raw.length(); i++) {
            JSONObject deviceObject = raw.getJSONObject(i);

            int id = deviceObject.getInt("id");
            String name = deviceObject.getString("name");
            String type = deviceObject.getString("type");
            String manufacturer = deviceObject.getString("manufacturer");
            String serialNumber = deviceObject.getString("serialNumber");
            String ward = deviceObject.getString("ward");
            String hospital = deviceObject.getString("hospital");
            boolean isWorking = deviceObject.getBoolean("isWorking");
            String dateString = deviceObject.getString("due");

            Date date = null;
            DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
            try {
                date = format.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            HospitalDevice device = new HospitalDevice(id, name, type, manufacturer, serialNumber, ward, hospital, isWorking, date);
            result.add(device);
        }

        return result;
    }

    @Deprecated
    private HospitalDevice parseDevice() throws IOException {
        int id = -1;
        String deviceName = null;
        String type = null;
        String manufacturer = null;
        String serialNumber = null;
        String ward = null;
        String hospital = null;
        boolean isWorking = false;
        Date date = null;

        while (reader.hasNext()) {
            String name = reader.nextName();

            if (name.equals("id")) {
                id = reader.nextInt();
            } else if (name.equals("name")) {
                deviceName = reader.nextString();
            } else if (name.equals("type")) {
                type = reader.nextString();
            } else if (name.equals("manufacturer")) {
                manufacturer = reader.nextString();
            } else if (name.equals("serialNumber")) {
                serialNumber = reader.nextString();
            } else if (name.equals("ward")) {
                ward = reader.nextString();
            } else if (name.equals("hospital")) {
                hospital = reader.nextString();
            } else if (name.equals("isWorking")) {
                isWorking = reader.nextBoolean();
            } else if (name.equals("due")) {
                String dateString = reader.nextString();

                DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
                try {
                    date = format.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                HospitalDevice device = new HospitalDevice(id, deviceName, type, manufacturer, serialNumber, ward, hospital, isWorking, date);
                return device;
            } else {
                reader.skipValue();
            }
        }

        return null;
    }
}

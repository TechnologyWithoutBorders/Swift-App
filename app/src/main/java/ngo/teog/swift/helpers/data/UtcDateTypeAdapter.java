package ngo.teog.swift.helpers.data;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import ngo.teog.swift.helpers.Defaults;

public final class UtcDateTypeAdapter extends TypeAdapter<Date> {

    @Override
    public void write(JsonWriter out, Date date) throws IOException {
        if (date == null) {
            out.nullValue();
        } else {
            DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            String value = dateFormat.format(date);
            out.value(value);
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            switch (in.peek()) {
                case NULL:
                    in.nextNull();
                    return null;
                default:
                    String date = in.nextString();
                    return dateFormat.parse(date);
            }
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }
}

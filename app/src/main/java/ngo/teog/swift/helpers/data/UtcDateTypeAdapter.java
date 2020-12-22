package ngo.teog.swift.helpers.data;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import ngo.teog.swift.helpers.Defaults;

import static com.google.gson.stream.JsonToken.NULL;

public final class UtcDateTypeAdapter extends TypeAdapter<Date> {

    @Override
    public void write(JsonWriter out, Date date) throws IOException {
        if (date == null) {
            out.nullValue();
        } else {
            DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN, Locale.ROOT);
            dateFormat.setTimeZone(TimeZone.getTimeZone(Defaults.TIMEZONE_UTC));

            String value = dateFormat.format(date);
            out.value(value);
        }
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat(Defaults.DATETIME_PRECISE_PATTERN, Locale.ROOT);
        dateFormat.setTimeZone(TimeZone.getTimeZone(Defaults.TIMEZONE_UTC));

        try {
            if(in.peek() == NULL) {
                in.nextNull();
                return null;
            } else {
                String date = in.nextString();
                return dateFormat.parse(date);
            }
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }
}

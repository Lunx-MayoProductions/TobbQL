package de.lunx.data;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class JsonDateAdapter extends TypeAdapter<JsonDate> {

    private final SimpleDateFormat isoFormat;

    public JsonDateAdapter() {
        isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public void write(JsonWriter out, JsonDate value) throws IOException {
        if (value == null || value.getDate() == null) {
            out.nullValue();
            return;
        }
        String dateString = isoFormat.format(value.getDate());
        out.beginObject();
        out.name("date").value(dateString);
        out.endObject();
    }

    @Override
    public JsonDate read(JsonReader in) throws IOException {
        Date parsedDate = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            if ("date".equals(name)) {
                String dateString = in.nextString();
                try {
                    parsedDate = isoFormat.parse(dateString);
                } catch (ParseException e) {
                    throw new IOException("Invalid date format", e);
                }
            } else {
                in.skipValue();
            }
        }
        in.endObject();

        return new JsonDate(parsedDate);
    }
}

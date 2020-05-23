package com.larryhsiao.nyx.jot;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.larryhsiao.nyx.core.jots.filter.ConstFilter;
import com.larryhsiao.nyx.core.jots.filter.Filter;

import java.io.IOException;

/**
 * Adapter for filter type
 */
public class FilterTypeAdapter extends TypeAdapter<Filter> {
    @Override
    public void write(JsonWriter out, Filter value) throws IOException {
        out.beginObject();
        out.name("keyword").value(value.keyword());
        out.name("started").value(value.dateRange()[0]);
        out.name("ended").value(value.dateRange()[1]);
        out.endObject();
    }

    @Override
    public Filter read(JsonReader in) throws IOException {
        String keyword = "";
        long started = 0L;
        long ended = 0L;
        in.beginObject();
        while (in.hasNext()) {
            String name = "";
            if (in.peek() == JsonToken.NAME) {
                name = in.nextName();
            }
            if ("keyword".equals(name)) {
                keyword = in.nextString();
            }
            if ("started".equals(name)) {
                started = in.nextLong();
            }
            if ("ended".equals(name)) {
                ended = in.nextLong();
            }
        }
        in.endObject();
        return new ConstFilter(new long[]{started, ended}, keyword);
    }
}

package com.larryhsiao.nyx.core.jots.moods;

import com.larryhsiao.nyx.core.jots.Jot;
import org.jetbrains.annotations.NotNull;

import javax.json.JsonObject;
import javax.json.JsonValue;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

/**
 * Json implementation of {@link Jot}.
 */
public class JsonJot implements Jot {
    private final JsonObject json;

    public JsonJot(JsonObject json) {
        this.json = json;
    }

    @Override
    public long id() {
        try {
            return json.getJsonNumber("id").longValue();
        }catch (Exception e){
            return -1L;
        }
    }

    @NotNull
    @Override
    public String title() {
        try {
            return json.getString("title");
        }catch (Exception e){
            return "";
        }
    }

    @NotNull
    @Override
    public String content() {
        try {
            return json.getString("content");
        }catch (Exception e){
            return "";
        }
    }

    @Override
    public long createdTime() {
        try {
            return json.getJsonNumber("createdTime").longValue();
        }catch (Exception e){
            return -1L;
        }
    }

    @NotNull
    @Override
    public double[] location() {
        try {
            return json.getJsonArray("location")
                .stream()
                .mapToDouble(jsonValue -> 0.0)
                .toArray();
        }catch (Exception e){
            return new double[]{0,0};
        }
    }

    @NotNull
    @Override
    public String mood() {
        try {
            return json.getString("mood");
        }catch (Exception e){
            return "";
        }
    }

    @Override
    public int version() {
        try {
            return json.getInt("version");
        }catch (Exception e){
            return -1;
        }
    }

    @Override
    public boolean privateLock() {
        try {
            return json.getBoolean("privateLock");
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public boolean deleted() {
        try {
            return json.getBoolean("deleted");
        }catch (Exception e){
            return false;
        }
    }
}

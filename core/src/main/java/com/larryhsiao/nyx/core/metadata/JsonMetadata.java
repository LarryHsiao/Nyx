package com.larryhsiao.nyx.core.metadata;

import org.jetbrains.annotations.NotNull;

import javax.json.JsonObject;
import java.math.BigDecimal;

/**
 * Json implementation of {@link Metadata}.
 */
public class JsonMetadata implements Metadata {
    private final JsonObject obj;

    public JsonMetadata(JsonObject obj) {this.obj = obj;}

    @Override
    public long id() {
        try {
            return obj.getJsonNumber("id").longValue();
        } catch (Exception e) {
            return -1L;
        }
    }

    @NotNull
    @Override
    public Type type() {
        try {
            return Type.valueOf(obj.getString("type"));
        } catch (Exception e) {
            return Type.TEXT;
        }
    }

    @NotNull
    @Override
    public String title() {
        try {
            return obj.getString("title");
        } catch (Exception e) {
            return "";
        }
    }

    @NotNull
    @Override
    public String value() {
        try {
            return obj.getString("value");
        } catch (Exception e) {
            return "";
        }
    }

    @NotNull
    @Override
    public BigDecimal valueBigDecimal() {
        try {
            return obj.getJsonNumber("valueBigDecimal").bigDecimalValue();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    @NotNull
    @Override
    public String comment() {
        try {
            return obj.getString("comment");
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public long jotId() {
        try {
            return obj.getJsonNumber("jotId").longValue();
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public long version() {
        try {
            return obj.getJsonNumber("version").longValue();
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public boolean deleted() {
        try {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}

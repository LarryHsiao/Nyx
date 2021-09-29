package com.larryhsiao.nyx.core.sync;

import javax.json.JsonNumber;
import javax.json.JsonObject;
import java.util.Collections;
import java.util.List;;
import java.util.stream.Collectors;

public class JsonJotIndex implements JotIndex {
    private final JsonObject jsonObj;

    public JsonJotIndex(JsonObject jsonObj) {
        this.jsonObj = jsonObj;
    }

    @Override
    public long id() {
        try {
            return jsonObj.getJsonNumber("id").longValue();
        } catch (Exception e) {
            return 0L;
        }
    }

    @Override
    public int version() {
        try {
            return jsonObj.getInt("version");
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean deleted() {
        try {
            return jsonObj.getBoolean("deleted");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Long> tagIds() {
        try {
            return jsonObj.getJsonArray("tagIds")
                .stream()
                .mapToLong(jsonValue -> {
                        try {
                            return ((JsonNumber) jsonValue).longValue();
                        } catch (Exception e) {
                            return -1L;
                        }
                    }
                )
                .filter(l -> l != -1)
                .boxed()
                .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}

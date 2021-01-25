package com.larryhsiao.nyx.core.sync.client;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.jots.Jot;

import javax.json.JsonArray;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Source to build a Jot list by given json array.
 */
public class JsonJots implements Source<List<Jot>> {
    private final JsonArray array;

    public JsonJots(JsonArray array) {
        this.array = array;
    }

    @Override
    public List<Jot> value() {
        return array.stream()
            .map(it -> new JsonJot(it.asJsonObject()))
            .collect(Collectors.toList());
    }
}

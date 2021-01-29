package com.larryhsiao.nyx.core.sync.json.jots;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.jots.Jot;

import javax.json.*;
import java.util.List;

/**
 * Source to build json array from list of {@link Jot}
 */
public class JotsJsonArray implements Source<JsonArray> {
    private final List<Jot> jots;

    public JotsJsonArray(List<Jot> jots) {
        this.jots = jots;
    }

    @Override
    public JsonArray value() {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (Jot jot : jots) {
            arrayBuilder.add(new JotJson(jot).value());
        }
        return arrayBuilder.build();
    }

}

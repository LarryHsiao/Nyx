package com.larryhsiao.nyx.core.sync.server;

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
            JsonObjectBuilder objBuilder = Json.createObjectBuilder();
            objBuilder.add("id", jot.id());
            objBuilder.add("title", jot.title());
            objBuilder.add("content", jot.content());
            objBuilder.add("deleted", jot.deleted());
            objBuilder.add("createdTime", jot.createdTime());
            objBuilder.add("location", location(jot));
            objBuilder.add("mood", jot.mood());
            objBuilder.add("privateLock", jot.privateLock());
            objBuilder.add("version", jot.version());
            objBuilder.add("version", jot.version());
            arrayBuilder.add(objBuilder.build());
        }
        return arrayBuilder.build();
    }

    private JsonArray location(Jot jot){
        JsonArrayBuilder array = Json.createArrayBuilder();
        double[] location = jot.location();
        array.add(location[0]);
        array.add(location[1]);
        return array.build();
    }
}

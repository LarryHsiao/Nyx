package com.larryhsiao.nyx.core.sync.json.jots;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.jots.Jot;

import javax.json.*;

/**
 * Source to build a JsonObject from jot.
 */
public class JotJson implements Source<JsonObject> {
    private final Jot jot;

    public JotJson(Jot jot) {
        this.jot = jot;
    }

    @Override
    public JsonObject value() {
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
        return objBuilder.build();
    }

    private JsonArray location(Jot jot){
        JsonArrayBuilder array = Json.createArrayBuilder();
        double[] location = jot.location();
        array.add(location[0]);
        array.add(location[1]);
        return array.build();
    }
}

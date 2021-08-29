package com.larryhsiao.nyx.core.jots.moods;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.jots.Jot;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Arrays;

/**
 * Source to build json string from {@link Jot}.
 */
public class JotJson implements Source<JsonObject> {
    private final Jot jot;

    public JotJson(Jot jot) {this.jot = jot;}

    @Override
    public JsonObject value() {
        return Json.createObjectBuilder()
            .add("id", jot.id())
            .add("title", jot.title())
            .add("content", jot.content())
            .add("createdTime", jot.createdTime())
            .add(
                "location",
                Json.createArrayBuilder()
                    .add(jot.location()[0])
                    .add(jot.location()[1])
                    .build()
            )
            .add("mood", jot.mood())
            .add("version", jot.version())
            .add("privateLock", jot.privateLock())
            .add("deleted", jot.deleted())
            .build();
    }
}

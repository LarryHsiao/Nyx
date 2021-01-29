package com.larryhsiao.nyx.core.sync.json.tags;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.tags.Tag;

import javax.json.JsonArray;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Source to build Tags from json.
 */
public class JsonTags implements Source<List<Tag>> {
    private final JsonArray array;

    public JsonTags(JsonArray array) {
        this.array = array;
    }

    @Override
    public List<Tag> value() {
        return array.stream()
            .map(it -> new JsonTag(it.asJsonObject()))
            .collect(Collectors.toList());
    }
}

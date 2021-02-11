package com.larryhsiao.nyx.core.sync.json.tags;

import com.larryhsiao.clotho.Source;
import com.larryhsiao.nyx.core.tags.JotTag;

import javax.json.JsonArray;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Source to build List of {@link JotTag} from json array.
 */
public class JsonJotTags implements Source<List<JotTag>> {
    private final JsonArray array;

    public JsonJotTags(JsonArray array) {
        this.array = array;
    }

    @Override
    public List<JotTag> value() {
        return array.stream()
            .map(it -> new JsonJotTag(it.asJsonObject()))
            .collect(Collectors.toList());
    }
}

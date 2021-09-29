package com.larryhsiao.nyx.core.metadata;

import com.larryhsiao.clotho.Source;

import javax.json.Json;
import javax.json.JsonObject;

public class MetadataJson implements Source<JsonObject> {
    private final Metadata metadata;

    public MetadataJson(Metadata metadata) {this.metadata = metadata;}

    @Override
    public JsonObject value() {
        return Json.createObjectBuilder()
            .add("id", metadata.id())
            .add("type", metadata.type().name())
            .add("title", metadata.title())
            .add("value", metadata.value())
            .add("valueBigDecimal", metadata.valueBigDecimal())
            .add("comment", metadata.comment())
            .add("jotId", metadata.jotId())
            .add("version", metadata.version())
            .add("delete", metadata.deleted())
            .build();
    }
}

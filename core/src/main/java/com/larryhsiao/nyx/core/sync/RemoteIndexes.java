package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.tags.JsonTag;
import com.larryhsiao.nyx.core.tags.Tag;
import com.larryhsiao.nyx.core.tags.TagJson;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonValue;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class RemoteIndexes implements NyxIndexes {
    private static final String PATH_TAG_JSON = "/tags.json";
    private static final String PATH_JOT_INDEX_JSON = "/jotIndex.json";
    private final Nyx nyx;
    private final RemoteFiles remoteFiles;

    public RemoteIndexes(Nyx nyx, RemoteFiles remoteFiles) {
        this.nyx = nyx;
        this.remoteFiles = remoteFiles;
    }

    @Override
    public List<JotIndex> jots() {
        if (remoteFiles.exist(PATH_JOT_INDEX_JSON)) {
            return Json.createReader(
                remoteFiles.get("/jotIndex.json")
            ).readArray().stream().map((Function<JsonValue, JotIndex>) jsonValue ->
                new JsonJotIndex(jsonValue.asJsonObject())
            ).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Tag> tags() {
        if (remoteFiles.exist(PATH_TAG_JSON)) {
            return Json.createReader(
                remoteFiles.get(PATH_TAG_JSON)
            ).readArray().stream().map((Function<JsonValue, Tag>) jsonValue ->
                new JsonTag(jsonValue.asJsonObject())
            ).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void updateTags(List<Tag> newTags) {
        final Map<Long, Tag> allTags = tags().stream().collect(toMap(Tag::id, Function.identity()));
        allTags.putAll(newTags.stream().collect(toMap(Tag::id, Function.identity())));
        final JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        for (Tag tag : allTags.values()) {
            jsonArray.add(new TagJson(tag).value());
        }
        remoteFiles.post(
            PATH_TAG_JSON,
            new ByteArrayInputStream(
                jsonArray.build().toString().getBytes()
            )
        );
    }

    @Override
    public void updateJots(List<Jot> newJots) {
        final Map<Long, JotIndex> allJotIndexes = jots()
            .stream()
            .collect(toMap(JotIndex::id, Function.identity()));
        allJotIndexes.putAll(
            newJots.stream().map((Function<Jot, JotIndex>) jot -> new ConstJotIndex(
                jot.id(),
                jot.version(),
                jot.deleted(),
                nyx.tags().byJotId(jot.id())
                    .stream()
                    .mapToLong(Tag::id)
                    .boxed()
                    .collect(Collectors.toList())
            )).collect(toMap(JotIndex::id, Function.identity()))
        );
        final JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        for (JotIndex jotIndex : allJotIndexes.values()) {
            jsonArray.add(new JotIndexJson(jotIndex).value());
        }
        remoteFiles.post(
            PATH_JOT_INDEX_JSON,
            new ByteArrayInputStream(
                jsonArray.build().toString().getBytes()
            )
        );
    }
}

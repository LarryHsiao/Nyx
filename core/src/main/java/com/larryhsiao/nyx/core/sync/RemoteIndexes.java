package com.larryhsiao.nyx.core.sync;

import com.larryhsiao.clotho.source.ConstSource;
import com.larryhsiao.nyx.core.Nyx;
import com.larryhsiao.nyx.core.attachments.Attachment;
import com.larryhsiao.nyx.core.attachments.AttachmentJson;
import com.larryhsiao.nyx.core.attachments.JsonAttachment;
import com.larryhsiao.nyx.core.jots.Jot;
import com.larryhsiao.nyx.core.metadata.JsonMetadata;
import com.larryhsiao.nyx.core.metadata.Metadata;
import com.larryhsiao.nyx.core.metadata.MetadataJson;
import com.larryhsiao.nyx.core.tags.JsonTag;
import com.larryhsiao.nyx.core.tags.Tag;
import com.larryhsiao.nyx.core.tags.TagJson;
import com.larryhsiao.nyx.core.util.FactorySource;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonValue;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class RemoteIndexes implements NyxIndexes {
    private static final String PATH_TAG_JSON = "/tags.json";
    private static final String PATH_JOT_INDEX_JSON = "/jotIndex.json";
    private static final String PATH_ATTACHMENT_JSON = "/attachment.json";
    private static final String PATH_METADATA_JSON = "/metadata.json";
    private static final String PATH_LOCK_FILE = "/lock.lck";
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
    public List<Attachment> attachments() {
        if (remoteFiles.exist(PATH_ATTACHMENT_JSON)) {
            return Json.createReader(
                remoteFiles.get(PATH_ATTACHMENT_JSON)
            ).readArray().stream().map((Function<JsonValue, Attachment>) jsonValue ->
                new JsonAttachment(jsonValue.asJsonObject())
            ).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void updateAttachments(List<Attachment> newAttachments) {
        final Map<Long, Attachment> allAttachments = attachments()
            .stream()
            .collect(toMap(Attachment::id, Function.identity()));
        allAttachments.putAll(
            newAttachments.stream()
                .collect(toMap(Attachment::id, Function.identity()))
        );
        final JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        for (Attachment attachment : allAttachments.values()) {
            jsonArray.add(new AttachmentJson(attachment).value());
        }
        remoteFiles.post(
            PATH_ATTACHMENT_JSON,
            new FactorySource<>(unused -> new ByteArrayInputStream(
                jsonArray.build().toString().getBytes()
            ))
        );
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
            new FactorySource<>(unused -> new ByteArrayInputStream(
                jsonArray.build().toString().getBytes()
            ))
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
            new FactorySource<>(unused -> new ByteArrayInputStream(
                jsonArray.build().toString().getBytes()
            ))
        );
    }

    @Override
    public void updateMetadata(List<Metadata> newMetadata) {
        final Map<Long, Metadata> allMetadata =
            metadata().stream().collect(toMap(Metadata::id, Function.identity()));
        allMetadata.putAll(newMetadata.stream().collect(toMap(Metadata::id, Function.identity())));
        final JsonArrayBuilder jsonArray = Json.createArrayBuilder();
        for (Metadata metadata : allMetadata.values()) {
            jsonArray.add(new MetadataJson(metadata).value());
        }
        remoteFiles.post(
            PATH_METADATA_JSON,
            new FactorySource<>(unused -> new ByteArrayInputStream(
                jsonArray.build().toString().getBytes()
            ))
        );
    }

    @Override
    public List<Metadata> metadata() {
        if (remoteFiles.exist(PATH_METADATA_JSON)) {
            return Json.createReader(
                remoteFiles.get(PATH_METADATA_JSON)
            ).readArray().stream().map((Function<JsonValue, Metadata>) jsonValue ->
                new JsonMetadata(jsonValue.asJsonObject())
            ).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void lock() {
        if (!remoteFiles.exist(PATH_LOCK_FILE)) {
            remoteFiles.post(
                PATH_LOCK_FILE,
                new FactorySource<>(unused ->
                    new ByteArrayInputStream(new byte[0])
                )
            );
        }
    }

    @Override
    public boolean isLocked() {
        return remoteFiles.exist(PATH_LOCK_FILE);
    }

    @Override
    public void unlock() {
        remoteFiles.delete(PATH_LOCK_FILE);
    }
}
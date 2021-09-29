package com.larryhsiao.nyx.core.attachments;

import com.larryhsiao.clotho.Source;

import javax.json.Json;
import javax.json.JsonObject;

public class AttachmentJson implements Source<JsonObject> {
    private final Attachment attachment;

    public AttachmentJson(Attachment attachment) {
        this.attachment = attachment;}

    @Override
    public JsonObject value() {
        return Json.createObjectBuilder()
            .add("id", attachment.id())
            .add("jotId", attachment.jotId())
            .add("uri", attachment.uri())
            .add("version", attachment.version())
            .add("deleted", attachment.deleted())
            .build();
    }
}

package com.larryhsiao.nyx.core.attachments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryAttachments implements Attachments {
    private final Map<Long, Attachment> attachmentMap = new HashMap<>();

    @Override
    public List<Attachment> all() {
        return new ArrayList<>(attachmentMap.values());
    }

    @Override
    public Attachment newAttachment(Attachment attachment) {
        attachmentMap.put(attachment.id(), attachment);
        return attachment;
    }

    @Override
    public Attachment byId(long id) {
        return attachmentMap.get(id);
    }
}

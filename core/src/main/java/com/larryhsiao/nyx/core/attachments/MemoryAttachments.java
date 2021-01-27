package com.larryhsiao.nyx.core.attachments;

import java.util.HashMap;
import java.util.Map;

public class MemoryAttachments implements Attachments {
    private final Map<Long, Attachment> attachmentMap = new HashMap<>();

    @Override
    public Attachment byId(long id) {
        return attachmentMap.get(id);
    }
}

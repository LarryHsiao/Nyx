package com.larryhsiao.nyx.core.attachments;

/**
 * Constant of Attachment
 */
public class ConstAttachment implements Attachment {
    private final long id;
    private final long jotId;
    private final String uri;

    public ConstAttachment(long id, long jotId, String uri) {
        this.id = id;
        this.jotId = jotId;
        this.uri = uri;
    }

    @Override
    public long jotId() {
        return jotId;
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public long id() {
        return id;
    }
}

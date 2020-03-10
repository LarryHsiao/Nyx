package com.larryhsiao.nyx.core.attachments;

/**
 * Constant of Attachment
 */
public class ConstAttachment implements Attachment {
    private final long id;
    private final long jotId;
    private final String uri;
    private final int version;
    private final int delete;

    public ConstAttachment(long id, long jotId, String uri, int version, int delete) {
        this.id = id;
        this.jotId = jotId;
        this.uri = uri;
        this.version = version;
        this.delete = delete;
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

    @Override
    public int version() {
        return version;
    }

    @Override
    public boolean deleted() {
        return delete == 1;
    }
}

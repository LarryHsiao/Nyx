package com.larryhsiao.nyx.core.attachments;

/**
 * Wrapped class of attachment
 */
public class WrappedAttachment implements Attachment {
    private final Attachment origin;

    public WrappedAttachment(Attachment origin) {
        this.origin = origin;
    }

    @Override
    public long id() {
        return origin.id();
    }

    @Override
    public long jotId() {
        return origin.jotId();
    }

    @Override
    public String uri() {
        return origin.uri();
    }

    @Override
    public int version() {
        return origin.version();
    }

    @Override
    public boolean deleted() {
        return false;
    }
}

package com.larryhsiao.nyx.core.attachments;

/**
 * Attachment of a Jot
 */
public interface Attachment {
    /**
     * Id of this attachment.
     */
    long id();

    /**
     * The Jot id that this attachment file attached to.
     */
    long jotId();

    /**
     * Uri of this attachment
     */
    String uri();

    /**
     * Version of this attachment item.
     */
    int version();

    /**
     * Indicates if this attachment is deleted or not.
     */
    boolean deleted();
}

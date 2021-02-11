package com.larryhsiao.nyx.core.tags;

/**
 * Object of a Jot Tag link.
 */
public interface JotTag {
    /**
     * The jot id.
     */
    long jotId();

    /**
     * The tag id.
     */
    long tagId();

    /**
     * Indicates if the Jot tag is deleted.
     */
    boolean deleted();

    /**
     * The version of this {@link JotTag}.
     */
    int version();
}

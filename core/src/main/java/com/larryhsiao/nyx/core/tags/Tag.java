package com.larryhsiao.nyx.core.tags;

/**
 * Tag of Jot
 */
public interface Tag {
    /**
     * The title of this tag
     */
    String title();

    /**
     * The id of this Tag
     */
    long id();

    /**
     * The version of this tag
     */
    int version();
}

package com.larryhsiao.nyx.jots;

/**
 * A note
 */
public interface Jot {
    /**
     * id of a jot.
     */
    long id();

    /**
     * The content of a jot.
     */
    String content();

    /**
     * Created time
     */
    long createdTime();
}

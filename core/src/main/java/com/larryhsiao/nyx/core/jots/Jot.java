package com.larryhsiao.nyx.core.jots;

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

    /**
     * The display location of this Jot
     *
     * @return length = 2, longitude and latitude.
     */
    double[] location();

    /**
     * A mood emoji for this jot.
     */
    String mood();

    /**
     * Version of this Jot. For syncing to replace from.
     */
    int version();

    /**
     * Determine this Jot is deleted.
     */
    boolean deleted();
}

package com.larryhsiao.nyx.core.jots.moods;

/**
 * A ranked mood.
 * Ranked by use times.
 */
public interface RankedMood {
    /**
     * The mood string, an emoji.
     */
    String mood();

    /**
     * Times being used.
     */
    int usedTimes();
}

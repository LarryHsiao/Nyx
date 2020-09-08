package com.larryhsiao.nyx.core.jots.moods

/**
 * A ranked mood.
 * Ranked by use times.
 */
interface RankedMood {
    /**
     * The mood string, an emoji.
     */
    fun mood(): String?

    /**
     * Times being used.
     */
    fun usedTimes(): Int
}
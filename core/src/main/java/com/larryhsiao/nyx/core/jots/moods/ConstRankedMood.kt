package com.larryhsiao.nyx.core.jots.moods

/**
 * Constant of [RankedMood]
 */
class ConstRankedMood(private val mood: String?, private val usedTimes: Int) : RankedMood {
    override fun mood(): String? {
        return mood
    }

    override fun usedTimes(): Int {
        return usedTimes
    }
}
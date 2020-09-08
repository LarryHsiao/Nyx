package com.larryhsiao.nyx.core.jots.moods

import com.silverhetch.clotho.Source

/**
 * Source to build moods for user to pick.
 */
class MergedMoods(
    private val ranked: Source<List<String>>,
    private val defaultSrc: Source<List<String>>
) : Source<List<String?>?> {
    override fun value(): List<String> {
        val result: MutableSet<String> = LinkedHashSet(ranked.value())
        val defaultMoods = defaultSrc.value()
        result.addAll(defaultMoods)
        return ArrayList(result).subList(0, defaultMoods.size - 1)
    }
}
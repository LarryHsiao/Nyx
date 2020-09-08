package com.larryhsiao.nyx.core.jots.moods

import com.larryhsiao.nyx.core.jots.AllJots
import com.larryhsiao.nyx.core.jots.QueriedJots
import com.silverhetch.clotho.Source
import com.silverhetch.clotho.utility.comparator.StringComparator
import java.sql.Connection
import java.util.*
import java.util.stream.Collectors

/**
 * Source to build moods by used ranking.
 */
class RankedMoods(private val db: Source<Connection>) : Source<List<RankedMood>> {
    override fun value(): List<RankedMood> {
        val result: MutableMap<String?, Int> = HashMap()
        for (jot in QueriedJots(AllJots(db)).value()) {
            if (jot.mood().length < 2 || jot.mood().substring(0, 1).contains(" ")) {
                continue
            }
            if (result.containsKey(jot.mood())) {
                result[jot.mood()] = result[jot.mood()]!! + 1
            } else {
                result[jot.mood()] = 1
            }
        }
        val strComparator: Comparator<String> = StringComparator()
        return result.entries.stream()
            .map {
                ConstRankedMood(
                    it.key,
                    it.value
                )
            }
            .sorted { constRankedMood: ConstRankedMood, t1: ConstRankedMood ->
                val value = t1.usedTimes() - constRankedMood.usedTimes()
                if (value == 0) {
                    return@sorted strComparator.compare(t1.mood(), constRankedMood.mood())
                }
                value
            }
            .collect(Collectors.toList())
    }
}
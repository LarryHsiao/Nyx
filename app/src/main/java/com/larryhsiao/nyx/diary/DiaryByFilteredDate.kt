package com.larryhsiao.nyx.diary

import com.silverhetch.clotho.Source
import com.silverhetch.clotho.SourceWrapper

/**
 * Source decorator for filter out origin source Diary at given date.
 */
class DiaryByFilteredDate(
    source: Source<List<Diary>>,
    private val utcTime: Long
) : SourceWrapper<List<Diary>>(source) {
    override fun value(): List<Diary> {
        return super.value().filter { it.timestamp() in utcTime..(utcTime + 86400000) }
    }
}

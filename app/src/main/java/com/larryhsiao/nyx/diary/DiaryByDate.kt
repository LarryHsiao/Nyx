package com.larryhsiao.nyx.diary

import com.silverhetch.clotho.Source
import java.time.ZoneOffset
import java.util.*

/**
 * Source that generates the [RDiary] from database by timestamp which is at same date of given [utcTimestamp].
 *
 * Sample:
 * Given
 */
class DiaryByDate(
    private val dao: DiaryDao,
    private val utcTimestamp: Long
) : Source<List<Diary>> {
    override fun value(): List<Diary> {
        return if (utcTimestamp == 0L) {
            DiaryFromRDiary(dao.all()).value()
        } else {
            DiaryFromRDiary(
                dao.byTimestamp(
                    utcTimestamp,
                    utcTimestamp + 86400000
                )
            ).value()
        }
    }
}
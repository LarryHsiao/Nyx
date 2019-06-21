package com.larryhsiao.nyx.diary

import com.silverhetch.clotho.Source

/**
 * Source that generates the [RDiary] from database.
 */
class DiaryByDate(private val dao: DiaryDao, private val utcTimestamp: Long) : Source<List<Diary>> {
    override fun value(): List<Diary> {
        return if (utcTimestamp == 0L) {
            DiaryFromRDiary(dao.all()).value()
        } else {
            DiaryFromRDiary(dao.byDate(utcTimestamp)).value()
        }
    }
}
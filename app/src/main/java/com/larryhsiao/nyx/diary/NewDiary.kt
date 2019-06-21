package com.larryhsiao.nyx.diary

import com.silverhetch.clotho.Source

/**
 * Create or update new diary.
 */
class NewDiary(
    private val dao: DiaryDao,
    private val title: String,
    private val utcTimestamp: Long
) : Source<Diary> {
    override fun value(): Diary {
        val newId = dao.create(RDiary(0, title, utcTimestamp))
        return RoomDiary(RDiary(newId, title, utcTimestamp))
    }
}
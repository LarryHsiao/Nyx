package com.larryhsiao.nyx.diary

import com.larryhsiao.nyx.diary.room.DiaryDao
import com.silverhetch.clotho.Source

/**
 * Source that generates the [RDiary] from database by timestamp which is at same date of given [utcTimestamp].
 *
 * Sample:
 * Given
 */
class DiaryById(
    private val dao: DiaryDao,
    private val id: Long
) : Source<Diary> {
    override fun value(): Diary {
        return RoomDiary(dao.byId(id))
    }
}
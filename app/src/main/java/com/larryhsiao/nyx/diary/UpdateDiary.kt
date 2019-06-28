package com.larryhsiao.nyx.diary

import com.larryhsiao.nyx.diary.room.DiaryDao
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.silverhetch.clotho.Source
import java.lang.IllegalArgumentException

/**
 * Update the exist diary.
 */
class UpdateDiary(
    private val dao: DiaryDao,
    private val id: Long,
    private val title: String,
    private val timestamp: Long
) : Source<Diary> {
    override fun value(): Diary {
        if (title.isBlank()) {
            throw IllegalArgumentException("The title should not be empty/blank")
        }
        dao.update(
            DiaryEntity(
                id,
                title,
                timestamp
            )
        )
        return RoomDiary(dao.byId(id))
    }
}
package com.larryhsiao.nyx.diary

import com.larryhsiao.nyx.diary.room.DiaryDao
import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.silverhetch.clotho.Action

/**
 * Update the exist diary.
 */
class UpdateDiary(
    private val dao: DiaryDao,
    private val id: Long,
    private val title: String,
    private val timestamp: Long
) : Action {
    override fun fire() {
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
        RoomDiary(dao.byId(id))
    }
}
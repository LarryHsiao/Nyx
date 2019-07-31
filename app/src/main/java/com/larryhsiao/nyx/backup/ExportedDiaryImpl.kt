package com.larryhsiao.nyx.backup

import com.google.gson.Gson
import com.larryhsiao.nyx.diary.room.DiaryEntity

/**
 * Implementation of [ExportedDiary].
 */
class ExportedDiaryImpl(
    private val gson: Gson,
    private val entity: DiaryEntity
) : ExportedDiary {
    override fun json(): String {
        return gson.toJson(entity)
    }
}
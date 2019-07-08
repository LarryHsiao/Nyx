package com.larryhsiao.nyx.diary

import com.larryhsiao.nyx.diary.room.RDiary
import java.util.*

/**
 * The [RDiary] adapter of diary.
 */
class RoomDiary(private val roomDiary: RDiary) : Diary {
    override fun id(): Long {
        return roomDiary.diary.id
    }

    override fun title(): String {
        return roomDiary.diary.title ?: ""
    }

    override fun timestamp(): Long {
        return Calendar.getInstance().also {
            it.timeZone = TimeZone.getTimeZone("UTC")
            it.timeInMillis = roomDiary.diary.timestamp ?: 0L
            it.timeZone = TimeZone.getDefault()
        }.timeInMillis
    }
}
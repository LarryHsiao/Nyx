package com.larryhsiao.nyx.diary

import java.util.*

/**
 * The room implementation of diary.
 */
class RoomDiary(private val RDiary: RDiary) : Diary {
    override fun id(): Long {
        return RDiary.id
    }

    override fun title(): String {
        return RDiary.title ?: ""
    }

    override fun timestamp(): Long {
        return Calendar.getInstance().also {
            it.timeZone = TimeZone.getTimeZone("UTC")
            it.timeInMillis = RDiary.timestamp ?: 0L
            it.timeZone = TimeZone.getDefault()
        }.timeInMillis
    }
}
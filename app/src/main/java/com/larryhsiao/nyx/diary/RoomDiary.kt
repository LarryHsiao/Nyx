package com.larryhsiao.nyx.diary

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
        return RDiary.timestamp ?: 0L
    }
}
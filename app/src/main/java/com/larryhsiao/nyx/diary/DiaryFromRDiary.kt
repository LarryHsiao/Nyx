package com.larryhsiao.nyx.diary

import com.larryhsiao.nyx.diary.room.DiaryEntity
import com.larryhsiao.nyx.diary.room.RDiary
import com.silverhetch.clotho.Source

/**
 * Source to generate [Diary] with [DiaryEntity]
 */
class DiaryFromRDiary(private val data: List<RDiary>) : Source<List<Diary>> {
    override fun value(): List<Diary> {
        return Array(data.size) {
            RoomDiary(data[it])
        }.toList()
    }
}
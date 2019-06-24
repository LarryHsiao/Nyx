package com.larryhsiao.nyx.diary

import com.larryhsiao.nyx.diary.room.DiaryDao
import com.silverhetch.clotho.Source

/**
 * All of diary.
 */
class AllDiary(private val dao: DiaryDao) : Source<List<Diary>> {
    override fun value(): List<Diary> {
        return DiaryFromRDiary(dao.all()).value()
    }
}
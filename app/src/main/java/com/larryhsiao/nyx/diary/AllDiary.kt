package com.larryhsiao.nyx.diary

import com.silverhetch.clotho.Source

/**
 * All of diary.
 */
class AllDiary(private val dao: DiaryDao) : Source<List<Diary>> {
    override fun value(): List<Diary> {
        val dbResult = dao.all()
        return Array(dbResult.size){
            RoomDiary(dbResult[it])
        }.toList()
    }
}
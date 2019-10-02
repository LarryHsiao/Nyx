package com.larryhsiao.nyx.diary

import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.Source

/**
 * Source to build diaries with location.
 */
class DiaryWithLocation(private val db: RDatabase) : Source<List<Diary>> {
    override fun value(): List<Diary> {
        return DiaryFromRDiary(db.diaryDao().allWithLocations()).value()
    }
}
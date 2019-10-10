package com.larryhsiao.nyx.diary

import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.Source

/**
 * Source to build Diary objects by given ids.
 */
class DiaryByIds(
    private val db: RDatabase,
    private val ids: LongArray
) : Source<List<Diary>> {
    override fun value(): List<Diary> {
        return DiaryFromRDiary(db.diaryDao().byIds(ids)).value()
    }
}
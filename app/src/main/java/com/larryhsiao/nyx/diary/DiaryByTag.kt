package com.larryhsiao.nyx.diary

import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.Source

/**
 * Source to build tags by given tag
 */
class DiaryByTag(
    private val db: RDatabase,
    private val tagId: Long
) : Source<List<Diary>> {
    override fun value(): List<Diary> {
        return DiaryFromRDiary(
            db.diaryDao().byTagId(tagId)
        ).value()
    }
}
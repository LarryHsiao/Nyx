package com.larryhsiao.nyx.tag

import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.Source

/**
 * Tags that have attached to a diary.
 */
class TagsByDiary(
    private val db: RDatabase,
    private val diaryId: Long
) : Source<List<Tag>> {
    override fun value(): List<Tag> {
        val dbTag = db.tagDao().byDiaryId(diaryId)
        return Array(dbTag.size) {
            DbTag(db, dbTag[it])
        }.toList()
    }
}
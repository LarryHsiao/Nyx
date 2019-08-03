package com.larryhsiao.nyx.tag

import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.Diary
import com.larryhsiao.nyx.tag.room.TagDiaryEntity
import com.silverhetch.clotho.Action

/**
 * Apply a releation to exist tag and diary
 */
class NewTagAttachment(
    private val db: RDatabase,
    private val tagId: Long,
    private val diaryId: Long
) : Action {
    override fun fire() {
        db.tagDiaryDao().create(TagDiaryEntity(0, diaryId, tagId))
    }
}
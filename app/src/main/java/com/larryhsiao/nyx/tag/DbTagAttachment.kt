package com.larryhsiao.nyx.tag

import com.larryhsiao.nyx.database.RDatabase

/**
 * Decorator for [Tag] that this is a attached tag.
 */
class DbTagAttachment(
    private val db: RDatabase,
    private val tag: Tag,
    private val diaryId: Long
) : Tag {
    override fun id(): Long {
        return tag.id()
    }

    override fun title(): String {
        return tag.title()
    }

    override fun delete() {
        db.tagDiaryDao().delete(
            diaryId, tag.id()
        )
    }
}
package com.larryhsiao.nyx.tag

import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.tag.room.TagEntity

/**
 * DbTag
 */
class DbTag(
    private val db: RDatabase,
    private val tagEntity: TagEntity
) : Tag {
    override fun id(): Long {
        return tagEntity.id
    }

    override fun title(): String {
        return tagEntity.title
    }

    override fun delete() {
        db.tagDao().delete(tagEntity.id)
        db.tagDiaryDao().deleteByTagId(tagEntity.id)
    }
}
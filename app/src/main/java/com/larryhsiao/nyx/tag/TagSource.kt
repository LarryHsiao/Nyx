package com.larryhsiao.nyx.tag

import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.Source

/**
 * Build a [Tag] by title, create one to db if not exist
 */
class TagSource(private val db: RDatabase, private val title: String) : Source<Tag> {
    override fun value(): Tag {
        return DbTag(db, db.tagDao().queryOrCreate(title))
    }
}
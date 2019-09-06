package com.larryhsiao.nyx.tag

import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.Source

/**
 * Source to build tag from id
 */
class TagById(private val db: RDatabase, private val id: Long) : Source<Tag> {
    override fun value(): Tag {
        return DbTag(db, db.tagDao().byId(id))
    }
}
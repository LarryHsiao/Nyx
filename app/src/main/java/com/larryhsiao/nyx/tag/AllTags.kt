package com.larryhsiao.nyx.tag

import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.Source

/**
 * Source to build a list of [Tag] which have store in database
 */
class AllTags(private val db: RDatabase) : Source<List<Tag>> {
    override fun value(): List<Tag> {
        val dbTags = db.tagDao().all()
        return Array(dbTags.size) {
            DbTag(db, dbTags[it])
        }.toList()
    }
}
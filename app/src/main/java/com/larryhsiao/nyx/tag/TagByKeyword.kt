package com.larryhsiao.nyx.tag

import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.Source

/**
 * Source to Build tag list that match the keyword.
 */
class TagByKeyword(
    private val db: RDatabase,
    private val keyword: String
) : Source<List<Tag>> {
    override fun value(): List<Tag> {
        return db.tagDao().searchByName("%$keyword%")
            .mapTo(ArrayList<Tag>()) { DbTag(db, it) }
    }
}
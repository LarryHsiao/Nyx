package com.larryhsiao.nyx.tag

import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.Action

/**
 * Action to delete a tag.
 *
 * @todo #12 test for checking the relation have delete correctly
 */
class TagDeletion(
    private val db: RDatabase,
    private val tagId: Long
) : Action {
    override fun fire() {
        db.tagDao().delete(tagId)
        db.tagDiaryDao().deleteByTagId(tagId)
    }
}
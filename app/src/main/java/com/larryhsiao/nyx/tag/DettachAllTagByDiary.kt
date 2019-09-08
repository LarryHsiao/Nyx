package com.larryhsiao.nyx.tag

import com.larryhsiao.nyx.database.RDatabase
import com.silverhetch.clotho.Action

/**
 * Action to remove all tag relations
 */
class DettachAllTagByDiary(
    private val db: RDatabase,
    private val diaryId: Long
) : Action {
    override fun fire() {
        db.tagDiaryDao().deleteByDiaryId(diaryId)
    }
}
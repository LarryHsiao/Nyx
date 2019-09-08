package com.larryhsiao.nyx.diary

import com.larryhsiao.nyx.database.RDatabase
import com.larryhsiao.nyx.diary.room.DiaryDao
import com.larryhsiao.nyx.media.room.MediaDao
import com.silverhetch.clotho.Action
import java.io.File
import java.net.URI

/**
 * Action of Diary deletion.
 *
 *
 * @todo #11 test for checking media, tags have deleted correctly
 */
class DiaryDeletion(
    private val db: RDatabase,
    private val diaryId: Long
) : Action {
    override fun fire() {
        db.diaryDao().delete(diaryId)
        db.mediaDao().apply {
            byDiaryId(diaryId).forEach {
                if (it.uri.startsWith("file:")) {
                    File(URI(it.uri)).delete()
                }
            }
            deleteByDiaryId(diaryId)
        }
        db.tagDiaryDao().deleteByDiaryId(diaryId)
    }
}
package com.larryhsiao.nyx.diary

import com.larryhsiao.nyx.diary.room.DiaryDao
import com.larryhsiao.nyx.media.room.MediaDao
import com.silverhetch.clotho.Action
import java.io.File
import java.net.URI

/**
 * Action of Diary deletion.
 */
class DiaryDeletion(
    private val diaryDao: DiaryDao,
    private val mediaDao: MediaDao,
    private val diaryId: Long
) : Action {
    override fun fire() {
        diaryDao.delete(diaryId)
        mediaDao.byDiaryId(diaryId).forEach {
            if (it.uri.startsWith("file:")) {
                File(URI(it.uri)).delete()
            }
        }
        mediaDao.deleteByDiaryId(diaryId)
    }
}
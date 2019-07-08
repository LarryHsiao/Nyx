package com.larryhsiao.nyx.diary

import com.larryhsiao.nyx.diary.room.DiaryDao
import com.silverhetch.clotho.Action

/**
 * Action of Diary deletion.
 */
class DiaryDeletion(private val dao: DiaryDao, private val id: Long) : Action {
    override fun fire() {
        dao.delete(id)
    }
}
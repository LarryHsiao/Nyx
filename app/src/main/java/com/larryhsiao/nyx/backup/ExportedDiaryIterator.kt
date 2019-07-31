package com.larryhsiao.nyx.backup

import com.google.gson.Gson
import com.larryhsiao.nyx.diary.room.RDiary

/**
 * Wrapper Iterator of exported diary.
 */
class ExportedDiaryIterator(
    private val diaryIterator: Iterator<RDiary>
) : Iterator<ExportedDiary> {
    private val gson = Gson()
    override fun hasNext(): Boolean {
        return diaryIterator.hasNext()
    }

    override fun next(): ExportedDiary {
        if (hasNext().not()){
            throw NoSuchElementException()
        }
        return ExportedDiaryImpl(gson, diaryIterator.next().diary)
    }
}
package com.larryhsiao.nyx.backup

import com.larryhsiao.nyx.diary.room.DiaryDao
import com.silverhetch.clotho.Source

/**
 * Export all diary to target file.
 */
class DiaryExport(
    private val dao: DiaryDao
) : Source<Iterator<ExportedDiary>> {
    override fun value(): Iterator<ExportedDiary> {
        return ExportedDiaryIterator(dao.all().iterator())
    }
}
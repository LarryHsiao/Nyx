package com.larryhsiao.nyx.backup.tag

import com.larryhsiao.nyx.tag.room.TagDiaryDao
import com.silverhetch.clotho.Source

/**
 * Export tag_diary relation as String.
 */
class TagDiaryExport(
    private val tagDiaryDao: TagDiaryDao
) : Source<Iterator<String>> {
    override fun value(): Iterator<String> {
        return ObjectIterator(tagDiaryDao.all().iterator())
    }
}
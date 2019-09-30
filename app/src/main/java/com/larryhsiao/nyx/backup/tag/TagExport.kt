package com.larryhsiao.nyx.backup.tag

import com.larryhsiao.nyx.tag.room.TagDao
import com.silverhetch.clotho.Source

/**
 * Export tags as strings
 */
class TagExport(private val tagDao: TagDao) : Source<Iterator<String>> {
    override fun value(): Iterator<String> {
        return ObjectIterator(tagDao.all().iterator())
    }
}
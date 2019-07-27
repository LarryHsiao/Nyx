package com.larryhsiao.nyx.backup

import com.larryhsiao.nyx.media.room.MediaDao
import com.silverhetch.clotho.Source

/**
 * Export current media files to
 */
class MediaExport(
    private val dao: MediaDao
) : Source<Iterator<ExportedMedia>> {
    override fun value(): Iterator<ExportedMedia> {
        return ExportedMediaIterator(dao.all().iterator())
    }
}
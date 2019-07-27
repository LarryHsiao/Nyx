package com.larryhsiao.nyx.backup

import com.google.gson.Gson
import com.larryhsiao.nyx.media.room.MediaEntity

/**
 * Iterator for [ExportedMedia].
 */
class ExportedMediaIterator(private val mediaIterator: Iterator<MediaEntity>) :
    Iterator<ExportedMedia> {
    private val gson = Gson()
    override fun hasNext(): Boolean {
        return mediaIterator.hasNext()
    }

    override fun next(): ExportedMedia {
        return ExportedMediaImpl(gson, mediaIterator.next())
    }
}
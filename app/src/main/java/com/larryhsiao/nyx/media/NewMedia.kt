package com.larryhsiao.nyx.media

import android.content.Context
import com.larryhsiao.nyx.media.room.MediaDao
import com.larryhsiao.nyx.media.room.MediaEntity
import com.larryhsiao.nyx.uri.InputStreamSource
import com.silverhetch.clotho.Source
import com.silverhetch.clotho.file.ToFile
import java.io.File
import java.util.*

/**
 * Source to create new Media in Internal storage from given source uri.
 */
class NewMedia(
    private val context: Context,
    private val dao: MediaDao,
    private val diaryId: Long,
    private val mediaRoot: File,
    private val uri: String
) : Source<MediaEntity> {
    override fun value(): MediaEntity {
        val dstUri = targetUri()

        return MediaEntity(
            dao.create(
                MediaEntity(
                    0,
                    diaryId,
                    dstUri
                )
            ), diaryId, dstUri
        )
    }

    private fun targetUri(): String {
        if (!uri.startsWith("content:")) {
            return uri
        }
        val dstFile = File(
            mediaRoot,
            UUID.randomUUID().toString().substring(0, 8)
        ).also {
            it.parentFile.mkdirs()
            it.createNewFile()
        }
        val dstUri = dstFile.toURI().toString()
        ToFile(
            InputStreamSource(
                context,
                uri
            ).value(),
            dstFile
        ) { /*Not monitoring Leave it empty.*/ }.fire()
        return dstUri
    }
}
package com.larryhsiao.nyx.backup

import android.net.Uri
import com.google.gson.Gson
import com.larryhsiao.nyx.media.room.MediaEntity
import java.io.IOException

/**
 * Implementation of [ExportedMedia]
 */
class ExportedMediaImpl(
    private val gson: Gson,
    private val entity: MediaEntity
) : ExportedMedia {

    override fun json(): String {
        return gson.toJson(entity)
    }

    override fun mediaUri(): Uri {
        return try {
            Uri.parse(entity.uri)
        } catch (e: IOException) {
            Uri.parse("null://null.null")
        }
    }

    override fun isFile(): Boolean {
        return (mediaUri().scheme ?: "").startsWith("file")
    }
}
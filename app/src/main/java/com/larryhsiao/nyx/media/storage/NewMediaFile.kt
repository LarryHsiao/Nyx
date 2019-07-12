package com.larryhsiao.nyx.media.storage

import android.content.Context
import com.larryhsiao.nyx.ConfigImpl
import com.silverhetch.clotho.Source
import java.io.File
import java.util.*

/**
 * Source to generate media file.
 */
class NewMediaFile(
    private val contex: Context,
    private val name: String = UUID.randomUUID().toString().substring(0, 7)
) : Source<File> {
    override fun value(): File {
        return File(
            File(ConfigImpl(contex).mediaRoot(), name),
            name
        ).also {
            it.parentFile.mkdirs()
            it.createNewFile()
        }
    }
}
package com.larryhsiao.nyx

import android.content.Context
import java.io.File

/**
 * Implementation of [Config], the default behavior of Nyx.
 */
class ConfigImpl(private val context: Context) : Config {
    companion object {
        private const val PATH_MEDIA = "medias/"
    }

    override fun mediaRoot(): File {
        return File(context.filesDir, PATH_MEDIA)
    }
}
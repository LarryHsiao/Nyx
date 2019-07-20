package com.larryhsiao.nyx.uri

import android.content.Context
import androidx.core.net.toUri
import com.silverhetch.clotho.Source

/**
 * Determine if the given uri.
 */
class IsInternalFile(
    private val context: Context,
    private val uri: String
) : Source<Boolean> {
    override fun value(): Boolean {
        val rootPath = context.filesDir.absoluteFile.toUri().toString()
        return uri.startsWith(rootPath.replace("///","/"))
    }

}
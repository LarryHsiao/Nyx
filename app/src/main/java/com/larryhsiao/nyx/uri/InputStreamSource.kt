package com.larryhsiao.nyx.uri

import android.content.Context
import android.net.Uri
import com.silverhetch.clotho.Source
import java.io.InputStream
import java.lang.UnsupportedOperationException

/**
 * Factory to generate [InputStream] from uri
 */
class InputStreamSource(
    private val context: Context,
    private val uri: String
) : Source<InputStream> {
    override fun value(): InputStream {
        return when {
            uri.startsWith("content://") -> {
                context.contentResolver.openInputStream(Uri.parse(uri))
            }
            else -> throw UnsupportedOperationException("Not support this type of uri: $uri")
        }
    }
}
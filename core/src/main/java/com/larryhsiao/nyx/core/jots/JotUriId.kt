package com.larryhsiao.nyx.core.jots

import com.silverhetch.clotho.Source
import java.net.URI

/**
 * Source to build Jot from uri.
 */
class JotUriId(private val uri: String) : Source<Long?> {
    override fun value(): Long {
        val uriObj = URI.create(uri)
        return if (uriObj.path.startsWith("/jots/")) {
            java.lang.Long.valueOf(uriObj.path.replace("/jots/", ""))
        } else {
            throw IllegalArgumentException("Not a jot uri")
        }
    }
}
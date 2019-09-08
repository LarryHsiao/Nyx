package com.larryhsiao.nyx.tag

import com.silverhetch.clotho.Source
import java.lang.IllegalArgumentException
import java.net.URI

/**
 * Source to build Tag id from URI.
 */
class UriTagId(private val uri: URI) : Source<Long> {
    override fun value(): Long {
        try {
            if (uri.path.startsWith("/tags/").not()){
                throw IllegalArgumentException("Not a tag uri")
            }
            return uri.path.replace("/tags/", "").toLong()
        } catch (e: NumberFormatException) {
            throw IllegalArgumentException("Not a tag uri")
        }
    }
}
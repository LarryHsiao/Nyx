package com.larryhsiao.nyx.core.jots

import com.larryhsiao.clotho.Source
import java.net.URI

/**
 * Source to build Jot uri
 */
class JotUri(private val host: String, private val jot: Jot) : Source<URI?> {
    override fun value(): URI {
        return URI.create(host).resolve("/jots/" + jot.id())
    }
}
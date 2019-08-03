package com.larryhsiao.nyx.uri.nyx

import com.silverhetch.clotho.Source
import java.net.URI

/**
 * Source to build URI that represent a resource of Nyx have.
 */
class NyxUri(
    private val path: String
) : Source<URI> {
    override fun value(): URI {
        return URI(
            "nyx",
            "larryhsiao.com",
            path,
            null
        )
    }
}
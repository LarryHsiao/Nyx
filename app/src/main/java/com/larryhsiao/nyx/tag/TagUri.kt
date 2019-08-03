package com.larryhsiao.nyx.tag

import com.larryhsiao.nyx.uri.nyx.NyxUri
import com.silverhetch.clotho.Source
import java.net.URI

/**
 *  Source to build a URI for Nyx`s Tag.
 */
class TagUri(private val tagId: Long) : Source<URI> {
    companion object {
        private const val PATH_TAG = "/tags/"
    }

    override fun value(): URI {
        return NyxUri(
            PATH_TAG + tagId
        ).value()
    }
}
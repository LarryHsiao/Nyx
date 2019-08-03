package com.larryhsiao.nyx.tag

import java.net.URI

/**
 * Represents a Tag
 */
interface Tag {
    /**
     * Id of this tag.
     */
    fun id():Long
    /**
     * The title of this tag.
     */
    fun title(): String

    /**
     * Delete this tag
     */
    fun delete()
}
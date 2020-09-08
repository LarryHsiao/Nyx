package com.larryhsiao.nyx.core.tags

/**
 * Tag of Jot
 */
interface Tag {
    /**
     * The title of this tag
     */
    fun title(): String?

    /**
     * The id of this Tag
     */
    fun id(): Long

    /**
     * The version of this tag
     */
    fun version(): Int

    /**
     * This tag is marked as deleted
     */
    fun deleted(): Boolean
}
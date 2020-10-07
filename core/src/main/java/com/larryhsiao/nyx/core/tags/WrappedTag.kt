package com.larryhsiao.nyx.core.tags

/**
 * Wrapper class of Tag
 */
open class WrappedTag(private val origin: Tag) : Tag {
    override fun title(): String {
        return origin.title()
    }

    override fun id(): Long {
        return origin.id()
    }

    override fun version(): Int {
        return origin.version()
    }

    override fun deleted(): Boolean {
        return origin.deleted()
    }
}
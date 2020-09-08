package com.larryhsiao.nyx.core.tags

/**
 * Constant of Tag
 */
class ConstTag(
    private val id: Long,
    private val title: String?,
    private val version: Int,
    private val deleted: Boolean
) : Tag {
    override fun title(): String? {
        return title
    }

    override fun id(): Long {
        return id
    }

    override fun version(): Int {
        return version
    }

    override fun deleted(): Boolean {
        return deleted
    }
}
package com.larryhsiao.nyx.core.metadata

import com.larryhsiao.nyx.core.metadata.Metadata.*

/**
 * Constant metadata
 */
class ConstMetadata(
    private val id: Long,
    private val jotId: Long,
    private val type: String,
    private val title: String,
    private val content: String,
    private val version: Long = 1
) : Metadata {
    override fun id(): Long {
        return id;
    }

    override fun version(): Long {
        return version
    }

    override fun type(): Type {
        return try {
            Type.valueOf(type)
        } catch (e: Exception) {
            Type.RAW
        }
    }

    override fun title(): String {
        return title
    }

    override fun content(): String {
        return content
    }

    override fun jotId(): Long {
        return jotId
    }
}
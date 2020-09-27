package com.larryhsiao.nyx.core.metadata

/**
 * Wrapped object of [Metadata].
 */
open class WrappedMetadata(
    private val metadata: Metadata
) : Metadata {
    override fun id(): Long = metadata.id()

    override fun type(): Metadata.Type = metadata.type()

    override fun title(): String = metadata.title()

    override fun content(): String = metadata.content()

    override fun jotId(): Long = metadata.jotId()

    override fun version(): Long = metadata.version()
}
package com.larryhsiao.nyx.core.metadata

import java.math.BigDecimal

/**
 * Wrapped object of [Metadata].
 */
open class WrappedMetadata(
    private val metadata: Metadata
) : Metadata {
    override fun id(): Long = metadata.id()
    override fun type(): Metadata.Type = metadata.type()
    override fun title(): String = metadata.title()
    override fun value(): String = metadata.value()
    override fun valueBigDecimal(): BigDecimal = metadata.valueBigDecimal()
    override fun comment(): String = metadata.comment()
    override fun jotId(): Long = metadata.jotId()
    override fun version(): Long = metadata.version()
    override fun deleted(): Boolean = metadata.deleted()
}
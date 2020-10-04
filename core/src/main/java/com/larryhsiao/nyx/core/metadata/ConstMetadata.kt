package com.larryhsiao.nyx.core.metadata

import com.larryhsiao.nyx.core.metadata.Metadata.*
import java.math.BigDecimal

/**
 * Constant metadata
 */
class ConstMetadata(
    private val id: Long,
    private val jotId: Long,
    private val type: Type,
    private val value: String,
    private val title: String = "",
    private val valueBigDecimal: BigDecimal = BigDecimal.ZERO,
    private val comment: String = "",
    private val version: Long = 1,
    private val deleted: Boolean = false
) : Metadata {
    override fun id(): Long = id
    override fun version(): Long = version
    override fun type(): Type = type
    override fun title(): String = title
    override fun value(): String = value
    override fun valueBigDecimal(): BigDecimal = valueBigDecimal
    override fun comment(): String = comment
    override fun jotId(): Long = jotId
    override fun deleted(): Boolean = deleted
}
package com.larryhsiao.nyx.core.metadata

import java.math.BigDecimal

/**
 * A metadata for a Jot.
 */
interface Metadata {
    fun id(): Long
    fun type(): Type
    fun title(): String
    fun value(): String
    fun valueBigDecimal(): BigDecimal
    fun comment():String
    fun jotId(): Long
    fun version():  Long
    fun deleted(): Boolean

    /**
     * The type of this metadata,
     * note that the name of the enum should not be
     * changed after the app has been publish.
     */
    enum class Type {
        TEXT,
        OPEN_WEATHER
    }
}
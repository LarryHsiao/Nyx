package com.larryhsiao.nyx.core.metadata

/**
 * A metadata for a Jot.
 */
interface Metadata {
    fun id(): Long
    fun type(): Type
    fun title(): String
    fun content(): String
    fun jotId(): Long
    fun version():  Long

    /**
     * The type of this metadata,
     * note that the name of the enum should not be
     * changed after the app has been publish.
     */
    enum class Type {
        RAW,
        OPEN_WEATHER
    }
}